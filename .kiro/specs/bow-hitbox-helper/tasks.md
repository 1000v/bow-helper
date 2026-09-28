# Tasks: Bow & Hitbox Helper (Minecraft 1.16.5)

## Progress Table

| Task ID | Description | Status | Evidence |
| :--- | :--- | :--- | :--- |
| TSK-001 | Setup Gradle project structure (`build.gradle`, `gradle.properties`, `settings.gradle`) | Completed | `build.gradle`, `gradle.properties`, `settings.gradle` created |
| TSK-002 | Setup Fabric metadata (`fabric.mod.json`, assets, lang) | Completed | `fabric.mod.json`, `en_us.json`, `ru_ru.json` created |
| TSK-003 | Implement core client classes (`BowHitboxHelperClient`, `TrajectoryMath`, `BowHudOverlay`) | Completed | `BowHitboxHelperClient.java`, `BowHudOverlay.java`, `TrajectoryMath.java` created |
| TSK-004 | Setup GitHub Actions CI workflow (`.github/workflows/build.yml`) | Completed | `.github/workflows/build.yml` created |
| TSK-005 | Setup Gradle wrapper files (`gradle-wrapper.properties`, `gradlew`, `gradlew.bat`) | Completed | Wrapper properties and scripts created |
| TSK-006 | Ready for git commit and push to remote repository | Completed | All project files created and ready |

---

## Tasks Details

- [x] **TSK-001: Setup Gradle project structure**
  - Deliverables: `build.gradle`, `gradle.properties`, `settings.gradle`.
  - Dependencies: None.

- [x] **TSK-002: Setup Fabric metadata and resources**
  - Deliverables: `src/main/resources/fabric.mod.json`, `src/main/resources/assets/bowhitbox/lang/en_us.json`, `src/main/resources/assets/bowhitbox/lang/ru_ru.json`.
  - Dependencies: TSK-001.

- [x] **TSK-003: Implement core client Java classes**
  - Deliverables:
    - `BowHitboxHelperClient.java` (entrypoint, keybinds, tick handler)
    - `BowHudOverlay.java` (render logic)
    - `TrajectoryMath.java` (ballistics calculations)
  - Dependencies: TSK-001, TSK-002.

- [x] **TSK-004: Setup GitHub Actions CI workflow**
  - Deliverables: `.github/workflows/build.yml`.
  - Dependencies: None.

- [x] **TSK-005: Setup Gradle wrapper files**
  - Deliverables: `gradle/wrapper/gradle-wrapper.properties`, `gradlew`, `gradlew.bat`.
  - Dependencies: None.

- [x] **TSK-006: Ready for git commit and push**
  - Deliverables: Clean git working tree, verified build config.
  - Dependencies: TSK-001 through TSK-005.
