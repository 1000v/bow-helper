package com.example.bowhitbox;

public final class BallisticsSolver {
    private BallisticsSolver() {}

    /**
     * Simulates arrow height when it reaches horizontal distance D.
     * @param launchPitch launch pitch in degrees (-90 = straight up, 0 = horizontal, +90 = down)
     * @param targetDistance horizontal distance in blocks
     * @param v0 initial arrow speed (typically 3.0 at full pull)
     * @return simulated Y displacement relative to eye level
     */
    public static double simulateArrowHeightAtDistance(float launchPitch, double targetDistance, float v0) {
        double pitchRad = -Math.toRadians(launchPitch);
        double vh = Math.cos(pitchRad) * v0;
        double vy = Math.sin(pitchRad) * v0;

        double curDist = 0.0;
        double curY = 0.0;

        for (int tick = 0; tick < 120; tick++) {
            curDist += vh;
            curY += vy;

            if (curDist >= targetDistance) {
                return curY;
            }

            vh *= 0.99;
            vy = (vy * 0.99) - 0.05;

            // Arrow fell too low
            if (curY < -100.0) {
                break;
            }
        }
        return curY;
    }

    /**
     * Solves for the required pitch angle to hit a target at horizontal distance D and height deltaY.
     * Uses binary search over pitch angles [-65.0, +35.0].
     * @return recommended pitch in degrees, or Float.NaN if unreachable
     */
    public static float solveRecommendedPitch(double horizontalDistance, double deltaY, float v0) {
        if (horizontalDistance <= 0.5 || horizontalDistance > 120.0) {
            return Float.NaN;
        }

        // Pitch search range: steep elevation (-65 deg) to downwards (+35 deg)
        float low = -65.0f;
        float high = 35.0f;

        // Check if unreachable even at high arc
        double maxHeight = simulateArrowHeightAtDistance(-45.0f, horizontalDistance, v0);
        if (maxHeight < deltaY) {
            return Float.NaN;
        }

        for (int i = 0; i < 14; i++) {
            float mid = (low + high) / 2.0f;
            double simY = simulateArrowHeightAtDistance(mid, horizontalDistance, v0);

            if (simY < deltaY) {
                // Need higher arc (more negative pitch)
                high = mid;
            } else {
                low = mid;
            }
        }

        return (low + high) / 2.0f;
    }
}
