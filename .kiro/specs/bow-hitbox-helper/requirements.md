# Requirements: Bow & Hitbox Helper (Minecraft 1.16.5)

## 1. Functional Requirements (FR)

### FR-1: Hitbox Toggle Keybind
- **FR-1.1**: The client SHALL provide a configurable keybinding (default: `B` or `Numpad /`) to toggle entity bounding boxes on/off without displaying the F3 debug text screen.
- **FR-1.2**: When the keybind is pressed, the client SHALL toggle `EntityRenderDispatcher.setRenderHitboxes(...)`.

### FR-2: Bow Aiming Angle & Trajectory HUD
- **FR-2.1**: When the local player holds a Bow (main hand or offhand), the client SHALL render an on-screen HUD element showing:
  - Current Azimuth (Yaw angle in degrees, normalized to 0–360° or -180°–180°).
  - Current Elevation / Pitch angle (in degrees from -90° to +90°).
  - Bow pull progress / charge status.
- **FR-2.2**: The HUD SHALL automatically hide when the player is not holding a bow.

### FR-3: Master Toggle Keybind
- **FR-3.1**: The client SHALL provide a master toggle keybind (default: `H`) to enable/disable the mod HUD without restarting the client.

### FR-4: GitHub Actions CI Build
- **FR-4.1**: The repository SHALL include a GitHub Actions workflow that executes on push or manual trigger (`workflow_dispatch`).
### FR-5: Angle Bookmarks / Пристрелочные метки
- **FR-5.1**: The client SHALL provide a keybind (default: `K`) to save the current aim angles (Yaw/Pitch) and player location as an active bookmark.
- **FR-5.2**: The client SHALL provide a keybind (default: `J`) to clear the active bookmark.
- **FR-5.3**: When an active bookmark exists and player holds a bow, the HUD SHALL display the delta offset ($\Delta$Yaw, $\Delta$Pitch) to align with the saved mark.

### FR-6: Distance Meter & Target Hit Elevation Advisor
- **FR-6.1**: The client SHALL calculate distance to the target block/entity in crosshair using raycasting (up to 128 blocks).
- **FR-6.2**: When looking at a target with a bow, the client SHALL calculate the recommended launch pitch angle to hit the target based on Minecraft arrow ballistics (velocity 3.0, drag 0.99, gravity 0.05).
- **FR-6.3**: The HUD SHALL display the target distance, recommended elevation pitch, and guidance difference (e.g. `Подними прицел на +X.X°`).

### FR-8: 3D World Arrow Trajectory Arc
- **FR-8.1**: When the player holds a bow, the client SHALL render a 3D parabolic trajectory arc in the world from the player's eye/hand along the simulated arrow path.
- **FR-8.2**: The trajectory SHALL raycast against world blocks (`world.raycast`) to detect the impact point and terminate the arc.
- **FR-8.3**: A visual landing marker (box/cross) SHALL be rendered at the exact impact location.
- **FR-8.4**: Trajectory rendering SHALL be toggleable via master HUD keybind `H`.

### FR-9: Dynamic Crosshair Bow Charge Indicator
- **FR-9.1**: When pulling a bow, the client SHALL display a dynamic visual indicator near the center crosshair reflecting pull progress (0% to 100%).
- **FR-9.2**: When pull reaches 100% (ready for full critical velocity), the crosshair indicator SHALL highlight bright green with a ready symbol (`§a◎`).

### FR-10: Multi-Slot Angle Bookmarks (5 Slots)
- **FR-10.1**: The bookmark system SHALL support 5 distinct memory slots (1 through 5).
- **FR-10.2**: The client SHALL provide a keybind (default: `N`) to cycle through bookmark slots.
- **FR-10.3**: Keybind `K` SHALL save aim angles and position into the currently selected slot.
- **FR-10.4**: Keybind `J` SHALL clear the currently selected slot.
- **FR-10.5**: The HUD SHALL display the currently active slot number (e.g., `[Метка 2/5]`) and its alignment guidance.

---

## 2. Non-Functional Requirements (NFR)

- **NFR-1 (Compatibility)**: Must build cleanly targeting Minecraft 1.16.5 with Fabric Loader and Yarn mappings.
- **NFR-2 (Performance)**: HUD and 3D trajectory rendering must execute with minimal overhead (max 80 simulation steps per frame).
- **NFR-3 (Clean Project Skeleton)**: Gradle configuration must be self-contained and reproducible.

---

## 3. Acceptance Criteria (AC)

- **AC-1.1**: Given player is in game, when pressing the hitbox toggle key, then entity bounding boxes toggle visibility without F3 screen text.
- **AC-2.1**: Given player holds a bow, when looking around, then Azimuth and Elevation angles update in real-time on the top-left HUD.
- **AC-3.1**: Given mod is enabled, when pressing master toggle key `H`, then the mod HUD is disabled.
- **AC-4.1**: Given GitHub Actions workflow runs, then a downloadable `.jar` file is generated under Artifacts.
- **AC-5.1**: Given player presses `K`, then current aim is saved and alignment guidance appears on HUD.
- **AC-6.1**: Given crosshair points at a distant target, then HUD shows exact distance and recommended pitch to hit.
- **AC-7.1**: Given player equips armor or holds damaged tool, then remaining durability displays on HUD.
- **AC-8.1**: Given player holds a bow, then a 3D curve with landing marker is drawn in the game world.
- **AC-9.1**: Given player charges a bow, then a dynamic indicator around the crosshair turns green at 100% charge.
- **AC-10.1**: Given player presses `N`, then active bookmark slot switches between 1 and 5.


