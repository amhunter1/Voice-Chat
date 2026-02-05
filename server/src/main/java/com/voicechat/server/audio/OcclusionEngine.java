package com.voicechat.server.audio;

import com.voicechat.server.VoiceChatPlugin;

public class OcclusionEngine {

    private final VoiceChatPlugin plugin;

    public OcclusionEngine(VoiceChatPlugin plugin) {
        this.plugin = plugin;
    }

    public int countBlockedBlocks(double x1, double y1, double z1, double x2, double y2, double z2) {
        int maxBlocks = plugin.getConfig().getOcclusion().getMaxBlocksChecked();
        int blockedCount = 0;

        double dx = x2 - x1;
        double dy = y2 - y1;
        double dz = z2 - z1;
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

        if (distance == 0)
            return 0;

        double stepX = dx / distance;
        double stepY = dy / distance;
        double stepZ = dz / distance;

        double x = x1, y = y1, z = z1;
        int lastBlockX = Integer.MIN_VALUE, lastBlockY = Integer.MIN_VALUE, lastBlockZ = Integer.MIN_VALUE;

        for (double d = 0; d < distance && blockedCount < maxBlocks; d += 0.5) {
            int blockX = (int) Math.floor(x);
            int blockY = (int) Math.floor(y);
            int blockZ = (int) Math.floor(z);

            if (blockX != lastBlockX || blockY != lastBlockY || blockZ != lastBlockZ) {
                if (isBlockSolid(blockX, blockY, blockZ)) {
                    blockedCount++;
                }
                lastBlockX = blockX;
                lastBlockY = blockY;
                lastBlockZ = blockZ;
            }

            x += stepX * 0.5;
            y += stepY * 0.5;
            z += stepZ * 0.5;
        }

        return blockedCount;
    }

    private boolean isBlockSolid(int x, int y, int z) {
        // TODO: Check via Hytale world API
        return false;
    }
}
