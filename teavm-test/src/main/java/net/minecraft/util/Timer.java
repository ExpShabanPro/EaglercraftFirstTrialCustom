package net.minecraft.util;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Timer {
   public float renderPartialTicks;
   public float elapsedPartialTicks;
   private long lastSyncSysClock;
   private final float tickLength;

   public Timer(float ticks, long lastSyncSysClock) {
      // tickLength is usually 50.0ms for 20 TPS
      this.tickLength = 1000.0F / ticks;
      this.lastSyncSysClock = lastSyncSysClock;
   }

   /**
    * Updated for Web: This method calculates how many logical ticks have passed 
    * since the last frame. In the browser, this helps keep movement smooth
    * even if the framerate (FPS) varies.
    */
   public int getPartialTicks(long gameTime) {
      // Calculate the time difference using the browser's millisecond clock
      this.elapsedPartialTicks = (float)(gameTime - this.lastSyncSysClock) / this.tickLength;
      this.lastSyncSysClock = gameTime;
      this.renderPartialTicks += this.elapsedPartialTicks;
      
      int i = (int)this.renderPartialTicks;
      this.renderPartialTicks -= (float)i;
      
      return i;
   }
}
