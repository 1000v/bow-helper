package com.example.bowhitbox;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

public final class BowHudOverlay {
    private BowHudOverlay() {}

    public static void render(MatrixStack matrices, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null || client.options.debugEnabled) {
            return;
        }

        PlayerEntity player = client.player;
        ItemStack mainHand = player.getMainHandStack();
        ItemStack offHand = player.getOffHandStack();

        boolean isHoldingBow = (mainHand != null && mainHand.getItem() == Items.BOW) ||
                               (offHand != null && offHand.getItem() == Items.BOW);

        if (!isHoldingBow) {
            return;
        }

        float yaw = TrajectoryMath.normalizeAngle360(player.yaw);
        float pitch = player.pitch;

        int useTicks = player.getItemUseTime();
        float pull = TrajectoryMath.getPullProgress(useTicks);
        float speed = pull * 3.0f;

        int x = 10;
        int y = 10;
        int lineHeight = 11;

        client.textRenderer.drawWithShadow(matrices, "§6[Помощник лука]", x, y, 0xFFFFFF);
        y += lineHeight;

        client.textRenderer.drawWithShadow(matrices, String.format("§fАзимут (Yaw): §e%.1f°", yaw), x, y, 0xFFFFFF);
        y += lineHeight;

        client.textRenderer.drawWithShadow(matrices, String.format("§fВозвышение (Pitch): §e%.1f°", pitch), x, y, 0xFFFFFF);
        y += lineHeight;

        // Target distance and recommended elevation angle
        HitResult hit = client.crosshairTarget;
        if (hit != null && hit.getType() != HitResult.Type.MISS) {
            Vec3d hitPos = hit.getPos();
            double dx = hitPos.x - player.getX();
            double dz = hitPos.z - player.getZ();
            double horizDist = Math.sqrt(dx * dx + dz * dz);
            double totalDist = player.getCameraPosVec(tickDelta).distanceTo(hitPos);
            double deltaY = hitPos.y - player.getEyeY();

            client.textRenderer.drawWithShadow(matrices, String.format("§fДистанция: §b%.1f бл.", totalDist), x, y, 0xFFFFFF);
            y += lineHeight;

            float recPitch = BallisticsSolver.solveRecommendedPitch(horizDist, deltaY, 3.0f);
            if (!Float.isNaN(recPitch)) {
                float diff = recPitch - pitch;
                String advice;
                if (Math.abs(diff) <= 0.6f) {
                    advice = "§a✔ ИДЕАЛЬНЫЙ УГОЛ (ОГОНЬ!)";
                } else if (diff < 0) {
                    advice = String.format("§e▲ Подними на §6+%.1f°", -diff);
                } else {
                    advice = String.format("§e▼ Опусти на §6-%.1f°", diff);
                }
                client.textRenderer.drawWithShadow(matrices, String.format("§fРеком. угол: §e%.1f° §7(%s§7)", recPitch, advice), x, y, 0xFFFFFF);
                y += lineHeight;
            } else {
                client.textRenderer.drawWithShadow(matrices, "§fРеком. угол: §cВне зоны поражения", x, y, 0xFFFFFF);
                y += lineHeight;
            }
        }

        String chargeColor = pull >= 1.0f ? "§a" : (pull > 0.0f ? "§e" : "§7");
        client.textRenderer.drawWithShadow(matrices, String.format("§fНатяжение: %s%.0f%% §7(V0=%.1f)", chargeColor, pull * 100.0f, speed), x, y, 0xFFFFFF);
        y += lineHeight;

        // Multi-slot Bookmark display
        int slotNum = BookmarkManager.getActiveSlotNumber();
        if (BookmarkManager.hasActiveBookmark()) {
            float dYaw = BookmarkManager.getDeltaYaw(player.yaw);
            float dPitch = BookmarkManager.getDeltaPitch(player.pitch);
            String yawGuide = dYaw > 0 ? String.format("вправо +%.1f°", dYaw) : String.format("влево %.1f°", dYaw);
            String pitchGuide = dPitch < 0 ? String.format("подними +%.1f°", -dPitch) : String.format("опусти -%.1f°", dPitch);
            client.textRenderer.drawWithShadow(
                matrices,
                String.format("§d[Метка %d/5] §7%s, %s", slotNum, yawGuide, pitchGuide),
                x, y, 0xFFFFFF
            );
        } else {
            client.textRenderer.drawWithShadow(
                matrices,
                String.format("§8[Метка %d/5 пуста (K-запомнить, N-слот)]", slotNum),
                x, y, 0xFFFFFF
            );
        }
    }
}
