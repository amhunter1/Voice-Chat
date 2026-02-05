package com.voicechat.common.audio;

public class ProximityCalculator {

    public static float calculateVolume(double distance, int maxDistance) {
        if (distance >= maxDistance)
            return 0f;
        if (distance <= 0)
            return 1f;

        double normalized = distance / maxDistance;
        float volume = (float) (1.0 - Math.log10(1 + 9 * normalized));
        return Math.max(0f, Math.min(1f, volume));
    }

    public static float applyOcclusion(float baseVolume, int blockedBlocks, float attenuationPerBlock) {
        float attenuation = blockedBlocks * attenuationPerBlock;
        return Math.max(0f, baseVolume - attenuation);
    }
}
