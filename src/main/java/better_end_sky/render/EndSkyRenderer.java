package better_end_sky.render;

import better_end_sky.Mod;
import better_end_sky.util.BackgroundInfo;
import better_end_sky.util.MHelper;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.OptionalDouble;
import java.util.OptionalInt;

public class EndSkyRenderer implements AutoCloseable {

    @FunctionalInterface
    interface BufferFunction {
        void make(BufferBuilder bufferBuilder, float minSize, float maxSize, int count, long seed);
    }

    public static final int COLOR = 0xff_ffffff;
    private static final ResourceLocation NEBULA_1_LOCATION = Mod.id("textures/sky/nebula_2.png");
    private static final ResourceLocation NEBULA_2_LOCATION = Mod.id("textures/sky/nebula_3.png");
    private static final ResourceLocation HORIZON_LOCATION = Mod.id("textures/sky/nebula_1.png");
    private static final ResourceLocation STARS_LOCATION = Mod.id("textures/sky/stars.png");
//    private static final ResourceLocation FOG_LOCATION = Mod.id("textures/sky/fog.png");

    @Nullable
    private AbstractTexture nebula1Texture;
    @Nullable
    private AbstractTexture nebula2Texture;
    @Nullable
    private AbstractTexture horizonTexture;
    @Nullable
    private AbstractTexture starsTexture;
//    @Nullable
//    private AbstractTexture fogTexture;

    private final GpuBuffer nebula1;
    private final GpuBuffer nebula2;
    private final GpuBuffer horizon;
    private final GpuBuffer stars1;
    private final GpuBuffer stars2;
    private final GpuBuffer stars3;
    private final GpuBuffer stars4;
//    private final GpuBuffer fog;
    private final Vector3f axis1;
    private final Vector3f axis2;
    private final Vector3f axis3;
    private final Vector3f axis4;

    private final Minecraft client;

    public EndSkyRenderer() {
        client = Minecraft.getInstance();
        stars1 = buildBuffer(0.1f, 0.30f, 3500, 41315, RenderPipelines.STARS, this::makeStars);
        stars2 = buildBuffer(0.1f, 0.35f, 2000, 35151, RenderPipelines.STARS, this::makeStars);
        stars3 = buildBuffer(0.4f, 1.2f, 1000, 61354, this::makeUVStars);
        stars4 = buildBuffer(0.4f, 1.2f, 1000, 61355, this::makeUVStars);
        nebula1 = buildBuffer(40, 60, 30, 11515, this::makeFarFog);
        nebula2 = buildBuffer(40, 60, 10, 14151, this::makeFarFog);
        horizon = buildBufferHorizon();
//        fog = buildBufferFog();

        RandomSource random = RandomSource.createNewThreadLocalInstance();
        axis1 = new Vector3f(random.nextFloat(), random.nextFloat(), random.nextFloat());
        axis2 = new Vector3f(random.nextFloat(), random.nextFloat(), random.nextFloat());
        axis3 = new Vector3f(random.nextFloat(), random.nextFloat(), random.nextFloat());
        axis4 = new Vector3f(random.nextFloat(), random.nextFloat(), random.nextFloat());
        axis1.normalize();
        axis2.normalize();
        axis3.normalize();
        axis4.normalize();
    }

    public void initTextures() {
        nebula1Texture = getTexture(NEBULA_1_LOCATION);
        nebula2Texture = getTexture(NEBULA_2_LOCATION);
        horizonTexture = getTexture(HORIZON_LOCATION);
        starsTexture = getTexture(STARS_LOCATION);
//        fogTexture = getTexture(FOG_LOCATION);
    }

    public static AbstractTexture getTexture(ResourceLocation resourceLocation) {
        TextureManager textureManager = Minecraft.getInstance().getTextureManager();
        AbstractTexture abstractTexture = textureManager.getTexture(resourceLocation);
        abstractTexture.setUseMipmaps(false);
        return abstractTexture;
    }

    public void extractRenderState(Level world, EndSkyRenderState state) {
        state.time = ((world.getDayTime() + client.getDeltaTracker().getRealtimeDeltaTicks()) % 360000) * 0.000017453292f;
        state.blindnessFog = 1F - BackgroundInfo.blindness;
    }

    public void render(EndSkyRenderState state) {
        PoseStack matrices = new PoseStack();
        matrices.mulPose(RenderSystem.getModelViewStack());

        float time = state.time;
        float time2 = time * 2;

        float blindnessFog = state.blindnessFog;
        float blindMod2 = blindnessFog * 0.2f;
        float blindMod6 = blindnessFog * 0.6f;

        if (blindnessFog > 0) {
            matrices.pushPose();
            matrices.mulPose(new Quaternionf().rotationXYZ(0, time, 0));
            renderBuffer(matrices, horizonTexture, horizon, RenderPipelines.END_SKY, 0.77f, 0.31f, 0.73f, 0.7f * blindnessFog);
            matrices.popPose();

            matrices.pushPose();
            matrices.mulPose(new Quaternionf().rotationXYZ(0, -time, 0));
            renderBuffer(matrices, nebula1Texture, nebula1, RenderPipelines.END_SKY, 0.77f, 0.31f, 0.73f, blindMod2);
            matrices.popPose();

            matrices.pushPose();
            matrices.mulPose(new Quaternionf().rotationXYZ(0, time2, 0));
            renderBuffer(matrices, nebula2Texture, nebula2, RenderPipelines.END_SKY, 0.77f, 0.31f, 0.73f, blindMod2);
            matrices.popPose();


            matrices.pushPose();
            matrices.mulPose(new Quaternionf().setAngleAxis(time, axis3.x, axis3.y, axis3.z));
            renderBuffer(matrices, starsTexture, stars3, RenderPipelines.END_SKY, 0.77f, 0.31f, 0.73f, blindMod6);
            matrices.popPose();

            matrices.pushPose();
            matrices.mulPose(new Quaternionf().setAngleAxis(time2, axis4.x, axis4.y, axis4.z));
            renderBuffer(matrices, starsTexture, stars4, RenderPipelines.END_SKY, 1F, 1F, 1F, blindMod6);
            matrices.popPose();
        }

       /* float a = (BackgroundInfo.fogDensity - 1F);
        if (a > 0) {
            if (a > 1) a = 1;
            renderBuffer(matrices, fogTexture, fog, RenderPipelines.END_SKY, BackgroundInfo.fogColorRed, BackgroundInfo.fogColorGreen, BackgroundInfo.fogColorBlue, a);
        }*/

        if (blindnessFog > 0) {
            matrices.pushPose();
            matrices.mulPose(new Quaternionf().setAngleAxis(time * 3, axis1.x, axis1.y, axis1.z));
            renderBuffer(matrices, horizonTexture, stars1, RenderPipelines.STARS, 1, 1, 1, blindMod6);
            matrices.popPose();

            matrices.pushPose();
            matrices.mulPose(new Quaternionf().setAngleAxis(time2, axis2.x, axis2.y, axis2.z));
            renderBuffer(matrices, horizonTexture, stars2, RenderPipelines.STARS, 0.95f, 0.64f, 0.93f, blindMod6);
            matrices.popPose();
        }

        BackgroundInfo.blindness = 0f;
    }

    private void renderBuffer(
            PoseStack matrices,
            AbstractTexture texture,
            GpuBuffer buffer,
            RenderPipeline pipeline,
            float r,
            float g,
            float b,
            float a
    ) {
        var autoBuf = RenderSystem.getSequentialBuffer(pipeline.getVertexFormatMode());
        GpuBuffer gpuBuffer = autoBuf.getBuffer(buffer.size());
        var colorView = client.getMainRenderTarget().getColorTextureView();
        var depthView = client.getMainRenderTarget().getDepthTextureView();
        var dynamicTransforms = RenderSystem.getDynamicUniforms().writeTransform(matrices.last().pose(), new Vector4f(r, g, b, a), new Vector3f(), new Matrix4f(), 1f);

        try (RenderPass pass = RenderSystem.getDevice()
                .createCommandEncoder()
                .createRenderPass(() -> "Better End sky", colorView, OptionalInt.empty(), depthView, OptionalDouble.empty())) {
            pass.setPipeline(pipeline);
            RenderSystem.bindDefaultUniforms(pass);
            pass.setUniform("DynamicTransforms", dynamicTransforms);
            pass.bindSampler("Sampler0", texture.getTextureView());
            pass.setVertexBuffer(0, buffer);
            pass.setIndexBuffer(gpuBuffer, autoBuf.type());
            pass.drawIndexed(0, 0, gpuBuffer.size(), 1);
        }
    }

    private GpuBuffer buildBuffer(
            float minSize,
            float maxSize,
            int count,
            long seed,
            RenderPipeline format,
            BufferFunction fkt
    ) {
        GpuBuffer var10;
        try (ByteBufferBuilder byteBufferBuilder = new ByteBufferBuilder(format.getVertexFormat().getVertexSize() * count * 4)) {
            BufferBuilder bufferBuilder = new BufferBuilder(byteBufferBuilder, format.getVertexFormatMode(), format.getVertexFormat());
            fkt.make(bufferBuilder, minSize, maxSize, count, seed);
            try (MeshData meshData = bufferBuilder.buildOrThrow()) {
                var10 = RenderSystem.getDevice().createBuffer(() -> "Better End Sky vertex buffer", 40, meshData.vertexBuffer());
            }
        }
        return var10;
    }

    private GpuBuffer buildBuffer(
            float minSize, float maxSize, int count, long seed, BufferFunction fkt
    ) {
        return buildBuffer(minSize, maxSize, count, seed, RenderPipelines.END_SKY, fkt);
    }

    private GpuBuffer buildBufferHorizon() {
        return buildBuffer(
                0, 0, 0, 0,
                (_builder, _minSize, _maxSize, _count, _seed) -> makeCylinder(_builder, 16, 50, 180)
        );

    }

/*    private GpuBuffer buildBufferFog() {
        return buildBuffer(
                0, 0, 0, 0,
                (_builder, _minSize, _maxSize, _count, _seed) -> makeCylinder(_builder, 16, 50, 70)
        );
    }*/

    private void makeStars(BufferBuilder buffer, float minSize, float maxSize, int count, long seed) {
        RandomSource random = new LegacyRandomSource(seed);

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
                    buffer.addVertex(px + dx, py + dy, pz + dz).setColor(COLOR);
                }
            }
        }
    }

    private void makeUVStars(BufferBuilder buffer, float minSize, float maxSize, int count, long seed) {
        RandomSource random = new LegacyRandomSource(seed);

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
                    buffer.addVertex(px + dx, py + dy, pz + dz).setUv(texU, texV).setColor(COLOR);
                }
            }
        }
    }

    private void makeFarFog(BufferBuilder buffer, float minSize, float maxSize, int count, long seed) {
        RandomSource random = new LegacyRandomSource(seed);

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
                    buffer.addVertex(px + dx, py + dy, pz + dz).setUv(texU, texV).setColor(COLOR);
                }
            }
        }
    }

    private void makeCylinder(BufferBuilder buffer, int segments, float height, float radius) {
        for (int i = 0; i < segments; i++) {
            float a1 = (float) i * (float) Math.PI * 2.0f / (float) segments;
            float a2 = (float) (i + 1) * (float) Math.PI * 2.0f / (float) segments;
            float px1 = (float) Math.sin(a1) * radius;
            float pz1 = (float) Math.cos(a1) * radius;
            float px2 = (float) Math.sin(a2) * radius;
            float pz2 = (float) Math.cos(a2) * radius;

            float u0 = (float) i / (float) segments;
            float u1 = (float) (i + 1) / (float) segments;

            buffer.addVertex(px1, -height, pz1).setUv(u0, 0).setColor(COLOR);
            buffer.addVertex(px1, height, pz1).setUv(u0, 1).setColor(COLOR);
            buffer.addVertex(px2, height, pz2).setUv(u1, 1).setColor(COLOR);
            buffer.addVertex(px2, -height, pz2).setUv(u1, 0).setColor(COLOR);
        }
    }

    @Override
    public void close() {
        nebula1.close();
        nebula2.close();
        horizon.close();
        stars1.close();
        stars2.close();
        stars3.close();
        stars4.close();
    }
}