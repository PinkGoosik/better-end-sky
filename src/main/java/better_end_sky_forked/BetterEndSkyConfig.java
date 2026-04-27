package better_end_sky;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class BetterEndSkyConfig {
    private boolean enabled = true;
    private static BetterEndSkyConfig INSTANCE;
    private static final String CONFIG_FILE = "better_end_sky.json";

    public static BetterEndSkyConfig create() {
        if (INSTANCE == null) {
            INSTANCE = new BetterEndSkyConfig();
            INSTANCE.load();
        }
        return INSTANCE;
    }

    public static BetterEndSkyConfig createDefault() {
        if (INSTANCE == null) {
            INSTANCE = new BetterEndSkyConfig();
        }
        return INSTANCE;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void load() {
        try {
            Path configPath = getConfigPath();
            if (Files.exists(configPath)) {
                String content = Files.readString(configPath).trim();
                if (content.contains("\"enabled\": false") || content.equals("false")) {
                    this.enabled = false;
                }
            }
        } catch (Exception e) {
            this.enabled = true;
        }
    }

    private Path getConfigPath() {
        return net.fabricmc.loader.api.FabricLoader.getInstance()
                .getConfigDir()
                .resolve(CONFIG_FILE);
    }
}