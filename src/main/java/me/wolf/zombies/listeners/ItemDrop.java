package me.wolf.zombies.listeners;

import me.wolf.zombies.ZombiePlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class ItemDrop implements Listener {

    private final ZombiePlugin plugin;

    public ItemDrop(final ZombiePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDrop(final PlayerDropItemEvent event) {
        event.setCancelled(plugin.getPlayerManager().getZombiePlayer(event.getPlayer().getUniqueId()) != null);
    }

}
