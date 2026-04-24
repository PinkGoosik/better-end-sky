package better_end_sky;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;

public class Mod {

    public static boolean isDisabled() {
        return FabricLoader.getInstance().isModLoaded("betterend");
    }

    public static boolean hasBetterSky(ClientLevel level) {
        return level.dimension() == Level.END;
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath("better_end_sky", path);
    }
}