package me.wolf.zombies.player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager {

    private final Map<UUID, ZombiePlayer> zombiePlayers = new HashMap<>();

    public void addZombiePlayer(final UUID uuid) {
        this.zombiePlayers.put(uuid, new ZombiePlayer(uuid));
    }

    // returns null if the player doesnt exist
    public ZombiePlayer getZombiePlayer(final UUID uuid) {
        return this.zombiePlayers.get(uuid);
    }

    public void removeZombiePlayer(final UUID uuid) {
        this.zombiePlayers.remove(uuid);
    }

}
