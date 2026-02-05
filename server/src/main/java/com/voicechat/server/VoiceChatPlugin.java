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
import java.util.logging.Logger;

public class VoiceChatPlugin extends JavaPlugin {

    private static VoiceChatPlugin instance;
    private ConfigManager configManager;
    private PlayerVoiceManager playerManager;
    private VoicePacketRouter packetRouter;
    private VoiceServer voiceServer;

    public VoiceChatPlugin(@Nonnull JavaPluginInit init) {
        super(init);
        instance = this;
    }

    public static VoiceChatPlugin getInstance() {
        return instance;
    }

    @Override
    protected void setup() {
        LogUtils.init(Logger.getLogger("VoiceChat"));
        LogUtils.info("Loading...");

        configManager = new ConfigManager(this);
        configManager.load();

        playerManager = new PlayerVoiceManager();
        packetRouter = new VoicePacketRouter(this);
        voiceServer = new VoiceServer(this);

        registerEvents();
        registerCommands();
    }

    @Override
    protected void start() {
        voiceServer.start();
        LogUtils.info("Enabled!");
    }

    @Override
    public void shutdown() {
        if (voiceServer != null)
            voiceServer.stop();
        LogUtils.info("Disabled!");
    }

    private void registerEvents() {
        // TODO: Register player join/quit listeners
    }

    private void registerCommands() {
        // TODO: Register voicechat command
    }

    public VoiceChatConfig getConfig() {
        return configManager.getConfig();
    }

    public PlayerVoiceManager getPlayerManager() {
        return playerManager;
    }

    public VoicePacketRouter getPacketRouter() {
        return packetRouter;
    }
}
