package com.voicechat.client.audio.processing;

public class VolumeAmplifier {

    private float gain = 1.0f;

    public byte[] process(byte[] samples) {
        byte[] result = new byte[samples.length];

        for (int i = 0; i < samples.length; i += 2) {
            short sample = (short) ((samples[i + 1] << 8) | (samples[i] & 0xFF));
            sample = (short) Math.max(-32768, Math.min(32767, sample * gain));
            result[i] = (byte) sample;
            result[i + 1] = (byte) (sample >> 8);
        }

        return result;
    }

    public void setGain(float gain) {
        this.gain = gain;
    }

    public float getGain() {
        return gain;
    }
}
