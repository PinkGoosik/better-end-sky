package better_end_sky;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;

public class Mod {

    public static boolean isDisabled() {
        return FabricLoader.getInstance().isModLoaded("betterend");
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath("better_end_sky", path);
    }
}
