package com.voicechat.server.task;

import com.voicechat.server.VoiceChatPlugin;
import com.voicechat.server.network.connection.ConnectionManager;

public class KeepAliveTask extends ScheduledTask {

    public KeepAliveTask(VoiceChatPlugin plugin) {
        super(plugin, 5000);
    }

    @Override
    public void run() {
        // TODO: Send keep-alive packets to all connected clients
    }
}
