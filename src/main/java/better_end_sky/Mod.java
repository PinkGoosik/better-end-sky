package better_end_sky;

import better_end_sky.render.EndSkyRenderState;
import better_end_sky.render.EndSkyRenderer;
//import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.dimension.DimensionType;

public class Mod {

    public static final EndSkyRenderer endSkyRenderer = new EndSkyRenderer();
    public static final EndSkyRenderState endSkyRenderState = new EndSkyRenderState();

    public static boolean isDisabled() {
//        return FabricLoader.getInstance().isModLoaded("betterend");
        return false;
    }

    public static boolean hasBetterSky(ClientLevel level) {
        return level.dimensionType().skybox() == DimensionType.Skybox.END;
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath("better_end_sky", path);
    }
}
