package me.wolf.zombies.listeners;

import me.wolf.zombies.ZombiePlugin;
import me.wolf.zombies.player.ZombiePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EntityDamage implements Listener {

    private final ZombiePlugin plugin;

    public EntityDamage(final ZombiePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerLobbyDamage(final EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player && event.getDamager() instanceof Player)) return;

        event.setCancelled(plugin.getPlayerManager().getZombiePlayer(event.getEntity().getUniqueId()) != null);


    }

}
