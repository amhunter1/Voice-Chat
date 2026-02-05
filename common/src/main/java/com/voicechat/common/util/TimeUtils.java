package com.voicechat.common.util;

public class TimeUtils {

    public static String formatDuration(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes % 60, seconds % 60);
        }
        return String.format("%d:%02d", minutes, seconds % 60);
    }

    public static long parseTime(String time) {
        String[] parts = time.split(":");
        if (parts.length == 2) {
            return (Long.parseLong(parts[0]) * 60 + Long.parseLong(parts[1])) * 1000;
        } else if (parts.length == 3) {
            return (Long.parseLong(parts[0]) * 3600 + Long.parseLong(parts[1]) * 60 + Long.parseLong(parts[2])) * 1000;
        }
        return 0;
    }
}
