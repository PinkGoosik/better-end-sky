package better_end_sky.mixin;

import better_end_sky.Mod;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.renderpearl.api.commands.RenderPass;
import net.minecraft.client.renderer.SkyRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SkyRenderer.class)
public class SkyRendererMixin {

    @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SkyRenderer;renderEndSky(Lcom/mojang/renderpearl/api/commands/RenderPass;)V"))
    private static boolean renderEndSky(SkyRenderer instance, RenderPass renderPass) {
        Mod.endSkyRenderer.render(Mod.endSkyRenderState, renderPass);
        return false;
    }

    @Inject(method = "close", at = @At("TAIL"))
    void closeCustom(CallbackInfo ci) {
        Mod.endSkyRenderer.close();
    }

}