# Hytale Core API Research Findings

## Overview
This document contains research findings from the Hytale Server Core API documentation, based on the decompiled Hytale server JAR from the community repository: [Ranork/Hytale-Server-Unpacked](https://github.com/Ranork/Hytale-Server-Unpacked)

**Important Note**: This is unofficial, community-decompiled API documentation. Official Hytale modding API is not yet publicly released.

---

## Core Package Structure

**Package**: `com.hypixel.hytale.server.core`

### 1. HytaleServer (Singleton)

The central management class of the server. Coordinates server lifecycle (startup, loop, shutdown), managers (plugin, command, event), and modules.

#### Important Public Methods:

```java
// Returns the single instance of the running server
static HytaleServer get()

// Returns the EventBus object where server-wide events are managed
EventBus getEventBus()

// Returns the plugin manager
PluginManager getPluginManager()

// Returns the command manager
CommandManager getCommandManager()

// Returns the object representing the server configuration file (hytale-server.json)
HytaleServerConfig getConfig()

// Shuts down the server for the specified reason
void shutdownServer(ShutdownReason reason)

// Returns the server name set in the configuration
String getServerName()
```

---

## Authentication Package

**Package**: `com.hypixel.hytale.server.core.auth`

### 1. ServerAuthManager

Manages authentication processes on the server. Checks the validity of players.

#### Important Public Methods:

```java
// Prepares authentication keys and structures
void initialize()

// Returns the server's authentication mode (ONLINE, OFFLINE, etc.)
AuthMode getAuthMode()
```

### 2. SessionServiceClient

Communicates with Hytale session services (Backend API). Used to verify player sessions and retrieve profile information.

#### Important Public Methods:

```java
// Requests authorization grant
CompletableFuture<String> requestAuthorizationGrantAsync(...)

// Exchanges the grant for an access token
CompletableFuture<String> exchangeAuthGrantForTokenAsync(...)

// Retrieves player profiles using the access token
GameProfile[] getGameProfiles(String oauthAccessToken)

// Starts a new game session
GameSessionResponse createGameSession(...)
```

### 3. PlayerAuthentication

A data class holding a player's authentication information (UUID, Username).

---

## Command Package

**Package**: `com.hypixel.hytale.server.core.command`

### 1. CommandManager

The heart of the command system. Registers, parses, and routes commands to the relevant processor.

#### Important Public Methods:

```java
// Registers default system commands
void registerCommands()

// Registers a new command object to the system (used to add custom commands in mods)
CommandRegistration register(AbstractCommand command)

// Executes a command line on behalf of the sender
CompletableFuture<Void> handleCommand(CommandSender sender, String commandString)

// Returns a map of all registered commands
Map<String, AbstractCommand> getCommandRegistration()
```

### 2. CommandSender

An interface representing the entity executing the command. Can be `Player` or `ConsoleSender`.

#### Methods:

```java
// Sends a message to the sender
void sendMessage(Message message)

// Returns the name of the sender
String getName()

// Checks if the sender has specific permission
boolean hasPermission(String permission)
```

---

## Event Package

**Package**: `com.hypixel.hytale.event` and `com.hypixel.hytale.server.core.event`

### 1. EventBus

The center of the event-based system. Enables dispatching and listening to events.

#### Important Public Methods:

```java
// Registers a listener for a specific event class
EventRegistration register(Class<T> eventClass, Consumer<T> consumer)

// Returns a publisher for an event class
IEventDispatcher dispatchFor(Class<T> eventClass)
```

### 2. Example Events (server.core.event.events)

```java
// Triggered when the server starts
BootEvent

// Triggered when the server begins to shut down
ShutdownEvent
```

**Note**: Player-specific events (join, leave, move, etc.) were not found in the current documentation. These may be in separate packages or not yet documented.

---

## Plugin Package

**Package**: `com.hypixel.hytale.server.core.plugin`

### 1. PluginManager

Manages plugin lifecycle and operations.

#### Important Public Methods:

```java
// Lists all loaded and active plugins
List<PluginBase> getPlugins()

// Retrieves the plugin with the specified ID
PluginBase getPlugin(PluginIdentifier identifier)

// Starts the setup phase of plugins
void setup()

// Starts (enables) the plugins
void start()

// Safely stops the plugins
void shutdown()
```

### 2. PluginBase

The base class for all plugins. This class is inherited when developing mods (usually via `JavaPlugin`).

**Note**: Our [`VoiceChatPlugin`](server/src/main/java/com/voicechat/server/VoiceChatPlugin.java) extends this class.

---

## Permissions Package

**Package**: `com.hypixel.hytale.server.core.permissions`

### 1. HytalePermissions

Contains constants defining standard permissions (permission nodes) within the server.

---

## Key Findings for Voice Chat Implementation

### ✅ Available APIs:

1. **Event System**: 
   - [`EventBus`](com.hypixel.hytale.event.EventBus) for registering event listeners
   - Can listen to server lifecycle events (BootEvent, ShutdownEvent)

2. **Plugin System**:
   - [`PluginBase`](com.hypixel.hytale.server.core.plugin.PluginBase) / [`JavaPlugin`](com.hypixel.hytale.server.core.plugin.JavaPlugin) for plugin lifecycle
   - Our implementation already uses this correctly

3. **Command System**:
   - [`CommandManager`](com.hypixel.hytale.server.core.command.CommandManager) for registering custom commands
   - Can create voice chat control commands

4. **Authentication**:
   - [`PlayerAuthentication`](com.hypixel.hytale.server.core.auth.PlayerAuthentication) provides UUID and Username
   - Can be used for player identification in voice chat

### ❌ Missing APIs (Not Found in Documentation):

1. **Player Management**:
   - No `PlayerManager` or `getOnlinePlayers()` method found
   - No player join/leave events documented
   - No player position/location tracking API

2. **World/Entity System**:
   - No world management API documented
   - No entity position tracking
   - No distance calculation utilities

3. **Network/Packet System**:
   - No custom packet registration API
   - No player connection management

### 🔍 Possible Reasons:

1. **Incomplete Documentation**: The decompiled API may not include all packages
2. **Separate Packages**: Player/World APIs might be in different packages not yet documented
3. **ECS Architecture**: Hytale uses Entity Component System - player data might be accessed differently
4. **Beta Status**: Some APIs may not be implemented yet in the beta server

---

## Implementation Strategy for Voice Chat

### Current Approach (Recommended):

Our current implementation uses **abstraction layers** and **stub implementations**:

1. **PlayerPositionManager** ([`server/src/main/java/com/voicechat/server/manager/PlayerPositionManager.java`](server/src/main/java/com/voicechat/server/manager/PlayerPositionManager.java))
   - Provides interface for player position tracking
   - Can be implemented when actual API becomes available

2. **ProximityVoiceManager** ([`server/src/main/java/com/voicechat/server/manager/ProximityVoiceManager.java`](server/src/main/java/com/voicechat/server/manager/ProximityVoiceManager.java))
   - Uses PlayerPositionManager abstraction
   - Distance-based voice routing logic

3. **Custom UDP Network** ([`server/src/main/java/com/voicechat/server/network/VoiceServer.java`](server/src/main/java/com/voicechat/server/network/VoiceServer.java))
   - Independent voice data transmission
   - Doesn't rely on Hytale's packet system

### Alternative Approaches:

#### Option 1: Wait for Official API
- **Pros**: Clean, officially supported implementation
- **Cons**: Unknown release timeline, project blocked

#### Option 2: Reflection/Bytecode Manipulation
- **Pros**: Can access private APIs
- **Cons**: Fragile, breaks with updates, potential security issues

#### Option 3: External Service (Current Hybrid)
- **Pros**: Works independently, flexible
- **Cons**: Requires separate server, more complex setup

---

## Next Steps

1. **Monitor Official Releases**: 
   - Watch for official Hytale modding API announcements
   - Check Hytale developer blog and forums

2. **Community Research**:
   - Explore other decompiled packages
   - Check if player/world APIs exist in other packages
   - Look for ECS-based player access patterns

3. **Test Current Implementation**:
   - Test voice chat with stub player positions
   - Verify UDP network functionality
   - Ensure plugin loads correctly in Hytale server

4. **Prepare for API Updates**:
   - Keep abstraction layers clean
   - Document all stub implementations
   - Make it easy to swap in real API calls

---

## Resources

- **GitHub Repository**: [Ranork/Hytale-Server-Unpacked](https://github.com/Ranork/Hytale-Server-Unpacked)
- **Core API Documentation**: [HYTALE_CORE_API.md](https://github.com/Ranork/Hytale-Server-Unpacked/blob/main/HYTALE_CORE_API.md)
- **Hytale Official Blog**: [Hytale Modding Blog Post](https://hytale.com/news/2019/3/a-closer-look-at-hytale-s-approach-to-modding)
- **Reddit Discussion**: [The Hytale Modding Bible](https://www.reddit.com/r/HytaleInfo/comments/1hqvqxe/the_hytale_modding_bible_full_server_api/)

---

## Conclusion

The Hytale Core API provides solid foundation for:
- ✅ Plugin lifecycle management
- ✅ Event system integration
- ✅ Command registration
- ✅ Player authentication

However, critical APIs for voice chat are missing:
- ❌ Player position tracking
- ❌ World/entity management
- ❌ Custom packet system

**Our current abstraction-based approach is the best solution** until official APIs are released. The code is structured to easily integrate real API calls when they become available.
