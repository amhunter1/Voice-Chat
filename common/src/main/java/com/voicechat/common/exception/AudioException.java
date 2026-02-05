package com.voicechat.common.exception;

public class AudioException extends VoiceChatException {

    public AudioException(String message) {
        super(message);
    }

    public AudioException(String message, Throwable cause) {
        super(message, cause);
    }
}
