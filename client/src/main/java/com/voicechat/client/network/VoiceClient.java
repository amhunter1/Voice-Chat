package com.voicechat.client.network;

import com.voicechat.client.VoiceChatClientMod;
import com.voicechat.common.network.packet.VoiceBroadcastPacket;
import com.voicechat.common.network.packet.VoiceMode;
import com.voicechat.common.network.packet.VoicePacket;
import com.voicechat.common.network.serialization.PacketDeserializer;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VoiceClient {

    private static final Logger LOGGER = Logger.getLogger(VoiceClient.class.getName());

    private final VoiceChatClientMod mod;
    private DatagramSocket socket;
    private InetAddress serverAddress;
    private int serverPort = 24454;
    private ExecutorService executor;
    private volatile boolean connected = false;
    private VoiceMode currentMode = VoiceMode.NORMAL;
    private UUID localPlayerId;

    public VoiceClient(VoiceChatClientMod mod) {
        this.mod = mod;
        // TODO: Get from Hytale client API
        this.localPlayerId = UUID.randomUUID();
    }

    public void connect(String address) {
        try {
            String[] parts = address.split(":");
            serverAddress = InetAddress.getByName(parts[0]);
            if (parts.length > 1)
                serverPort = Integer.parseInt(parts[1]);

            socket = new DatagramSocket();
            socket.setSoTimeout(5000); // 5 second timeout
            connected = true;

            executor = Executors.newSingleThreadExecutor(r -> {
                Thread t = new Thread(r, "VoiceClient-Receiver");
                t.setDaemon(true);
                return t;
            });
            executor.submit(this::receiveLoop);

            LOGGER.info("Connected to voice server at " + address);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to connect to voice server", e);
        }
    }

    public void disconnect() {
        connected = false;
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
        LOGGER.info("Disconnected from voice server");
    }

    public void sendVoiceData(byte[] opusData) {
        if (!connected || socket == null || socket.isClosed())
            return;

        try {
            VoicePacket packet = new VoicePacket(localPlayerId, opusData, currentMode, System.currentTimeMillis());
            byte[] data = packet.serialize();
            socket.send(new DatagramPacket(data, data.length, serverAddress, serverPort));
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to send voice data", e);
        }
    }

    private void receiveLoop() {
        byte[] buffer = new byte[2048]; // Increased buffer size

        while (connected) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                byte[] data = Arrays.copyOf(packet.getData(), packet.getLength());
                VoiceBroadcastPacket broadcast = (VoiceBroadcastPacket) PacketDeserializer.deserialize(data);

                if (broadcast != null && mod.getAudioPlayback() != null) {
                    mod.getAudioPlayback().playVoice(broadcast);
                }
            } catch (SocketTimeoutException e) {
                // Normal timeout, continue
            } catch (IOException e) {
                if (connected) {
                    LOGGER.log(Level.WARNING, "Error receiving voice data", e);
                }
            }
        }
    }

    public void setVoiceMode(VoiceMode mode) {
        this.currentMode = mode;
        LOGGER.info("Voice mode changed to: " + mode);
    }

    public VoiceMode getVoiceMode() {
        return currentMode;
    }

    public boolean isConnected() {
        return connected;
    }

    public UUID getLocalPlayerId() {
        return localPlayerId;
    }

    public void setLocalPlayerId(UUID playerId) {
        this.localPlayerId = playerId;
    }
}
