package com.voicechat.client.handler;

import com.voicechat.client.VoiceChatClientMod;

public class KeyInputHandler {

    private final VoiceChatClientMod mod;
    private int whisperKey = 66; // B
    private int shoutKey = 71; // G

    public KeyInputHandler(VoiceChatClientMod mod) {
        this.mod = mod;
    }

    public void onKeyDown(int keyCode) {
        if (keyCode == mod.getConfigManager().getConfig().getPushToTalkKey()) {
            mod.setTalking(true);
        } else if (keyCode == whisperKey) {
            mod.getVoiceClient().setVoiceMode(com.voicechat.common.network.packet.VoiceMode.WHISPER);
        } else if (keyCode == shoutKey) {
            mod.getVoiceClient().setVoiceMode(com.voicechat.common.network.packet.VoiceMode.SHOUT);
        }
    }

    public void onKeyUp(int keyCode) {
        if (keyCode == mod.getConfigManager().getConfig().getPushToTalkKey()) {
            mod.setTalking(false);
        } else if (keyCode == whisperKey || keyCode == shoutKey) {
            mod.getVoiceClient().setVoiceMode(com.voicechat.common.network.packet.VoiceMode.NORMAL);
        }
    }

    public void setWhisperKey(int key) {
        this.whisperKey = key;
    }

    public void setShoutKey(int key) {
        this.shoutKey = key;
    }
}
