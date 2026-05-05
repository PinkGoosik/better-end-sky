package better_end_sky.mixin;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static better_end_sky.Mod.hasBetterSky;
import static better_end_sky.Mod.isDisabled;

@Mixin(Camera.class)
public class CameraMixin {

    @Shadow
    private @Nullable Level level;

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    void nullifyMobEffects(CameraRenderState cameraState, float cameraEntityPartialTicks, CallbackInfo ci) {
        if (!isDisabled() && level instanceof ClientLevel cLevel && hasBetterSky(cLevel)) {
            cameraState.entityRenderState.doesMobEffectBlockSky = false;
        }
    }

}