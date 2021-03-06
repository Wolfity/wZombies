package me.wolf.zombies.files;

import me.wolf.zombies.ZombiePlugin;
import me.wolf.zombies.utils.Utils;
import org.bukkit.Bukkit;

public class FileManager {

    private YamlConfig gunsConfig, perksConfig;

    public FileManager(final ZombiePlugin plugin) {
        try {
            gunsConfig = new YamlConfig("guns.yml", plugin);
            perksConfig = new YamlConfig("perks.yml", plugin);
        } catch (final Exception e) {
            Bukkit.getLogger().info(Utils.colorize("&4Something went wrong while loading the yml files"));
        }

    }

    public void reloadConfigs() {
        gunsConfig.reloadConfig();

    }

    public YamlConfig getGunsConfig() {
        return gunsConfig;
    }

    public YamlConfig getPerksConfig() {
        return perksConfig;
    }
}
