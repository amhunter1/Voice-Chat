package com.voicechat.client.audio.input;

import com.voicechat.client.VoiceChatClientMod;
import com.voicechat.client.audio.codec.VoiceOpusEncoder;
import com.voicechat.common.audio.AudioConstants;
import com.voicechat.common.network.packet.ActivationMode;

import javax.sound.sampled.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MicrophoneCapture {

    private static final Logger LOGGER = Logger.getLogger(MicrophoneCapture.class.getName());

    private final VoiceChatClientMod mod;
    private VoiceOpusEncoder encoder;
    private TargetDataLine microphone;
    private ExecutorService executor;
    private volatile boolean running = false;

    public MicrophoneCapture(VoiceChatClientMod mod) {
        this.mod = mod;
        this.encoder = new VoiceOpusEncoder();
    }

    public void start() {
        if (encoder == null) {
            LOGGER.severe("Cannot start microphone capture: encoder not initialized");
            return;
        }

        try {
            AudioFormat format = new AudioFormat(
                    AudioConstants.SAMPLE_RATE,
                    16, // 16-bit
                    AudioConstants.CHANNELS,
                    true, // signed
                    false // little-endian
            );

            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

            if (!AudioSystem.isLineSupported(info)) {
                LOGGER.severe("Microphone line not supported");
                return;
            }

            microphone = (TargetDataLine) AudioSystem.getLine(info);
            microphone.open(format);
            microphone.start();

            running = true;
            executor = Executors.newSingleThreadExecutor(r -> {
                Thread t = new Thread(r, "MicrophoneCapture");
                t.setDaemon(true);
                return t;
            });
            executor.submit(this::captureLoop);

            LOGGER.info("Microphone capture started");
        } catch (LineUnavailableException e) {
            LOGGER.log(Level.SEVERE, "Failed to open microphone", e);
        }
    }

    private void captureLoop() {
        byte[] buffer = new byte[encoder.getFrameSizeBytes()];

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

    /**
     * Detect voice activity using simple amplitude threshold
     */
    private boolean detectVoice(byte[] samples) {
        float threshold = mod.getConfigManager().getConfig().getVoiceActivationThreshold();

        double sum = 0;
        for (int i = 0; i < samples.length; i += 2) {
            // Convert little-endian 16-bit samples to short
            short sample = (short) ((samples[i + 1] << 8) | (samples[i] & 0xFF));
            sum += Math.abs(sample);
        }

        double avg = sum / (samples.length / 2);
        double normalized = avg / 32768.0; // Normalize to 0-1 range

        return normalized > threshold;
    }

    public void stop() {
        running = false;

        if (microphone != null && microphone.isOpen()) {
            microphone.stop();
            microphone.close();
        }

        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }

        LOGGER.info("Microphone capture stopped");
    }

    public boolean isRunning() {
        return running;
    }

    /**
     * Get available microphone devices
     */
    public static Mixer.Info[] getAvailableMicrophones() {
        return AudioSystem.getMixerInfo();
    }
}
