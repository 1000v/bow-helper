package com.example.bowhitbox;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public final class ArmorHudOverlay {
    private static boolean enabled = true;

    private ArmorHudOverlay() {}

    public static void toggle() {
        enabled = !enabled;
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void render(MatrixStack matrices, float tickDelta) {
        if (!enabled) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null || client.options.debugEnabled) {
            return;
        }

        PlayerEntity player = client.player;
        int screenHeight = client.getWindow().getScaledHeight();
        int x = 10;
        int y = screenHeight - 85;
        int lineHeight = 10;

        boolean renderedAny = false;

        // Armor pieces: 3=Helmet, 2=Chestplate, 1=Leggings, 0=Boots
        String[] slotNames = {"Ботинки", "Поножи", "Нагрудник", "Шлем"};
        for (int i = 3; i >= 0; i--) {
            ItemStack armorPiece = player.inventory.armor.get(i);
            if (armorPiece != null && !armorPiece.isEmpty() && armorPiece.isDamageable()) {
                if (!renderedAny) {
                    client.textRenderer.drawWithShadow(matrices, "§b[Броня и снаряжение]", x, y, 0xFFFFFF);
                    y += lineHeight;
                    renderedAny = true;
                }
                renderItemDurability(client, matrices, slotNames[i], armorPiece, x, y);
                y += lineHeight;
            }
        }

        // Main hand item
        ItemStack mainHand = player.getMainHandStack();
        if (mainHand != null && !mainHand.isEmpty() && mainHand.isDamageable()) {
            if (!renderedAny) {
                client.textRenderer.drawWithShadow(matrices, "§b[Броня и снаряжение]", x, y, 0xFFFFFF);
                y += lineHeight;
            }
            renderItemDurability(client, matrices, "Рука", mainHand, x, y);
        }
    }

    private static void renderItemDurability(MinecraftClient client, MatrixStack matrices, String label, ItemStack stack, int x, int y) {
        int max = stack.getMaxDamage();
        int current = max - stack.getDamage();
        float percent = ((float) current / (float) max) * 100.0f;

        String color = percent > 50.0f ? "§a" : (percent > 20.0f ? "§e" : "§c");
        String warning = percent <= 20.0f ? " §c⚠" : "";
        String text = String.format("§f%s: %s%d§7/%d (%d%%)%s", label, color, current, max, (int) percent, warning);

        client.textRenderer.drawWithShadow(matrices, text, x, y, 0xFFFFFF);
    }
}
