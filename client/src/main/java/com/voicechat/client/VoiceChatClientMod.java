package com.voicechat.client;

import com.voicechat.client.audio.input.MicrophoneCapture;
import com.voicechat.client.audio.output.AudioPlayback;
import com.voicechat.client.config.ClientConfigManager;
import com.voicechat.client.network.VoiceClient;
import com.voicechat.common.network.packet.ActivationMode;

public class VoiceChatClientMod {

    private static VoiceChatClientMod instance;
    private ClientConfigManager configManager;
    private MicrophoneCapture microphoneCapture;
    private AudioPlayback audioPlayback;
    private VoiceClient voiceClient;
    private boolean talking = false;

    public void onInitialize() {
        instance = this;

        configManager = new ClientConfigManager();
        configManager.load();

        microphoneCapture = new MicrophoneCapture(this);
        audioPlayback = new AudioPlayback(this);
        voiceClient = new VoiceClient(this);
    }

    public void onConnect(String serverAddress) {
        voiceClient.connect(serverAddress);
        microphoneCapture.start();
        audioPlayback.start();
    }

    public void onDisconnect() {
        microphoneCapture.stop();
        audioPlayback.stop();
        voiceClient.disconnect();
    }

    public void onKeyPress(int keyCode) {
        if (keyCode == configManager.getConfig().getPushToTalkKey()) {
            if (configManager.getConfig().getActivationMode() == ActivationMode.PUSH_TO_TALK) {
                talking = true;
            }
        }
    }

    public void onKeyRelease(int keyCode) {
        if (keyCode == configManager.getConfig().getPushToTalkKey()) {
            if (configManager.getConfig().getActivationMode() == ActivationMode.PUSH_TO_TALK) {
                talking = false;
            }
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
}
