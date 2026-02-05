package com.voicechat.server;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.voicechat.server.manager.PlayerVoiceManager;
import com.voicechat.server.network.VoicePacketRouter;
import com.voicechat.server.network.VoiceServer;
import com.voicechat.server.config.ConfigManager;
import com.voicechat.common.config.VoiceChatConfig;
import com.voicechat.server.util.LogUtils;

import javax.annotation.Nonnull;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VoiceChatPlugin extends JavaPlugin {

    private static VoiceChatPlugin instance;
    private ConfigManager configManager;
    private PlayerVoiceManager playerManager;
    private VoicePacketRouter packetRouter;
    private VoiceServer voiceServer;
    private volatile boolean initialized = false;
    private volatile boolean running = false;

    public VoiceChatPlugin(@Nonnull JavaPluginInit init) {
        super(init);
        instance = this;
    }

    public static VoiceChatPlugin getInstance() {
        return instance;
    }

    @Override
    protected void setup() {
        if (initialized) {
            LogUtils.warn("VoiceChat plugin already initialized");
            return;
        }

        try {
            LogUtils.init(Logger.getLogger("VoiceChat"));
            LogUtils.info("Loading VoiceChat plugin...");

            // Load configuration
            configManager = new ConfigManager(this);
            configManager.load();

            // Initialize managers
            playerManager = new PlayerVoiceManager();
            packetRouter = new VoicePacketRouter(this);
            voiceServer = new VoiceServer(this);

            // Register events and commands
            registerEvents();
            registerCommands();

            initialized = true;
            LogUtils.info("VoiceChat plugin loaded successfully");

        } catch (Exception e) {
            LogUtils.error("Failed to setup VoiceChat plugin: " + e.getMessage());
            e.printStackTrace();
            cleanup();
        }
    }

    @Override
    protected void start() {
        if (!initialized) {
            LogUtils.error("Cannot start: plugin not initialized");
            return;
        }

        if (running) {
            LogUtils.warn("VoiceChat plugin already running");
            return;
        }

        try {
            voiceServer.start();
            running = true;
            LogUtils.info("VoiceChat plugin enabled!");

        } catch (Exception e) {
            LogUtils.error("Failed to start VoiceChat plugin: " + e.getMessage());
            e.printStackTrace();
            shutdown();
        }
    }

    @Override
    public void shutdown() {
        if (!running && !initialized) {
            return;
        }

        try {
            LogUtils.info("Shutting down VoiceChat plugin...");

            // Stop voice server
            if (voiceServer != null && voiceServer.isRunning()) {
                voiceServer.stop();
            }

            // Save configuration
            if (configManager != null) {
                configManager.save();
            }

            // Clear player data
            if (playerManager != null) {
                playerManager.clear();
            }

            running = false;
            LogUtils.info("VoiceChat plugin disabled!");

        } catch (Exception e) {
            LogUtils.error("Error during shutdown: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cleanup();
        }
    }

    private void cleanup() {
        try {
            voiceServer = null;
            packetRouter = null;
            playerManager = null;
            configManager = null;
            initialized = false;
            instance = null;

        } catch (Exception e) {
            LogUtils.error("Error during cleanup: " + e.getMessage());
        }
    }

    private void registerEvents() {
        LogUtils.info("Registering event listeners...");
        // Event listeners will be registered here when Hytale API is available
        // For now, we'll use the PlayerConnectionListener directly
    }

    private void registerCommands() {
        LogUtils.info("Registering commands...");
        try {
            this.getCommandRegistry().registerCommand(
                new com.voicechat.server.command.VoiceChatCommand(this)
            );
            LogUtils.info("Commands registered successfully");
        } catch (Exception e) {
            LogUtils.error("Failed to register commands: " + e.getMessage());
        }
    }

    public VoiceChatConfig getConfig() {
        if (configManager == null) {
            throw new IllegalStateException("ConfigManager not initialized");
        }
        return configManager.getConfig();
    }

    public PlayerVoiceManager getPlayerManager() {
        if (playerManager == null) {
            throw new IllegalStateException("PlayerVoiceManager not initialized");
        }
        return playerManager;
    }

    public VoicePacketRouter getPacketRouter() {
        if (packetRouter == null) {
            throw new IllegalStateException("VoicePacketRouter not initialized");
        }
        return packetRouter;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public boolean isRunning() {
        return running;
    }
}
