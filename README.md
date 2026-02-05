# Hytale Voice Chat Plugin

A proximity-based voice chat plugin for Hytale with 3D spatial audio, occlusion, and Opus codec support.

## Features

### Core Features
- **Proximity-based voice chat** - Voice volume decreases with distance
- **3D Spatial audio** - Directional audio based on player position
- **Opus codec** - High-quality, low-latency audio compression
- **Multiple voice modes** - Normal, whisper, and shout modes
- **Occlusion system** - Voice muffling through walls and blocks

### Security
- **Rate limiting** - Prevents packet flooding attacks
- **Automatic banning** - Temporarily bans abusive clients
- **Packet validation** - Size and format validation

### Configuration
- **JSON-based config** - Easy to read and modify
- **Hot-reload support** - Changes apply without restart
- **Per-player settings** - Individual volume, mute, deafen controls

## Architecture

### Modules
- **common** - Shared code between client and server
- **server** - Server-side plugin for Hytale
- **client** - Client-side mod for Hytale

### Key Components

#### Server
- `VoiceServer` - UDP server for receiving voice packets
- `VoicePacketRouter` - Routes packets to nearby players
- `PlayerVoiceManager` - Manages player voice states
- `OcclusionEngine` - Calculates voice occlusion
- `RateLimiter` - Security and rate limiting

#### Client
- `VoiceClient` - UDP client for sending/receiving voice
- `MicrophoneCapture` - Captures audio from microphone
- `AudioPlayback` - Plays received voice audio
- `OpusEncoder/Decoder` - Audio compression

#### Common
- `BasePacket` - Base class for all packets
- `VoicePacket` - Voice data packet
- `VoiceBroadcastPacket` - Broadcast packet with spatial data
- `ProximityCalculator` - Distance-based volume calculation

## Configuration

### Server Config (`plugins/VoiceChat/config.json`)

```json
{
  "network": {
    "voicePort": 24454,
    "maxPacketSize": 2048,
    "keepAliveInterval": 5000
  },
  "audio": {
    "normalDistance": 64,
    "whisperDistance": 8,
    "shoutDistance": 128,
    "fadeStartPercent": 0.5,
    "bitrate": 24000
  },
  "occlusion": {
    "enabled": true,
    "maxBlocksChecked": 16,
    "attenuationPerBlock": 0.15
  },
  "performance": {
    "maxPlayersPerPacket": 50,
    "updateInterval": 50,
    "cacheExpirationMs": 10000
  },
  "security": {
    "enableRateLimiting": true,
    "maxPacketsPerSecond": 100,
    "banDuration": 300000
  }
}
```

### Client Config (`config/voicechat/client.json`)

```json
{
  "audio": {
    "inputVolume": 1.0,
    "outputVolume": 1.0,
    "voiceActivationThreshold": 0.05,
    "microphoneDevice": "default",
    "speakerDevice": "default"
  },
  "activation": {
    "mode": "VOICE_ACTIVATION",
    "pushToTalkKey": "V"
  },
  "ui": {
    "showVoiceIndicator": true,
    "showPlayerNames": true,
    "hudPosition": "TOP_LEFT",
    "hudScale": 1.0
  },
  "network": {
    "serverAddress": "localhost",
    "serverPort": 24454,
    "autoConnect": true
  },
  "advanced": {
    "enableNoiseGate": true,
    "noiseGateThreshold": 0.02,
    "enableEchoCancellation": false,
    "audioBufferSize": 960
  }
}
```

## Building

```bash
# Build all modules
./gradlew build

# Build specific module
./gradlew :server:build
./gradlew :client:build
./gradlew :common:build

# Run tests
./gradlew test
```

## Installation

### Server
1. Copy `server/build/libs/voicechat-server-1.0.0.jar` to `plugins/` folder
2. Start the server
3. Configure `plugins/VoiceChat/config.json` as needed
4. Restart or reload the server

### Client
1. Copy `client/build/libs/voicechat-client-1.0.0.jar` to `mods/` folder
2. Start Hytale
3. Configure settings in-game or edit `config/voicechat/client.json`

## Dependencies

- **Concentus** - Pure Java Opus codec implementation
- **Gson** - JSON serialization
- **Hytale API** - Server and client APIs (when available)

## TODO

- [ ] Hytale API integration (waiting for official API)
- [ ] GUI implementation for client settings
- [ ] Test suite and unit tests
- [ ] Documentation and API docs
- [ ] Group/party voice channels
- [ ] Admin commands (/voicechat mute, /voicechat reload, etc.)
- [ ] Metrics and monitoring
- [ ] Voice recording/playback features

## Technical Details

### Audio Pipeline

**Client (Sending)**
1. Microphone captures PCM audio (48kHz, 16-bit, mono)
2. OpusEncoder compresses to ~24kbps
3. VoicePacket created with compressed data
4. Packet serialized and sent via UDP

**Server (Routing)**
1. VoiceServer receives UDP packet
2. RateLimiter checks for abuse
3. PacketDeserializer deserializes packet
4. VoicePacketRouter calculates proximity
5. OcclusionEngine applies wall muffling
6. VoiceBroadcastPacket sent to nearby players

**Client (Receiving)**
1. VoiceClient receives broadcast packet
2. OpusDecoder decompresses audio
3. Volume applied based on distance
4. AudioPlayback plays through speakers

### Network Protocol

All packets use a simple binary format:
- 1 byte: Packet type ID
- 4 bytes: Packet length
- N bytes: Packet data (serialized with Gson)

### Thread Safety

- All managers use `ConcurrentHashMap` for thread-safe storage
- Volatile flags for state management
- Executor services for async operations
- Defensive copies where needed

### Performance

- UDP for low-latency voice transmission
- Opus codec for efficient compression (24kbps)
- Spatial audio calculations cached
- Rate limiting prevents server overload
- Configurable update intervals

## License

MIT License - See LICENSE file for details

## Credits

- Opus codec: Concentus library
- Inspired by Simple Voice Chat mod for Minecraft
