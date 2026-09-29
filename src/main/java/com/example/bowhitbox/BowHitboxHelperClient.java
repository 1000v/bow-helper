package com.example.bowhitbox;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.LiteralText;
import org.lwjgl.glfw.GLFW;

public class BowHitboxHelperClient implements ClientModInitializer {
    public static final String MOD_ID = "bow-hitbox-helper";

    private static KeyBinding toggleHudKey;
    private static KeyBinding toggleHitboxKey;
    private static KeyBinding saveBookmarkKey;
    private static KeyBinding cycleBookmarkKey;
    private static KeyBinding clearBookmarkKey;
    private static KeyBinding toggleArmorKey;

    private static boolean hudEnabled = true;

    @Override
    public void onInitializeClient() {
        toggleHudKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.bowhitbox.toggle_hud",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_H,
            "category.bowhitbox.general"
        ));

        toggleHitboxKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.bowhitbox.toggle_hitbox",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_B,
            "category.bowhitbox.general"
        ));

        saveBookmarkKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.bowhitbox.save_bookmark",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_K,
            "category.bowhitbox.general"
        ));

        cycleBookmarkKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.bowhitbox.cycle_bookmark",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_N,
            "category.bowhitbox.general"
        ));

        clearBookmarkKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.bowhitbox.clear_bookmark",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_J,
            "category.bowhitbox.general"
        ));

        toggleArmorKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.bowhitbox.toggle_armor",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_U,
            "category.bowhitbox.general"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleHudKey.wasPressed()) {
                hudEnabled = !hudEnabled;
                if (client.player != null) {
                    client.player.sendMessage(
                        new LiteralText("§e[Helper] §fHUD и траектория: " + (hudEnabled ? "§aВКЛ" : "§cВЫКЛ")),
                        true
                    );
                }
            }

            while (toggleHitboxKey.wasPressed()) {
                if (client.getEntityRenderDispatcher() != null) {
                    boolean nextState = !client.getEntityRenderDispatcher().shouldRenderHitboxes();
                    client.getEntityRenderDispatcher().setRenderHitboxes(nextState);
                    if (client.player != null) {
                        client.player.sendMessage(
                            new LiteralText("§e[Helper] §fХитбоксы: " + (nextState ? "§aВКЛ" : "§cВЫКЛ")),
                            true
                        );
                    }
                }
            }

            while (saveBookmarkKey.wasPressed()) {
                if (client.player != null) {
                    BookmarkManager.saveCurrentSlot(
                        client.player.yaw,
                        client.player.pitch,
                        client.player.getX(),
                        client.player.getY(),
                        client.player.getZ()
                    );
                    int slot = BookmarkManager.getActiveSlotNumber();
                    client.player.sendMessage(
                        new LiteralText(String.format("§d[Helper] §fМетка [%d/5] сохранена: Yaw: §e%.1f°§f, Pitch: §e%.1f°", slot, client.player.yaw, client.player.pitch)),
                        true
                    );
                }
            }

            while (cycleBookmarkKey.wasPressed()) {
                int nextSlot = BookmarkManager.cycleSlot();
                if (client.player != null) {
                    String state = BookmarkManager.hasActiveBookmark() ? "§a(активна)" : "§7(пусто)";
                    client.player.sendMessage(
                        new LiteralText(String.format("§d[Helper] §fВыбран слот метки: §e[%d/5] %s", nextSlot, state)),
                        true
                    );
                }
            }

            while (clearBookmarkKey.wasPressed()) {
                int slot = BookmarkManager.getActiveSlotNumber();
                BookmarkManager.clearCurrentSlot();
                if (client.player != null) {
                    client.player.sendMessage(
                        new LiteralText(String.format("§7[Helper] Метка [%d/5] сброшена", slot)),
                        true
                    );
                }
            }

            while (toggleArmorKey.wasPressed()) {
                ArmorHudOverlay.toggle();
                if (client.player != null) {
                    client.player.sendMessage(
                        new LiteralText("§b[Helper] §fArmor HUD: " + (ArmorHudOverlay.isEnabled() ? "§aВКЛ" : "§cВЫКЛ")),
                        true
                    );
                }
            }
        });

        // 2D HUD Rendering
        HudRenderCallback.EVENT.register((matrices, tickDelta) -> {
            if (hudEnabled) {
                BowHudOverlay.render(matrices, tickDelta);
                CrosshairIndicator.render(matrices, tickDelta);
            }
            ArmorHudOverlay.render(matrices, tickDelta);
        });

        // 3D World Trajectory Rendering
        WorldRenderEvents.AFTER_TRANSLUCENT.register(TrajectoryRenderer::render);
    }

    public static boolean isHudEnabled() {
        return hudEnabled;
    }
}
