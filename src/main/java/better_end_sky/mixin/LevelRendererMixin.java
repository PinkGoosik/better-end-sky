package better_end_sky.mixin;

import better_end_sky.render.EndSkyRenderState;
import better_end_sky.render.EndSkyRenderer;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SkyRenderer;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static better_end_sky.Mod.hasBetterSky;
import static better_end_sky.Mod.isDisabled;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @Shadow
    private @Nullable ClientLevel level;
    @Unique
    private static EndSkyRenderer better_end_sky$EndSkyRenderer;
    @Unique
    static final private EndSkyRenderState better_end_sky$EndSkyRenderStat = new EndSkyRenderState();

    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(CallbackInfo ci) {
        better_end_sky$EndSkyRenderer = new EndSkyRenderer();
    }

    @Inject(method = "renderLevel", at = @At("HEAD"))
    public void renderHeadHook(GraphicsResourceAllocator graphicsResourceAllocator, DeltaTracker deltaTracker, boolean bl, Camera camera, Matrix4f matrix4f, Matrix4f positionMatrix, Matrix4f matrix4f3, GpuBufferSlice gpuBufferSlice, Vector4f vector4f, boolean bl2, CallbackInfo ci, @Local(argsOnly = true, ordinal = 1) LocalBooleanRef fogCheck) {
        if (!isDisabled() && hasBetterSky(level)) {
            fogCheck.set(true);
        }
        better_end_sky$EndSkyRenderStat.reset();
    }

    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SkyRenderer;extractRenderState(Lnet/minecraft/client/multiplayer/ClientLevel;FLnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/state/SkyRenderState;)V"))
    public void extractEndSkyRenderState(GraphicsResourceAllocator graphicsResourceAllocator, DeltaTracker deltaTracker, boolean bl, Camera camera, Matrix4f matrix4f, Matrix4f matrix4f2, Matrix4f matrix4f3, GpuBufferSlice gpuBufferSlice, Vector4f vector4f, boolean bl2, CallbackInfo ci) {
        better_end_sky$EndSkyRenderer.extractRenderState(level, better_end_sky$EndSkyRenderStat);
    }

    @WrapWithCondition(method = "method_62215", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SkyRenderer;renderEndSky()V"))
    private static boolean renderEndSky(SkyRenderer instance) {
        if (isDisabled()) return true;
        better_end_sky$EndSkyRenderer.render(better_end_sky$EndSkyRenderStat);
        return false;
    }

    @ModifyReturnValue(method = "doesMobEffectBlockSky", at = @At("RETURN"))
    boolean nullifyMobEffects(boolean original, Camera camera) {
        if (isDisabled()) return original;
        if (hasBetterSky(level)) {
            return false;
        }
        return original;
    }

    @Inject(method = "onResourceManagerReload", at = @At("TAIL"))
    void reloadTextures(ResourceManager resourceManager, CallbackInfo ci) {
        better_end_sky$EndSkyRenderer.initTextures();
    }

    @Inject(method = "close", at = @At("TAIL"))
    void closeCustom(CallbackInfo ci) {
        better_end_sky$EndSkyRenderer.close();
    }

}
