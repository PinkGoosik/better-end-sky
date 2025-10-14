package better_end_sky;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.resources.ResourceLocation;

public class Mod {

    public static boolean isDisabled() {
        return FabricLoader.getInstance().isModLoaded("betterend");
    }

    public static boolean hasBetterSky(ClientLevel level) {
        return level.effects().skyType() == DimensionSpecialEffects.SkyType.END;
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath("better_end_sky", path);
    }
}
