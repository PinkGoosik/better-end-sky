package better_end_sky;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;

public class Mod {

    private static final BetterEndSkyConfig CONFIG = FabricLoader.getInstance().getModContainer("better_end_sky")
            .map(container -> BetterEndSkyConfig.create())
            .orElse(BetterEndSkyConfig.createDefault());

    public static boolean isDisabled() {
        return FabricLoader.getInstance().isModLoaded("betterend") || !CONFIG.isEnabled();
    }

    public static boolean hasBetterSky(ClientLevel level) {
        return level.dimension() == Level.END;
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath("better_end_sky", path);
    }
}