package better_end_sky.mixin;

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
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SkyRenderer;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static better_end_sky.Mod.isDisabled;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @Shadow
    private @Nullable ClientLevel level;
    @Unique
    private EndSkyRenderer better_end_sky$customEndSky;
    @Unique
    private Matrix4f better_end_sky$positionMatrix;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(CallbackInfo ci) {
        better_end_sky$customEndSky = new EndSkyRenderer();
    }

    @Inject(method = "renderLevel", at = @At("HEAD"))
    public void captureMatrix(GraphicsResourceAllocator graphicsResourceAllocator, DeltaTracker deltaTracker, boolean bl, Camera camera, Matrix4f positionMatrix, Matrix4f matrix4f2, GpuBufferSlice gpuBufferSlice, Vector4f vector4f, boolean bl2, CallbackInfo ci, @Local(argsOnly = true, ordinal = 1) LocalBooleanRef fogCheck) {
        if (isDisabled()) return;

        better_end_sky$positionMatrix = positionMatrix;
        if (level.effects().skyType() == DimensionSpecialEffects.SkyType.END) {
            fogCheck.set(true);
        }
    }

    @WrapWithCondition(method = "method_62215", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SkyRenderer;renderEndSky()V"))
    public boolean renderCustomEndSky(SkyRenderer instance) {
        if (isDisabled()) return true;

        better_end_sky$customEndSky.render(level, better_end_sky$positionMatrix);
        return false;
    }

    @ModifyReturnValue(method = "doesMobEffectBlockSky", at = @At("RETURN"))
    boolean allowSkyRender(boolean original, Camera camera) {
        if (isDisabled()) return original;
        if (level.effects().skyType() == DimensionSpecialEffects.SkyType.END) {
            return false;
        }
        return original;
    }

    @Inject(method = "close", at = @At("TAIL"))
    void closeCustom(CallbackInfo ci) {
        better_end_sky$customEndSky.close();
    }

}
