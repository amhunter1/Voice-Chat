# Libraries Directory

This directory should contain the Hytale Server JAR file for compilation.

## Required Files

- `HytaleServer.jar` - The Hytale server JAR file (provided by Hytale)

## How to Obtain

1. Download the Hytale server from the official Hytale website
2. Copy the `HytaleServer.jar` file to this directory
3. The plugin will compile against this JAR

## Note

The HytaleServer.jar is not included in this repository as it is proprietary software owned by Hypixel Studios. You must obtain it yourself from official sources.

## Alternative

If you don't have the HytaleServer.jar yet, you can:
1. Comment out the `compileOnly files('../libs/HytaleServer.jar')` line in `server/build.gradle`
2. The code will still compile but you won't be able to test it without the actual server
