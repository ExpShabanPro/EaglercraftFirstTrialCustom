package net.minecraft.world.raid;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ArrayList; // Added for list handling
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.monster.AbstractRaiderEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.network.play.server.SEntityStatusPacket;
import net.minecraft.potion.Effects;
import net.minecraft.stats.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.village.PointOfInterest;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.DimensionType;
import net.minecraft.world.GameRules;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;

public class RaidManager extends WorldSavedData {
   private final Map<Integer, Raid> byId = Maps.newHashMap();
   private final ServerWorld world;
   private int nextAvailableId;
   private int tick;

   public RaidManager(ServerWorld worldIn) {
      super(getStorageId(worldIn.getDimensionType()));
      this.world = worldIn;
      this.nextAvailableId = 1;
      this.markDirty();
   }

   public Raid get(int id) {
      return this.byId.get(id);
   }

   public void tick() {
      ++this.tick;
      Iterator<Raid> iterator = this.byId.values().iterator();

      while(iterator.hasNext()) {
         Raid raid = iterator.next();
         if (this.world.getGameRules().getBoolean(GameRules.DISABLE_RAIDS)) {
            raid.stop();
         }

         if (raid.isStopped()) {
            iterator.remove();
            this.markDirty();
         } else {
            raid.tick();
         }
      }

      if (this.tick % 200 == 0) {
         this.markDirty();
      }

      DebugPacketSender.sendRaids(this.world, this.byId.values());
   }

   public static boolean canJoinRaid(AbstractRaiderEntity raider, Raid raid) {
      if (raider != null && raid != null && raid.getWorld() != null) {
         return raider.isAlive() && raider.canJoinRaid() && raider.getIdleTime() <= 2400 && raider.world.getDimensionType() == raid.getWorld().getDimensionType();
      } else {
         return false;
      }
   }

   public Raid badOmenTick(ServerPlayerEntity player) {
      if (player.isSpectator()) {
         return null;
      } else if (this.world.getGameRules().getBoolean(GameRules.DISABLE_RAIDS)) {
         return null;
      } else {
         DimensionType dimensiontype = player.world.getDimensionType();
         if (!dimensiontype.isHasRaids()) {
            return null;
         } else {
            BlockPos playerPos = player.getPosition();
            
            // TeaVM-optimized: Replaced .collect(Collectors.toList()) with manual collection if necessary, 
            // but we use the iterator directly to avoid stream overhead.
            PointOfInterestManager poiManager = this.world.getPointOfInterestManager();
            Iterator<PointOfInterest> poiIterator = poiManager.func_219146_b(PointOfInterestType.MATCH_ANY, playerPos, 64, PointOfInterestManager.Status.IS_OCCUPIED).iterator();
            
            int count = 0;
            Vector3d averagePos = Vector3d.ZERO;

            while(poiIterator.hasNext()) {
               PointOfInterest poi = poiIterator.next();
               BlockPos poiPos = poi.getPos();
               averagePos = averagePos.add((double)poiPos.getX(), (double)poiPos.getY(), (double)poiPos.getZ());
               ++count;
            }

            BlockPos raidPos;
            if (count > 0) {
               averagePos = averagePos.scale(1.0D / (double)count);
               raidPos = new BlockPos(averagePos);
            } else {
               raidPos = playerPos;
            }

            Raid raid = this.findOrCreateRaid(player.getServerWorld(), raidPos);
            boolean shouldIncreaseLevel = false;
            
            if (!raid.isStarted()) {
               if (!this.byId.containsKey(raid.getId())) {
                  this.byId.put(raid.getId(), raid);
               }
               shouldIncreaseLevel = true;
            } else if (raid.getBadOmenLevel() < raid.getMaxLevel()) {
               shouldIncreaseLevel = true;
            } else {
               player.removePotionEffect(Effects.BAD_OMEN);
               player.connection.sendPacket(new SEntityStatusPacket(player, (byte)43));
            }

            if (shouldIncreaseLevel) {
               raid.increaseLevel(player);
               player.connection.sendPacket(new SEntityStatusPacket(player, (byte)43));
               if (!raid.func_221297_c()) {
                  player.addStat(Stats.RAID_TRIGGER);
                  CriteriaTriggers.VOLUNTARY_EXILE.trigger(player);
               }
            }

            this.markDirty();
            return raid;
         }
      }
   }

   private Raid findOrCreateRaid(ServerWorld worldIn, BlockPos pos) {
      Raid raid = worldIn.findRaid(pos);
      return raid != null ? raid : new Raid(this.incrementNextId(), worldIn, pos);
   }

   public void read(CompoundNBT nbt) {
      this.nextAvailableId = nbt.getInt("NextAvailableID");
      this.tick = nbt.getInt("Tick");
      ListNBT raidList = nbt.getList("Raids", 10);

      for(int i = 0; i < raidList.size(); ++i) {
         CompoundNBT raidNbt = raidList.getCompound(i);
         Raid raid = new Raid(this.world, raidNbt);
         this.byId.put(raid.getId(), raid);
      }
   }

   public CompoundNBT write(CompoundNBT compound) {
      compound.putInt("NextAvailableID", this.nextAvailableId);
      compound.putInt("Tick", this.tick);
      ListNBT raidList = new ListNBT();

      for(Raid raid : this.byId.values()) {
         CompoundNBT raidNbt = new CompoundNBT();
         raid.write(raidNbt);
         raidList.add(raidNbt);
      }

      compound.put("Raids", raidList);
      return compound;
   }

   public static String getStorageId(DimensionType dimension) {
      return "raids" + dimension.getSuffix();
   }

   private int incrementNextId() {
      return ++this.nextAvailableId;
   }

   public Raid findRaid(BlockPos pos, int distance) {
      Raid closestRaid = null;
      double minDistance = (double)distance;

      for(Raid raid : this.byId.values()) {
         double currentDist = raid.getCenter().distanceSq(pos);
         if (raid.isActive() && currentDist < minDistance) {
            closestRaid = raid;
            minDistance = currentDist;
         }
      }

      return closestRaid;
   }
}
