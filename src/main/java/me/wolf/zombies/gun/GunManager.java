package me.wolf.zombies.gun;

import me.wolf.zombies.ZombiePlugin;
import me.wolf.zombies.utils.Utils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashSet;
import java.util.Set;

public class GunManager {

    private final ZombiePlugin plugin;
    private final Set<Gun> guns = new HashSet<>();

    public GunManager(final ZombiePlugin plugin) {
        this.plugin = plugin;
    }

    public void initGuns() {
        final FileConfiguration gunsCfg = plugin.getFileManager().getGunsConfig().getConfig();
        for (final String gun : gunsCfg.getConfigurationSection("guns").getKeys(false)) {
            final String gunName = Utils.colorize(gunsCfg.getString("guns." + gun + ".name"));
            final double damage = gunsCfg.getInt("guns." + gun + ".damage");
            final Material icon = Material.valueOf(gunsCfg.getString("guns." + gun + ".material"));
            final int ammoID = gunsCfg.getInt("guns." + gun + ".ammoID");
            final int ammoAmount = gunsCfg.getInt("guns." + gun + ".ammo-amount");
            final int fireRate = gunsCfg.getInt("guns." + gun + ".firerate");
            final int maxLevel = gunsCfg.getInt("guns." + gun + ".max-level");
            final int price = gunsCfg.getInt("guns." + gun + ".price");
            guns.add(new Gun(gun, gunName, damage, icon, ammoID, ammoAmount, ammoAmount, fireRate, maxLevel, price));
        }

    }

    // get the gun by passing in the ammo ID
    public Gun getGunByIdentifier(final String identifier) {
        for (final Gun gun : guns) {
            if (gun.getIdentifier().equalsIgnoreCase(identifier)) {
                return gun;
            }
        }
        return null;
    }

    public Set<Gun> getGuns() {
        return guns;
    }
}
