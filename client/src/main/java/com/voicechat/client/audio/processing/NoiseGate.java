package com.voicechat.client.audio.processing;

public class NoiseGate {

    private float threshold;
    private float holdTime;
    private long lastActiveTime = 0;
    private boolean isOpen = false;

    public NoiseGate(float threshold, float holdTimeMs) {
        this.threshold = threshold;
        this.holdTime = holdTimeMs;
    }

    public boolean process(byte[] samples) {
        float level = calculateLevel(samples);
        long now = System.currentTimeMillis();

        if (level > threshold) {
            isOpen = true;
            lastActiveTime = now;
        } else if (isOpen && (now - lastActiveTime) > holdTime) {
            isOpen = false;
        }

        return isOpen;
    }

    private float calculateLevel(byte[] samples) {
        double sum = 0;
        for (int i = 0; i < samples.length; i += 2) {
            short sample = (short) ((samples[i + 1] << 8) | (samples[i] & 0xFF));
            sum += Math.abs(sample);
        }
        return (float) (sum / (samples.length / 2) / 32768.0);
    }

    public void setThreshold(float threshold) {
        this.threshold = threshold;
    }
}
