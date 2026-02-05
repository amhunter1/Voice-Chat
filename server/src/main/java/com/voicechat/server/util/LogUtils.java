package com.voicechat.server.util;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LogUtils {

    private static Logger logger;

    public static void init(Logger log) {
        logger = log;
    }

    public static void info(String message) {
        if (logger != null)
            logger.log(Level.INFO, "[VoiceChat] " + message);
    }

    public static void warn(String message) {
        if (logger != null)
            logger.log(Level.WARNING, "[VoiceChat] " + message);
    }

    public static void error(String message) {
        if (logger != null)
            logger.log(Level.SEVERE, "[VoiceChat] " + message);
    }

    public static void debug(String message) {
        if (logger != null)
            logger.log(Level.INFO, "[VoiceChat] [DEBUG] " + message);
    }
}
