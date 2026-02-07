# Hytale Client Analysis

## 📍 Hytale Client Konumu
```
C:\Users\Admin\AppData\Roaming\Hytale\install\release\package\game\latest\Client\
```

## 📦 Ana Dosyalar

### Executable
- **HytaleClient.exe** - Ana oyun executable (C# .NET)

### Native Libraries
- `libpng16.dll` - PNG image library
- `libsodium.dll` - Cryptography library
- `msquic.dll` - QUIC protocol (Microsoft implementation)
- `Noesis.dll` - UI framework (WPF-like for games)
- `ogg.dll`, `vorbis.dll`, `vorbisfile.dll` - Audio codec libraries
- `OpenAL32.dll` - 3D audio library
- `SDL3.dll`, `SDL3_image.dll` - SDL3 multimedia library
- `wuffs.dll` - Image decoder
- `zlib.dll`, `zstd.dll` - Compression libraries

### Data Directories
- `Data/Game/` - Game assets (UI, textures, configs)
- `Data/Shared/` - Shared resources (fonts, language files)
- `Data/Editor/` - Editor-specific data
- `NodeEditor/` - Visual scripting editor

## 🔍 Teknoloji Stack Analizi

### 1. **UI Framework: Noesis**
Hytale, UI için **Noesis** kullanıyor. Bu, XAML tabanlı bir UI framework'ü (WPF benzeri).
- Website: https://www.noesisengine.com/
- XAML-based UI
- C# API
- Game engine integration

### 2. **Audio System: OpenAL + Vorbis**
- **OpenAL32.dll** - 3D positional audio
- **Vorbis** - Audio codec (Ogg Vorbis format)
- Voice chat için OpenAL API'sini kullanabiliriz

### 3. **Networking: QUIC Protocol**
- **msquic.dll** - Microsoft QUIC implementation
- Modern, UDP-based protocol
- Low latency, multiplexed streams
- Voice chat için ideal

### 4. **Cryptography: libsodium**
- Modern cryptography library
- Encryption, authentication, key exchange
- Voice chat encryption için kullanılabilir

## 🎯 API Extraction Stratejisi

### Adım 1: dnSpy ile Decompile
```bash
# dnSpy indir: https://github.com/dnSpy/dnSpy/releases
# HytaleClient.exe'yi dnSpy ile aç
```

### Adım 2: Aranacak Namespace'ler
Hytale'da muhtemelen şu namespace'ler var:
- `Hytale.Client.*` - Client-side code
- `Hytale.Network.*` - Networking
- `Hytale.Audio.*` - Audio system
- `Hytale.Player.*` - Player management
- `Hytale.UI.*` - User interface
- `Hytale.Input.*` - Input handling

### Adım 3: Voice Chat İçin Gerekli API'ler

#### A. Player Management
```csharp
// Aranacak class'lar:
- Player
- PlayerManager
- LocalPlayer
- RemotePlayer
```

#### B. Audio System
```csharp
// Aranacak class'lar:
- AudioManager
- AudioSource
- AudioListener
- MicrophoneInput
```

#### C. Network System
```csharp
// Aranacak class'lar:
- NetworkManager
- NetworkClient
- PacketHandler
- Connection
```

#### D. Input System
```csharp
// Aranacak class'lar:
- InputManager
- KeyBinding
- InputAction
```

## 🔧 Modding Yaklaşımları

### Yaklaşım 1: Managed DLL Injection
C# DLL oluştur ve Hytale process'ine inject et:
```csharp
// VoiceChatMod.dll
public class VoiceChatMod : IHytaleMod
{
    public void OnLoad() { }
    public void OnUpdate() { }
}
```

### Yaklaşım 2: Harmony Patching
Harmony library kullanarak runtime patching:
```csharp
[HarmonyPatch(typeof(Player), "Update")]
public static class PlayerUpdatePatch
{
    static void Postfix(Player __instance)
    {
        // Voice chat logic
    }
}
```

### Yaklaşım 3: External Process
Ayrı bir process olarak çalış, memory reading/writing ile entegre ol:
```csharp
// VoiceChatClient.exe (bizim mevcut Java client'ımız)
// Memory reading ile player positions al
// Voice data gönder/al
```

### Yaklaşım 4: Plugin System (Eğer varsa)
Hytale'ın kendi plugin system'ini kullan:
```csharp
// Eğer Hytale plugin API'si varsa
public class VoiceChatPlugin : HytalePlugin
{
    // ...
}
```

## 📝 Sonraki Adımlar

### 1. dnSpy ile Decompile (ÖNCELİKLİ)
- [ ] dnSpy indir ve kur
- [ ] `HytaleClient.exe` dosyasını aç
- [ ] Namespace'leri incele
- [ ] Player, Network, Audio API'lerini bul
- [ ] API'leri dokümante et

### 2. Modding Approach Seç
- [ ] Hytale'ın resmi modding API'sini araştır
- [ ] Eğer yoksa, Harmony patching veya DLL injection kullan
- [ ] External process yaklaşımını değerlendir

### 3. C# Bridge Oluştur
- [ ] Java voice chat server'ımız ile C# Hytale client arasında bridge
- [ ] Named pipes, shared memory veya TCP socket kullan
- [ ] Voice data transfer protokolü

### 4. Test
- [ ] Hytale'da mod yükle
- [ ] Voice chat bağlantısını test et
- [ ] Audio quality test et

## ⚠️ Önemli Notlar

1. **EULA Compliance**: Hytale EULA'sına göre, interoperability için reverse engineering yapabiliriz.

2. **Anti-Cheat**: Hytale'ın anti-cheat sistemi varsa, dikkatli olmalıyız. Voice chat modu cheat değil, ama yine de tespit edilebilir.

3. **Updates**: Hytale güncellendiğinde, API'ler değişebilir. Versiyonlama önemli.

4. **Performance**: Voice chat, oyun performansını etkilememeli. Ayrı thread'lerde çalışmalı.

## 🔗 Faydalı Linkler

- **dnSpy**: https://github.com/dnSpy/dnSpy
- **ILSpy**: https://github.com/icsharpcode/ILSpy
- **Harmony**: https://github.com/pardeike/Harmony
- **Noesis Engine**: https://www.noesisengine.com/
- **OpenAL**: https://www.openal.org/

---

**Son Güncelleme:** 2026-02-07  
**Hytale Client Versiyonu:** Latest (from launcher)  
**Durum:** API Extraction Hazır 🚀
