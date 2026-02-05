package com.voicechat.common.api;

import java.util.UUID;

public interface VoiceChatAPI {

    void mutePlayer(UUID playerId, UUID targetId);

    void unmutePlayer(UUID playerId, UUID targetId);

    boolean isPlayerMuted(UUID playerId, UUID targetId);

    void setPlayerVolume(UUID playerId, float volume);

    float getPlayerVolume(UUID playerId);
}
