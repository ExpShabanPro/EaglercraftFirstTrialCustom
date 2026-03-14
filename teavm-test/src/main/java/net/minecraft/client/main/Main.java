package net.minecraft.client.main;

import net.minecraft.client.GameConfiguration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ScreenSize;
import net.minecraft.crash.CrashReport;
import net.minecraft.util.Session;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Bootstrap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// --- TEAVM IMPORTS ---
import org.teavm.jso.JSBody;
import org.teavm.jso.browser.Window;

public class Main {
    private static final Logger LOGGER = LogManager.getLogger();
    private static Minecraft minecraft;

    // --- TEAVM JAVASCRIPT BRIDGES ---
    
    // This bridge reads the username from the HTML file's eaglercraftXOpts instead of command line args!
    @JSBody(script = "return window.eaglercraftXOpts && window.eaglercraftXOpts.username ? window.eaglercraftXOpts.username : 'EaglerPlayer';")
    public static native String getJSUsername();

    // This bridge gets the width of the browser window
    @JSBody(script = "return window.innerWidth;")
    public static native int getBrowserWidth();

    // This bridge gets the height of the browser window
    @JSBody(script = "return window.innerHeight;")
    public static native int getBrowserHeight();

    // This asks the browser to call the game loop on the next animation frame (usually 60fps)
    @JSBody(params = { "callback" }, script = "window.requestAnimationFrame(callback);")
    public static native void requestAnimationFrame(Runnable callback);


    // --- MAIN BOOT SEQUENCE ---
    public static void main(String[] args) {
        LOGGER.info("Starting Eaglercraft Web Port Initialization...");

        // 1. We no longer use JOptSimple. We grab basic settings from the browser directly.
        int width = getBrowserWidth();
        int height = getBrowserHeight();
        String username = getJSUsername();

        CrashReport.crash(); // Required Minecraft setup
        Bootstrap.register(); // Required Minecraft setup
        Bootstrap.checkTranslations();
        Util.func_240994_l_(); // Some obscure 1.16 initialization routine

        // 2. Set up dummy configurations since we don't have a real file system or Microsoft authentication.
        // NOTE: We pass "null" to the File directories because we will rewrite GameConfiguration later to use IndexedDB.
        Session session = new Session(username, "00000000-0000-0000-0000-000000000000", "dummy_token", "legacy");
        GameConfiguration gameconfiguration = new GameConfiguration(
                new GameConfiguration.UserInformation(session, null, null, null), 
                new ScreenSize(width, height, null, null, false), 
                new GameConfiguration.FolderInformation(null, null, null, null), 
                new GameConfiguration.GameInformation(false, "1.16.5", "release", false, false), 
                new GameConfiguration.ServerInformation(null, 25565)
        );

        // 3. Initialize the main game object
        try {
            LOGGER.info("Initializing Minecraft instance...");
            // Notice: We removed all the Threading/RenderSystem.initRenderThread() stuff.
            // TeaVM runs entirely on a single thread in the browser.
            minecraft = new Minecraft(gameconfiguration);
        } catch (Throwable throwable) {
            LOGGER.error("Failed to initialize game", throwable);
            return;
        }

        // 4. Start the browser game loop!
        // Desktop uses a blocking while() loop. We use an event-driven loop to prevent the tab from freezing.
        runGameLoop();
    }

    /**
     * This replaces the desktop "while(true)" loop. 
     * It runs exactly one frame of the game, and then tells the browser to run it again on the next frame.
     */
    private static void runGameLoop() {
        if (minecraft.isRunning()) {
            try {
                // Run one single tick/frame of the game
                minecraft.runTick(); 
                
                // Tell the browser to call runGameLoop() again in 16 milliseconds (60 FPS)
                requestAnimationFrame(new Runnable() {
                    @Override
                    public void run() {
                        runGameLoop();
                    }
                });
            } catch (Throwable throwable) {
                LOGGER.error("Unhandled game exception during web loop", throwable);
                minecraft.shutdown();
            }
        } else {
            // If the game is no longer running (user quit), shut down safely.
            minecraft.shutdown();
        }
    }
}