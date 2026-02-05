package com.voicechat.common.exception;

public class VoiceChatException extends Exception {

    public VoiceChatException(String message) {
        super(message);
    }

    public VoiceChatException(String message, Throwable cause) {
        super(message, cause);
    }
}
