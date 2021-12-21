package me.wolf.zombies.player;

import java.util.Objects;
import java.util.UUID;

public class ZombiePlayer {

    private final UUID uuid;
    private double coins;
    private boolean isDown;

    public ZombiePlayer(final UUID uuid) {
        this.uuid = uuid;
        this.coins = 0;
        this.isDown = false;

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
