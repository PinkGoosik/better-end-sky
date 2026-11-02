package better_end_sky.mixin;

import better_end_sky.Mod;
import better_end_sky.render.EndSkyRenderer;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {

    @Inject(method = "addSkyPass", at = @At(value = "NEW", target = "(Lnet/minecraft/client/renderer/texture/TextureManager;Lnet/minecraft/client/resources/model/sprite/AtlasManager;Lcom/mojang/blaze3d/pipeline/RenderTarget;)Lnet/minecraft/client/renderer/SkyRenderer;"))
    private static void reinitRenderer(FrameGraphBuilder frame, CameraRenderState cameraState, GpuBufferSlice skyFog, CallbackInfo ci) {
        Mod.endSkyRenderer = new EndSkyRenderer();
        Mod.endSkyRenderer.initTextures();
    }

}