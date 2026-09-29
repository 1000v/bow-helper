package com.example.bowhitbox;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public final class CrosshairIndicator {
    private CrosshairIndicator() {}

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

        int useTicks = player.getItemUseTime();
        if (useTicks <= 0) {
            return;
        }

        float pull = TrajectoryMath.getPullProgress(useTicks);

        int cx = client.getWindow().getScaledWidth() / 2;
        int cy = client.getWindow().getScaledHeight() / 2;

        if (pull >= 1.0f) {
            // 100% full charge: bright green snap brackets and center dot
            client.textRenderer.drawWithShadow(matrices, "§a[", cx - 11, cy - 4, 0xFFFFFF);
            client.textRenderer.drawWithShadow(matrices, "§a]", cx + 7, cy - 4, 0xFFFFFF);
            client.textRenderer.drawWithShadow(matrices, "§a●", cx - 2, cy - 4, 0xFFFFFF);
            client.textRenderer.drawWithShadow(matrices, "§aMAX", cx - 9, cy + 8, 0xFFFFFF);
        } else {
            // Charging progress: closing parentheses and percentage
            int offset = 16 - (int) (pull * 6);
            String color = pull > 0.6f ? "§e" : "§7";

            client.textRenderer.drawWithShadow(matrices, color + "(", cx - offset, cy - 4, 0xFFFFFF);
            client.textRenderer.drawWithShadow(matrices, color + ")", cx + offset - 4, cy - 4, 0xFFFFFF);

            String text = String.format("%s%d%%", color, (int) (pull * 100));
            int textWidth = client.textRenderer.getWidth(text);
            client.textRenderer.drawWithShadow(matrices, text, cx - (textWidth / 2), cy + 8, 0xFFFFFF);
        }
    }
}
