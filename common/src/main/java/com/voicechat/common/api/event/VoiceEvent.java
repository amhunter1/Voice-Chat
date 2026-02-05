package com.voicechat.common.api.event;

import java.util.UUID;

public class VoiceEvent {

    private final UUID playerId;
    private final long timestamp;
    private boolean cancelled = false;

    public VoiceEvent(UUID playerId) {
        this.playerId = playerId;
        this.timestamp = System.currentTimeMillis();
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
