package com.voicechat.client.audio.codec;

import com.voicechat.common.audio.AudioConstants;
import org.concentus.OpusDecoder;
import org.concentus.OpusException;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Opus decoder using Concentus library
 */
public class VoiceOpusDecoder {

    private static final Logger LOGGER = Logger.getLogger(VoiceOpusDecoder.class.getName());

    private final OpusDecoder decoder;
    private final int frameSize;

    public VoiceOpusDecoder() throws OpusException {
        this.frameSize = AudioConstants.FRAME_SIZE;

        // Create Opus decoder
        decoder = new OpusDecoder(
            AudioConstants.SAMPLE_RATE,
            AudioConstants.CHANNELS
        );

        LOGGER.info("Opus decoder initialized: " + AudioConstants.SAMPLE_RATE + "Hz, " +
                    AudioConstants.CHANNELS + " channel(s)");
    }

    /**
     * Decode Opus audio data to PCM format
     * @param opusData Opus encoded data
     * @return PCM audio data (16-bit signed little-endian)
     */
    public byte[] decode(byte[] opusData) {
        if (opusData == null || opusData.length == 0) {
            return null;
        }

        try {
            // Decode from Opus
            short[] pcmShorts = new short[frameSize];
            int decodedSamples = decoder.decode(opusData, 0, opusData.length, pcmShorts, 0, frameSize, false);

            if (decodedSamples < 0) {
                LOGGER.warning("Opus decoding failed with error code: " + decodedSamples);
                return null;
            }

            // Convert short array to byte array
            return shortsToBytes(pcmShorts, decodedSamples);

        } catch (OpusException e) {
            LOGGER.log(Level.SEVERE, "Opus decoding error", e);
            return null;
        }
    }

    /**
     * Decode with packet loss concealment (PLC)
     * Used when a packet is lost to generate replacement audio
     * @return PCM audio data with concealed packet loss
     */
    public byte[] decodePLC() {
        try {
            short[] pcmShorts = new short[frameSize];
            int decodedSamples = decoder.decode(null, 0, 0, pcmShorts, 0, frameSize, true);

            if (decodedSamples < 0) {
                LOGGER.warning("PLC decoding failed with error code: " + decodedSamples);
                return null;
            }

            return shortsToBytes(pcmShorts, decodedSamples);

        } catch (OpusException e) {
            LOGGER.log(Level.WARNING, "PLC decoding error", e);
            return null;
        }
    }

    /**
     * Convert short array to byte array (16-bit PCM)
     */
    private byte[] shortsToBytes(short[] shorts, int length) {
        byte[] bytes = new byte[length * 2];
        ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(shorts, 0, length);
        return bytes;
    }

    /**
     * Reset the decoder state
     */
    public void reset() {
        try {
            decoder.resetState();
        } catch (OpusException e) {
            LOGGER.log(Level.WARNING, "Failed to reset decoder", e);
        }
    }

    /**
     * Get the frame size in samples
     */
    public int getFrameSize() {
        return frameSize;
    }

    /**
     * Get the frame size in bytes (16-bit PCM)
     */
    public int getFrameSizeBytes() {
        return frameSize * 2; // 2 bytes per sample (16-bit)
    }
}
