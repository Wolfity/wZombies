package me.wolf.zombies.listeners;

import me.wolf.zombies.ZombiePlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class FoodChange implements Listener {

    private final ZombiePlugin plugin;
    public FoodChange(final ZombiePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onFoodChange(final FoodLevelChangeEvent event) {
        if(!(event.getEntity() instanceof Player)) return;
        if(!plugin.getZombiePlayers().containsKey(event.getEntity().getUniqueId())) return;
        event.setCancelled(true);
    }
}
