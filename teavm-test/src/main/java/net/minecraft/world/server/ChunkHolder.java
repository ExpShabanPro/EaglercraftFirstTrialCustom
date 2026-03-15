package net.minecraft.world.server;

import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.shorts.ShortArraySet;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.stream.Stream;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SChangeBlockPacket;
import net.minecraft.network.play.server.SMultiBlockChangePacket;
import net.minecraft.network.play.server.SUpdateLightPacket;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.ChunkPrimerWrapper;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.lighting.WorldLightManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ChunkHolder {
   public static final Either<IChunk, ChunkHolder.IChunkLoadingError> MISSING_CHUNK = Either.right(ChunkHolder.IChunkLoadingError.UNLOADED);
   public static final CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> MISSING_CHUNK_FUTURE = CompletableFuture.completedFuture(MISSING_CHUNK);
   public static final Either<Chunk, ChunkHolder.IChunkLoadingError> UNLOADED_CHUNK = Either.right(ChunkHolder.IChunkLoadingError.UNLOADED);
   private static final CompletableFuture<Either<Chunk, ChunkHolder.IChunkLoadingError>> UNLOADED_CHUNK_FUTURE = CompletableFuture.completedFuture(UNLOADED_CHUNK);
   private static final List<ChunkStatus> CHUNK_STATUS_LIST = ChunkStatus.getAll();
   private static final ChunkHolder.LocationType[] LOCATION_TYPES = ChunkHolder.LocationType.values();
   
   // Optimized for TeaVM: Replaced AtomicReferenceArray with a standard array
   private final CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>>[] futures;
   
   private volatile CompletableFuture<Either<Chunk, ChunkHolder.IChunkLoadingError>> borderFuture = UNLOADED_CHUNK_FUTURE;
   private volatile CompletableFuture<Either<Chunk, ChunkHolder.IChunkLoadingError>> tickingFuture = UNLOADED_CHUNK_FUTURE;
   private volatile CompletableFuture<Either<Chunk, ChunkHolder.IChunkLoadingError>> entityTickingFuture = UNLOADED_CHUNK_FUTURE;
   private CompletableFuture<IChunk> field_219315_j = CompletableFuture.completedFuture((IChunk)null);
   private int prevChunkLevel;
   private int chunkLevel;
   private int field_219318_m;
   private final ChunkPos pos;
   private boolean field_244382_p;
   private final ShortSet[] field_244383_q = new ShortSet[16];
   private int blockLightChangeMask;
   private int skyLightChangeMask;
   private final WorldLightManager lightManager;
   private final ChunkHolder.IListener field_219327_v;
   private final ChunkHolder.IPlayerProvider playerProvider;
   private boolean accessible;
   private boolean field_244384_x;

   @SuppressWarnings("unchecked")
   public ChunkHolder(ChunkPos chunkPos, int level, WorldLightManager lightManager, ChunkHolder.IListener listener, ChunkHolder.IPlayerProvider playerProvider) {
      this.pos = chunkPos;
      this.lightManager = lightManager;
      this.field_219327_v = listener;
      this.playerProvider = playerProvider;
      this.prevChunkLevel = ChunkManager.MAX_LOADED_LEVEL + 1;
      this.chunkLevel = this.prevChunkLevel;
      this.field_219318_m = this.prevChunkLevel;
      this.futures = new CompletableFuture[CHUNK_STATUS_LIST.size()];
      this.setChunkLevel(level);
   }

   public CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> func_219301_a(ChunkStatus status) {
      CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> completablefuture = this.futures[status.ordinal()];
      return completablefuture == null ? MISSING_CHUNK_FUTURE : completablefuture;
   }

   public CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> func_225410_b(ChunkStatus status) {
      return getChunkStatusFromLevel(this.chunkLevel).isAtLeast(status) ? this.func_219301_a(status) : MISSING_CHUNK_FUTURE;
   }

   public CompletableFuture<Either<Chunk, ChunkHolder.IChunkLoadingError>> getTickingFuture() {
      return this.tickingFuture;
   }

   public CompletableFuture<Either<Chunk, ChunkHolder.IChunkLoadingError>> getEntityTickingFuture() {
      return this.entityTickingFuture;
   }

   public CompletableFuture<Either<Chunk, ChunkHolder.IChunkLoadingError>> getBorderFuture() {
      return this.borderFuture;
   }

   public Chunk getChunkIfComplete() {
      CompletableFuture<Either<Chunk, ChunkHolder.IChunkLoadingError>> completablefuture = this.getTickingFuture();
      Either<Chunk, ChunkHolder.IChunkLoadingError> either = completablefuture.getNow((Either<Chunk, ChunkHolder.IChunkLoadingError>)null);
      return either == null ? null : either.left().orElse((Chunk)null);
   }

   @OnlyIn(Dist.CLIENT)
   public ChunkStatus func_219285_d() {
      for(int i = CHUNK_STATUS_LIST.size() - 1; i >= 0; --i) {
         ChunkStatus chunkstatus = CHUNK_STATUS_LIST.get(i);
         CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> completablefuture = this.func_219301_a(chunkstatus);
         if (completablefuture.getNow(MISSING_CHUNK).left().isPresent()) {
            return chunkstatus;
         }
      }
      return null;
   }

   public IChunk func_219287_e() {
      for(int i = CHUNK_STATUS_LIST.size() - 1; i >= 0; --i) {
         ChunkStatus chunkstatus = CHUNK_STATUS_LIST.get(i);
         CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> completablefuture = this.func_219301_a(chunkstatus);
         if (!completablefuture.isCompletedExceptionally()) {
            Optional<IChunk> optional = completablefuture.getNow(MISSING_CHUNK).left();
            if (optional.isPresent()) {
               return optional.get();
            }
         }
      }
      return null;
   }

   public CompletableFuture<IChunk> func_219302_f() {
      return this.field_219315_j;
   }

   public void func_244386_a(BlockPos pos) {
      Chunk chunk = this.getChunkIfComplete();
      if (chunk != null) {
         byte sectionY = (byte)SectionPos.toChunk(pos.getY());
         if (this.field_244383_q[sectionY] == null) {
            this.field_244382_p = true;
            this.field_244383_q[sectionY] = new ShortArraySet();
         }
         this.field_244383_q[sectionY].add(SectionPos.toRelativeOffset(pos));
      }
   }

   public void markLightChanged(LightType type, int sectionY) {
      Chunk chunk = this.getChunkIfComplete();
      if (chunk != null) {
         chunk.setModified(true);
         if (type == LightType.SKY) {
            this.skyLightChangeMask |= 1 << sectionY - -1;
         } else {
            this.blockLightChangeMask |= 1 << sectionY - -1;
         }
      }
   }

   public void sendChanges(Chunk chunkIn) {
      if (this.field_244382_p || this.skyLightChangeMask != 0 || this.blockLightChangeMask != 0) {
         World world = chunkIn.getWorld();
         int totalChanges = 0;

         for(int j = 0; j < this.field_244383_q.length; ++j) {
            totalChanges += this.field_244383_q[j] != null ? this.field_244383_q[j].size() : 0;
         }

         this.field_244384_x |= totalChanges >= 64;
         if (this.skyLightChangeMask != 0 || this.blockLightChangeMask != 0) {
            this.sendToTracking(new SUpdateLightPacket(chunkIn.getPos(), this.lightManager, this.skyLightChangeMask, this.blockLightChangeMask, true), !this.field_244384_x);
            this.skyLightChangeMask = 0;
            this.blockLightChangeMask = 0;
         }

         for(int k = 0; k < this.field_244383_q.length; ++k) {
            ShortSet shortset = this.field_244383_q[k];
            if (shortset != null) {
               SectionPos sectionpos = SectionPos.from(chunkIn.getPos(), k);
               if (shortset.size() == 1) {
                  BlockPos blockpos = sectionpos.func_243647_g(shortset.iterator().nextShort());
                  BlockState blockstate = world.getBlockState(blockpos);
                  this.sendToTracking(new SChangeBlockPacket(blockpos, blockstate), false);
                  this.func_244385_a(world, blockpos, blockstate);
               } else {
                  ChunkSection chunksection = chunkIn.getSections()[sectionpos.getY()];
                  SMultiBlockChangePacket packet = new SMultiBlockChangePacket(sectionpos, shortset, chunksection, this.field_244384_x);
                  this.sendToTracking(packet, false);
                  packet.func_244310_a((bp, bs) -> {
                     this.func_244385_a(world, bp, bs);
                  });
               }
               this.field_244383_q[k] = null;
            }
         }
         this.field_244382_p = false;
      }
   }

   private void func_244385_a(World world, BlockPos pos, BlockState state) {
      if (state.getBlock().isTileEntityProvider()) {
         this.sendTileEntity(world, pos);
      }
   }

   private void sendTileEntity(World worldIn, BlockPos posIn) {
      TileEntity tileentity = worldIn.getTileEntity(posIn);
      if (tileentity != null) {
         SUpdateTileEntityPacket packet = tileentity.getUpdatePacket();
         if (packet != null) {
            this.sendToTracking(packet, false);
         }
      }
   }

   private void sendToTracking(IPacket<?> packetIn, boolean boundaryOnly) {
      // Replaced Stream.forEach with standard iterable access for TeaVM
      this.playerProvider.getTrackingPlayers(this.pos, boundaryOnly).forEach(player -> {
         player.connection.sendPacket(packetIn);
      });
   }

   public CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> func_219276_a(ChunkStatus status, ChunkManager chunkManager) {
      int idx = status.ordinal();
      CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> completablefuture = this.futures[idx];
      if (completablefuture != null) {
         Either<IChunk, ChunkHolder.IChunkLoadingError> either = completablefuture.getNow((Either<IChunk, ChunkHolder.IChunkLoadingError>)null);
         if (either == null || either.left().isPresent()) {
            return completablefuture;
         }
      }

      if (getChunkStatusFromLevel(this.chunkLevel).isAtLeast(status)) {
         CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> completablefuture1 = chunkManager.func_219244_a(this, status);
         this.chain(completablefuture1);
         this.futures[idx] = completablefuture1;
         return completablefuture1;
      } else {
         return completablefuture == null ? MISSING_CHUNK_FUTURE : completablefuture;
      }
   }

   private void chain(CompletableFuture<? extends Either<? extends IChunk, ChunkHolder.IChunkLoadingError>> eitherChunk) {
      this.field_219315_j = this.field_219315_j.thenCombine(eitherChunk, (prev, next) -> {
         return next.map(chunk -> chunk, err -> prev);
      });
   }

   @OnlyIn(Dist.CLIENT)
   public ChunkHolder.LocationType func_219300_g() {
      return getLocationTypeFromLevel(this.chunkLevel);
   }

   public ChunkPos getPosition() {
      return this.pos;
   }

   public int getChunkLevel() {
      return this.chunkLevel;
   }

   public int func_219281_j() {
      return this.field_219318_m;
   }

   private void func_219275_d(int level) {
      this.field_219318_m = level;
   }

   public void setChunkLevel(int level) {
      this.chunkLevel = level;
   }

   protected void processUpdates(ChunkManager chunkManagerIn) {
      ChunkStatus chunkstatus = getChunkStatusFromLevel(this.prevChunkLevel);
      ChunkStatus chunkstatus1 = getChunkStatusFromLevel(this.chunkLevel);
      boolean wasLoaded = this.prevChunkLevel <= ChunkManager.MAX_LOADED_LEVEL;
      boolean isLoaded = this.chunkLevel <= ChunkManager.MAX_LOADED_LEVEL;
      ChunkHolder.LocationType locPrev = getLocationTypeFromLevel(this.prevChunkLevel);
      ChunkHolder.LocationType locNext = getLocationTypeFromLevel(this.chunkLevel);
      
      if (wasLoaded) {
         Either<IChunk, ChunkHolder.IChunkLoadingError> unloadedError = Either.right(new ChunkHolder.IChunkLoadingError() {
            public String toString() {
               return "Unloaded ticket level " + ChunkHolder.this.pos.toString();
            }
         });

         for(int i = isLoaded ? chunkstatus1.ordinal() + 1 : 0; i <= chunkstatus.ordinal(); ++i) {
            CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> completablefuture = this.futures[i];
            if (completablefuture != null) {
               completablefuture.complete(unloadedError);
            } else {
               this.futures[i] = CompletableFuture.completedFuture(unloadedError);
            }
         }
      }

      boolean wasBorder = locPrev.isAtLeast(ChunkHolder.LocationType.BORDER);
      boolean isBorder = locNext.isAtLeast(ChunkHolder.LocationType.BORDER);
      this.accessible |= isBorder;
      
      if (!wasBorder && isBorder) {
         this.borderFuture = chunkManagerIn.func_222961_b(this);
         this.chain(this.borderFuture);
      }

      if (wasBorder && !isBorder) {
         CompletableFuture<Either<Chunk, ChunkHolder.IChunkLoadingError>> prevBorder = this.borderFuture;
         this.borderFuture = UNLOADED_CHUNK_FUTURE;
         this.chain(prevBorder.thenApply(res -> res.ifLeft(chunkManagerIn::func_222973_a)));
      }

      boolean wasTicking = locPrev.isAtLeast(ChunkHolder.LocationType.TICKING);
      boolean isTicking = locNext.isAtLeast(ChunkHolder.LocationType.TICKING);
      if (!wasTicking && isTicking) {
         this.tickingFuture = chunkManagerIn.func_219179_a(this);
         this.chain(this.tickingFuture);
      }

      if (wasTicking && !isTicking) {
         this.tickingFuture.complete(UNLOADED_CHUNK);
         this.tickingFuture = UNLOADED_CHUNK_FUTURE;
      }

      boolean wasEntityTicking = locPrev.isAtLeast(ChunkHolder.LocationType.ENTITY_TICKING);
      boolean isEntityTicking = locNext.isAtLeast(ChunkHolder.LocationType.ENTITY_TICKING);
      if (!wasEntityTicking && isEntityTicking) {
         if (this.entityTickingFuture != UNLOADED_CHUNK_FUTURE) {
            throw (IllegalStateException)Util.pauseDevMode(new IllegalStateException());
         }
         this.entityTickingFuture = chunkManagerIn.func_219188_b(this.pos);
         this.chain(this.entityTickingFuture);
      }

      if (wasEntityTicking && !isEntityTicking) {
         this.entityTickingFuture.complete(UNLOADED_CHUNK);
         this.entityTickingFuture = UNLOADED_CHUNK_FUTURE;
      }

      this.field_219327_v.func_219066_a(this.pos, this::func_219281_j, this.chunkLevel, this::func_219275_d);
      this.prevChunkLevel = this.chunkLevel;
   }

   public static ChunkStatus getChunkStatusFromLevel(int level) {
      return level < 33 ? ChunkStatus.FULL : ChunkStatus.getStatus(level - 33);
   }

   public static ChunkHolder.LocationType getLocationTypeFromLevel(int level) {
      return LOCATION_TYPES[MathHelper.clamp(33 - level + 1, 0, LOCATION_TYPES.length - 1)];
   }

   public boolean isAccessible() {
      return this.accessible;
   }

   public void updateAccessible() {
      this.accessible = getLocationTypeFromLevel(this.chunkLevel).isAtLeast(ChunkHolder.LocationType.BORDER);
   }

   public void func_219294_a(ChunkPrimerWrapper wrapper) {
      for(int i = 0; i < this.futures.length; ++i) {
         CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> completablefuture = this.futures[i];
         if (completablefuture != null) {
            Optional<IChunk> optional = completablefuture.getNow(MISSING_CHUNK).left();
            if (optional.isPresent() && optional.get() instanceof ChunkPrimer) {
               this.futures[i] = CompletableFuture.completedFuture(Either.left(wrapper));
            }
         }
      }
      this.chain(CompletableFuture.completedFuture(Either.left(wrapper.getChunk())));
   }

   public interface IChunkLoadingError {
      ChunkHolder.IChunkLoadingError UNLOADED = new ChunkHolder.IChunkLoadingError() {
         public String toString() {
            return "UNLOADED";
         }
      };
   }

   public interface IListener {
      void func_219066_a(ChunkPos pos, IntSupplier p_219066_2_, int p_219066_3_, IntConsumer p_219066_4_);
   }

   public interface IPlayerProvider {
      Stream<ServerPlayerEntity> getTrackingPlayers(ChunkPos pos, boolean boundaryOnly);
   }

   public static enum LocationType {
      INACCESSIBLE,
      BORDER,
      TICKING,
      ENTITY_TICKING;

      public boolean isAtLeast(ChunkHolder.LocationType type) {
         return this.ordinal() >= type.ordinal();
      }
   }
}
