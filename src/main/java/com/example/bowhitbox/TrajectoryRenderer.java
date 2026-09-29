package com.example.bowhitbox;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public final class TrajectoryRenderer {
    private TrajectoryRenderer() {}

    public static void render(WorldRenderContext context) {
        if (!BowHitboxHelperClient.isHudEnabled()) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null || client.world == null) {
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
        float pull = useTicks > 0 ? TrajectoryMath.getPullProgress(useTicks) : 1.0f;
        float speed = pull * 3.0f;

        float yaw = player.yaw;
        float pitch = player.pitch;

        double pitchRad = Math.toRadians(-pitch);
        double yawRad = Math.toRadians(-yaw) - Math.PI;

        double vx = -Math.sin(yawRad) * Math.cos(pitchRad) * speed;
        double vy = Math.sin(pitchRad) * speed;
        double vz = Math.cos(yawRad) * Math.cos(pitchRad) * speed;

        Vec3d eyePos = player.getCameraPosVec(context.tickDelta());
        Vec3d currentPos = eyePos.add(vx * 0.15, vy * 0.15, vz * 0.15);
        Vec3d motion = new Vec3d(vx, vy, vz);

        List<Vec3d> points = new ArrayList<>();
        points.add(currentPos);
        Vec3d impactPoint = null;

        for (int tick = 0; tick < 80; tick++) {
            Vec3d nextPos = currentPos.add(motion);
            RaycastContext rc = new RaycastContext(
                currentPos,
                nextPos,
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                player
            );
            BlockHitResult hit = player.world.raycast(rc);

            if (hit.getType() != HitResult.Type.MISS) {
                impactPoint = hit.getPos();
                points.add(impactPoint);
                break;
            }

            points.add(nextPos);
            currentPos = nextPos;
            motion = new Vec3d(motion.x * 0.99, motion.y * 0.99 - 0.05, motion.z * 0.99);
        }

        if (points.size() < 2) {
            return;
        }

        Camera camera = context.camera();
        Vec3d camPos = camera.getPos();
        MatrixStack matrices = context.matrixStack();
        Matrix4f model = matrices.peek().getModel();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableTexture();
        RenderSystem.disableDepthTest();
        RenderSystem.lineWidth(2.5f);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        // 1. Draw trajectory line
        buffer.begin(GL11.GL_LINE_STRIP, VertexFormats.POSITION_COLOR);
        int r = pull >= 1.0f ? 80 : 255;
        int g = pull >= 1.0f ? 255 : 215;
        int b = pull >= 1.0f ? 80 : 0;
        int a = 230;

        for (Vec3d p : points) {
            float relX = (float) (p.x - camPos.x);
            float relY = (float) (p.y - camPos.y);
            float relZ = (float) (p.z - camPos.z);
            buffer.vertex(model, relX, relY, relZ).color(r, g, b, a).next();
        }
        tessellator.draw();

        // 2. Draw impact cross marker if hit a surface
        if (impactPoint != null) {
            float ix = (float) (impactPoint.x - camPos.x);
            float iy = (float) (impactPoint.y - camPos.y);
            float iz = (float) (impactPoint.z - camPos.z);
            float s = 0.25f;

            buffer.begin(GL11.GL_LINES, VertexFormats.POSITION_COLOR);
            // X-bar (Red)
            buffer.vertex(model, ix - s, iy, iz).color(255, 60, 60, 255).next();
            buffer.vertex(model, ix + s, iy, iz).color(255, 60, 60, 255).next();
            // Y-bar (Green)
            buffer.vertex(model, ix, iy - s, iz).color(60, 255, 60, 255).next();
            buffer.vertex(model, ix, iy + s, iz).color(60, 255, 60, 255).next();
            // Z-bar (Blue)
            buffer.vertex(model, ix, iy, iz - s).color(60, 150, 255, 255).next();
            buffer.vertex(model, ix, iy, iz + s).color(60, 150, 255, 255).next();
            tessellator.draw();
        }

        RenderSystem.enableDepthTest();
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }
}
