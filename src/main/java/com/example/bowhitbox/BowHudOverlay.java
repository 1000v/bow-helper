package com.example.bowhitbox;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

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

        String chargeColor = pull >= 1.0f ? "§a" : (pull > 0.0f ? "§e" : "§7");
        client.textRenderer.drawWithShadow(matrices, String.format("§fНатяжение: %s%.0f%% §7(V0=%.1f)", chargeColor, pull * 100.0f, speed), x, y, 0xFFFFFF);
    }
}
