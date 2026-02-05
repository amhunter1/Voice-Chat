package com.voicechat.client.audio.codec;

import com.voicechat.common.audio.AudioConstants;
import org.concentus.OpusApplication;
import org.concentus.OpusEncoder;
import org.concentus.OpusException;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Opus encoder using Concentus library
 */
public class VoiceOpusEncoder {

    private static final Logger LOGGER = Logger.getLogger(VoiceOpusEncoder.class.getName());

    private final OpusEncoder encoder;
    private final int frameSize;

    public VoiceOpusEncoder() throws OpusException {
        this.frameSize = AudioConstants.FRAME_SIZE;

        // Create Opus encoder
        encoder = new OpusEncoder(
            AudioConstants.SAMPLE_RATE,
            AudioConstants.CHANNELS,
            OpusApplication.OPUS_APPLICATION_VOIP
        );

        // Set bitrate
        encoder.setBitrate(AudioConstants.BITRATE);

        // Enable VBR (Variable Bit Rate) for better quality
        encoder.setUseVBR(true);

        // Set complexity (0-10, higher = better quality but more CPU)
        encoder.setComplexity(5);

        // Enable DTX (Discontinuous Transmission) to save bandwidth during silence
        encoder.setUseDTX(true);

        LOGGER.info("Opus encoder initialized: " + AudioConstants.SAMPLE_RATE + "Hz, " +
                    AudioConstants.CHANNELS + " channel(s), " + AudioConstants.BITRATE + " bps");
    }

    /**
     * Encode PCM audio data to Opus format
     * @param pcmData PCM audio data (16-bit signed little-endian)
     * @return Opus encoded data
     */
    public byte[] encode(byte[] pcmData) {
        if (pcmData == null || pcmData.length == 0) {
            return null;
        }

        try {
            // Convert byte array to short array (PCM is 16-bit)
            short[] pcmShorts = bytesToShorts(pcmData);

            // Ensure we have the correct frame size
            if (pcmShorts.length != frameSize) {
                LOGGER.warning("Invalid PCM frame size: " + pcmShorts.length + ", expected: " + frameSize);
                return null;
            }

            // Encode to Opus
            byte[] opusData = new byte[AudioConstants.MAX_PACKET_SIZE];
            int encodedLength = encoder.encode(pcmShorts, 0, frameSize, opusData, 0, opusData.length);

            if (encodedLength < 0) {
                LOGGER.warning("Opus encoding failed with error code: " + encodedLength);
                return null;
            }

            // Return only the used portion of the buffer
            byte[] result = new byte[encodedLength];
            System.arraycopy(opusData, 0, result, 0, encodedLength);

            return result;

        } catch (OpusException e) {
            LOGGER.log(Level.SEVERE, "Opus encoding error", e);
            return null;
        }
    }

    /**
     * Convert byte array to short array (16-bit PCM)
     */
    private short[] bytesToShorts(byte[] bytes) {
        short[] shorts = new short[bytes.length / 2];
        ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);
        return shorts;
    }

    /**
     * Reset the encoder state
     */
    public void reset() {
        try {
            encoder.resetState();
        } catch (OpusException e) {
            LOGGER.log(Level.WARNING, "Failed to reset encoder", e);
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
