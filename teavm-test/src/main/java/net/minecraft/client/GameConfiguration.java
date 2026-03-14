package net.minecraft.client;

import com.mojang.authlib.properties.PropertyMap;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.ScreenSize;
import net.minecraft.client.resources.FolderResourceIndex;
import net.minecraft.client.resources.ResourceIndex;
import net.minecraft.util.Session;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

// NOTE: We removed java.io.File and java.net.Proxy because they don't work in browsers!

@OnlyIn(Dist.CLIENT)
public class GameConfiguration {
   public final GameConfiguration.UserInformation userInfo;
   public final ScreenSize displayInfo;
   public final GameConfiguration.FolderInformation folderInfo;
   public final GameConfiguration.GameInformation gameInfo;
   public final GameConfiguration.ServerInformation serverInfo;

   public GameConfiguration(GameConfiguration.UserInformation userInfo, ScreenSize screenSize, GameConfiguration.FolderInformation folderInfo, GameConfiguration.GameInformation gameInfo, GameConfiguration.ServerInformation serverInfo) {
      this.userInfo = userInfo;
      this.displayInfo = screenSize;
      this.folderInfo = folderInfo;
      this.gameInfo = gameInfo;
      this.serverInfo = serverInfo;
   }

   @OnlyIn(Dist.CLIENT)
   public static class FolderInformation {
      // Changed from File to String for Web compatibility
      public final String gameDir;
      public final String resourcePacksDir;
      public final String assetsDir;
      @Nullable
      public final String assetIndex;

      public FolderInformation(String mcDataDirIn, String resourcePacksDirIn, String assetsDirIn, @Nullable String assetIndexIn) {
         this.gameDir = mcDataDirIn;
         this.resourcePacksDir = resourcePacksDirIn;
         this.assetsDir = assetsDirIn;
         this.assetIndex = assetIndexIn;
      }

      // We still return a ResourceIndex, but it will be a Web-compatible version 
      // once we fix ResourceIndex.java later.
      public ResourceIndex getAssetsIndex() {
         // In a browser, these 'new' calls will eventually use a Virtual File System
         return (ResourceIndex)(this.assetIndex == null ? new FolderResourceIndex(this.assetsDir) : new ResourceIndex(this.assetsDir, this.assetIndex));
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class GameInformation {
      public final boolean isDemo;
      public final String version;
      public final String versionType;
      public final boolean disableMultiplayer;
      public final boolean disableChat;

      public GameInformation(boolean isDemo, String version, String versionType, boolean disableMultiplayer, boolean disableChat) {
         this.isDemo = isDemo;
         this.version = version;
         this.versionType = versionType;
         this.disableMultiplayer = disableMultiplayer;
         this.disableChat = disableChat;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class ServerInformation {
      @Nullable
      public final String serverName;
      public final int serverPort;

      public ServerInformation(@Nullable String serverNameIn, int serverPortIn) {
         this.serverName = serverNameIn;
         this.serverPort = serverPortIn;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class UserInformation {
      public final Session session;
      public final PropertyMap userProperties;
      public final PropertyMap profileProperties;
      
      // Changed from java.net.Proxy to Object to avoid import crashes.
      // Web browsers handle proxies automatically!
      public final Object proxy; 

      public UserInformation(Session sessionIn, PropertyMap userPropertiesIn, PropertyMap profilePropertiesIn, Object proxyIn) {
         this.session = sessionIn;
         this.userProperties = userPropertiesIn;
         this.profileProperties = profilePropertiesIn;
         this.proxy = proxyIn;
      }
   }
}
