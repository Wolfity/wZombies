package me.wolf.zombies.listeners;

import me.wolf.zombies.ZombiePlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlace implements Listener {

    private final ZombiePlugin plugin;

    public BlockPlace(final ZombiePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlace(final BlockPlaceEvent event) {
        event.setCancelled(plugin.getPlayerManager().getZombiePlayer(event.getPlayer().getUniqueId()) != null);
    }
}
