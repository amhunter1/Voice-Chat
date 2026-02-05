package com.voicechat.client.gui.screen;

import com.voicechat.client.VoiceChatClientMod;

import com.voicechat.common.network.packet.ActivationMode;

public class VoiceSettingsScreen {

    private final VoiceChatClientMod mod;

    public VoiceSettingsScreen(VoiceChatClientMod mod) {
        this.mod = mod;
    }

    public void render() {
        // TODO: Implement with Hytale GUI API
        // Pseudocode:
        // Draw background
        // drawRect(0, 0, width, height, 0x88000000);

        // Draw text
        // drawCenteredString(fontRenderer, "Voice Chat Settings", width / 2, 20,
        // 0xFFFFFF);

        // Render sliders and buttons
        // volumeSlider.render(width / 2 - 100, 50, 200, 20);
        // micSlider.render(width / 2 - 100, 80, 200, 20);
    }

    public void onMicrophoneVolumeChanged(float value) {
        mod.getConfigManager().getConfig().setMicrophoneVolume(value);
        mod.getConfigManager().save();
    }

    public void onOutputVolumeChanged(float value) {
        mod.getConfigManager().getConfig().setOutputVolume(value);
        mod.getConfigManager().save();
    }

    public void onActivationModeChanged(ActivationMode mode) {
        mod.getConfigManager().getConfig().setActivationMode(mode);
        mod.getConfigManager().save();
    }

    public void onOcclusionToggled(boolean enabled) {
        mod.getConfigManager().getConfig().setOcclusionEnabled(enabled);
        mod.getConfigManager().save();
    }

    public void onPushToTalkKeyChanged(int keyCode) {
        mod.getConfigManager().getConfig().setPushToTalkKey(keyCode);
        mod.getConfigManager().save();
    }

    public void onVoiceActivationThresholdChanged(float value) {
        mod.getConfigManager().getConfig().setVoiceActivationThreshold(value);
        mod.getConfigManager().save();
    }
}
