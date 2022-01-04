package me.wolf.zombies.perks;

import me.wolf.zombies.ZombiePlugin;
import me.wolf.zombies.utils.Utils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashSet;
import java.util.Set;

public class PerkManager {

    private final ZombiePlugin plugin;
    private final Set<Perk> perks = new HashSet<>();

    public PerkManager(final ZombiePlugin plugin) {
        this.plugin = plugin;
    }

    public void initPerks() {
        final FileConfiguration perksCfg = plugin.getFileManager().getPerksConfig().getConfig();
        for (final String perk : perksCfg.getConfigurationSection("perks").getKeys(false)) {
            final String name = perksCfg.getString(Utils.colorize("perks." + perk + ".name"));
            final String identifier = perksCfg.getString("perks." + perk + ".identifier");
            final int maxLevel = perksCfg.getInt("perks." + perk + ".max-level");
            final double price = perksCfg.getDouble("perks." + perk + ".price");
            final double priceMultiplier = perksCfg.getDouble("perks." + perk + ".price-multiplier");
            final Material icon = Material.valueOf(perksCfg.getString("perks." + perk + ".icon"));
            final boolean enabled = perksCfg.getBoolean("perks." + perk + ".enabled");

            perks.add(new Perk(icon, identifier, name, maxLevel, price, priceMultiplier, enabled));

        }
    }

    // get a perk by its name
    public Perk getPerkByIdentifier(final String identifier) {
        for (final Perk perk : perks) {
            if (perk.getIdentifier().equalsIgnoreCase(identifier)) {
                return perk;
            }
        }
        return null;
    }

    public Set<Perk> getPerks() {
        return perks;
    }
}
