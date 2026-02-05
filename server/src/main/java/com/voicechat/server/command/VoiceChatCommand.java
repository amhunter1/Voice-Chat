package com.voicechat.server.command;

import com.voicechat.server.VoiceChatPlugin;
import java.util.UUID;

public class VoiceChatCommand {

    private final VoiceChatPlugin plugin;

    public VoiceChatCommand(VoiceChatPlugin plugin) {
        this.plugin = plugin;
    }

    public void execute(UUID senderId, String[] args) {
        if (args.length == 0) {
            sendHelp(senderId);
            return;
        }

        switch (args[0].toLowerCase()) {
            case "mute" -> handleMute(senderId, args);
            case "unmute" -> handleUnmute(senderId, args);
            case "reload" -> handleReload(senderId);
            default -> sendHelp(senderId);
        }
    }

    private void handleMute(UUID senderId, String[] args) {
        if (args.length < 2) {
            sendMessage(senderId, "&cKullanim: /voicechat mute <oyuncu>");
            return;
        }
        UUID targetId = getPlayerByName(args[1]);
        if (targetId == null) {
            sendMessage(senderId, "&cOyuncu bulunamadi!");
            return;
        }
        plugin.getPlayerManager().getState(senderId).mutePlayer(targetId);
        sendMessage(senderId, "&aOyuncu sessize alindi!");
    }

    private void handleUnmute(UUID senderId, String[] args) {
        if (args.length < 2) {
            sendMessage(senderId, "&cKullanim: /voicechat unmute <oyuncu>");
            return;
        }
        UUID targetId = getPlayerByName(args[1]);
        if (targetId == null) {
            sendMessage(senderId, "&cOyuncu bulunamadi!");
            return;
        }
        plugin.getPlayerManager().getState(senderId).unmutePlayer(targetId);
        sendMessage(senderId, "&aOyuncu sesi acildi!");
    }

    private void handleReload(UUID senderId) {
        // TODO: Check permission
        sendMessage(senderId, "&aYapilandirma yeniden yuklendi!");
    }

    private void sendHelp(UUID playerId) {
        sendMessage(playerId, "&6VoiceChat Komutlari:");
        sendMessage(playerId, "&e/voicechat mute <oyuncu> &7- Oyuncuyu sessize al");
        sendMessage(playerId, "&e/voicechat unmute <oyuncu> &7- Sessizi kaldir");
    }

    private UUID getPlayerByName(String name) {
        // TODO: Lookup via Hytale API
        return null;
    }

    private void sendMessage(UUID playerId, String message) {
        // TODO: Send via Hytale API
    }
}
