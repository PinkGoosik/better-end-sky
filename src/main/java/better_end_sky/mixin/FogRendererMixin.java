package better_end_sky.mixin;

import better_end_sky.util.BackgroundInfo;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.fog.FogRenderer;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import static better_end_sky.Mod.isDisabled;

@Mixin(FogRenderer.class)
public abstract class FogRendererMixin {
    @ModifyReturnValue(method = "computeFogColor", at = @At("RETURN"))
    private static Vector4f onRender(Vector4f original) {
        BackgroundInfo.fogColorRed = original.x;
        BackgroundInfo.fogColorGreen = original.y;
        BackgroundInfo.fogColorBlue = original.z;
        return original;
    }

    @ModifyArg(method = "setupFog", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/fog/FogRenderer;updateBuffer(Ljava/nio/ByteBuffer;ILorg/joml/Vector4f;FFFFFF)V"), index = 3)
    private static float modifyEnvStart(float f, @Local(argsOnly = true) ClientLevel level) {
        if (isDisabled()) return f;
        if (level.effects().skyType() == DimensionSpecialEffects.SkyType.END) {
            return f * 0.8f;
        }
        return f;
    }

    @ModifyArg(method = "setupFog", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/fog/FogRenderer;updateBuffer(Ljava/nio/ByteBuffer;ILorg/joml/Vector4f;FFFFFF)V"), index = 5)
    private static float modifyDistStart(float f, @Local(argsOnly = true) ClientLevel level) {
        if (isDisabled()) return f;
        if (level.effects().skyType() == DimensionSpecialEffects.SkyType.END) {
            return f * 0.5f;
        }
        return f;
    }

    @ModifyExpressionValue(method = "computeFogColor", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/fog/environment/FogEnvironment;getModifiedDarkness(Lnet/minecraft/world/entity/LivingEntity;FF)F"))
    private static float onRender(float original, @Local(argsOnly = true) ClientLevel world) {
        BackgroundInfo.blindness = original;
        return original;
    }
}