package com.voicechat.common.config;

public class VoiceChatConfig {

    private NetworkConfig network = new NetworkConfig();
    private AudioConfig audio = new AudioConfig();
    private OcclusionConfig occlusion = new OcclusionConfig();

    public NetworkConfig getNetwork() {
        return network;
    }

    public AudioConfig getAudio() {
        return audio;
    }

    public OcclusionConfig getOcclusion() {
        return occlusion;
    }

    public static class NetworkConfig {
        private int voicePort = 24454;
        private int maxPacketSize = 1024;

        public int getVoicePort() {
            return voicePort;
        }

        public int getMaxPacketSize() {
            return maxPacketSize;
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

        public int getWhisperDistance() {
            return whisperDistance;
        }

        public int getShoutDistance() {
            return shoutDistance;
        }

        public float getFadeStartPercent() {
            return fadeStartPercent;
        }

        public int getBitrate() {
            return bitrate;
        }
    }

    public static class OcclusionConfig {
        private boolean enabled = true;
        private int maxBlocksChecked = 16;
        private float attenuationPerBlock = 0.15f;

        public boolean isEnabled() {
            return enabled;
        }

        public int getMaxBlocksChecked() {
            return maxBlocksChecked;
        }

        public float getAttenuationPerBlock() {
            return attenuationPerBlock;
        }
    }
}
