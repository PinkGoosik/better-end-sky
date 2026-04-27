package better_end_sky.mixin;

import better_end_sky.render.EndSkyRenderState;
import better_end_sky.render.EndSkyRenderer;
import better_end_sky.util.BackgroundInfo;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SkyRenderer;
import net.minecraft.client.renderer.state.level.SkyRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static better_end_sky.Mod.hasBetterSky;
import static better_end_sky.Mod.isDisabled;

@Mixin(SkyRenderer.class)
public class SkyRendererMixin {
    @Unique
    private EndSkyRenderer better_end_sky$EndSkyRenderer;
    @Unique
    final private EndSkyRenderState better_end_sky$EndSkyRenderState = new EndSkyRenderState();
    @Unique
    private ClientLevel better_end_sky$currentLevel;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(CallbackInfo ci) {
        better_end_sky$EndSkyRenderer = new EndSkyRenderer();
        better_end_sky$EndSkyRenderer.initTextures();
    }

    @Inject(method = "extractRenderState", at = @At("HEAD"))
    public void extractEndSkyRenderState(ClientLevel level, float tickDelta, Camera camera, SkyRenderState state, CallbackInfo ci) {
        better_end_sky$currentLevel = level;
        if (!isDisabled() && hasBetterSky(level)) {
            long gameTime = level.getLevelData().getGameTime();
            better_end_sky$EndSkyRenderState.time = ((gameTime + (long)(tickDelta * 20.0F)) % 360000) * 0.000017453292F;
            better_end_sky$EndSkyRenderState.darknessModifier = 1F - BackgroundInfo.darknessModifier;
        }
    }

    @Inject(method = "renderEndSky", at = @At("HEAD"), cancellable = true)
    public void renderEndSky(CallbackInfo ci) {
        if (isDisabled()) return;
        if (!hasBetterSky(better_end_sky$currentLevel)) return;
        better_end_sky$EndSkyRenderer.render(better_end_sky$EndSkyRenderState);
        ci.cancel();
    }

    @Inject(method = "close", at = @At("TAIL"))
    public void close(CallbackInfo ci) {
        if (better_end_sky$EndSkyRenderer != null) {
            better_end_sky$EndSkyRenderer.close();
        }
    }
}