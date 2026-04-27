package better_end_sky;

import better_end_sky.BetterEndSkyConfig;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.network.chat.Component;

public class BetterEndSkyConfigScreen extends Screen {
    private final Screen parent;
    private final BetterEndSkyConfig config;
    private CycleButton<Boolean> enabledButton;

    public BetterEndSkyConfigScreen(Screen parent, BetterEndSkyConfig config) {
        super(Component.literal("Better End Sky Config"));
        this.parent = parent;
        this.config = config;
    }

    @Override
    protected void init() {
        super.init();

        this.enabledButton = this.addRenderableWidget(
                CycleButton.booleanBuilder(Component.literal("Enabled"), Component.literal("Disabled"), this.config.isEnabled())
                        .create(this.width / 2 - 155, this.height / 4, 150, 20, Component.literal("Mod Enabled"), (button, value) -> {
                            this.config.setEnabled(value);
                        })
        );

        this.addRenderableWidget(
                Button.builder(Component.literal("Done"), (button) -> {
                    this.onClose();
                }).bounds(this.width / 2 - 155 + 160, this.height / 4, 150, 20).build()
        );

        this.addRenderableWidget(
                Button.builder(Component.literal("Back"), (button) -> {
                    this.minecraft.setScreen(this.parent);
                }).bounds(this.width / 2 - 155, this.height / 4 + 40, 310, 20).build()
        );
    }

    @Override
    public void onClose() {
        this.config.load();
        this.minecraft.setScreen(this.parent);
    }
}