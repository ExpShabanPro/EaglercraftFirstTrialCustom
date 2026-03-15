package net.minecraft.world.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SUpdateBossInfoPacket;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.BossInfo;

public class ServerBossInfo extends BossInfo {
   // TeaVM Optimization: Replaced Guava's Sets.newHashSet() with standard HashSet
   private final Set<ServerPlayerEntity> players = new HashSet<>();
   private final Set<ServerPlayerEntity> readOnlyPlayers = Collections.unmodifiableSet(this.players);
   private boolean visible = true;

   public ServerBossInfo(ITextComponent nameIn, BossInfo.Color colorIn, BossInfo.Overlay overlayIn) {
      super(MathHelper.getRandomUUID(), nameIn, colorIn, overlayIn);
   }

   public void setPercent(float percentIn) {
      if (percentIn != this.percent) {
         super.setPercent(percentIn);
         this.sendUpdate(SUpdateBossInfoPacket.Operation.UPDATE_PCT);
      }
   }

   public void setColor(BossInfo.Color colorIn) {
      if (colorIn != this.color) {
         super.setColor(colorIn);
         this.sendUpdate(SUpdateBossInfoPacket.Operation.UPDATE_STYLE);
      }
   }

   public void setOverlay(BossInfo.Overlay overlayIn) {
      if (overlayIn != this.overlay) {
         super.setOverlay(overlayIn);
         this.sendUpdate(SUpdateBossInfoPacket.Operation.UPDATE_STYLE);
      }
   }

   public BossInfo setDarkenSky(boolean darkenSkyIn) {
      if (darkenSkyIn != this.darkenSky) {
         super.setDarkenSky(darkenSkyIn);
         this.sendUpdate(SUpdateBossInfoPacket.Operation.UPDATE_PROPERTIES);
      }
      return this;
   }

   public BossInfo setPlayEndBossMusic(boolean playEndBossMusicIn) {
      if (playEndBossMusicIn != this.playEndBossMusic) {
         super.setPlayEndBossMusic(playEndBossMusicIn);
         this.sendUpdate(SUpdateBossInfoPacket.Operation.UPDATE_PROPERTIES);
      }
      return this;
   }

   public BossInfo setCreateFog(boolean createFogIn) {
      if (createFogIn != this.createFog) {
         super.setCreateFog(createFogIn);
         this.sendUpdate(SUpdateBossInfoPacket.Operation.UPDATE_PROPERTIES);
      }
      return this;
   }

   public void setName(ITextComponent nameIn) {
      // TeaVM Optimization: Replaced Guava's Objects.equal with java.util.Objects.equals
      if (!Objects.equals(nameIn, this.name)) {
         super.setName(nameIn);
         this.sendUpdate(SUpdateBossInfoPacket.Operation.UPDATE_NAME);
      }
   }

   private void sendUpdate(SUpdateBossInfoPacket.Operation operationIn) {
      if (this.visible) {
         SUpdateBossInfoPacket supdatebossinfopacket = new SUpdateBossInfoPacket(operationIn, this);

         for(ServerPlayerEntity serverplayerentity : this.players) {
            // Note for TeaVM: networking layer intercept should handle this packet sending
            serverplayerentity.connection.sendPacket(supdatebossinfopacket);
         }
      }
   }

   public void addPlayer(ServerPlayerEntity player) {
      if (this.players.add(player) && this.visible) {
         player.connection.sendPacket(new SUpdateBossInfoPacket(SUpdateBossInfoPacket.Operation.ADD, this));
      }
   }

   public void removePlayer(ServerPlayerEntity player) {
      if (this.players.remove(player) && this.visible) {
         player.connection.sendPacket(new SUpdateBossInfoPacket(SUpdateBossInfoPacket.Operation.REMOVE, this));
      }
   }

   public void removeAllPlayers() {
      if (!this.players.isEmpty()) {
         // TeaVM Optimization: Replaced Guava's Lists.newArrayList with standard ArrayList
         for(ServerPlayerEntity serverplayerentity : new ArrayList<>(this.players)) {
            this.removePlayer(serverplayerentity);
         }
      }
   }

   public boolean isVisible() {
      return this.visible;
   }

   public void setVisible(boolean visibleIn) {
      if (visibleIn != this.visible) {
         this.visible = visibleIn;

         for(ServerPlayerEntity serverplayerentity : this.players) {
            serverplayerentity.connection.sendPacket(new SUpdateBossInfoPacket(visibleIn ? SUpdateBossInfoPacket.Operation.ADD : SUpdateBossInfoPacket.Operation.REMOVE, this));
         }
      }
   }

   public Collection<ServerPlayerEntity> getPlayers() {
      return this.readOnlyPlayers;
   }
}
