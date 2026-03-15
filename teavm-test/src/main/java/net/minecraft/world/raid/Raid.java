package net.minecraft.world.raid;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
// Removed unused Stream import for TeaVM efficiency
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.monster.AbstractRaiderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.play.server.SPlaySoundEffectPacket;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.BossInfo;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.spawner.WorldEntitySpawner;

public class Raid {
   private static final ITextComponent RAID = new TranslationTextComponent("event.minecraft.raid");
   private static final ITextComponent VICTORY = new TranslationTextComponent("event.minecraft.raid.victory");
   private static final ITextComponent DEFEAT = new TranslationTextComponent("event.minecraft.raid.defeat");
   private static final ITextComponent RAID_VICTORY = RAID.deepCopy().appendString(" - ").append(VICTORY);
   private static final ITextComponent RAID_DEFEAT = RAID.deepCopy().appendString(" - ").append(DEFEAT);
   private final Map<Integer, AbstractRaiderEntity> leaders = Maps.newHashMap();
   private final Map<Integer, Set<AbstractRaiderEntity>> raiders = Maps.newHashMap();
   private final Set<UUID> heroes = Sets.newHashSet();
   private long ticksActive;
   private BlockPos center;
   private final ServerWorld world;
   private boolean started;
   private final int id;
   private float totalHealth;
   private int badOmenLevel;
   private boolean active;
   private int groupsSpawned;
   private final ServerBossInfo bossInfo = new ServerBossInfo(RAID, BossInfo.Color.RED, BossInfo.Overlay.NOTCHED_10);
   private int postRaidTicks;
   private int preRaidTicks;
   private final Random random = new Random();
   private final int numGroups;
   private Raid.Status status;
   private int celebrationTicks;
   private Optional<BlockPos> waveSpawnPos = Optional.empty();

   public Raid(int id, ServerWorld worldIn, BlockPos centerIn) {
      this.id = id;
      this.world = worldIn;
      this.active = true;
      this.preRaidTicks = 300;
      this.bossInfo.setPercent(0.0F);
      this.center = centerIn;
      this.numGroups = this.getWaves(worldIn.getDifficulty());
      this.status = Raid.Status.ONGOING;
   }

   public Raid(ServerWorld worldIn, CompoundNBT nbt) {
      this.world = worldIn;
      this.id = nbt.getInt("Id");
      this.started = nbt.getBoolean("Started");
      this.active = nbt.getBoolean("Active");
      this.ticksActive = nbt.getLong("TicksActive");
      this.badOmenLevel = nbt.getInt("BadOmenLevel");
      this.groupsSpawned = nbt.getInt("GroupsSpawned");
      this.preRaidTicks = nbt.getInt("PreRaidTicks");
      this.postRaidTicks = nbt.getInt("PostRaidTicks");
      this.totalHealth = nbt.getFloat("TotalHealth");
      this.center = new BlockPos(nbt.getInt("CX"), nbt.getInt("CY"), nbt.getInt("CZ"));
      this.numGroups = nbt.getInt("NumGroups");
      this.status = Raid.Status.getByName(nbt.getString("Status"));
      this.heroes.clear();
      if (nbt.contains("HeroesOfTheVillage", 11)) {
         ListNBT listnbt = nbt.getList("HeroesOfTheVillage", 11);
         for(int i = 0; i < listnbt.size(); ++i) {
            this.heroes.add(NBTUtil.readUniqueId(listnbt.get(i)));
         }
      }
   }

   public boolean isOver() {
      return this.isVictory() || this.isLoss();
   }

   public boolean isBetweenWaves() {
      return this.func_221297_c() && this.getRaiderCount() == 0 && this.preRaidTicks > 0;
   }

   public boolean func_221297_c() {
      return this.groupsSpawned > 0;
   }

   public boolean isStopped() {
      return this.status == Raid.Status.STOPPED;
   }

   public boolean isVictory() {
      return this.status == Raid.Status.VICTORY;
   }

   public boolean isLoss() {
      return this.status == Raid.Status.LOSS;
   }

   public World getWorld() {
      return this.world;
   }

   public boolean isStarted() {
      return this.started;
   }

   public int getGroupsSpawned() {
      return this.groupsSpawned;
   }

   private Predicate<ServerPlayerEntity> getParticipantsPredicate() {
      return (player) -> {
         BlockPos blockpos = player.getPosition();
         return player.isAlive() && this.world.findRaid(blockpos) == this;
      };
   }

   private void updateBossInfoVisibility() {
      Set<ServerPlayerEntity> currentPlayers = Sets.newHashSet(this.bossInfo.getPlayers());
      List<ServerPlayerEntity> nearbyPlayers = this.world.getPlayers(this.getParticipantsPredicate());

      for(ServerPlayerEntity player : nearbyPlayers) {
         if (!currentPlayers.contains(player)) {
            this.bossInfo.addPlayer(player);
         }
      }

      for(ServerPlayerEntity player : currentPlayers) {
         if (!nearbyPlayers.contains(player)) {
            this.bossInfo.removePlayer(player);
         }
      }
   }

   public int getMaxLevel() {
      return 5;
   }

   public int getBadOmenLevel() {
      return this.badOmenLevel;
   }

   public void increaseLevel(PlayerEntity player) {
      if (player.isPotionActive(Effects.BAD_OMEN)) {
         this.badOmenLevel += player.getActivePotionEffect(Effects.BAD_OMEN).getAmplifier() + 1;
         this.badOmenLevel = MathHelper.clamp(this.badOmenLevel, 0, this.getMaxLevel());
      }
      player.removePotionEffect(Effects.BAD_OMEN);
   }

   public void stop() {
      this.active = false;
      this.bossInfo.removeAllPlayers();
      this.status = Raid.Status.STOPPED;
   }

   public void tick() {
      if (!this.isStopped()) {
         if (this.status == Raid.Status.ONGOING) {
            boolean wasActive = this.active;
            this.active = this.world.isBlockLoaded(this.center);
            if (this.world.getDifficulty() == Difficulty.PEACEFUL) {
               this.stop();
               return;
            }

            if (wasActive != this.active) {
               this.bossInfo.setVisible(this.active);
            }

            if (!this.active) return;

            if (!this.world.isVillage(this.center)) {
               this.moveRaidCenterToNearbyVillageSection();
            }

            if (!this.world.isVillage(this.center)) {
               if (this.groupsSpawned > 0) {
                  this.status = Raid.Status.LOSS;
               } else {
                  this.stop();
               }
            }

            ++this.ticksActive;
            if (this.ticksActive >= 48000L) {
               this.stop();
               return;
            }

            int count = this.getRaiderCount();
            if (count == 0 && this.hasMoreWaves()) {
               if (this.preRaidTicks <= 0) {
                  if (this.preRaidTicks == 0 && this.groupsSpawned > 0) {
                     this.preRaidTicks = 300;
                     this.bossInfo.setName(RAID);
                     return;
                  }
               } else {
                  boolean hasWaveSpawn = this.waveSpawnPos.isPresent();
                  boolean shouldFindPos = !hasWaveSpawn && this.preRaidTicks % 5 == 0;
                  if (hasWaveSpawn && !this.world.getChunkProvider().isChunkLoaded(new ChunkPos(this.waveSpawnPos.get()))) {
                     shouldFindPos = true;
                  }

                  if (shouldFindPos) {
                     int difficultyBuffer = 0;
                     if (this.preRaidTicks < 100) {
                        difficultyBuffer = 1;
                     } else if (this.preRaidTicks < 40) {
                        difficultyBuffer = 2;
                     }
                     this.waveSpawnPos = this.getValidSpawnPos(difficultyBuffer);
                  }

                  if (this.preRaidTicks == 300 || this.preRaidTicks % 20 == 0) {
                     this.updateBossInfoVisibility();
                  }

                  --this.preRaidTicks;
                  this.bossInfo.setPercent(MathHelper.clamp((float)(300 - this.preRaidTicks) / 300.0F, 0.0F, 1.0F));
               }
            }

            if (this.ticksActive % 20L == 0L) {
               this.updateBossInfoVisibility();
               this.updateRaiders();
               if (count > 0) {
                  if (count <= 2) {
                     this.bossInfo.setName(RAID.deepCopy().appendString(" - ").append(new TranslationTextComponent("event.minecraft.raid.raiders_remaining", count)));
                  } else {
                     this.bossInfo.setName(RAID);
                  }
               } else {
                  this.bossInfo.setName(RAID);
               }
            }

            boolean playedSound = false;
            int spawnFailCount = 0;

            while(this.shouldSpawnGroup()) {
               BlockPos blockpos = this.waveSpawnPos.isPresent() ? this.waveSpawnPos.get() : this.findRandomSpawnPos(spawnFailCount, 20);
               if (blockpos != null) {
                  this.started = true;
                  this.spawnNextWave(blockpos);
                  if (!playedSound) {
                     this.playWaveStartSound(blockpos);
                     playedSound = true;
                  }
               } else {
                  ++spawnFailCount;
               }

               if (spawnFailCount > 3) {
                  this.stop();
                  break;
               }
            }

            if (this.isStarted() && !this.hasMoreWaves() && count == 0) {
               if (this.postRaidTicks < 40) {
                  ++this.postRaidTicks;
               } else {
                  this.status = Raid.Status.VICTORY;
                  for(UUID uuid : this.heroes) {
                     Entity entity = this.world.getEntityByUuid(uuid);
                     if (entity instanceof LivingEntity && !entity.isSpectator()) {
                        LivingEntity living = (LivingEntity)entity;
                        living.addPotionEffect(new EffectInstance(Effects.HERO_OF_THE_VILLAGE, 48000, this.badOmenLevel - 1, false, false, true));
                        if (living instanceof ServerPlayerEntity) {
                           ServerPlayerEntity player = (ServerPlayerEntity)living;
                           player.addStat(Stats.RAID_WIN);
                           CriteriaTriggers.HERO_OF_THE_VILLAGE.trigger(player);
                        }
                     }
                  }
               }
            }
            this.markDirty();
         } else if (this.isOver()) {
            ++this.celebrationTicks;
            if (this.celebrationTicks >= 600) {
               this.stop();
               return;
            }

            if (this.celebrationTicks % 20 == 0) {
               this.updateBossInfoVisibility();
               this.bossInfo.setVisible(true);
               if (this.isVictory()) {
                  this.bossInfo.setPercent(0.0F);
                  this.bossInfo.setName(RAID_VICTORY);
               } else {
                  this.bossInfo.setName(RAID_DEFEAT);
               }
            }
         }
      }
   }

   /**
    * TeaVM-optimized: Replaced Stream API with manual loop to find nearest village section center.
    */
   private void moveRaidCenterToNearbyVillageSection() {
      SectionPos currentSection = SectionPos.from(this.center);
      BlockPos bestPos = null;
      double minDistance = Double.MAX_VALUE;

      for(int dx = -2; dx <= 2; ++dx) {
         for(int dy = -2; dy <= 2; ++dy) {
            for(int dz = -2; dz <= 2; ++dz) {
               SectionPos sectionpos = SectionPos.from(currentSection.sectionX() + dx, currentSection.sectionY() + dy, currentSection.sectionZ() + dz);
               if (this.world.isVillage(sectionpos)) {
                  BlockPos sectionCenter = sectionpos.getCenter();
                  double dist = sectionCenter.distanceSq(this.center);
                  if (dist < minDistance) {
                     minDistance = dist;
                     bestPos = sectionCenter;
                  }
               }
            }
         }
      }

      if (bestPos != null) {
         this.setCenter(bestPos);
      }
   }

   private Optional<BlockPos> getValidSpawnPos(int retryCount) {
      for(int i = 0; i < 3; ++i) {
         BlockPos blockpos = this.findRandomSpawnPos(retryCount, 1);
         if (blockpos != null) {
            return Optional.of(blockpos);
         }
      }
      return Optional.empty();
   }

   private boolean hasMoreWaves() {
      if (this.hasBonusWave()) {
         return !this.hasSpawnedBonusWave();
      } else {
         return !this.isFinalWave();
      }
   }

   private boolean isFinalWave() {
      return this.getGroupsSpawned() == this.numGroups;
   }

   private boolean hasBonusWave() {
      return this.badOmenLevel > 1;
   }

   private boolean hasSpawnedBonusWave() {
      return this.getGroupsSpawned() > this.numGroups;
   }

   private boolean shouldSpawnBonusGroup() {
      return this.isFinalWave() && this.getRaiderCount() == 0 && this.hasBonusWave();
   }

   private void updateRaiders() {
      Set<AbstractRaiderEntity> toRemove = Sets.newHashSet();
      for(Set<AbstractRaiderEntity> waveSet : this.raiders.values()) {
         for(AbstractRaiderEntity raider : waveSet) {
            BlockPos pos = raider.getPosition();
            if (!raider.removed && raider.world.getDimensionKey() == this.world.getDimensionKey() && !(this.center.distanceSq(pos) >= 12544.0D)) {
               if (raider.ticksExisted > 600) {
                  if (this.world.getEntityByUuid(raider.getUniqueID()) == null) {
                     toRemove.add(raider);
                  }
                  if (!this.world.isVillage(pos) && raider.getIdleTime() > 2400) {
                     raider.setJoinDelay(raider.getJoinDelay() + 1);
                  }
                  if (raider.getJoinDelay() >= 30) {
                     toRemove.add(raider);
                  }
               }
            } else {
               toRemove.add(raider);
            }
         }
      }

      for(AbstractRaiderEntity raider : toRemove) {
         this.leaveRaid(raider, true);
      }
   }

   private void playWaveStartSound(BlockPos pos) {
      Collection<ServerPlayerEntity> bossPlayers = this.bossInfo.getPlayers();
      for(ServerPlayerEntity player : this.world.getPlayers()) {
         Vector3d playerPos = player.getPositionVec();
         Vector3d soundPos = Vector3d.copyCentered(pos);
         float dist = MathHelper.sqrt((soundPos.x - playerPos.x) * (soundPos.x - playerPos.x) + (soundPos.z - playerPos.z) * (soundPos.z - playerPos.z));
         double dx = playerPos.x + (double)(13.0F / dist) * (soundPos.x - playerPos.x);
         double dz = playerPos.z + (double)(13.0F / dist) * (soundPos.z - playerPos.z);
         if (dist <= 64.0F || bossPlayers.contains(player)) {
            // Note: TeaVM will need an intercepted implementation of sendPacket for the web environment
            player.connection.sendPacket(new SPlaySoundEffectPacket(SoundEvents.EVENT_RAID_HORN, SoundCategory.NEUTRAL, dx, player.getPosY(), dz, 64.0F, 1.0F));
         }
      }
   }

   private void spawnNextWave(BlockPos pos) {
      boolean leaderSpawned = false;
      int waveId = this.groupsSpawned + 1;
      this.totalHealth = 0.0F;
      DifficultyInstance difficulty = this.world.getDifficultyForLocation(pos);
      boolean isBonusWave = this.shouldSpawnBonusGroup();

      for(Raid.WaveMember member : Raid.WaveMember.VALUES) {
         int count = this.getDefaultNumSpawns(member, waveId, isBonusWave) + this.getPotentialBonusSpawns(member, this.random, waveId, difficulty, isBonusWave);
         int ravagerRiderCount = 0;

         for(int i = 0; i < count; ++i) {
            AbstractRaiderEntity raider = member.type.create(this.world);
            if (raider == null) continue;
            
            if (!leaderSpawned && raider.canBeLeader()) {
               raider.setLeader(true);
               this.setLeader(waveId, raider);
               leaderSpawned = true;
            }

            this.joinRaid(waveId, raider, pos, false);
            if (member.type == EntityType.RAVAGER) {
               AbstractRaiderEntity rider = null;
               if (waveId == this.getWaves(Difficulty.NORMAL)) {
                  rider = EntityType.PILLAGER.create(this.world);
               } else if (waveId >= this.getWaves(Difficulty.HARD)) {
                  rider = (ravagerRiderCount == 0) ? EntityType.EVOKER.create(this.world) : EntityType.VINDICATOR.create(this.world);
               }

               ravagerRiderCount++;
               if (rider != null) {
                  this.joinRaid(waveId, rider, pos, false);
                  rider.moveToBlockPosAndAngles(pos, 0.0F, 0.0F);
                  rider.startRiding(raider);
               }
            }
         }
      }

      this.waveSpawnPos = Optional.empty();
      ++this.groupsSpawned;
      this.updateBarPercentage();
      this.markDirty();
   }

   public void joinRaid(int wave, AbstractRaiderEntity raider, BlockPos pos, boolean isNbtLoad) {
      if (this.joinRaid(wave, raider)) {
         raider.setRaid(this);
         raider.setWave(wave);
         raider.setCanJoinRaid(true);
         raider.setJoinDelay(0);
         if (!isNbtLoad && pos != null) {
            raider.setPosition((double)pos.getX() + 0.5D, (double)pos.getY() + 1.0D, (double)pos.getZ() + 0.5D);
            raider.onInitialSpawn(this.world, this.world.getDifficultyForLocation(pos), SpawnReason.EVENT, (ILivingEntityData)null, (CompoundNBT)null);
            raider.applyWaveBonus(wave, false);
            raider.setOnGround(true);
            this.world.func_242417_l(raider);
         }
      }
   }

   public void updateBarPercentage() {
      this.bossInfo.setPercent(MathHelper.clamp(this.getCurrentHealth() / this.totalHealth, 0.0F, 1.0F));
   }

   public float getCurrentHealth() {
      float health = 0.0F;
      for(Set<AbstractRaiderEntity> set : this.raiders.values()) {
         for(AbstractRaiderEntity raider : set) {
            health += raider.getHealth();
         }
      }
      return health;
   }

   private boolean shouldSpawnGroup() {
      return this.preRaidTicks == 0 && (this.groupsSpawned < this.numGroups || this.shouldSpawnBonusGroup()) && this.getRaiderCount() == 0;
   }

   /**
    * TeaVM-optimized: Avoided Stream API for counting raiders.
    */
   public int getRaiderCount() {
      int count = 0;
      for (Set<AbstractRaiderEntity> waveRaiders : this.raiders.values()) {
         count += waveRaiders.size();
      }
      return count;
   }

   public void leaveRaid(AbstractRaiderEntity raider, boolean reduceHealth) {
      Set<AbstractRaiderEntity> set = this.raiders.get(raider.getWave());
      if (set != null) {
         if (set.remove(raider)) {
            if (reduceHealth) {
               this.totalHealth -= raider.getHealth();
            }
            raider.setRaid((Raid)null);
            this.updateBarPercentage();
            this.markDirty();
         }
      }
   }

   private void markDirty() {
      this.world.getRaids().markDirty();
   }

   public static ItemStack createIllagerBanner() {
      ItemStack itemstack = new ItemStack(Items.WHITE_BANNER);
      CompoundNBT compoundnbt = itemstack.getOrCreateChildTag("BlockEntityTag");
      ListNBT patterns = (new BannerPattern.Builder())
            .setPatternWithColor(BannerPattern.RHOMBUS_MIDDLE, DyeColor.CYAN)
            .setPatternWithColor(BannerPattern.STRIPE_BOTTOM, DyeColor.LIGHT_GRAY)
            .setPatternWithColor(BannerPattern.STRIPE_CENTER, DyeColor.GRAY)
            .setPatternWithColor(BannerPattern.BORDER, DyeColor.LIGHT_GRAY)
            .setPatternWithColor(BannerPattern.STRIPE_MIDDLE, DyeColor.BLACK)
            .setPatternWithColor(BannerPattern.HALF_HORIZONTAL, DyeColor.LIGHT_GRAY)
            .setPatternWithColor(BannerPattern.CIRCLE_MIDDLE, DyeColor.LIGHT_GRAY)
            .setPatternWithColor(BannerPattern.BORDER, DyeColor.BLACK).buildNBT();
      compoundnbt.put("Patterns", patterns);
      itemstack.func_242395_a(ItemStack.TooltipDisplayFlags.ADDITIONAL);
      itemstack.setDisplayName((new TranslationTextComponent("block.minecraft.ominous_banner")).mergeStyle(TextFormatting.GOLD));
      return itemstack;
   }

   public AbstractRaiderEntity getLeader(int wave) {
      return this.leaders.get(wave);
   }

   private BlockPos findRandomSpawnPos(int retryCount, int attempts) {
      int rangeMultiplier = retryCount == 0 ? 2 : 2 - retryCount;
      BlockPos.Mutable mutablePos = new BlockPos.Mutable();

      for(int i = 0; i < attempts; ++i) {
         float angle = this.world.rand.nextFloat() * ((float)Math.PI * 2F);
         int x = this.center.getX() + MathHelper.floor(MathHelper.cos(angle) * 32.0F * (float)rangeMultiplier) + this.world.rand.nextInt(5);
         int z = this.center.getZ() + MathHelper.floor(MathHelper.sin(angle) * 32.0F * (float)rangeMultiplier) + this.world.rand.nextInt(5);
         int y = this.world.getHeight(Heightmap.Type.WORLD_SURFACE, x, z);
         mutablePos.setPos(x, y, z);
         
         if ((!this.world.isVillage(mutablePos) || retryCount >= 2) 
               && this.world.isAreaLoaded(mutablePos.getX() - 10, mutablePos.getY() - 10, mutablePos.getZ() - 10, mutablePos.getX() + 10, mutablePos.getY() + 10, mutablePos.getZ() + 10) 
               && this.world.getChunkProvider().isChunkLoaded(new ChunkPos(mutablePos)) 
               && (WorldEntitySpawner.canCreatureTypeSpawnAtLocation(EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, this.world, mutablePos, EntityType.RAVAGER) 
               || (this.world.getBlockState(mutablePos.down()).isIn(Blocks.SNOW) && this.world.getBlockState(mutablePos).isAir()))) {
            return mutablePos;
         }
      }
      return null;
   }

   private boolean joinRaid(int wave, AbstractRaiderEntity raider) {
      return this.joinRaid(wave, raider, true);
   }

   public boolean joinRaid(int wave, AbstractRaiderEntity raider, boolean addHealth) {
      this.raiders.computeIfAbsent(wave, (k) -> Sets.newHashSet());
      Set<AbstractRaiderEntity> waveSet = this.raiders.get(wave);
      
      AbstractRaiderEntity existing = null;
      for(AbstractRaiderEntity r : waveSet) {
         if (r.getUniqueID().equals(raider.getUniqueID())) {
            existing = r;
            break;
         }
      }

      if (existing != null) {
         waveSet.remove(existing);
      }

      waveSet.add(raider);
      if (addHealth) {
         this.totalHealth += raider.getHealth();
      }

      this.updateBarPercentage();
      this.markDirty();
      return true;
   }

   public void setLeader(int wave, AbstractRaiderEntity raider) {
      this.leaders.put(wave, raider);
      raider.setItemStackToSlot(EquipmentSlotType.HEAD, createIllagerBanner());
      raider.setDropChance(EquipmentSlotType.HEAD, 2.0F);
   }

   public void removeLeader(int wave) {
      this.leaders.remove(wave);
   }

   public BlockPos getCenter() {
      return this.center;
   }

   private void setCenter(BlockPos centerIn) {
      this.center = centerIn;
   }

   public int getId() {
      return this.id;
   }

   private int getDefaultNumSpawns(Raid.WaveMember member, int wave, boolean isBonus) {
      return isBonus ? member.waveCounts[this.numGroups] : member.waveCounts[wave];
   }

   private int getPotentialBonusSpawns(Raid.WaveMember member, Random rand, int wave, DifficultyInstance difficultyInstance, boolean isBonus) {
      Difficulty difficulty = difficultyInstance.getDifficulty();
      boolean isEasy = difficulty == Difficulty.EASY;
      boolean isNormal = difficulty == Difficulty.NORMAL;
      int bonus;
      
      switch(member) {
      case WITCH:
         if (isEasy || wave <= 2 || wave == 4) return 0;
         bonus = 1;
         break;
      case PILLAGER:
      case VINDICATOR:
         if (isEasy) bonus = rand.nextInt(2);
         else if (isNormal) bonus = 1;
         else bonus = 2;
         break;
      case RAVAGER:
         bonus = !isEasy && isBonus ? 1 : 0;
         break;
      default:
         return 0;
      }
      return bonus > 0 ? rand.nextInt(bonus + 1) : 0;
   }

   public boolean isActive() {
      return this.active;
   }

   public CompoundNBT write(CompoundNBT nbt) {
      nbt.putInt("Id", this.id);
      nbt.putBoolean("Started", this.started);
      nbt.putBoolean("Active", this.active);
      nbt.putLong("TicksActive", this.ticksActive);
      nbt.putInt("BadOmenLevel", this.badOmenLevel);
      nbt.putInt("GroupsSpawned", this.groupsSpawned);
      nbt.putInt("PreRaidTicks", this.preRaidTicks);
      nbt.putInt("PostRaidTicks", this.postRaidTicks);
      nbt.putFloat("TotalHealth", this.totalHealth);
      nbt.putInt("NumGroups", this.numGroups);
      nbt.putString("Status", this.status.getName());
      nbt.putInt("CX", this.center.getX());
      nbt.putInt("CY", this.center.getY());
      nbt.putInt("CZ", this.center.getZ());
      ListNBT heroesList = new ListNBT();
      for(UUID uuid : this.heroes) {
         heroesList.add(NBTUtil.func_240626_a_(uuid));
      }
      nbt.put("HeroesOfTheVillage", heroesList);
      return nbt;
   }

   public int getWaves(Difficulty difficulty) {
      switch(difficulty) {
      case EASY: return 3;
      case NORMAL: return 5;
      case HARD: return 7;
      default: return 0;
      }
   }

   public float getEnchantOdds() {
      int level = this.getBadOmenLevel();
      if (level == 2) return 0.1F;
      else if (level == 3) return 0.25F;
      else if (level == 4) return 0.5F;
      else return level == 5 ? 0.75F : 0.0F;
   }

   public void addHero(Entity entity) {
      this.heroes.add(entity.getUniqueID());
   }

   static enum Status {
      ONGOING,
      VICTORY,
      LOSS,
      STOPPED;

      private static final Raid.Status[] VALUES = values();

      /**
       * TeaVM-optimized: Avoid redundant calls to values() in a loop.
       */
      private static Raid.Status getByName(String name) {
         for(int i = 0; i < VALUES.length; i++) {
            if (VALUES[i].name().equalsIgnoreCase(name)) {
               return VALUES[i];
            }
         }
         return ONGOING;
      }

      public String getName() {
         // Minimal reliance on complex Locale logic
         return this.name().toLowerCase();
      }
   }

   static enum WaveMember {
      VINDICATOR(EntityType.VINDICATOR, new int[]{0, 0, 2, 0, 1, 4, 2, 5}),
      EVOKER(EntityType.EVOKER, new int[]{0, 0, 0, 0, 0, 1, 1, 2}),
      PILLAGER(EntityType.PILLAGER, new int[]{0, 4, 3, 3, 4, 4, 4, 2}),
      WITCH(EntityType.WITCH, new int[]{0, 0, 0, 0, 3, 0, 0, 1}),
      RAVAGER(EntityType.RAVAGER, new int[]{0, 0, 0, 1, 0, 1, 0, 2});

      public static final Raid.WaveMember[] VALUES = values();
      public final EntityType<? extends AbstractRaiderEntity> type;
      public final int[] waveCounts;

      private WaveMember(EntityType<? extends AbstractRaiderEntity> type, int[] waveCounts) {
         this.type = type;
         this.waveCounts = waveCounts;
      }
   }
}
