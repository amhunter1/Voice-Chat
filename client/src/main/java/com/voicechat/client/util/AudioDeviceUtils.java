package com.voicechat.client.util;

import javax.sound.sampled.*;
import java.util.ArrayList;
import java.util.List;

public class AudioDeviceUtils {

    public static List<String> getAvailableMicrophones() {
        List<String> devices = new ArrayList<>();
        Mixer.Info[] mixers = AudioSystem.getMixerInfo();

        for (Mixer.Info info : mixers) {
            Mixer mixer = AudioSystem.getMixer(info);
            Line.Info[] lineInfos = mixer.getTargetLineInfo();
            for (Line.Info lineInfo : lineInfos) {
                if (lineInfo instanceof DataLine.Info) {
                    devices.add(info.getName());
                    break;
                }
            }
        }
        return devices;
    }

    public static TargetDataLine getMicrophone(String name, AudioFormat format) throws LineUnavailableException {
        Mixer.Info[] mixers = AudioSystem.getMixerInfo();

        for (Mixer.Info info : mixers) {
            if (info.getName().equals(name)) {
                Mixer mixer = AudioSystem.getMixer(info);
                DataLine.Info lineInfo = new DataLine.Info(TargetDataLine.class, format);
                if (mixer.isLineSupported(lineInfo)) {
                    return (TargetDataLine) mixer.getLine(lineInfo);
                }
            }
        }

        DataLine.Info lineInfo = new DataLine.Info(TargetDataLine.class, format);
        return (TargetDataLine) AudioSystem.getLine(lineInfo);
    }
}
