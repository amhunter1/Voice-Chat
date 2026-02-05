package com.voicechat.client.service;

import com.voicechat.client.VoiceChatClientMod;
import com.voicechat.common.network.packet.VoiceMode;

public class VoiceModeService {

    private final VoiceChatClientMod mod;
    private VoiceMode currentMode = VoiceMode.NORMAL;

    public VoiceModeService(VoiceChatClientMod mod) {
        this.mod = mod;
    }

    public void setMode(VoiceMode mode) {
        this.currentMode = mode;
        mod.getVoiceClient().setVoiceMode(mode);
    }

    public VoiceMode getMode() {
        return currentMode;
    }

    public void toggleWhisper() {
        setMode(currentMode == VoiceMode.WHISPER ? VoiceMode.NORMAL : VoiceMode.WHISPER);
    }

    public void toggleShout() {
        setMode(currentMode == VoiceMode.SHOUT ? VoiceMode.NORMAL : VoiceMode.SHOUT);
    }
}
