package com.voicechat.common.data.statistics;

import java.util.UUID;

public class VoiceStatistics {

    private final UUID playerId;
    private long totalSpeakTime = 0;
    private int totalPacketsSent = 0;
    private int totalPacketsReceived = 0;
    private long sessionStartTime;

    public VoiceStatistics(UUID playerId) {
        this.playerId = playerId;
        this.sessionStartTime = System.currentTimeMillis();
    }

    public void addSpeakTime(long millis) {
        this.totalSpeakTime += millis;
    }

    public void incrementPacketsSent() {
        this.totalPacketsSent++;
    }

    public void incrementPacketsReceived() {
        this.totalPacketsReceived++;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public long getTotalSpeakTime() {
        return totalSpeakTime;
    }

    public int getTotalPacketsSent() {
        return totalPacketsSent;
    }

    public int getTotalPacketsReceived() {
        return totalPacketsReceived;
    }

    public long getSessionDuration() {
        return System.currentTimeMillis() - sessionStartTime;
    }
}
