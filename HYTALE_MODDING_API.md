# Hytale Modding API Documentation

## 🎮 Hytale Client Information

### Version
- **Client Version**: `v2026.01.28-87d03be09`
- **Platform**: Windows x64 Release
- **.NET Version**: .NET 10.0.2
- **Revision**: `87d03be09c7f114f385971a3caa437033671b771`
- **Environment**: release (hytale.com)
- **Patchline**: release

### System Architecture
- **Language**: C# (.NET 10)
- **UI Framework**: Noesis v3.2.10 (XAML-based)
- **Audio**: OpenAL 1.1 ALSOFT 1.24.3
- **Graphics**: OpenGL 3.3
- **Networking**: QUIC Protocol (msquic.dll)

---

## 📁 Mod System

### Mod Directory
```
C:\Users\Admin\AppData\Roaming\Hytale\UserData\Mods\
```

### Mod UI Component
- **File**: `Data/Game/Interface/MainMenu/Adventure/ModItem.ui`
- **Format**: Custom UI format (similar to XAML)
- **Features**:
  - Mod name display
  - Mod type display
  - Mod details
  - Enable/disable checkbox

### Mod Structure (Inferred)
```
Mods/
├── ModName/
│   ├── manifest.json (likely)
│   ├── assets/
│   └── scripts/
```

---

## 🔌 Key Namespaces (From Logs)

### 1. **HytaleClient.Application**
- `HytaleClient.Application.Program` - Main program entry
- `HytaleClient.Application.AppGameLoading` - Game loading logic
- `HytaleClient.Application.AppStartup` - Startup logic

### 2. **HytaleClient.Networking**
- `HytaleClient.Networking.PacketHandler` - Network packet handling
- **Packet Types**:
  - `AuthGrant` - Authentication grant
  - `AuthToken` - Authentication token
  - `ServerAuthToken` - Server authentication
  - `RequiredAssets` - Asset requirements
  - `JoinWorldPacket` - World join
  - `Equipment` - Equipment updates

### 3. **HytaleClient.Interface**
- `HytaleClient.Interface.MainMenu.Pages.ServersPage` - Server list
- `HytaleClient.Interface.UI.Elements.BaseButton` - UI buttons

### 4. **HytaleClient.Utils**
- `HytaleClient.Utils.SentryHelper` - Session management

---

## 🎨 Asset System

### Asset Types (From Logs)
```csharp
- BlockSets
- Weather
- BlockTypes
- Items
- AmbienceFX
- EntityStatTypes
- BlockHitboxes
- ResourceTypes
- EqualizerEffects
- ReverbEffects
- TagPatterns
- Fluids
- ItemQuality
- ItemCategories
- BlockParticleSets
- ItemReticles
- EntityEffects
- ParticleSpawners
- Environment
- SoundEvents
- SoundSets
- BlockSoundSets
- EntityUIComponents
- AudioCategories
- Interactions
- RepulsionConfig
- ItemSoundSets
- FluidFX
- ItemAnimations
- UnarmedInteractions
- ParticleSystems
- FieldcraftCategories
- BlockBreakingDecals
- Recipes
- RootInteractions
- Trails
- HitboxCollisionConfig
- ModelVFX
- Translations
```

### Asset Loading Pipeline
1. `RequiredAssets` packet received
2. Individual asset types loaded sequentially
3. Asset preparation phase:
   - `PrepareAmbienceFX`
   - `PrepareRootInterations`
   - `PrepareInterations`
   - `PrepareBlockOverlayAtlas`
   - `PrepareWorldMap`
   - `PrepareParticles`
   - `PrepareItemAnimations`
   - `PrepareItems`
   - `PrepareBlockTypes`
   - `PrepareEntitiesAtlas`

---

## 🔊 Audio System

### OpenAL Configuration
```
OpenAL : 1.1 ALSOFT 1.24.3
Renderer : OpenAL Soft
Vendor : OpenAL Community
Output mode : Stereo
Output limiter : Enabled
Frequency : 48000 Hz
Max sources : 255 mono, 1 stereo
HRTF status : Disabled
Gain limit : 1000.00 (60.0 dB)
```

### Audio Asset Types
- `SoundEvents` - Sound event definitions
- `SoundSets` - Sound set collections
- `BlockSoundSets` - Block-specific sounds
- `ItemSoundSets` - Item-specific sounds
- `AudioCategories` - Audio category definitions
- `EqualizerEffects` - Equalizer effects
- `ReverbEffects` - Reverb effects

### Voice Chat Integration Points
1. **OpenAL Sources**: 255 mono sources available
2. **Sample Rate**: 48000 Hz (perfect for voice)
3. **Audio Categories**: Can create custom category for voice chat
4. **3D Audio**: HRTF support (currently disabled, can be enabled)

---

## 🌐 Network System

### QUIC Protocol
```
- mTLS (mutual TLS) authentication
- Client certificate fingerprinting
- JWT access tokens
- Ed25519 signature verification
- Certificate binding validation
```

### Connection Flow
1. Generate self-signed certificate
2. Open QUIC connection
3. mTLS handshake
4. Request auth grant
5. Exchange grant for access token
6. Send AuthToken to server
7. Receive ServerAuthToken
8. Mutual authentication complete

### Network Stages
```csharp
enum NetworkStage {
    WaitingForSetup,
    SettingUp,
    Playing
}
```

---

## 🎯 Voice Chat Mod Strategy

### Approach 1: Native Integration (RECOMMENDED)
Create a C# mod that integrates directly with Hytale's systems:

```csharp
// VoiceChatMod/manifest.json
{
  "name": "Voice Chat",
  "version": "1.0.0",
  "author": "YourName",
  "description": "Proximity voice chat for Hytale",
  "type": "ClientMod"
}

// VoiceChatMod/VoiceChatMod.cs
using HytaleClient.Application;
using HytaleClient.Networking;
using System;

namespace VoiceChatMod
{
    public class VoiceChatMod : IHytaleMod
    {
        private VoiceClient voiceClient;
        
        public void OnLoad()
        {
            // Initialize voice chat
            voiceClient = new VoiceClient();
            voiceClient.Connect("localhost", 24454);
        }
        
        public void OnUpdate()
        {
            // Update voice chat
            voiceClient.Update();
        }
        
        public void OnUnload()
        {
            voiceClient.Disconnect();
        }
    }
}
```

### Approach 2: External Process Bridge
Java voice chat client communicates with Hytale via:
- **Named Pipes** (Windows)
- **Shared Memory**
- **TCP Socket** (localhost)

```csharp
// HytaleBridge.cs
using System.IO.Pipes;

public class HytaleBridge
{
    private NamedPipeServerStream pipeServer;
    
    public void Start()
    {
        pipeServer = new NamedPipeServerStream("HytaleVoiceChat");
        pipeServer.WaitForConnection();
        
        // Receive player positions from Java client
        // Send voice data to Java client
    }
}
```

---

## 📝 Modding API (Inferred)

### IHytaleMod Interface (Likely)
```csharp
public interface IHytaleMod
{
    string Name { get; }
    string Version { get; }
    string Author { get; }
    
    void OnLoad();
    void OnUpdate();
    void OnUnload();
}
```

### Event System (Likely)
```csharp
// From log: "No interface-side handler for engine event: settings.windowSizeChanged"
public interface IEventHandler
{
    void OnEvent(string eventName, object data);
}
```

---

## 🚀 Next Steps

### 1. Create C# Voice Chat Mod
- [ ] Create mod manifest
- [ ] Implement IHytaleMod interface
- [ ] Integrate with OpenAL for audio
- [ ] Connect to Java voice server

### 2. Test Mod Loading
- [ ] Place mod in `UserData/Mods/`
- [ ] Launch Hytale
- [ ] Check logs for mod loading
- [ ] Verify mod appears in mod list

### 3. Implement Voice Features
- [ ] Microphone capture (OpenAL)
- [ ] Voice encoding (Opus)
- [ ] Network transmission (QUIC or TCP)
- [ ] Voice playback (OpenAL 3D audio)
- [ ] Proximity calculation

### 4. Bridge with Java Server
- [ ] Named pipe communication
- [ ] Player position sync
- [ ] Voice data transfer
- [ ] Server authentication

---

## ⚠️ Important Notes

1. **Mod API**: Hytale has a mod system, but the exact API is not publicly documented yet.

2. **Reverse Engineering**: We can infer the API structure from logs and UI files.

3. **C# Required**: Mods must be written in C# (.NET 10) to integrate with Hytale.

4. **OpenAL Integration**: Voice chat can use Hytale's existing OpenAL system.

5. **QUIC Protocol**: Network communication uses QUIC, which is perfect for voice chat (low latency, UDP-based).

---

## 🔗 Resources

- **Hytale Client**: `C:\Users\Admin\AppData\Roaming\Hytale\install\release\package\game\latest\Client\`
- **Mod Directory**: `C:\Users\Admin\AppData\Roaming\Hytale\UserData\Mods\`
- **Logs**: `C:\Users\Admin\AppData\Roaming\Hytale\UserData\Logs\`
- **Assets**: `C:\Users\Admin\AppData\Roaming\Hytale\install\release\package\game\latest\Assets.zip`

---

**Last Updated**: 2026-02-07  
**Hytale Version**: v2026.01.28-87d03be09  
**Status**: Ready for C# Mod Development 🚀
