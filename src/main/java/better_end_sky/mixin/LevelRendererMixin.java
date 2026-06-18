package better_end_sky.mixin;

import better_end_sky.Mod;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SkyRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static better_end_sky.Mod.isDisabled;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

    /*@Inject(method = "render", at = @At("HEAD"))
    public void renderHeadHook(GraphicsResourceAllocator resourceAllocator, DeltaTracker deltaTracker, boolean renderOutline, CameraRenderState cameraState, Matrix4fc modelViewMatrix, GpuBufferSlice terrainFog, Vector4f fogColor, boolean shouldRenderSky, CallbackInfo ci, @Local(argsOnly = true, ordinal = 1) LocalBooleanRef fogCheck) {
        if (!isDisabled() && hasBetterSky(this.levelRenderState)) {
            fogCheck.set(true);
        }
    }*/

    @WrapWithCondition(method = "lambda$addSkyPass$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SkyRenderer;renderEndSky()V"))
    private static boolean renderEndSky(SkyRenderer instance) {
        if (isDisabled()) return true;
        Mod.endSkyRenderer.render(Mod.endSkyRenderState);
        return false;
    }



    @Inject(method = "close", at = @At("TAIL"))
    void closeCustom(CallbackInfo ci) {
        Mod.endSkyRenderer.close();
    }

}