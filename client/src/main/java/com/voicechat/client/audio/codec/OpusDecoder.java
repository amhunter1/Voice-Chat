package com.voicechat.client.audio.codec;

import com.voicechat.common.audio.AudioConstants;

/**
 * Opus decoder stub - TODO: Implement with actual Opus codec library
 * Currently returns uncompressed PCM data
 */
public class OpusDecoder {

    public OpusDecoder() {
        // Stub constructor - TODO: Initialize actual Opus decoder
    }

    public byte[] decode(byte[] opusData) {
        if (opusData == null || opusData.length == 0) {
            return null;
        }

        // TODO: Implement actual Opus decoding
        // For now, return the data as-is (assuming uncompressed PCM)
        // This is a temporary solution until we integrate a proper Opus library
        return opusData;
    }
}
