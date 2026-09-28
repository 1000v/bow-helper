package com.example.bowhitbox;

public final class BookmarkManager {
    private static boolean active = false;
    private static float savedYaw = 0.0f;
    private static float savedPitch = 0.0f;
    private static double savedX = 0.0;
    private static double savedY = 0.0;
    private static double savedZ = 0.0;

    private BookmarkManager() {}

    public static void saveBookmark(float yaw, float pitch, double x, double y, double z) {
        savedYaw = TrajectoryMath.normalizeAngle360(yaw);
        savedPitch = pitch;
        savedX = x;
        savedY = y;
        savedZ = z;
        active = true;
    }

    public static void clearBookmark() {
        active = false;
    }

    public static boolean hasBookmark() {
        return active;
    }

    public static float getSavedYaw() {
        return savedYaw;
    }

    public static float getSavedPitch() {
        return savedPitch;
    }

    public static float getDeltaYaw(float currentYaw) {
        float cur = TrajectoryMath.normalizeAngle360(currentYaw);
        float diff = savedYaw - cur;
        if (diff > 180.0f) {
            diff -= 360.0f;
        } else if (diff < -180.0f) {
            diff += 360.0f;
        }
        return diff;
    }

    public static float getDeltaPitch(float currentPitch) {
        return savedPitch - currentPitch;
    }

    public static double getDistance(double x, double y, double z) {
        double dx = savedX - x;
        double dy = savedY - y;
        double dz = savedZ - z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
}
