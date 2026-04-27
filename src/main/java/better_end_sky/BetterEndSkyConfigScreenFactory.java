package better_end_sky;

import net.minecraft.client.gui.screens.Screen;

public class BetterEndSkyConfigScreenFactory {
    public static Screen create(Screen parent) {
        return new BetterEndSkyConfigScreen(parent, BetterEndSkyConfig.create());
    }
}