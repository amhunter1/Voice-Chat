package com.voicechat.client.audio.input;

import com.voicechat.client.VoiceChatClientMod;
import com.voicechat.client.audio.codec.OpusEncoder;
import com.voicechat.common.audio.AudioConstants;
import com.voicechat.common.network.packet.ActivationMode;

import javax.sound.sampled.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MicrophoneCapture {

    private final VoiceChatClientMod mod;
    private final OpusEncoder encoder;
    private TargetDataLine microphone;
    private ExecutorService executor;
    private volatile boolean running = false;

    public MicrophoneCapture(VoiceChatClientMod mod) {
        this.mod = mod;
        this.encoder = new OpusEncoder();
    }

    public void start() {
        try {
            AudioFormat format = new AudioFormat(AudioConstants.SAMPLE_RATE, 16, AudioConstants.CHANNELS, true, false);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            microphone = (TargetDataLine) AudioSystem.getLine(info);
            microphone.open(format);
            microphone.start();

            running = true;
            executor = Executors.newSingleThreadExecutor();
            executor.submit(this::captureLoop);
        } catch (LineUnavailableException ignored) {
        }
    }

    private void captureLoop() {
        byte[] buffer = new byte[AudioConstants.FRAME_SIZE * 2];

        while (running) {
            int read = microphone.read(buffer, 0, buffer.length);
            if (read > 0) {
                boolean shouldTransmit = false;

                if (mod.getConfigManager().getConfig().getActivationMode() == ActivationMode.PUSH_TO_TALK) {
                    shouldTransmit = mod.isTalking();
                } else {
                    shouldTransmit = detectVoice(buffer);
                }

                if (shouldTransmit) {
                    byte[] encoded = encoder.encode(buffer);
                    if (encoded != null) {
                        mod.getVoiceClient().sendVoiceData(encoded);
                    }
                }
            }
        }
    }

    private boolean detectVoice(byte[] samples) {
        float threshold = mod.getConfigManager().getConfig().getVoiceActivationThreshold();
        double sum = 0;
        for (int i = 0; i < samples.length; i += 2) {
            short sample = (short) ((samples[i + 1] << 8) | (samples[i] & 0xFF));
            sum += Math.abs(sample);
        }
        double avg = sum / (samples.length / 2);
        return (avg / 32768.0) > threshold;
    }

    public void stop() {
        running = false;
        if (microphone != null) {
            microphone.stop();
            microphone.close();
        }
        if (executor != null)
            executor.shutdown();
    }
}
