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
- **FR-4.2**: The workflow SHALL run `./gradlew build` and upload the compiled `.jar` artifact for download.

---

## 2. Non-Functional Requirements (NFR)

- **NFR-1 (Compatibility)**: Must build cleanly targeting Minecraft 1.16.5 with Fabric Loader and Yarn mappings.
- **NFR-2 (Performance)**: HUD rendering must execute in O(1) time per frame without causing frame drops or garbage collection spikes.
- **NFR-3 (Clean Project Skeleton)**: Gradle configuration must be self-contained and reproducible.

---

## 3. Acceptance Criteria (AC)

- **AC-1.1**: Given player is in game, when pressing the hitbox toggle key, then entity bounding boxes toggle visibility without F3 screen text.
- **AC-2.1**: Given player holds a bow, when looking around, then Azimuth and Elevation angles update in real-time on the top-left HUD.
- **AC-3.1**: Given mod is enabled, when pressing master toggle key `H`, then the mod HUD is disabled.
- **AC-4.1**: Given GitHub Actions workflow runs, then a downloadable `.jar` file is generated under Artifacts.
