# Hytale Client API Extraction Guide

## 🎯 Amaç
HytaleClient.exe'den API'yi çıkarıp Java voice chat modunu oyuna entegre etmek.

## 📍 Hytale Client Konumu
```
C:\Users\Admin\AppData\Roaming\Hytale\install\release\package\game\latest\Client\HytaleClient.exe
```

## 🔍 Hytale Client Teknolojisi
- **Platform**: .NET (C#)
- **Engine**: Noesis (Noesis.dll)
- **Audio**: OpenAL32.dll
- **Networking**: msquic.dll, libsodium.dll

## 🛠️ API Extraction Yöntemleri

### Yöntem 1: dnSpy (Önerilen)
**dnSpy** - .NET decompiler ve debugger

#### Adımlar:
1. **dnSpy İndir**
   ```
   https://github.com/dnSpy/dnSpy/releases
   ```
   - `dnSpy-net-win64.zip` indir
   - Extract et

2. **HytaleClient.exe'yi Aç**
   ```
   dnSpy.exe → File → Open → HytaleClient.exe
   ```

3. **API Sınıflarını Bul**
   - Assembly Explorer'da ara:
     - `Hytale.Client.*`
     - `Hytale.Modding.*`
     - `Hytale.API.*`
     - `Hytale.UI.*`
     - `Hytale.Audio.*`

4. **İlgili Sınıfları Export Et**
   - Sağ tık → Export to Project
   - C# kodunu kaydet

### Yöntem 2: ILSpy
**ILSpy** - Alternatif .NET decompiler

#### Adımlar:
1. **ILSpy İndir**
   ```
   https://github.com/icsharpcode/ILSpy/releases
   ```

2. **HytaleClient.exe'yi Aç**
   ```
   ILSpy.exe → File → Open → HytaleClient.exe
   ```

3. **API'yi Export Et**
   - File → Save Code
   - Tüm assembly'yi C# olarak kaydet

### Yöntem 3: dotPeek (JetBrains)
**dotPeek** - JetBrains'in ücretsiz decompiler'ı

#### Adımlar:
1. **dotPeek İndir**
   ```
   https://www.jetbrains.com/decompiler/
   ```

2. **HytaleClient.exe'yi Aç**
   - Drag & drop HytaleClient.exe

3. **Export to Project**
   - File → Export to Project
   - Visual Studio solution oluştur

## 🎯 Aranacak API Sınıfları

### 1. Mod Loading System
```csharp
namespace Hytale.Modding {
    public interface IMod {
        void OnLoad();
        void OnUnload();
    }
    
    public class ModLoader {
        void RegisterMod(IMod mod);
    }
}
```

### 2. UI/GUI System
```csharp
namespace Hytale.UI {
    public class Screen { }
    public class Widget { }
    public class Button { }
    public class Slider { }
}
```

### 3. Input System
```csharp
namespace Hytale.Input {
    public class KeyBinding {
        void Register(string name, int keyCode);
    }
}
```

### 4. Audio System
```csharp
namespace Hytale.Audio {
    public class AudioManager {
        void PlaySound(string soundId);
    }
}
```

### 5. Player/Entity System
```csharp
namespace Hytale.Entity {
    public class Player {
        UUID GetUniqueId();
        Vector3 GetPosition();
    }
}
```

### 6. Network System
```csharp
namespace Hytale.Network {
    public class NetworkManager {
        void SendPacket(IPacket packet);
    }
}
```

## 📦 C# → Java Bridge Oluşturma

### Seçenek A: IKVM.NET (C# → Java)
**IKVM** - Java bytecode'u .NET'te çalıştırır

#### Adımlar:
1. **IKVM İndir**
   ```
   https://github.com/ikvm-revived/ikvm
   ```

2. **Java Mod'u .NET DLL'e Çevir**
   ```bash
   ikvmc -target:library voicechat-client-1.0.0.jar -out:VoiceChat.dll
   ```

3. **Hytale'a Yükle**
   - VoiceChat.dll → Hytale mods klasörüne kopyala

### Seçenek B: C# Wrapper Yazma
**Hytale Mod API'si C# ise**, Java yerine C# mod yaz

#### Adımlar:
1. **C# Voice Chat Client Yaz**
   ```csharp
   using Hytale.Modding;
   using System.Net.Sockets;
   
   public class VoiceChatMod : IMod {
       private UdpClient client;
       
       public void OnLoad() {
           // Voice chat başlat
           client = new UdpClient();
           // ...
       }
   }
   ```

2. **Java Server ile İletişim**
   - UDP socket kullan
   - Packet format aynı (BasePacket)

### Seçenek C: JNI Bridge (Java ↔ C++)
**Native bridge** oluştur

#### Adımlar:
1. **C++ Bridge Yaz**
   ```cpp
   // voice_bridge.cpp
   extern "C" {
       __declspec(dllexport) void SendVoiceData(byte* data, int length);
   }
   ```

2. **Java'dan Çağır**
   ```java
   public class VoiceBridge {
       static {
           System.loadLibrary("voice_bridge");
       }
       
       public native void sendVoiceData(byte[] data);
   }
   ```

3. **C#'dan Çağır**
   ```csharp
   [DllImport("voice_bridge.dll")]
   public static extern void SendVoiceData(byte[] data, int length);
   ```

## 🚀 Hızlı Başlangıç

### 1. API'yi Decompile Et
```bash
# dnSpy kullanarak
1. dnSpy.exe aç
2. HytaleClient.exe yükle
3. Hytale.Modding namespace'ini bul
4. Export to Project
```

### 2. Mod Loader'ı Anla
```csharp
// Decompile edilmiş kodda ara:
- ModLoader class
- IMod interface
- Mod registration metodu
```

### 3. Basit Test Modu Yaz
```csharp
using Hytale.Modding;

public class TestMod : IMod {
    public void OnLoad() {
        Console.WriteLine("Test mod loaded!");
    }
}
```

### 4. Voice Chat'i Entegre Et
```csharp
using Hytale.Modding;
using System.Net.Sockets;

public class VoiceChatMod : IMod {
    private UdpClient voiceClient;
    
    public void OnLoad() {
        // Java server'a bağlan
        voiceClient = new UdpClient();
        voiceClient.Connect("localhost", 24454);
        
        // Mikrofon capture başlat
        StartMicrophoneCapture();
    }
}
```

## 📋 Alternatif Yaklaşımlar

### Yaklaşım 1: Standalone Client Kullan (Şu an mevcut)
- ✅ Hemen kullanılabilir
- ❌ Oyundan bağımsız
- ❌ Kullanışsız

### Yaklaşım 2: C# Mod Yaz
- ✅ Native Hytale entegrasyonu
- ✅ In-game GUI
- ❌ Java kodunu C#'a port etmek gerekir

### Yaklaşım 3: IKVM Bridge
- ✅ Java kodunu kullan
- ✅ .NET'te çalıştır
- ⚠️ Performance overhead

### Yaklaşım 4: External Overlay
- ✅ Oyunun üstüne overlay
- ✅ Java kullan
- ❌ Gerçek entegrasyon değil

## 🔧 Gerekli Araçlar

### Decompiler'lar
- [dnSpy](https://github.com/dnSpy/dnSpy) - Önerilen
- [ILSpy](https://github.com/icsharpcode/ILSpy)
- [dotPeek](https://www.jetbrains.com/decompiler/)

### Bridge Araçları
- [IKVM.NET](https://github.com/ikvm-revived/ikvm) - Java → .NET
- [JNI](https://docs.oracle.com/javase/8/docs/technotes/guides/jni/) - Java ↔ C++

### Development Tools
- Visual Studio 2022 (C# development)
- .NET SDK 6.0+

## 📝 Sonraki Adımlar

1. **dnSpy ile HytaleClient.exe'yi decompile et**
2. **Mod loading API'sini bul**
3. **Basit test modu yaz**
4. **Voice chat'i entegre et**

## ⚠️ Yasal Uyarı

Hytale'ın EULA'sını kontrol et:
- Reverse engineering izinli mi?
- Mod geliştirme destekleniyor mu?
- API resmi olarak yayınlanacak mı?

**Not**: Hytale henüz release olmadı, resmi mod API'si gelecekte yayınlanabilir.

## 🔗 Kaynaklar

- [Hytale Modding Community](https://hytale.com/community)
- [dnSpy Documentation](https://github.com/dnSpy/dnSpy/wiki)
- [IKVM.NET Guide](https://github.com/ikvm-revived/ikvm/wiki)
- [JNI Specification](https://docs.oracle.com/javase/8/docs/technotes/guides/jni/spec/jniTOC.html)
