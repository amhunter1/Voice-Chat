package com.voicechat.client.config;

import com.voicechat.common.network.packet.ActivationMode;

public class ClientConfig {

    private float microphoneVolume = 1.0f;
    private float outputVolume = 1.0f;
    private boolean occlusionEnabled = true;
    private ActivationMode activationMode = ActivationMode.PUSH_TO_TALK;
    private int pushToTalkKey = 86; // V key
    private float voiceActivationThreshold = 0.05f;
    private String selectedMicrophone = "default";

    public float getMicrophoneVolume() {
        return microphoneVolume;
    }

    public void setMicrophoneVolume(float vol) {
        this.microphoneVolume = vol;
    }

    public float getOutputVolume() {
        return outputVolume;
    }

    public void setOutputVolume(float vol) {
        this.outputVolume = vol;
    }

    public boolean isOcclusionEnabled() {
        return occlusionEnabled;
    }

    public void setOcclusionEnabled(boolean enabled) {
        this.occlusionEnabled = enabled;
    }

    public ActivationMode getActivationMode() {
        return activationMode;
    }

    public void setActivationMode(ActivationMode mode) {
        this.activationMode = mode;
    }

    public int getPushToTalkKey() {
        return pushToTalkKey;
    }

    public void setPushToTalkKey(int key) {
        this.pushToTalkKey = key;
    }

    public float getVoiceActivationThreshold() {
        return voiceActivationThreshold;
    }

    public void setVoiceActivationThreshold(float threshold) {
        this.voiceActivationThreshold = threshold;
    }

    public String getSelectedMicrophone() {
        return selectedMicrophone;
    }

    public void setSelectedMicrophone(String mic) {
        this.selectedMicrophone = mic;
    }
}
