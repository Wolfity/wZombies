package me.wolf.zombies.player;

import me.wolf.zombies.gun.Gun;
import me.wolf.zombies.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class ZombiePlayer {

    private final UUID uuid;
    private double coins;
    private boolean isDown;
    private Set<Gun> guns;

    public ZombiePlayer(final UUID uuid) {
        this.uuid = uuid;
        this.coins = 0;
        this.isDown = false;
        this.guns = new HashSet<>();
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

    public void removeCoins(final double amount) {
        this.coins = coins - amount;
    }

    public Set<Gun> getGuns() {
        return guns;
    }

    public void setGuns(Set<Gun> guns) {
        this.guns = guns;
    }

    public Player getBukkitPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public void sendMessage(final String msg) {
        getBukkitPlayer().sendMessage(Utils.colorize(msg));
    }

    public void teleport(final Location location) {
        getBukkitPlayer().teleport(location);
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
