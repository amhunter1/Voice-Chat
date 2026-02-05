package com.voicechat.client.config;

import com.voicechat.common.network.packet.ActivationMode;

public class ClientConfig {

    private AudioSettings audio = new AudioSettings();
    private ActivationSettings activation = new ActivationSettings();
    private UISettings ui = new UISettings();
    private NetworkSettings network = new NetworkSettings();
    private AdvancedSettings advanced = new AdvancedSettings();

    public AudioSettings getAudio() {
        return audio;
    }

    public ActivationSettings getActivation() {
        return activation;
    }

    public UISettings getUi() {
        return ui;
    }

    public NetworkSettings getNetwork() {
        return network;
    }

    public AdvancedSettings getAdvanced() {
        return advanced;
    }

    // Legacy getters for backward compatibility
    public float getMicrophoneVolume() {
        return audio.inputVolume;
    }

    public void setMicrophoneVolume(float vol) {
        audio.inputVolume = vol;
    }

    public float getOutputVolume() {
        return audio.outputVolume;
    }

    public void setOutputVolume(float vol) {
        audio.outputVolume = vol;
    }

    public ActivationMode getActivationMode() {
        return activation.mode;
    }

    public void setActivationMode(ActivationMode mode) {
        activation.mode = mode;
    }

    public float getVoiceActivationThreshold() {
        return audio.voiceActivationThreshold;
    }

    public void setVoiceActivationThreshold(float threshold) {
        audio.voiceActivationThreshold = threshold;
    }

    public static class AudioSettings {
        private float inputVolume = 1.0f;
        private float outputVolume = 1.0f;
        private float voiceActivationThreshold = 0.05f;
        private String microphoneDevice = "default";
        private String speakerDevice = "default";

        public float getInputVolume() {
            return inputVolume;
        }

        public void setInputVolume(float inputVolume) {
            this.inputVolume = inputVolume;
        }

        public float getOutputVolume() {
            return outputVolume;
        }

        public void setOutputVolume(float outputVolume) {
            this.outputVolume = outputVolume;
        }

        public float getVoiceActivationThreshold() {
            return voiceActivationThreshold;
        }

        public void setVoiceActivationThreshold(float voiceActivationThreshold) {
            this.voiceActivationThreshold = voiceActivationThreshold;
        }

        public String getMicrophoneDevice() {
            return microphoneDevice;
        }

        public void setMicrophoneDevice(String microphoneDevice) {
            this.microphoneDevice = microphoneDevice;
        }

        public String getSpeakerDevice() {
            return speakerDevice;
        }

        public void setSpeakerDevice(String speakerDevice) {
            this.speakerDevice = speakerDevice;
        }
    }

    public static class ActivationSettings {
        private ActivationMode mode = ActivationMode.VOICE_ACTIVATION;
        private String pushToTalkKey = "V";

        public ActivationMode getMode() {
            return mode;
        }

        public void setMode(ActivationMode mode) {
            this.mode = mode;
        }

        public String getPushToTalkKey() {
            return pushToTalkKey;
        }

        public void setPushToTalkKey(String pushToTalkKey) {
            this.pushToTalkKey = pushToTalkKey;
        }
    }

    public static class UISettings {
        private boolean showVoiceIndicator = true;
        private boolean showPlayerNames = true;
        private String hudPosition = "TOP_LEFT";
        private float hudScale = 1.0f;

        public boolean isShowVoiceIndicator() {
            return showVoiceIndicator;
        }

        public void setShowVoiceIndicator(boolean showVoiceIndicator) {
            this.showVoiceIndicator = showVoiceIndicator;
        }

        public boolean isShowPlayerNames() {
            return showPlayerNames;
        }

        public void setShowPlayerNames(boolean showPlayerNames) {
            this.showPlayerNames = showPlayerNames;
        }

        public String getHudPosition() {
            return hudPosition;
        }

        public void setHudPosition(String hudPosition) {
            this.hudPosition = hudPosition;
        }

        public float getHudScale() {
            return hudScale;
        }

        public void setHudScale(float hudScale) {
            this.hudScale = hudScale;
        }
    }

    public static class NetworkSettings {
        private String serverAddress = "localhost";
        private int serverPort = 24454;
        private boolean autoConnect = true;

        public String getServerAddress() {
            return serverAddress;
        }

        public void setServerAddress(String serverAddress) {
            this.serverAddress = serverAddress;
        }

        public int getServerPort() {
            return serverPort;
        }

        public void setServerPort(int serverPort) {
            this.serverPort = serverPort;
        }

        public boolean isAutoConnect() {
            return autoConnect;
        }

        public void setAutoConnect(boolean autoConnect) {
            this.autoConnect = autoConnect;
        }
    }

    public static class AdvancedSettings {
        private boolean enableNoiseGate = true;
        private float noiseGateThreshold = 0.02f;
        private boolean enableEchoCancellation = false;
        private int audioBufferSize = 960;

        public boolean isEnableNoiseGate() {
            return enableNoiseGate;
        }

        public void setEnableNoiseGate(boolean enableNoiseGate) {
            this.enableNoiseGate = enableNoiseGate;
        }

        public float getNoiseGateThreshold() {
            return noiseGateThreshold;
        }

        public void setNoiseGateThreshold(float noiseGateThreshold) {
            this.noiseGateThreshold = noiseGateThreshold;
        }

        public boolean isEnableEchoCancellation() {
            return enableEchoCancellation;
        }

        public void setEnableEchoCancellation(boolean enableEchoCancellation) {
            this.enableEchoCancellation = enableEchoCancellation;
        }

        public int getAudioBufferSize() {
            return audioBufferSize;
        }

        public void setAudioBufferSize(int audioBufferSize) {
            this.audioBufferSize = audioBufferSize;
        }
    }
}
