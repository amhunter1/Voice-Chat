package com.voicechat.client;

import com.voicechat.client.audio.input.MicrophoneCapture;
import com.voicechat.client.audio.output.AudioPlayback;
import com.voicechat.client.config.ClientConfigManager;
import com.voicechat.client.network.VoiceClient;
import com.voicechat.common.network.packet.ActivationMode;

import java.util.logging.Level;
import java.util.logging.Logger;

public class VoiceChatClientMod {

    private static final Logger LOGGER = Logger.getLogger(VoiceChatClientMod.class.getName());
    private static VoiceChatClientMod instance;

    private ClientConfigManager configManager;
    private MicrophoneCapture microphoneCapture;
    private AudioPlayback audioPlayback;
    private VoiceClient voiceClient;
    private volatile boolean talking = false;
    private volatile boolean initialized = false;
    private volatile boolean connected = false;

    public void onInitialize() {
        if (initialized) {
            LOGGER.warning("VoiceChat client already initialized");
            return;
        }

        try {
            instance = this;

            configManager = new ClientConfigManager();
            configManager.load();

            microphoneCapture = new MicrophoneCapture(this);
            audioPlayback = new AudioPlayback(this);
            voiceClient = new VoiceClient(this);

            initialized = true;
            LOGGER.info("VoiceChat client initialized successfully");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize VoiceChat client", e);
            cleanup();
        }
    }

    public void onConnect(String serverAddress) {
        if (!initialized) {
            LOGGER.severe("Cannot connect: client not initialized");
            return;
        }

        if (connected) {
            LOGGER.warning("Already connected to voice server");
            return;
        }

        try {
            voiceClient.connect(serverAddress);
            microphoneCapture.start();
            audioPlayback.start();

            connected = true;
            LOGGER.info("Connected to voice server: " + serverAddress);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to connect to voice server", e);
            onDisconnect();
        }
    }

    public void onDisconnect() {
        if (!connected) {
            return;
        }

        try {
            if (microphoneCapture != null) {
                microphoneCapture.stop();
            }
            if (audioPlayback != null) {
                audioPlayback.stop();
            }
            if (voiceClient != null) {
                voiceClient.disconnect();
            }

            connected = false;
            LOGGER.info("Disconnected from voice server");

        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error during disconnect", e);
        }
    }

    public void onShutdown() {
        LOGGER.info("Shutting down VoiceChat client");
        onDisconnect();
        cleanup();
    }

    private void cleanup() {
        try {
            if (configManager != null) {
                configManager.save();
            }

            microphoneCapture = null;
            audioPlayback = null;
            voiceClient = null;
            configManager = null;

            initialized = false;
            instance = null;

        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error during cleanup", e);
        }
    }

    public void onKeyPress(int keyCode) {
        if (!initialized || !connected) {
            return;
        }

        try {
            // TODO: Get key code from config properly
            if (configManager.getConfig().getActivationMode() == ActivationMode.PUSH_TO_TALK) {
                talking = true;
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error handling key press", e);
        }
    }

    public void onKeyRelease(int keyCode) {
        if (!initialized || !connected) {
            return;
        }

        try {
            // TODO: Get key code from config properly
            if (configManager.getConfig().getActivationMode() == ActivationMode.PUSH_TO_TALK) {
                talking = false;
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error handling key release", e);
        }
    }

    public static VoiceChatClientMod getInstance() {
        return instance;
    }

    public ClientConfigManager getConfigManager() {
        return configManager;
    }

    public MicrophoneCapture getMicrophoneCapture() {
        return microphoneCapture;
    }

    public AudioPlayback getAudioPlayback() {
        return audioPlayback;
    }

    public VoiceClient getVoiceClient() {
        return voiceClient;
    }

    public boolean isTalking() {
        return talking;
    }

    public void setTalking(boolean talking) {
        this.talking = talking;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public boolean isConnected() {
        return connected;
    }
}
