package com.voicechat.server.command;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.voicechat.server.VoiceChatPlugin;

import javax.annotation.Nonnull;
import java.util.UUID;

public class VoiceChatCommand extends CommandBase {

    private final VoiceChatPlugin plugin;

    public VoiceChatCommand(VoiceChatPlugin plugin) {
        super("voicechat", "Voice chat plugin commands");
        this.plugin = plugin;
    }

    @Override
    protected void executeSync(@Nonnull CommandContext ctx) {
        String[] args = ctx.getArgs();
        UUID senderId = ctx.getPlayer().getUniqueId();

        if (args.length == 0) {
            sendHelp(ctx);
            return;
        }

        switch (args[0].toLowerCase()) {
            case "mute" -> handleMute(ctx, senderId, args);
            case "unmute" -> handleUnmute(ctx, senderId, args);
            case "reload" -> handleReload(ctx, senderId);
            default -> sendHelp(ctx);
        }
    }

    private void handleMute(CommandContext ctx, UUID senderId, String[] args) {
        if (args.length < 2) {
            ctx.sendMessage(Message.raw("§cUsage: /voicechat mute <player>"));
            return;
        }

        UUID targetId = getPlayerByName(args[1]);
        if (targetId == null) {
            ctx.sendMessage(Message.raw("§cPlayer not found!"));
            return;
        }

        plugin.getPlayerManager().getState(senderId).mutePlayer(targetId);
        ctx.sendMessage(Message.raw("§aPlayer muted!"));
    }

    private void handleUnmute(CommandContext ctx, UUID senderId, String[] args) {
        if (args.length < 2) {
            ctx.sendMessage(Message.raw("§cUsage: /voicechat unmute <player>"));
            return;
        }

        UUID targetId = getPlayerByName(args[1]);
        if (targetId == null) {
            ctx.sendMessage(Message.raw("§cPlayer not found!"));
            return;
        }

        plugin.getPlayerManager().getState(senderId).unmutePlayer(targetId);
        ctx.sendMessage(Message.raw("§aPlayer unmuted!"));
    }

    private void handleReload(CommandContext ctx, UUID senderId) {
        // TODO: Check permission
        try {
            plugin.getConfig(); // Reload config
            ctx.sendMessage(Message.raw("§aConfiguration reloaded!"));
        } catch (Exception e) {
            ctx.sendMessage(Message.raw("§cFailed to reload configuration!"));
        }
    }

    private void sendHelp(CommandContext ctx) {
        ctx.sendMessage(Message.raw("§6VoiceChat Commands:"));
        ctx.sendMessage(Message.raw("§e/voicechat mute <player> §7- Mute a player"));
        ctx.sendMessage(Message.raw("§e/voicechat unmute <player> §7- Unmute a player"));
        ctx.sendMessage(Message.raw("§e/voicechat reload §7- Reload configuration"));
    }

    private UUID getPlayerByName(String name) {
        // TODO: Implement player lookup via Hytale API
        // For now, return null - this will be implemented when we have access to the server instance
        return null;
    }
}
