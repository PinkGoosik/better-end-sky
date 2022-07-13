package better_end_sky;

import better_end_sky.render.EndSkyRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class Mod implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        if(FabricLoader.getInstance().isModLoaded("betterend")) return;
        DimensionRenderingRegistry.registerSkyRenderer(Level.END, new EndSkyRenderer());
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation("better_end_sky", path);
    }
}
