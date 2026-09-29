package com.example.bowhitbox;

public final class BookmarkManager {
    public static final int TOTAL_SLOTS = 5;

    public static class BookmarkSlot {
        public boolean active = false;
        public float yaw = 0.0f;
        public float pitch = 0.0f;
        public double x = 0.0;
        public double y = 0.0;
        public double z = 0.0;
    }

    private static final BookmarkSlot[] slots = new BookmarkSlot[TOTAL_SLOTS];
    private static int activeSlotIndex = 0;

    static {
        for (int i = 0; i < TOTAL_SLOTS; i++) {
            slots[i] = new BookmarkSlot();
        }
    }

    private BookmarkManager() {}

    public static int getActiveSlotNumber() {
        return activeSlotIndex + 1;
    }

    public static int cycleSlot() {
        activeSlotIndex = (activeSlotIndex + 1) % TOTAL_SLOTS;
        return getActiveSlotNumber();
    }

    public static void saveCurrentSlot(float yaw, float pitch, double x, double y, double z) {
        BookmarkSlot slot = slots[activeSlotIndex];
        slot.yaw = TrajectoryMath.normalizeAngle360(yaw);
        slot.pitch = pitch;
        slot.x = x;
        slot.y = y;
        slot.z = z;
        slot.active = true;
    }

    public static void clearCurrentSlot() {
        slots[activeSlotIndex].active = false;
    }

    public static boolean hasActiveBookmark() {
        return slots[activeSlotIndex].active;
    }

    public static float getActiveSlotYaw() {
        return slots[activeSlotIndex].yaw;
    }

    public static float getActiveSlotPitch() {
        return slots[activeSlotIndex].pitch;
    }

    public static float getDeltaYaw(float currentYaw) {
        BookmarkSlot slot = slots[activeSlotIndex];
        if (!slot.active) return 0.0f;

        float cur = TrajectoryMath.normalizeAngle360(currentYaw);
        float diff = slot.yaw - cur;
        if (diff > 180.0f) {
            diff -= 360.0f;
        } else if (diff < -180.0f) {
            diff += 360.0f;
        }
        return diff;
    }

    public static float getDeltaPitch(float currentPitch) {
        BookmarkSlot slot = slots[activeSlotIndex];
        if (!slot.active) return 0.0f;
        return slot.pitch - currentPitch;
    }

    public static double getDistance(double x, double y, double z) {
        BookmarkSlot slot = slots[activeSlotIndex];
        if (!slot.active) return 0.0;
        double dx = slot.x - x;
        double dy = slot.y - y;
        double dz = slot.z - z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
}
