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
        });

        HudRenderCallback.EVENT.register((matrices, tickDelta) -> {
            if (hudEnabled) {
                BowHudOverlay.render(matrices, tickDelta);
            }
        });
    }

    public static boolean isHudEnabled() {
        return hudEnabled;
    }
}
