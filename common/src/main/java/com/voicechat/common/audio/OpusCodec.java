package com.voicechat.common.audio;

import com.voicechat.common.exception.AudioException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Opus audio codec implementation using JNI (Java Native Interface).
 * Provides high-quality, low-latency audio encoding and decoding.
 * 
 * <p>
 * Opus is specifically designed for interactive speech and music transmission
 * over the Internet. It provides excellent audio quality at low bitrates.
 * </p>
 * 
 * <p>
 * <strong>Note:</strong> This implementation requires native Opus libraries to
 * be present:
 * <ul>
 * <li>Windows: opus.dll</li>
 * <li>Linux: libopus.so</li>
 * <li>macOS: libopus.dylib</li>
 * </ul>
 * If native libraries are not available, the codec operates in stub mode
 * (pass-through).
 * </p>
 * 
 * @author VoiceChat Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class OpusCodec {
    private static final Logger LOGGER = LoggerFactory.getLogger(OpusCodec.class);

    // Opus configuration
    private static final int SAMPLE_RATE = AudioConstants.SAMPLE_RATE; // 48000 Hz
    private static final int CHANNELS = AudioConstants.CHANNELS; // 1 (mono)
    private static final int FRAME_SIZE = AudioConstants.FRAME_SIZE; // 960 samples (20ms at 48kHz)
    private static final int BITRATE = 64000; // 64 kbps (good quality for voice)
    private static final int MAX_PACKET_SIZE = 4000; // Maximum encoded packet size

    // Opus application types
    private static final int OPUS_APPLICATION_VOIP = 2048;
    private static final int OPUS_APPLICATION_AUDIO = 2049;
    private static final int OPUS_APPLICATION_RESTRICTED_LOWDELAY = 2051;

    // Native encoder/decoder handles
    private long encoderHandle = 0;
    private long decoderHandle = 0;
    private boolean isInitialized = false;
    private static boolean nativeLibraryLoaded = false;

    static {
        try {
            loadNativeLibrary();
            nativeLibraryLoaded = true;
            LOGGER.info("Opus native library loaded successfully");
        } catch (UnsatisfiedLinkError e) {
            LOGGER.warn("Failed to load Opus native library: {}", e.getMessage());
            LOGGER.warn("Opus codec will operate in stub mode (no actual encoding/decoding)");
            nativeLibraryLoaded = false;
        }
    }

    /**
     * Loads the native Opus library based on the operating system.
     */
    private static void loadNativeLibrary() {
        String osName = System.getProperty("os.name").toLowerCase();
        String osArch = System.getProperty("os.arch").toLowerCase();

        String libraryName;
        if (osName.contains("win")) {
            libraryName = "opus";
        } else if (osName.contains("mac")) {
            libraryName = "opus";
        } else {
            libraryName = "opus";
        }

        try {
            System.loadLibrary(libraryName);
        } catch (UnsatisfiedLinkError e) {
            // Try loading from natives directory
            String nativesPath = "natives/" + getPlatformName() + "/" + System.mapLibraryName(libraryName);
            System.load(new java.io.File(nativesPath).getAbsolutePath());
        }
    }

    /**
     * Gets the platform name for native library loading.
     */
    private static String getPlatformName() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            return "windows";
        } else if (osName.contains("mac")) {
            return "macos";
        } else {
            return "linux";
        }
    }

    /**
     * Creates a new Opus codec instance.
     * 
     * @throws AudioException if codec initialization fails
     */
    public OpusCodec() throws AudioException {
        if (!nativeLibraryLoaded) {
            LOGGER.warn("Creating OpusCodec in stub mode (native library not loaded)");
            isInitialized = true; // Allow stub mode
            return;
        }

        try {
            LOGGER.info("Initializing Opus codec ({}Hz, {} channels, {}ms frames, {} kbps)",
                    SAMPLE_RATE, CHANNELS, (FRAME_SIZE * 1000 / SAMPLE_RATE), BITRATE / 1000);

            // Create encoder
            encoderHandle = nativeCreateEncoder(SAMPLE_RATE, CHANNELS, OPUS_APPLICATION_VOIP);
            if (encoderHandle == 0) {
                throw new AudioException("Failed to create Opus encoder");
            }

            // Configure encoder
            nativeEncoderSetBitrate(encoderHandle, BITRATE);
            nativeEncoderSetComplexity(encoderHandle, 10); // Maximum quality
            nativeEncoderSetVBR(encoderHandle, true);
            nativeEncoderSetDTX(encoderHandle, true);

            // Create decoder
            decoderHandle = nativeCreateDecoder(SAMPLE_RATE, CHANNELS);
            if (decoderHandle == 0) {
                nativeDestroyEncoder(encoderHandle);
                throw new AudioException("Failed to create Opus decoder");
            }

            isInitialized = true;
            LOGGER.info("Opus codec initialized successfully");

        } catch (Exception e) {
            cleanup();
            throw new AudioException("Failed to initialize Opus codec: " + e.getMessage(), e);
        }
    }

    /**
     * Encodes PCM audio data to Opus format.
     * 
     * @param pcmData PCM audio data (16-bit signed integers)
     * @return encoded Opus data
     * @throws AudioException if encoding fails
     */
    public byte[] encode(short[] pcmData) throws AudioException {
        if (!isInitialized) {
            throw new AudioException("Opus codec not initialized");
        }

        if (pcmData == null || pcmData.length == 0) {
            throw new AudioException("PCM data is null or empty");
        }

        if (pcmData.length != FRAME_SIZE) {
            throw new AudioException(String.format(
                    "Invalid PCM data length: expected %d samples, got %d",
                    FRAME_SIZE, pcmData.length));
        }

        if (!nativeLibraryLoaded) {
            // Stub mode: return dummy data
            return createStubEncodedData(pcmData);
        }

        try {
            byte[] encoded = new byte[MAX_PACKET_SIZE];
            int encodedLength = nativeEncode(encoderHandle, pcmData, FRAME_SIZE, encoded, MAX_PACKET_SIZE);

            if (encodedLength < 0) {
                throw new AudioException("Encoding failed with error code: " + encodedLength);
            }

            // Create a properly sized array
            byte[] result = new byte[encodedLength];
            System.arraycopy(encoded, 0, result, 0, encodedLength);

            return result;

        } catch (Exception e) {
            throw new AudioException("Failed to encode audio: " + e.getMessage(), e);
        }
    }

    /**
     * Decodes Opus audio data to PCM format.
     * 
     * @param opusData encoded Opus data
     * @return decoded PCM audio data (16-bit signed integers)
     * @throws AudioException if decoding fails
     */
    public short[] decode(byte[] opusData) throws AudioException {
        if (!isInitialized) {
            throw new AudioException("Opus codec not initialized");
        }

        if (opusData == null || opusData.length == 0) {
            throw new AudioException("Opus data is null or empty");
        }

        if (!nativeLibraryLoaded) {
            // Stub mode: return dummy data
            return createStubDecodedData(opusData);
        }

        try {
            short[] decoded = new short[FRAME_SIZE];
            int decodedLength = nativeDecode(decoderHandle, opusData, opusData.length, decoded, FRAME_SIZE, false);

            if (decodedLength < 0) {
                throw new AudioException("Decoding failed with error code: " + decodedLength);
            }

            if (decodedLength != FRAME_SIZE) {
                LOGGER.warn("Decoded frame size mismatch: expected {}, got {}", FRAME_SIZE, decodedLength);
            }

            return decoded;

        } catch (Exception e) {
            throw new AudioException("Failed to decode audio: " + e.getMessage(), e);
        }
    }

    /**
     * Decodes a lost packet (packet loss concealment).
     * Generates plausible audio data based on previous packets.
     * 
     * @return decoded PCM audio data for the lost packet
     * @throws AudioException if decoding fails
     */
    public short[] decodeLostPacket() throws AudioException {
        if (!isInitialized) {
            throw new AudioException("Opus codec not initialized");
        }

        if (!nativeLibraryLoaded) {
            // Stub mode: return silence
            return new short[FRAME_SIZE];
        }

        try {
            short[] decoded = new short[FRAME_SIZE];
            int decodedLength = nativeDecode(decoderHandle, null, 0, decoded, FRAME_SIZE, true);

            if (decodedLength < 0) {
                throw new AudioException("Lost packet decoding failed with error code: " + decodedLength);
            }

            return decoded;

        } catch (Exception e) {
            throw new AudioException("Failed to decode lost packet: " + e.getMessage(), e);
        }
    }

    /**
     * Creates stub encoded data for testing (when native library is not available).
     */
    private byte[] createStubEncodedData(short[] pcmData) {
        // Simple compression: just copy the data
        return shortsToBytes(pcmData);
    }

    /**
     * Creates stub decoded data for testing (when native library is not available).
     */
    private short[] createStubDecodedData(byte[] opusData) {
        // Simple decompression: just copy the data
        return bytesToShorts(opusData);
    }

    /**
     * Converts byte array to short array (16-bit PCM).
     * 
     * @param bytes byte array (little-endian)
     * @return short array
     */
    public static short[] bytesToShorts(byte[] bytes) {
        if (bytes == null || bytes.length % 2 != 0) {
            throw new IllegalArgumentException("Invalid byte array length");
        }

        short[] shorts = new short[bytes.length / 2];
        for (int i = 0; i < shorts.length; i++) {
            shorts[i] = (short) ((bytes[i * 2] & 0xFF) | ((bytes[i * 2 + 1] & 0xFF) << 8));
        }
        return shorts;
    }

    /**
     * Converts short array to byte array (16-bit PCM).
     * 
     * @param shorts short array
     * @return byte array (little-endian)
     */
    public static byte[] shortsToBytes(short[] shorts) {
        if (shorts == null) {
            throw new IllegalArgumentException("Short array is null");
        }

        byte[] bytes = new byte[shorts.length * 2];
        for (int i = 0; i < shorts.length; i++) {
            bytes[i * 2] = (byte) (shorts[i] & 0xFF);
            bytes[i * 2 + 1] = (byte) ((shorts[i] >> 8) & 0xFF);
        }
        return bytes;
    }

    /**
     * Resets the encoder state.
     */
    public void resetEncoder() throws AudioException {
        if (!isInitialized) {
            throw new AudioException("Opus codec not initialized");
        }

        if (nativeLibraryLoaded && encoderHandle != 0) {
            nativeEncoderReset(encoderHandle);
            LOGGER.debug("Encoder state reset");
        }
    }

    /**
     * Resets the decoder state.
     */
    public void resetDecoder() throws AudioException {
        if (!isInitialized) {
            throw new AudioException("Opus codec not initialized");
        }

        if (nativeLibraryLoaded && decoderHandle != 0) {
            nativeDecoderReset(decoderHandle);
            LOGGER.debug("Decoder state reset");
        }
    }

    /**
     * Cleans up native resources.
     */
    private void cleanup() {
        if (nativeLibraryLoaded) {
            if (encoderHandle != 0) {
                nativeDestroyEncoder(encoderHandle);
                encoderHandle = 0;
            }
            if (decoderHandle != 0) {
                nativeDestroyDecoder(decoderHandle);
                decoderHandle = 0;
            }
        }
        isInitialized = false;
    }

    /**
     * Closes the codec and releases resources.
     */
    public void close() {
        cleanup();
        LOGGER.info("Opus codec closed");
    }

    // Getters
    public int getSampleRate() {
        return SAMPLE_RATE;
    }

    public int getChannels() {
        return CHANNELS;
    }

    public int getFrameSize() {
        return FRAME_SIZE;
    }

    public int getBitrate() {
        return BITRATE;
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    public static boolean isNativeLibraryLoaded() {
        return nativeLibraryLoaded;
    }

    // Native methods (to be implemented in C/C++)
    private native long nativeCreateEncoder(int sampleRate, int channels, int application);

    private native long nativeCreateDecoder(int sampleRate, int channels);

    private native void nativeDestroyEncoder(long handle);

    private native void nativeDestroyDecoder(long handle);

    private native void nativeEncoderSetBitrate(long handle, int bitrate);

    private native void nativeEncoderSetComplexity(long handle, int complexity);

    private native void nativeEncoderSetVBR(long handle, boolean vbr);

    private native void nativeEncoderSetDTX(long handle, boolean dtx);

    private native int nativeEncode(long handle, short[] pcm, int frameSize, byte[] encoded, int maxSize);

    private native int nativeDecode(long handle, byte[] encoded, int length, short[] pcm, int frameSize, boolean fec);

    private native void nativeEncoderReset(long handle);

    private native void nativeDecoderReset(long handle);
}
