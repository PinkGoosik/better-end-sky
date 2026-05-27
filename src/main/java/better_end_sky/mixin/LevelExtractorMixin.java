package better_end_sky.mixin;

import better_end_sky.Mod;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.extract.LevelExtractor;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static better_end_sky.Mod.isDisabled;


@Mixin(LevelExtractor.class)
public class LevelExtractorMixin {


    @Shadow
    private @Nullable ClientLevel level;

    @Inject(method = "extract", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SkyRenderer;extractRenderState(Lnet/minecraft/client/multiplayer/ClientLevel;FLnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/state/level/SkyRenderState;)V"))
    public void extractEndSkyRenderState(DeltaTracker deltaTracker, Camera camera, float deltaPartialTick, CallbackInfo ci) {
        if (isDisabled()) return;
        Mod.endSkyRenderer.extractRenderState(level, Mod.endSkyRenderState);
    }

    @Inject(method = "extract", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/state/level/LevelRenderState;reset()V"))
    public void renderTailHook(DeltaTracker deltaTracker, Camera camera, float deltaPartialTick, CallbackInfo ci) {
        if (isDisabled()) return;
        Mod.endSkyRenderState.reset();
    }

    @Inject(method = "onResourceManagerReload", at = @At("TAIL"))
    void reloadTextures(ResourceManager resourceManager, CallbackInfo ci) {
        Mod.endSkyRenderer.initTextures();
    }

}
