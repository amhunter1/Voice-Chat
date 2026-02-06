package com.voicechat.client.audio.codec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Stub implementation of Opus decoder
 * TODO: Replace with actual Opus implementation when library is available
 */
public class VoiceOpusDecoder {

    private static final Logger LOGGER = LoggerFactory.getLogger(VoiceOpusDecoder.class);
    private static final int SAMPLE_RATE = 48000;
    private static final int CHANNELS = 1;
    private static final int FRAME_SIZE = 960; // 20ms at 48kHz

    public VoiceOpusDecoder() {
        LOGGER.info("VoiceOpusDecoder initialized (STUB - no actual decoding)");
    }

    /**
     * Decode Opus audio data to PCM format (STUB)
     * Currently returns the input data as-is
     */
    public short[] decode(byte[] opusData) {
        if (opusData == null || opusData.length == 0) {
            return new short[0];
        }

        // STUB: Convert byte[] to short[] without actual Opus decoding
        short[] output = new short[opusData.length / 2];
        for (int i = 0; i < output.length; i++) {
            output[i] = (short) ((opusData[i * 2] & 0xFF) | ((opusData[i * 2 + 1] & 0xFF) << 8));
        }

        return output;
    }

    /**
     * Decode Opus audio data to PCM format (STUB)
     * Currently returns the input data as-is
     */
    public byte[] decodeBytes(byte[] opusData) {
        if (opusData == null || opusData.length == 0) {
            return new byte[0];
        }

        // STUB: Return input as-is
        return opusData;
    }

    /**
     * Decode packet loss concealment (STUB)
     */
    public short[] decodeFEC() {
        // STUB: Return silence
        return new short[FRAME_SIZE];
    }

    public void destroy() {
        // STUB: No-op
    }

    public int getSampleRate() {
        return SAMPLE_RATE;
    }

    public int getChannels() {
        return CHANNELS;
    }

    public int getFrameSize() {
        return FRAME_SIZE;
    }
}
