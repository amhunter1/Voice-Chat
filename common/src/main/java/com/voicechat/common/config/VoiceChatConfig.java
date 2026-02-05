package com.voicechat.common.config;

public class VoiceChatConfig {

    private NetworkConfig network = new NetworkConfig();
    private AudioConfig audio = new AudioConfig();
    private OcclusionConfig occlusion = new OcclusionConfig();
    private PerformanceConfig performance = new PerformanceConfig();
    private SecurityConfig security = new SecurityConfig();

    public NetworkConfig getNetwork() {
        return network;
    }

    public AudioConfig getAudio() {
        return audio;
    }

    public OcclusionConfig getOcclusion() {
        return occlusion;
    }

    public PerformanceConfig getPerformance() {
        return performance;
    }

    public SecurityConfig getSecurity() {
        return security;
    }

    public static class NetworkConfig {
        private int voicePort = 24454;
        private int maxPacketSize = 2048;
        private int keepAliveInterval = 5000;

        public int getVoicePort() {
            return voicePort;
        }

        public void setVoicePort(int voicePort) {
            this.voicePort = voicePort;
        }

        public int getMaxPacketSize() {
            return maxPacketSize;
        }

        public void setMaxPacketSize(int maxPacketSize) {
            this.maxPacketSize = maxPacketSize;
        }

        public int getKeepAliveInterval() {
            return keepAliveInterval;
        }

        public void setKeepAliveInterval(int keepAliveInterval) {
            this.keepAliveInterval = keepAliveInterval;
        }
    }

    public static class AudioConfig {
        private int normalDistance = 64;
        private int whisperDistance = 8;
        private int shoutDistance = 128;
        private float fadeStartPercent = 0.5f;
        private int bitrate = 24000;

        public int getNormalDistance() {
            return normalDistance;
        }

        public void setNormalDistance(int normalDistance) {
            this.normalDistance = normalDistance;
        }

        public int getWhisperDistance() {
            return whisperDistance;
        }

        public void setWhisperDistance(int whisperDistance) {
            this.whisperDistance = whisperDistance;
        }

        public int getShoutDistance() {
            return shoutDistance;
        }

        public void setShoutDistance(int shoutDistance) {
            this.shoutDistance = shoutDistance;
        }

        public float getFadeStartPercent() {
            return fadeStartPercent;
        }

        public void setFadeStartPercent(float fadeStartPercent) {
            this.fadeStartPercent = fadeStartPercent;
        }

        public int getBitrate() {
            return bitrate;
        }

        public void setBitrate(int bitrate) {
            this.bitrate = bitrate;
        }
    }

    public static class OcclusionConfig {
        private boolean enabled = true;
        private int maxBlocksChecked = 16;
        private float attenuationPerBlock = 0.15f;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public int getMaxBlocksChecked() {
            return maxBlocksChecked;
        }

        public void setMaxBlocksChecked(int maxBlocksChecked) {
            this.maxBlocksChecked = maxBlocksChecked;
        }

        public float getAttenuationPerBlock() {
            return attenuationPerBlock;
        }

        public void setAttenuationPerBlock(float attenuationPerBlock) {
            this.attenuationPerBlock = attenuationPerBlock;
        }
    }

    public static class PerformanceConfig {
        private int maxPlayersPerPacket = 50;
        private int updateInterval = 50;
        private int cacheExpirationMs = 10000;

        public int getMaxPlayersPerPacket() {
            return maxPlayersPerPacket;
        }

        public void setMaxPlayersPerPacket(int maxPlayersPerPacket) {
            this.maxPlayersPerPacket = maxPlayersPerPacket;
        }

        public int getUpdateInterval() {
            return updateInterval;
        }

        public void setUpdateInterval(int updateInterval) {
            this.updateInterval = updateInterval;
        }

        public int getCacheExpirationMs() {
            return cacheExpirationMs;
        }

        public void setCacheExpirationMs(int cacheExpirationMs) {
            this.cacheExpirationMs = cacheExpirationMs;
        }
    }

    public static class SecurityConfig {
        private boolean enableRateLimiting = true;
        private int maxPacketsPerSecond = 100;
        private int banDuration = 300000;

        public boolean isEnableRateLimiting() {
            return enableRateLimiting;
        }

        public void setEnableRateLimiting(boolean enableRateLimiting) {
            this.enableRateLimiting = enableRateLimiting;
        }

        public int getMaxPacketsPerSecond() {
            return maxPacketsPerSecond;
        }

        public void setMaxPacketsPerSecond(int maxPacketsPerSecond) {
            this.maxPacketsPerSecond = maxPacketsPerSecond;
        }

        public int getBanDuration() {
            return banDuration;
        }

        public void setBanDuration(int banDuration) {
            this.banDuration = banDuration;
        }
    }
}
