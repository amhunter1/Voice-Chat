package com.voicechat.server.service;

import com.voicechat.common.api.event.VoiceEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EventService {

    private final List<Consumer<VoiceEvent>> listeners = new ArrayList<>();

    public void register(Consumer<VoiceEvent> listener) {
        listeners.add(listener);
    }

    public void unregister(Consumer<VoiceEvent> listener) {
        listeners.remove(listener);
    }

    public void fire(VoiceEvent event) {
        for (Consumer<VoiceEvent> listener : listeners) {
            if (event.isCancelled())
                break;
            listener.accept(event);
        }
    }

    public void clear() {
        listeners.clear();
    }
}
