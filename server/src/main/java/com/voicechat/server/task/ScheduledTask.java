package com.voicechat.server.task;

import com.voicechat.server.VoiceChatPlugin;

public abstract class ScheduledTask implements Runnable {

    protected final VoiceChatPlugin plugin;
    private final long intervalMs;
    private volatile boolean running = false;
    private Thread thread;

    public ScheduledTask(VoiceChatPlugin plugin, long intervalMs) {
        this.plugin = plugin;
        this.intervalMs = intervalMs;
    }

    public void start() {
        running = true;
        thread = new Thread(() -> {
            while (running) {
                try {
                    run();
                    Thread.sleep(intervalMs);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    public void stop() {
        running = false;
        if (thread != null)
            thread.interrupt();
    }

    public boolean isRunning() {
        return running;
    }
}
