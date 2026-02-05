package com.voicechat.server.task;

import com.voicechat.server.VoiceChatPlugin;

public class PositionUpdateTask extends ScheduledTask {

    public PositionUpdateTask(VoiceChatPlugin plugin) {
        super(plugin, 50);
    }

    @Override
    public void run() {
        // TODO: Update player positions from Hytale API
    }
}
