package com.example.bowhitbox;

public final class TrajectoryMath {
    private TrajectoryMath() {}

    /**
     * Calculates the pull charge of a bow (0.0 to 1.0) based on use ticks.
     * Full charge is reached at 20 ticks.
     */
    public static float getPullProgress(int useTicks) {
        if (useTicks <= 0) {
            return 0.0f;
        }
        float progress = (float) useTicks / 20.0f;
        progress = (progress * progress + progress * 2.0f) / 3.0f;
        if (progress > 1.0f) {
            progress = 1.0f;
        }
        return progress;
    }

    /**
     * Normalizes an angle into the [0, 360) range.
     */
    public static float normalizeAngle360(float angle) {
        float normalized = angle % 360.0f;
        if (normalized < 0.0f) {
            normalized += 360.0f;
        }
        return normalized;
    }
}
