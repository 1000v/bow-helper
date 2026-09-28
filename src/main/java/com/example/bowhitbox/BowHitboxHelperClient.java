package com.example.bowhitbox;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
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
                        new LiteralText("§e[Helper] §fHUD прицеливания: " + (hudEnabled ? "§aВКЛ" : "§cВЫКЛ")),
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
                    BookmarkManager.saveBookmark(
                        client.player.yaw,
                        client.player.pitch,
                        client.player.getX(),
                        client.player.getY(),
                        client.player.getZ()
                    );
                    client.player.sendMessage(
                        new LiteralText(String.format("§d[Helper] §fМетка сохранена: Yaw: §e%.1f°§f, Pitch: §e%.1f°", client.player.yaw, client.player.pitch)),
                        true
                    );
                }
            }

            while (clearBookmarkKey.wasPressed()) {
                BookmarkManager.clearBookmark();
                if (client.player != null) {
                    client.player.sendMessage(
                        new LiteralText("§7[Helper] Метка сброшена"),
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

        HudRenderCallback.EVENT.register((matrices, tickDelta) -> {
            if (hudEnabled) {
                BowHudOverlay.render(matrices, tickDelta);
            }
            ArmorHudOverlay.render(matrices, tickDelta);
        });
    }

    public static boolean isHudEnabled() {
        return hudEnabled;
    }
}
