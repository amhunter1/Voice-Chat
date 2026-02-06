package com.voicechat.client.audio.output;

import com.voicechat.client.VoiceChatClientMod;
import com.voicechat.client.audio.codec.VoiceOpusDecoder;
import com.voicechat.common.audio.AudioConstants;
import com.voicechat.common.network.packet.VoiceBroadcastPacket;

import javax.sound.sampled.*;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AudioPlayback {

    private static final Logger LOGGER = Logger.getLogger(AudioPlayback.class.getName());

    private final VoiceChatClientMod mod;
    private VoiceOpusDecoder decoder;
    private final Map<UUID, SourceDataLine> playerLines = new ConcurrentHashMap<>();
    private volatile boolean running = false;

    public AudioPlayback(VoiceChatClientMod mod) {
        this.mod = mod;
        this.decoder = new VoiceOpusDecoder();
    }

    public void start() {
        if (decoder == null) {
            LOGGER.severe("Cannot start audio playback: decoder not initialized");
            return;
        }
        running = true;
        LOGGER.info("Audio playback started");
    }

    public void stop() {
        running = false;
        playerLines.values().forEach(line -> {
            if (line.isOpen()) {
                line.stop();
                line.close();
            }
        });
        playerLines.clear();
        LOGGER.info("Audio playback stopped");
    }

    public void playVoice(VoiceBroadcastPacket packet) {
        if (!running || decoder == null) {
            return;
        }

        try {
            SourceDataLine line = playerLines.computeIfAbsent(packet.getSenderId(), this::createLine);
            if (line == null) {
                LOGGER.warning("Failed to get audio line for player: " + packet.getSenderId());
                return;
            }

            byte[] decoded = decoder.decodeBytes(packet.getOpusData());
            if (decoded == null) {
                LOGGER.warning("Failed to decode audio data");
                return;
            }

            // Apply volume adjustments
            float volume = packet.getVolume() * mod.getConfigManager().getConfig().getOutputVolume();
            byte[] adjusted = adjustVolume(decoded, volume);

            // Write to audio line
            line.write(adjusted, 0, adjusted.length);

        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error playing voice", e);
        }
    }

    private SourceDataLine createLine(UUID playerId) {
        try {
            AudioFormat format = new AudioFormat(
                    AudioConstants.SAMPLE_RATE,
                    16, // 16-bit
                    AudioConstants.CHANNELS,
                    true, // signed
                    false // little-endian
            );

            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

            if (!AudioSystem.isLineSupported(info)) {
                LOGGER.warning("Audio line not supported for player: " + playerId);
                return null;
            }

            SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();

            LOGGER.info("Created audio line for player: " + playerId);
            return line;

        } catch (LineUnavailableException e) {
            LOGGER.log(Level.WARNING, "Failed to create audio line for player: " + playerId, e);
            return null;
        }
    }

    /**
     * Adjust volume of PCM samples
     */
    private byte[] adjustVolume(byte[] samples, float volume) {
        // Clamp volume to reasonable range
        volume = Math.max(0.0f, Math.min(2.0f, volume));

        byte[] result = new byte[samples.length];

        for (int i = 0; i < samples.length; i += 2) {
            // Convert little-endian 16-bit samples to short
            short sample = (short) ((samples[i + 1] << 8) | (samples[i] & 0xFF));

            // Apply volume and clamp
            sample = (short) Math.max(-32768, Math.min(32767, sample * volume));

            // Convert back to little-endian bytes
            result[i] = (byte) sample;
            result[i + 1] = (byte) (sample >> 8);
        }

        return result;
    }

    public void removePlayer(UUID playerId) {
        SourceDataLine line = playerLines.remove(playerId);
        if (line != null) {
            if (line.isOpen()) {
                line.stop();
                line.close();
            }
            LOGGER.info("Removed audio line for player: " + playerId);
        }
    }

    public boolean isRunning() {
        return running;
    }

    /**
     * Get the number of active player audio lines
     */
    public int getActivePlayerCount() {
        return playerLines.size();
    }
}
