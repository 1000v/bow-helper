# Technical Design: Bow & Hitbox Helper (Minecraft 1.16.5)

## 1. System Architecture

The mod is a client-only Fabric mod (`environment: "client"` in `fabric.mod.json`).

```
                    ┌─────────────────────────┐
                    │     Fabric Loader       │
                    └────────────┬────────────┘
                                 │
                 ┌───────────────┴───────────────┐
                 ▼                               ▼
       ClientModInitializer            Fabric API Events
     (BowHitboxHelperClient)      (ClientTick, HudRender)
                 │                               │
        ┌────────┴────────┐             ┌────────┴────────┐
        ▼                 ▼             ▼                 ▼
   Keybindings       HitboxManager    BowHudOverlay   TrajectoryMath
   (Toggle/Key)      (Dispatcher)      (2D Canvas)     (Kinematics)
```

### Components
1. **`BowHitboxHelperClient`**: Entry point (`net.fabricmc.api.ClientModInitializer`). Registers keybindings and lifecycle event handlers.
2. **`HitboxController`**: Interacts with `MinecraftClient.getInstance().getEntityRenderDispatcher().setRenderHitboxes(...)`.
3. **`BowHudOverlay`**: Subscribes to `HudRenderCallback.EVENT`. Detects if the player is holding a bow (`Items.BOW`), calculates yaw/pitch/charge, and renders text onto the screen matrix.
4. **`TrajectoryMath`**: Helper class calculating arrow speed $V_0 = \text{charge} \cdot 3.0$ and ballistic angles.

---

## 2. Keybindings & Controls

- **Master Toggle (`key.bowhitbox.toggle_hud`)**: Default `KEY_H`. Toggles HUD overlay visibility.
- **Hitbox Toggle (`key.bowhitbox.toggle_hitbox`)**: Default `KEY_B`. Directly toggles vanilla hitboxes.

Category in Controls menu: `category.bowhitbox.general`.

---

## 3. HUD Display Format

Top-left position $(X=8, Y=8)$:
```
[Bow Assistant]
Azimuth (Yaw): 142.4°
Elevation (Pitch): -12.1°
Pull Charge: 100% (V0 = 3.0 blk/t)
```
Coloring:
- Title: Green / Gold (`0x55FF55`)
- Angles: White (`0xFFFFFF`)
- Charge: Yellow if charging, Green if 100% ready.

---

## 4. Build & CI Specification

- Root contains `gradlew`, `gradlew.bat`, `gradle/wrapper/gradle-wrapper.properties`.
- GitHub Actions workflow (`.github/workflows/build.yml`) builds via JDK 17, compiles Java 8 bytecode compatible with Minecraft 1.16.5, and stores artifact as `BowHitboxHelper-1.16.5.jar`.
