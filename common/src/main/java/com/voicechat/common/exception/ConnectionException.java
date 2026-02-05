package com.voicechat.common.exception;

public class ConnectionException extends VoiceChatException {

    public ConnectionException(String message) {
        super(message);
    }

    public ConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
