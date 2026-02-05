package com.voicechat.client.util;

public class KeyBindingUtils {

    public static String getKeyName(int keyCode) {
        return switch (keyCode) {
            case 86 -> "V";
            case 66 -> "B";
            case 77 -> "M";
            case 78 -> "N";
            case 71 -> "G";
            case 16 -> "Shift";
            case 17 -> "Ctrl";
            case 18 -> "Alt";
            default -> "Key" + keyCode;
        };
    }
}
