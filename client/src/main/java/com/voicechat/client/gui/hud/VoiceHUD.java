package com.voicechat.client.gui.hud;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class VoiceHUD {

    private final Map<UUID, Long> speakingPlayers = new ConcurrentHashMap<>();
    private static final long SPEAKING_TIMEOUT = 500;

    public void onPlayerSpeak(UUID playerId) {
        speakingPlayers.put(playerId, System.currentTimeMillis());
    }

    public void render() {
        long now = System.currentTimeMillis();
        speakingPlayers.entrySet().removeIf(e -> now - e.getValue() > SPEAKING_TIMEOUT);

        // Render current player's microphone status
        // Pseudocode for Hytale rendering
        // HytaleClient.getInstance().getTextureManager().bindTexture(new
        // ResourceLocation("voicechat", "textures/gui/microphone_talking.png"));
        // Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, height,
        // textureWidth, textureHeight);

        // Note: Actual implementation depends on Hytale API availability.
        // Assuming a simpler prompt style for now to show logic.

        // TODO: Replace with actual Hytale Rendering API
        /*
         * boolean talking =
         * com.voicechat.client.VoiceChatClientMod.getInstance().isTalking();
         * boolean muted = false; // Retrieve from config or state
         * 
         * String texture = "microphone_off.png";
         * if (talking) {
         * texture = "microphone_talking.png";
         * } else if (muted) {
         * texture = "microphone_muted.png";
         * }
         * 
         * // Render(texture);
         */
    }

    public boolean isPlayerSpeaking(UUID playerId) {
        Long lastSpeak = speakingPlayers.get(playerId);
        return lastSpeak != null && System.currentTimeMillis() - lastSpeak <= SPEAKING_TIMEOUT;
    }
}
