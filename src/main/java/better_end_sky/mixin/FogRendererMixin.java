package better_end_sky.mixin;

import better_end_sky.util.BackgroundInfo;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.FogRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import static better_end_sky.Mod.hasBetterSky;
import static better_end_sky.Mod.isDisabled;

@Mixin(FogRenderer.class)
public abstract class FogRendererMixin {

    @ModifyReturnValue(method = "setupFog", at = @At("RETURN"))
    FogData modifyOutputFog(FogData original, @Local(argsOnly = true) ClientLevel level) {
        if (!isDisabled() && hasBetterSky(level)) {
            original.environmentalStart *= 0.8f;
            original.renderDistanceStart *= 0.5f;
        }
        return original;
    }

    @ModifyExpressionValue(method = "computeFogColor", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/fog/environment/FogEnvironment;getModifiedDarkness(Lnet/minecraft/world/entity/LivingEntity;FF)F"))
    private static float setDarknessMod(float original) {
        BackgroundInfo.darknessModifier = original;
        return original;
    }

}