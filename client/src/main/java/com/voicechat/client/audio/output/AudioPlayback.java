package com.voicechat.client.audio.output;

import com.voicechat.client.VoiceChatClientMod;
import com.voicechat.client.audio.codec.OpusDecoder;
import com.voicechat.common.audio.AudioConstants;
import com.voicechat.common.network.packet.VoiceBroadcastPacket;

import javax.sound.sampled.*;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AudioPlayback {

    private final VoiceChatClientMod mod;
    private final OpusDecoder decoder;
    private final Map<UUID, SourceDataLine> playerLines = new ConcurrentHashMap<>();
    private volatile boolean running = false;

    public AudioPlayback(VoiceChatClientMod mod) {
        this.mod = mod;
        this.decoder = new OpusDecoder();
    }

    public void start() {
        running = true;
    }

    public void stop() {
        running = false;
        playerLines.values().forEach(line -> {
            line.stop();
            line.close();
        });
        playerLines.clear();
    }

    public void playVoice(VoiceBroadcastPacket packet) {
        if (!running)
            return;

        SourceDataLine line = playerLines.computeIfAbsent(packet.getSenderId(), this::createLine);
        if (line == null)
            return;

        byte[] decoded = decoder.decode(packet.getOpusData());
        if (decoded == null)
            return;

        float volume = packet.getVolume() * mod.getConfigManager().getConfig().getOutputVolume();
        byte[] adjusted = adjustVolume(decoded, volume);

        line.write(adjusted, 0, adjusted.length);
    }

    private SourceDataLine createLine(UUID playerId) {
        try {
            AudioFormat format = new AudioFormat(AudioConstants.SAMPLE_RATE, 16, AudioConstants.CHANNELS, true, false);
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();
            return line;
        } catch (LineUnavailableException e) {
            return null;
        }
    }

    private byte[] adjustVolume(byte[] samples, float volume) {
        byte[] result = new byte[samples.length];
        for (int i = 0; i < samples.length; i += 2) {
            short sample = (short) ((samples[i + 1] << 8) | (samples[i] & 0xFF));
            sample = (short) Math.max(-32768, Math.min(32767, sample * volume));
            result[i] = (byte) sample;
            result[i + 1] = (byte) (sample >> 8);
        }
        return result;
    }

    public void removePlayer(UUID playerId) {
        SourceDataLine line = playerLines.remove(playerId);
        if (line != null) {
            line.stop();
            line.close();
        }
    }
}
