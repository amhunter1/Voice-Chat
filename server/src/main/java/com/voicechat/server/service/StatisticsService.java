package com.voicechat.server.service;

import com.voicechat.common.data.statistics.VoiceStatistics;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class StatisticsService {

    private final Map<UUID, VoiceStatistics> statistics = new ConcurrentHashMap<>();

    public VoiceStatistics getStats(UUID playerId) {
        return statistics.computeIfAbsent(playerId, VoiceStatistics::new);
    }

    public void removeStats(UUID playerId) {
        statistics.remove(playerId);
    }

    public void recordPacketSent(UUID playerId) {
        getStats(playerId).incrementPacketsSent();
    }

    public void recordPacketReceived(UUID playerId) {
        getStats(playerId).incrementPacketsReceived();
    }

    public void recordSpeakTime(UUID playerId, long millis) {
        getStats(playerId).addSpeakTime(millis);
    }

    public Map<UUID, VoiceStatistics> getAllStats() {
        return statistics;
    }
}
