package me.wolf.zombies.player;

import me.wolf.zombies.ZombiePlugin;
import me.wolf.zombies.gun.Gun;
import me.wolf.zombies.perks.Perk;
import me.wolf.zombies.utils.Utils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@SuppressWarnings("ConstantConditions")
public class ZombiePlayer {

    private final UUID uuid;
    private double coins;
    private boolean isDown;
    private final Set<Gun> gunsList = new HashSet<>();
    private final Set<Perk> perksList = new HashSet<>();

    public ZombiePlayer(final UUID uuid, final ZombiePlugin plugin) {
        this.uuid = uuid;
        this.coins = 0;
        this.isDown = false;

        // initialising guns
        final FileConfiguration gunsCfg = plugin.getFileManager().getGunsConfig().getConfig();
        for (final String gun : gunsCfg.getConfigurationSection("guns").getKeys(false)) {
            final String identifier = gunsCfg.getString("guns." + gun + ".identifier");
            final String gunName = Utils.colorize(gunsCfg.getString("guns." + gun + ".name"));
            final double damage = gunsCfg.getInt("guns." + gun + ".damage");
            final Material icon = Material.valueOf(gunsCfg.getString("guns." + gun + ".material"));
            final int ammoID = gunsCfg.getInt("guns." + gun + ".ammoID");
            final int ammoAmount = gunsCfg.getInt("guns." + gun + ".ammo-amount");
            final int fireRate = gunsCfg.getInt("guns." + gun + ".firerate");
            final int maxLevel = gunsCfg.getInt("guns." + gun + ".max-level");
            final int price =  gunsCfg.getInt("guns." + gun + ".price");
            gunsList.add(new Gun(identifier, gunName, damage, icon, ammoID, ammoAmount, ammoAmount, fireRate, maxLevel, price));
        }

        final FileConfiguration perksCfg = plugin.getFileManager().getPerksConfig().getConfig();
        for(final String perk : perksCfg.getConfigurationSection("perks").getKeys(false)) {
            final String name = perksCfg.getString(Utils.colorize("perks." + perk + ".name"));
            final String identifier = perksCfg.getString("perks." + perk + ".identifier");
            final int maxLevel = perksCfg.getInt("perks." + perk + ".max-level");
            final double price = perksCfg.getDouble("perks." + perk + ".price");
            final double priceMultiplier = perksCfg.getDouble("perks." + perk + ".price-multiplier");
            final Material icon = Material.valueOf(perksCfg.getString("perks." + perk + ".icon"));
            final boolean enabled = perksCfg.getBoolean("perks." + perk + ".enabled");

            perksList.add(new Perk(icon, identifier, name, maxLevel, price, priceMultiplier, enabled));

        }
    }

    // get the gun by passing in the ammo ID
    public Gun getGunByIdentifier(final String identifier) {
        for (final Gun gun : gunsList) {
            if (gun.getIdentifier().equalsIgnoreCase(identifier)) {
                return gun;
            }
        }
        return null;
    }
    // get a perk by its name
    public Perk getPerkByIdentifier(final String identifier) {
        for(final Perk perk : perksList) {
            if(perk.getIdentifier().equalsIgnoreCase(identifier)) {
                return perk;
            }
        }
        return null;
    }

    public UUID getUuid() {
        return uuid;
    }

    public double getCoins() {
        return coins;
    }

    public boolean isDown() {
        return isDown;
    }

    public void setDown(boolean down) {
        isDown = down;
    }


    public void addCoins(final int amount) {
        this.coins = coins + amount;
    }

    public Set<Gun> getGunList() {
        return gunsList;
    }

    public void removeCoins(final double amount) {
        this.coins = coins - amount;
    }

    public Set<Perk> getPerksList() {
        return perksList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ZombiePlayer that = (ZombiePlayer) o;
        return uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
