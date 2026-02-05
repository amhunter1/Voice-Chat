package com.voicechat.common.api.event;

import java.util.UUID;

public class PlayerMuteEvent extends VoiceEvent {

    private final UUID targetId;
    private final boolean muted;

    public PlayerMuteEvent(UUID playerId, UUID targetId, boolean muted) {
        super(playerId);
        this.targetId = targetId;
        this.muted = muted;
    }

    public UUID getTargetId() {
        return targetId;
    }

    public boolean isMuted() {
        return muted;
    }
}
