package better_end_sky.render;

import better_end_sky.Mod;
import better_end_sky.util.BackgroundInfo;
import better_end_sky.util.MHelper;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class EndSkyRenderer implements DimensionRenderingRegistry.SkyRenderer {
    @FunctionalInterface
    interface BufferFunction {
        BufferBuilder make(Tesselator tesselator, float minSize, float maxSize, int count, long seed);
    }

    private static final ResourceLocation NEBULA_1 = Mod.id("textures/sky/nebula_2.png");
    private static final ResourceLocation NEBULA_2 = Mod.id("textures/sky/nebula_3.png");
    private static final ResourceLocation HORIZON = Mod.id("textures/sky/nebula_1.png");
    private static final ResourceLocation STARS = Mod.id("textures/sky/stars.png");
    private static final ResourceLocation FOG = Mod.id("textures/sky/fog.png");

    private VertexBuffer nebula1;
    private VertexBuffer nebula2;
    private VertexBuffer horizon;
    private VertexBuffer stars1;
    private VertexBuffer stars2;
    private VertexBuffer stars3;
    private VertexBuffer stars4;
    private VertexBuffer fog;
    private Vector3f axis1;
    private Vector3f axis2;
    private Vector3f axis3;
    private Vector3f axis4;

    private boolean initialised;

    private void initialise() {
        if (!initialised) {
            initStars();
            RandomSource random = new LegacyRandomSource(131);
            axis1 = new Vector3f(random.nextFloat(), random.nextFloat(), random.nextFloat());
            axis2 = new Vector3f(random.nextFloat(), random.nextFloat(), random.nextFloat());
            axis3 = new Vector3f(random.nextFloat(), random.nextFloat(), random.nextFloat());
            axis4 = new Vector3f(random.nextFloat(), random.nextFloat(), random.nextFloat());
            axis1.normalize();
            axis2.normalize();
            axis3.normalize();
            axis4.normalize();
            initialised = true;
        }
    }

    @Override
    public void render(WorldRenderContext context) {
        if (context.world() == null ) {
            return;
        }
        initialise();
        Matrix4f projectionMatrix = context.projectionMatrix();
        PoseStack matrices = context.matrixStack();
        if (matrices == null ) {
            matrices = new PoseStack();
            matrices.mulPose(context.positionMatrix());
        }

        float time = ((context.world().getDayTime() + context
                .tickCounter()
                .getRealtimeDeltaTicks()) % 360000) * 0.000017453292f;
        float time2 = time * 2;
        float time3 = time * 3;

        FogRenderer.levelFogColor();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        float blindA = 1F - BackgroundInfo.blindness;
        float blind02 = blindA * 0.2f;
        float blind06 = blindA * 0.6f;

        if (blindA > 0) {
            matrices.pushPose();
            matrices.mulPose(new Quaternionf().rotationXYZ(0, time, 0));
            RenderSystem.setShaderTexture(0, HORIZON);
            renderBuffer(
                    matrices,
                    projectionMatrix,
                    horizon,
                    DefaultVertexFormat.POSITION_TEX,
                    0.77f,
                    0.31f,
                    0.73f,
                    0.7f * blindA
            );
            matrices.popPose();

            matrices.pushPose();
            matrices.mulPose(new Quaternionf().rotationXYZ(0, -time, 0));
            RenderSystem.setShaderTexture(0, NEBULA_1);
            renderBuffer(
                    matrices,
                    projectionMatrix,
                    nebula1,
                    DefaultVertexFormat.POSITION_TEX,
                    0.77f,
                    0.31f,
                    0.73f,
                    blind02
            );
            matrices.popPose();

            matrices.pushPose();
            matrices.mulPose(new Quaternionf().rotationXYZ(0, time2, 0));
            RenderSystem.setShaderTexture(0, NEBULA_2);
            renderBuffer(
                    matrices,
                    projectionMatrix,
                    nebula2,
                    DefaultVertexFormat.POSITION_TEX,
                    0.77f,
                    0.31f,
                    0.73f,
                    blind02
            );
            matrices.popPose();

            RenderSystem.setShaderTexture(0, STARS);

            matrices.pushPose();
            matrices.mulPose(new Quaternionf().setAngleAxis(time, axis3.x, axis3.y, axis3.z));
            renderBuffer(
                    matrices,
                    projectionMatrix,
                    stars3,
                    DefaultVertexFormat.POSITION_TEX,
                    0.77f,
                    0.31f,
                    0.73f,
                    blind06
            );
            matrices.popPose();

            matrices.pushPose();
            matrices.mulPose(new Quaternionf().setAngleAxis(time2, axis4.x, axis4.y, axis4.z));
            renderBuffer(matrices, projectionMatrix, stars4, DefaultVertexFormat.POSITION_TEX, 1F, 1F, 1F, blind06);
            matrices.popPose();
        }

        float a = (BackgroundInfo.fogDensity - 1F);
        if (a > 0) {
            if (a > 1) a = 1;
            RenderSystem.setShaderTexture(0, FOG);
            renderBuffer(
                    matrices,
                    projectionMatrix,
                    fog,
                    DefaultVertexFormat.POSITION_TEX,
                    BackgroundInfo.fogColorRed,
                    BackgroundInfo.fogColorGreen,
                    BackgroundInfo.fogColorBlue,
                    a
            );
        }

        if (blindA > 0) {
            matrices.pushPose();
            matrices.mulPose(new Quaternionf().setAngleAxis(time3, axis1.x, axis1.y, axis1.z));
            renderBuffer(matrices, projectionMatrix, stars1, DefaultVertexFormat.POSITION, 1, 1, 1, blind06);
            matrices.popPose();

            matrices.pushPose();
            matrices.mulPose(new Quaternionf().setAngleAxis(time2, axis2.x, axis2.y, axis2.z));
            renderBuffer(
                    matrices,
                    projectionMatrix,
                    stars2,
                    DefaultVertexFormat.POSITION,
                    0.95f,
                    0.64f,
                    0.93f,
                    blind06
            );
            matrices.popPose();
        }

        RenderSystem.depthMask(true);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    private void renderBuffer(
            PoseStack matrices,
            Matrix4f matrix4f,
            VertexBuffer buffer,
            VertexFormat format,
            float r,
            float g,
            float b,
            float a
    ) {
        RenderSystem.setShaderColor(r, g, b, a);
        buffer.bind();
        if (format == DefaultVertexFormat.POSITION) {
            buffer.drawWithShader(matrices.last().pose(), matrix4f, GameRenderer.getPositionShader());
        } else {
            buffer.drawWithShader(matrices.last().pose(), matrix4f, GameRenderer.getPositionTexShader());
        }
        VertexBuffer.unbind();
    }

    private void initStars() {
        Tesselator tesselator = Tesselator.getInstance();

        stars1 = buildBuffer(tesselator, stars1, 0.1f, 0.30f, 3500, 41315, this::makeStars);
        stars2 = buildBuffer(tesselator, stars2, 0.1f, 0.35f, 2000, 35151, this::makeStars);
        stars3 = buildBuffer(tesselator, stars3, 0.4f, 1.2f, 1000, 61354, this::makeUVStars);
        stars4 = buildBuffer(tesselator, stars4, 0.4f, 1.2f, 1000, 61355, this::makeUVStars);
        nebula1 = buildBuffer(tesselator, nebula1, 40, 60, 30, 11515, this::makeFarFog);
        nebula2 = buildBuffer(tesselator, nebula2, 40, 60, 10, 14151, this::makeFarFog);
        horizon = buildBufferHorizon(tesselator, horizon);
        fog = buildBufferFog(tesselator, fog);
    }

    private VertexBuffer buildBuffer(
            Tesselator tesselator,
            VertexBuffer vertexBuffer,
            float minSize,
            float maxSize,
            int count,
            long seed,
            BufferFunction fkt
    ) {
        if (vertexBuffer != null) {
            vertexBuffer.close();
        }

        vertexBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        BufferBuilder bufferBuilder = fkt.make(tesselator, minSize, maxSize, count, seed);
        MeshData meshData = bufferBuilder.build();
        vertexBuffer.bind();
        vertexBuffer.upload(meshData);

        return vertexBuffer;
    }


    private VertexBuffer buildBufferHorizon(Tesselator tesselator, VertexBuffer buffer) {
        return buildBuffer(
                tesselator, buffer, 0, 0, 0, 0,
                (_builder, _minSize, _maxSize, _count, _seed) -> makeCylinder(_builder, 16, 50, 100)
        );

    }

    private VertexBuffer buildBufferFog(Tesselator tesselator, VertexBuffer buffer) {
        return buildBuffer(
                tesselator, buffer, 0, 0, 0, 0,
                (_builder, _minSize, _maxSize, _count, _seed) -> makeCylinder(_builder, 16, 50, 70)
        );
    }

    private BufferBuilder makeStars(Tesselator tesselator, float minSize, float maxSize, int count, long seed) {
        RandomSource random = new LegacyRandomSource(seed);
        RenderSystem.setShader(GameRenderer::getPositionShader);
        final BufferBuilder buffer = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);

        for (int i = 0; i < count; ++i) {
            float posX = random.nextFloat() * 2.0f - 1.0f;
            float posY = random.nextFloat() * 2.0f - 1.0f;
            float posZ = random.nextFloat() * 2.0f - 1.0f;
            float size = MHelper.randRange(minSize, maxSize, random);
            float length = posX * posX + posY * posY + posZ * posZ;

            if (length < 1.0f && length > 0.001f) {
                length = 1.0f / (float) Math.sqrt(length);
                posX *= length;
                posY *= length;
                posZ *= length;

                float px = posX * 100.0f;
                float py = posY * 100.0f;
                float pz = posZ * 100.0f;

                float angle = (float) Math.atan2(posX, posZ);
                float sin1 = (float) Math.sin(angle);
                float cos1 = (float) Math.cos(angle);
                angle = (float) Math.atan2(Math.sqrt(posX * posX + posZ * posZ), posY);
                float sin2 = (float) Math.sin(angle);
                float cos2 = (float) Math.cos(angle);
                angle = random.nextFloat() * (float) Math.PI * 2.0f;
                float sin3 = (float) Math.sin(angle);
                float cos3 = (float) Math.cos(angle);

                for (int index = 0; index < 4; ++index) {
                    float x = (float) ((index & 2) - 1) * size;
                    float y = (float) ((index + 1 & 2) - 1) * size;
                    float aa = x * cos3 - y * sin3;
                    float ab = y * cos3 + x * sin3;
                    float dy = aa * sin2 + 0.0f * cos2;
                    float ae = 0.0f * sin2 - aa * cos2;
                    float dx = ae * sin1 - ab * cos1;
                    float dz = ab * sin1 + ae * cos1;
                    buffer.addVertex(px + dx, py + dy, pz + dz);
                }
            }
        }

        return buffer;
    }

    private BufferBuilder makeUVStars(Tesselator tesselator, float minSize, float maxSize, int count, long seed) {
        RandomSource random = new LegacyRandomSource(seed);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        final BufferBuilder buffer = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        for (int i = 0; i < count; ++i) {
            float posX = random.nextFloat() * 2.0f - 1.0f;
            float posY = random.nextFloat() * 2.0f - 1.0f;
            float posZ = random.nextFloat() * 2.0f - 1.0f;
            float size = MHelper.randRange(minSize, maxSize, random);
            float length = posX * posX + posY * posY + posZ * posZ;

            if (length < 1.0f && length > 0.001f) {
                length = 1.0f / (float) Math.sqrt(length);
                posX *= length;
                posY *= length;
                posZ *= length;

                float px = posX * 100.0f;
                float py = posY * 100.0f;
                float pz = posZ * 100.0f;

                float angle = (float) Math.atan2(posX, posZ);
                float sin1 = (float) Math.sin(angle);
                float cos1 = (float) Math.cos(angle);
                angle = (float) Math.atan2(Math.sqrt(posX * posX + posZ * posZ), posY);
                float sin2 = (float) Math.sin(angle);
                float cos2 = (float) Math.cos(angle);
                angle = random.nextFloat() * (float) Math.PI * 2.0f;
                float sin3 = (float) Math.sin(angle);
                float cos3 = (float) Math.cos(angle);

                float minV = random.nextInt(4) / 4F;
                for (int index = 0; index < 4; ++index) {
                    float x = (float) ((index & 2) - 1) * size;
                    float y = (float) ((index + 1 & 2) - 1) * size;
                    float aa = x * cos3 - y * sin3;
                    float ab = y * cos3 + x * sin3;
                    float dy = aa * sin2 + 0.0f * cos2;
                    float ae = 0.0f * sin2 - aa * cos2;
                    float dx = ae * sin1 - ab * cos1;
                    float dz = ab * sin1 + ae * cos1;
                    float texU = (index >> 1) & 1;
                    float texV = (((index + 1) >> 1) & 1) / 4F + minV;
                    buffer.addVertex(px + dx, py + dy, pz + dz).setUv(texU, texV);
                }
            }
        }
        return buffer;
    }

    private BufferBuilder makeFarFog(Tesselator tesselator, float minSize, float maxSize, int count, long seed) {
        RandomSource random = new LegacyRandomSource(seed);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        final BufferBuilder buffer = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        for (int i = 0; i < count; ++i) {
            float posX = random.nextFloat() * 2.0f - 1.0f;
            float posY = random.nextFloat() - 0.5f;
            float posZ = random.nextFloat() * 2.0f - 1.0f;
            float size = MHelper.randRange(minSize, maxSize, random);
            float length = posX * posX + posY * posY + posZ * posZ;
            float distance = 2.0f;

            if (length < 1.0f && length > 0.001f) {
                length = distance / (float) Math.sqrt(length);
                size *= distance;
                posX *= length;
                posY *= length;
                posZ *= length;

                float px = posX * 100.0f;
                float py = posY * 100.0f;
                float pz = posZ * 100.0f;

                float angle = (float) Math.atan2(posX, posZ);
                float sin1 = (float) Math.sin(angle);
                float cos1 = (float) Math.cos(angle);
                angle = (float) Math.atan2(Math.sqrt(posX * posX + posZ * posZ), posY);
                float sin2 = (float) Math.sin(angle);
                float cos2 = (float) Math.cos(angle);
                angle = random.nextFloat() * (float) Math.PI * 2.0f;
                float sin3 = (float) Math.sin(angle);
                float cos3 = (float) Math.cos(angle);

                for (int index = 0; index < 4; ++index) {
                    float x = (float) ((index & 2) - 1) * size;
                    float y = (float) ((index + 1 & 2) - 1) * size;
                    float aa = x * cos3 - y * sin3;
                    float ab = y * cos3 + x * sin3;
                    float dy = aa * sin2 + 0.0f * cos2;
                    float ae = 0.0f * sin2 - aa * cos2;
                    float dx = ae * sin1 - ab * cos1;
                    float dz = ab * sin1 + ae * cos1;
                    float texU = (index >> 1) & 1;
                    float texV = ((index + 1) >> 1) & 1;
                    buffer.addVertex(px + dx, py + dy, pz + dz).setUv(texU, texV);
                }
            }
        }
        return buffer;
    }

    private BufferBuilder makeCylinder(Tesselator tesselator, int segments, float height, float radius) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        final BufferBuilder buffer = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        for (int i = 0; i < segments; i++) {
            float a1 = (float) i * (float) Math.PI * 2.0f / (float) segments;
            float a2 = (float) (i + 1) * (float) Math.PI * 2.0f / (float) segments;
            float px1 = (float) Math.sin(a1) * radius;
            float pz1 = (float) Math.cos(a1) * radius;
            float px2 = (float) Math.sin(a2) * radius;
            float pz2 = (float) Math.cos(a2) * radius;

            float u0 = (float) i / (float) segments;
            float u1 = (float) (i + 1) / (float) segments;

            buffer.addVertex(px1, -height, pz1).setUv(u0, 0);
            buffer.addVertex(px1, height, pz1).setUv(u0, 1);
            buffer.addVertex(px2, height, pz2).setUv(u1, 1);
            buffer.addVertex(px2, -height, pz2).setUv(u1, 0);
        }
        return buffer;
    }
}