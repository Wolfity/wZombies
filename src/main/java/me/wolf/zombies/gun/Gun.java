package me.wolf.zombies.gun;

import org.bukkit.Material;

import java.util.Objects;

public class Gun {

    private final String identifier, name;
    private double damage;
    private final Material icon;
    private final int ammoID, maxLevel, fireRate, defaultAmount, price;
    private int ammoAmount, level;

    public Gun(final String identifier, final String name, final double damage, final Material icon, final int ammoID, final int ammoAmount, final int defaultAmount, final int fireRate, final int maxLevel, final int price) {
        this.identifier = identifier;
        this.name = name;
        this.damage = damage;
        this.icon = icon;
        this.ammoID = ammoID;
        this.ammoAmount = ammoAmount;
        this.defaultAmount = defaultAmount;
        this.fireRate = fireRate;
        this.maxLevel = maxLevel;
        this.level = 1;
        this.price = price;
    }

    public int getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }

    public double getDamage() {
        return damage;
    }

    public Material getIcon() {
        return icon;
    }

    public int getAmmoID() {
        return ammoID;
    }

    public void decrementAmmo() {
        ammoAmount--;
    }

    public String getIdentifier() {
        return identifier;
    }

    public int getAmmoAmount() {
        return ammoAmount;
    }

    public void setAmmo(final int amount) {
        this.ammoAmount = ammoAmount + amount;
    }

    public int getDefaultAmount() {
        return defaultAmount;
    }

    public int getFireRate() {
        return fireRate;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public void incrementLevel() {
        level++;
    }

    public int getLevel() {
        return this.level;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public double getNewDamage() {
        this.damage = damage * 1.2;
        return damage * 1.2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Gun gun = (Gun) o;
        return name.equals(gun.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

}
