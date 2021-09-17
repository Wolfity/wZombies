package me.wolf.zombies.listeners;

import me.wolf.zombies.ZombiePlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuit implements Listener {

    private final ZombiePlugin plugin;
    public PlayerQuit(final ZombiePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        if (plugin.getZombiePlayers().containsKey(player.getUniqueId())) {
            plugin.getGameManager().removePlayer(player);
        }
    }

}
