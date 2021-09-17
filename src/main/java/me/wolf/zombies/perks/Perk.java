package me.wolf.zombies.perks;

import org.bukkit.Material;

public class Perk {

    private final Material icon;
    private final String identifier, name;
    private int level;
    private final int maxLevel;
    private double price;
    private final double priceMultiplier;
    private final boolean enabled;


    public Perk(final Material icon, final String identifier, final String name, final int maxLevel, final double price, final double priceMultiplier, final boolean enabled) {
        this.icon = icon;
        this.identifier = identifier;
        this.name = name;
        this.maxLevel = maxLevel;
        this.level = 1;
        this.priceMultiplier = priceMultiplier;
        this.price = price;
        this.enabled = enabled;
    }

    public Material getIcon() {
        return icon;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public void incrementLevel() {
        level++;
    }


    public double getPriceMultiplier() {
        return priceMultiplier;
    }

    public double getPrice() {
        return price;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
