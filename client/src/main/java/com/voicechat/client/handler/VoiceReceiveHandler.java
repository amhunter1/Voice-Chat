package com.voicechat.client.handler;

import com.voicechat.client.VoiceChatClientMod;
import com.voicechat.common.network.packet.VoiceBroadcastPacket;

public class VoiceReceiveHandler {

    private final VoiceChatClientMod mod;

    public VoiceReceiveHandler(VoiceChatClientMod mod) {
        this.mod = mod;
    }

    public void handle(VoiceBroadcastPacket packet) {
        if (mod.getConfigManager().getConfig().getOutputVolume() <= 0)
            return;

        mod.getAudioPlayback().playVoice(packet);
    }
}
