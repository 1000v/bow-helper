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
3. **`BowHudOverlay`**: Subscribes to `HudRenderCallback.EVENT`. Renders top-left HUD (Azimuth, Elevation, Distance, Ballistics guidance, Bookmark offsets).
4. **`ArmorHudOverlay`**: Renders durability and remaining uses for equipped armor and main-hand item above/beside hotbar.
5. **`TrajectoryMath` & `BallisticsSolver`**:
   - Discrete physics simulation matching MC 1.16.5 ($v_0 = 3.0$, drag $= 0.99$, gravity $= 0.05$).
   - Calculates recommended launch pitch to hit target at $(D, \Delta y)$.
6. **`BookmarkManager`**: Stores active angle/position bookmark, computes $\Delta$Yaw and $\Delta$Pitch relative to current look vector.

---

## 2. Keybindings & Controls

- **Master Toggle (`key.bowhitbox.toggle_hud`)**: Default `KEY_H`. Toggles HUD overlay visibility.
- **Hitbox Toggle (`key.bowhitbox.toggle_hitbox`)**: Default `KEY_B`. Directly toggles vanilla hitboxes.
- **Save Bookmark (`key.bowhitbox.save_bookmark`)**: Default `KEY_K`. Saves current aim and player location.
- **Clear Bookmark (`key.bowhitbox.clear_bookmark`)**: Default `KEY_J`. Clears saved bookmark.
- **Armor HUD Toggle (`key.bowhitbox.toggle_armor`)**: Default `KEY_U`. Toggles Armor HUD.

Category in Controls menu: `category.bowhitbox.general`.

---

## 3. HUD Display Format

### Top-Left Bow HUD:
```
[Помощник прицеливания]
Азимут (Yaw): 142.4°
Возвышение (Pitch): -12.1°
Дистанция до цели: 34.2 бл.
Реком. угол: -18.6° (▲ подними на 6.5°)
Натяжение: 100% (V0 = 3.0)
[Метка] ΔYaw: +2.1°, ΔPitch: -0.8°
```

### Armor & Hand HUD (Bottom-left):
```
[Шлем: 320/363 (88%)]
[Нагрудник: 450/528 (85%)]
[Поножи: 12/495 (2%) ВНИМАНИЕ]
[Ботинки: 390/429 (91%)]
[Оружие: 1200/1561 (77%)]
```

---

## 4. Ballistics Solver Algorithm

Given horizontal distance $D$ and vertical height difference $\Delta y$:
```
pitch_low = -89.0°, pitch_high = 20.0°
for i = 0 to 12 iterations (Binary search):
    pitch_mid = (pitch_low + pitch_high) / 2
    simulated_y = simulateArrowTrajectory(D, pitch_mid)
    if (simulated_y < delta_y) pitch_high = pitch_mid // need steeper elevation
    else pitch_low = pitch_mid
return pitch_mid
```
Execution takes < 0.001 ms, producing exact recommended pitch for any target up to 120 blocks.

---

## 5. Build & CI Specification

- Root contains `gradlew`, `gradlew.bat`, `gradle/wrapper/gradle-wrapper.properties`.
- GitHub Actions workflow (`.github/workflows/build.yml`) builds via JDK 17, compiles Java 8 bytecode compatible with Minecraft 1.16.5, and stores artifact as `BowHitboxHelper-1.16.5.jar`.

