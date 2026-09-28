# Project Context: Bow & Hitbox Helper (Minecraft 1.16.5)

## Domain & Purpose
Client-side utility mod for Minecraft Java Edition 1.16.5 built with the Fabric Loader toolchain.
Provides client-side visual aids:
- Standalone hitbox bounding box rendering toggle (independent of F3 debug screen).
- Bow aiming HUD displaying player yaw (azimuth) and pitch (elevation angle).
- Keybindings to toggle features on and off.
- Automated CI build configuration via GitHub Actions.

## Tech Stack
- **Game Version:** Minecraft 1.16.5
- **Mod Loader:** Fabric Loader (>=0.14.21)
- **API:** Fabric API (Keybinding API v1, Rendering v1, Lifecycle Events v1)
- **Mappings:** Yarn 1.16.5+build.10:v2
- **Build System:** Gradle with `fabric-loom` plugin
- **Java Version:** Java 8 / 11 target (compiled on JDK 17 with Gradle)
- **CI/CD:** GitHub Actions (Ubuntu latest, JDK 17, `./gradlew build`)

## Architecture Principles
- Lightweight: zero external heavy dependencies, standard Fabric modules.
- Non-invasive: operates via standard client events (`HudRenderCallback`, `ClientTickEvents`).
- Modular: clear separation between input handling, math/ballistics, and HUD rendering.
