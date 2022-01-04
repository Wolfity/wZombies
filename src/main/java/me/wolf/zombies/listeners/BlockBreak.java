package me.wolf.zombies.listeners;

import me.wolf.zombies.ZombiePlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreak implements Listener {

    private final ZombiePlugin plugin;

    public BlockBreak(final ZombiePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBreak(final BlockBreakEvent event) {
        event.setCancelled(plugin.getPlayerManager().getZombiePlayer(event.getPlayer().getUniqueId()) != null);
    }

}
