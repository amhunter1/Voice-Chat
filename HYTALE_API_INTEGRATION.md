# Hytale API Integration Guide

This document explains how to complete the Hytale API integration for the VoiceChat plugin.

## Current Status

The plugin is structured to work with the Hytale API, but some integration points need to be completed once you have access to the HytaleServer.jar file.

## Required Steps

### 1. Obtain HytaleServer.jar

Download the Hytale server JAR and place it in the `libs/` directory:
```
Voice-Chat/
└── libs/
    └── HytaleServer.jar
```

### 2. Complete Player Lookup

**File**: `server/src/main/java/com/voicechat/server/command/VoiceChatCommand.java`

**Method**: `getPlayerByName(String name)`

**Current Code**:
```java
private UUID getPlayerByName(String name) {
    // TODO: Implement player lookup via Hytale API
    return null;
}
```

**Expected Implementation**:
```java
private UUID getPlayerByName(String name) {
    // Get the server instance from plugin
    var server = plugin.getServer();

    // Find player by name
    var player = server.getPlayer(name);

    return player != null ? player.getUniqueId() : null;
}
```

### 3. Implement Event Listeners

**File**: `server/src/main/java/com/voicechat/server/VoiceChatPlugin.java`

**Method**: `registerEvents()`

**What to Add**:
- Player join event listener
- Player quit event listener
- Player move event listener (optional, for position updates)

**Expected Implementation**:
```java
private void registerEvents() {
    LogUtils.info("Registering event listeners...");

    var eventManager = this.getEventManager();

    // Player join event
    eventManager.registerListener(PlayerJoinEvent.class, event -> {
        UUID playerId = event.getPlayer().getUniqueId();
        playerManager.getState(playerId); // Initialize player state
        LogUtils.info("Player joined: " + playerId);
    });

    // Player quit event
    eventManager.registerListener(PlayerQuitEvent.class, event -> {
        UUID playerId = event.getPlayer().getUniqueId();
        playerManager.removePlayer(playerId);
        LogUtils.info("Player quit: " + playerId);
    });
}
```

### 4. Implement Position Updates

**File**: `server/src/main/java/com/voicechat/server/task/PositionUpdateTask.java`

**Method**: `run()`

**Current Code**:
```java
@Override
public void run() {
    // TODO: Update player positions from Hytale API
}
```

**Expected Implementation**:
```java
@Override
public void run() {
    var server = plugin.getServer();

    for (var player : server.getOnlinePlayers()) {
        UUID playerId = player.getUniqueId();
        var location = player.getLocation();

        double[] position = new double[]{
            location.getX(),
            location.getY(),
            location.getZ()
        };

        plugin.getPlayerManager().updatePosition(playerId, position);
    }
}
```

### 5. Implement Block Occlusion

**File**: `server/src/main/java/com/voicechat/server/audio/OcclusionEngine.java`

**Method**: `isBlockSolid(int x, int y, int z)`

**Current Code**:
```java
private boolean isBlockSolid(int x, int y, int z) {
    // TODO: Check via Hytale world API
    return false;
}
```

**Expected Implementation**:
```java
private boolean isBlockSolid(int x, int y, int z) {
    var server = plugin.getServer();
    var world = server.getWorld("overworld"); // or get from player context

    var block = world.getBlockAt(x, y, z);
    var material = block.getType();

    // Check if block is solid and not transparent
    return material.isSolid() && !material.isTransparent();
}
```

### 6. Add Scheduled Tasks

**File**: `server/src/main/java/com/voicechat/server/VoiceChatPlugin.java`

**Method**: `start()`

**What to Add**:
Schedule the position update task to run periodically.

**Expected Implementation**:
```java
@Override
protected void start() {
    if (!initialized) {
        LogUtils.error("Cannot start: plugin not initialized");
        return;
    }

    if (running) {
        LogUtils.warn("VoiceChat plugin already running");
        return;
    }

    try {
        voiceServer.start();

        // Schedule position updates
        var scheduler = this.getScheduler();
        var updateInterval = getConfig().performance.updateInterval;

        scheduler.scheduleRepeating(
            new PositionUpdateTask(this),
            updateInterval,
            updateInterval
        );

        running = true;
        LogUtils.info("VoiceChat plugin enabled!");

    } catch (Exception e) {
        LogUtils.error("Failed to start VoiceChat plugin: " + e.getMessage());
        e.printStackTrace();
        shutdown();
    }
}
```

## API Classes to Explore

Once you have HytaleServer.jar, explore these packages:

1. **com.hypixel.hytale.server.core.plugin**
   - JavaPlugin
   - JavaPluginInit
   - CommandRegistry
   - EventManager

2. **com.hypixel.hytale.server.core.command**
   - CommandBase
   - CommandContext

3. **com.hypixel.hytale.server.core**
   - Server
   - Player
   - World
   - Location
   - Message

4. **com.hypixel.hytale.server.events**
   - PlayerJoinEvent
   - PlayerQuitEvent
   - PlayerMoveEvent

## Testing

1. Build the plugin:
```bash
./gradlew :server:build
```

2. Copy the JAR to your Hytale server:
```bash
cp server/build/libs/voicechat-server-1.0.0.jar /path/to/hytale/plugins/
```

3. Start the server and check logs:
```bash
./gradlew runServer
```

4. Test commands in-game:
```
/voicechat
/voicechat mute <player>
/voicechat unmute <player>
/voicechat reload
```

## Debugging

Enable debug logging in `config.json`:
```json
{
  "debug": true
}
```

Check logs in:
- `plugins/VoiceChat/logs/voicechat.log`
- Server console output

## Common Issues

### Issue: Plugin not loading
**Solution**: Check manifest.json format and entrypoint class name

### Issue: Commands not working
**Solution**: Verify command registration in setup() method

### Issue: Events not firing
**Solution**: Check event listener registration and event class names

### Issue: Position updates not working
**Solution**: Verify scheduler is running and task is scheduled correctly

## Next Steps

After completing the API integration:

1. Test with multiple players
2. Verify voice proximity works correctly
3. Test occlusion through walls
4. Implement GUI for client settings
5. Add more admin commands
6. Write unit tests

## Resources

- Hytale Plugin Template: https://github.com/realBritakee/hytale-template-plugin
- Hytale Modding Documentation: [GitBook link from hytale-api-help.txt]
- Video Tutorials: Modding by Kaupenjoe

## Support

If you encounter issues:
1. Check the Hytale modding documentation
2. Review example plugins on GitHub
3. Ask in the Hytale modding community
4. Check server logs for error messages
