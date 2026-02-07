# Hytale Modding API Research Summary

**Research Date:** February 7, 2026  
**Source:** Official Hytale Blog - "Hytale Modding Strategy and Status" (November 20, 2025)

---

## Executive Summary

Hytale's modding approach is **server-side first**, meaning mods run on the server and no client-side modifications are required. This is a fundamental architectural decision that impacts how our voice chat mod should be designed.

---

## Key Findings

### 1. **Server-Side Plugin Architecture**

- **Primary Modding Method:** Java-based server plugins (`.jar` files)
- **No Client Mods Required:** Players don't need to install anything to join modded servers
- **Language:** Java (same as our current implementation)
- **Distribution:** Plugins are `.jar` files loaded by the server

### 2. **Four Content Categories**

Hytale supports four major types of content:

1. **Server Plugins** (Java `.jar` files)
   - Core modding mechanism
   - Full server-side logic control
   - Event-driven architecture
   
2. **Data Files** (JSON/configuration)
   - Game data customization
   - Configuration-driven design
   
3. **Art Assets** (Models, textures, animations)
   - Blockbench support
   - Custom visual content
   
4. **Save Files** (World data)
   - Custom world generation
   - Persistent data storage

### 3. **Visual Scripting System**

- **No Text-Based Scripting:** Hytale explicitly chose NOT to use Lua or similar scripting languages
- **Visual Scripting Instead:** Similar to Unreal Engine Blueprints
- **Reasoning:** More accessible to non-programmers, easier to debug
- **Target Audience:** Content creators, not just programmers

### 4. **Development Tools**

- **GitBook Documentation:** Official API documentation being developed
- **Server API:** Comprehensive Java API for server plugins
- **Blockbench Integration:** For 3D models and animations
- **Visual Scripting Editor:** For game logic without code

### 5. **Plugin Lifecycle**

Based on the blog post, server plugins follow a standard lifecycle:

- `JavaPlugin` base class
- `setup()` - Initialization
- `start()` - Server start
- `shutdown()` - Cleanup

---

## Implications for Voice Chat Mod

### ✅ **What Works Well**

1. **Java-Based:** Our current Java implementation is perfect
2. **Server Plugin:** Our server module architecture aligns with Hytale's approach
3. **No Client Mods:** This is actually a challenge (see below)

### ⚠️ **Challenges**

1. **Client-Side Audio Capture**
   - Hytale's "no client mods" philosophy conflicts with voice chat requirements
   - Voice chat inherently needs client-side microphone access
   - **Possible Solutions:**
     - Standalone client application (our current approach)
     - Browser-based WebRTC client
     - Wait for official client API (if ever released)

2. **Player Position Tracking**
   - Server plugin can track player positions ✅
   - Can send position data to voice chat server ✅
   - Proximity calculations can be server-side ✅

3. **Network Communication**
   - Server plugin can open UDP sockets for voice data ✅
   - Can integrate with our existing UDP voice server ✅

### 🎯 **Recommended Architecture**

Based on Hytale's modding philosophy, our voice chat system should be:

```
┌─────────────────────────────────────────┐
│         Hytale Game Client              │
│  (No modifications - vanilla client)    │
└─────────────────┬───────────────────────┘
                  │
                  │ Game Connection
                  │
┌─────────────────▼───────────────────────┐
│       Hytale Server + Plugin            │
│  ┌────────────────────────────────────┐ │
│  │  Voice Chat Server Plugin (.jar)   │ │
│  │  - Player position tracking        │ │
│  │  - Proximity calculations          │ │
│  │  - Voice server management         │ │
│  └────────────┬───────────────────────┘ │
└───────────────┼─────────────────────────┘
                │
                │ UDP Voice Data
                │
┌───────────────▼─────────────────────────┐
│    Standalone Voice Chat Client         │
│  - Microphone capture                   │
│  - Audio playback                       │
│  - Opus encoding/decoding               │
│  - Connects to voice server             │
└─────────────────────────────────────────┘
```

---

## API Components (Based on Research)

### Server Plugin API

```java
// Plugin lifecycle
public class VoiceChatPlugin extends JavaPlugin {
    @Override
    public void setup() {
        // Initialize voice server
    }
    
    @Override
    public void start() {
        // Start voice server
    }
    
    @Override
    public void shutdown() {
        // Cleanup
    }
}
```

### Expected API Features

Based on standard server plugin patterns:

1. **Player Management**
   - Player join/leave events
   - Player position tracking
   - Player UUID/identification

2. **World Management**
   - World/dimension information
   - Entity tracking
   - Distance calculations

3. **Event System**
   - Event registration
   - Custom event firing
   - Event priorities

4. **Network API**
   - Custom packet handling (maybe)
   - Server-to-client communication
   - Plugin messaging channels

5. **Configuration**
   - Config file management
   - Data persistence
   - Settings synchronization

---

## Current Implementation Status

### ✅ **Completed**

- [x] Java-based server plugin structure
- [x] Standalone client application
- [x] UDP network layer
- [x] Opus codec integration
- [x] Player position tracking (basic)
- [x] Proximity voice chat logic

### 🔄 **Needs Adaptation**

- [ ] Hytale-specific plugin integration
- [ ] Hytale Player API usage
- [ ] Hytale World API usage
- [ ] Hytale Event system integration

### ⏳ **Waiting for Official Release**

- [ ] Official Hytale Server API documentation
- [ ] HytaleServer.jar with full API
- [ ] Plugin loading mechanism details
- [ ] Network protocol specifications

---

## Next Steps

### Immediate Actions

1. **Monitor Hytale Development**
   - Watch for official API documentation releases
   - Join Hytale modding community
   - Track GitBook documentation updates

2. **Prepare Plugin Structure**
   - Keep server plugin modular
   - Abstract Hytale-specific APIs
   - Create adapter layer for easy integration

3. **Test with Available Tools**
   - Use any available Hytale server builds
   - Test plugin loading mechanism
   - Verify Java compatibility

### Long-Term Strategy

1. **Maintain Dual Architecture**
   - Keep standalone client approach
   - Prepare for potential official client API
   - Support both modes if possible

2. **Community Engagement**
   - Share progress with Hytale community
   - Gather feedback on voice chat needs
   - Collaborate with other modders

3. **Documentation**
   - Document integration process
   - Create setup guides
   - Provide troubleshooting resources

---

## Technical Specifications

### Hytale Requirements

- **Java Version:** TBD (likely Java 17+)
- **Plugin Format:** `.jar` file
- **Plugin Descriptor:** `manifest.json` or similar
- **Base Class:** `JavaPlugin` or equivalent
- **API Package:** `com.hypixel.hytale.api.*` (estimated)

### Our Implementation

- **Java Version:** 17+ (compatible)
- **Build System:** Gradle (standard)
- **Module Structure:** Multi-module (server/client/common)
- **Network Protocol:** UDP (custom)
- **Audio Codec:** Opus (industry standard)

---

## Resources

### Official Sources

- **Hytale Blog:** https://hytale.com/news
- **Modding Strategy Post:** November 20, 2025
- **GitBook Documentation:** (In development)

### Community Resources

- **Reddit:** r/HytaleInfo
- **Discord:** Hytale Official Discord
- **GitHub:** Community-maintained API references (when available)

### Related Technologies

- **JavaPlugin Pattern:** Standard Minecraft/Bukkit pattern
- **Opus Codec:** https://opus-codec.org/
- **WebRTC:** Potential browser-based client alternative
- **LWJGL:** OpenAL bindings for audio

---

## Conclusion

Hytale's server-side first modding approach is both an opportunity and a challenge for voice chat:

**Opportunities:**
- Java-based plugins align with our implementation
- Server-side position tracking is fully supported
- No client installation burden for players

**Challenges:**
- Voice chat requires client-side audio capture
- Standalone client application is necessary
- Integration complexity with vanilla client

**Recommendation:**
Continue with our current dual-architecture approach (server plugin + standalone client) while monitoring Hytale's official API releases. This provides maximum flexibility and ensures we can adapt to any future changes in Hytale's modding ecosystem.

---

**Last Updated:** February 7, 2026  
**Status:** Research Complete - Awaiting Official API Release
