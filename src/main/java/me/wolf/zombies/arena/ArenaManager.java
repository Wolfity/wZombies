package me.wolf.zombies.arena;

import me.wolf.zombies.ZombiePlugin;
import me.wolf.zombies.player.ZombiePlayer;
import me.wolf.zombies.utils.CustomLocation;
import me.wolf.zombies.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftZombie;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Villager;

import java.io.File;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@SuppressWarnings("ConstantConditions")
public class ArenaManager {

    private final ZombiePlugin plugin;

    public ArenaManager(final ZombiePlugin plugin) {
        this.plugin = plugin;
    }
    private final Set<Arena> arenas = new HashSet<>();

    public Arena createArena(final String arenaName) {
        for (final Arena arena : arenas)
            if (arena.getName().equalsIgnoreCase(arenaName))
                return getArena(arenaName);

        final Arena arena = new Arena(arenaName, 10, 1, 4, plugin);
        arena.createConfig(arenaName);


        this.arenas.add(arena);
        final World arenaWorld = Bukkit.createWorld(new WorldCreator(arenaName));
        arenaWorld.setAutoSave(false);

        return arena;
    }

    public void deleteArena(final String name) {
        final Arena arena = getArena(name);
        if (arena == null) return;

        arena.getArenaConfigFile().delete();
        this.arenas.remove(arena);

        Bukkit.getWorld(name).getPlayers().stream().filter(Objects::nonNull).forEach(player -> player.teleport((Location) plugin.getConfig().get("WorldSpawn")));
        final World world = Bukkit.getWorld(name);
        Bukkit.unloadWorld(world, false);
        final File world_folder = new File(plugin.getServer().getWorldContainer() + File.separator + name + File.separator);
        deleteMap(world_folder);
    }


    // get an arena by passing in it's name
    public Arena getArena(final String name) {
        for (final Arena arena : arenas)
            if (arena.getName().equalsIgnoreCase(name))
                return arena;

        return null;
    }

    // getting an arena by passing in a player, looping over all arenas to see if the player is in there
    public Arena getArenaByPlayer(final ZombiePlayer zombiePlayer) {
        for (final Arena arena : arenas) {
            if (arena.getArenaMembers().contains(zombiePlayer)) {
                return arena;
            }
        }
        return null;
    }

    // clearing the arena from custom mobs
    public void clearArena(final Arena arena) {
        final World world = arena.getSpawnLocation().toBukkitLocation().getWorld();
        arena.getArenaMonsterList().clear();
        for (final Entity entity : world.getEntities()) {
            if (entity instanceof Villager) {
                if (entity.getCustomName().equalsIgnoreCase(Utils.colorize("&b&lPerk Shop"))) {
                    entity.remove();
                }
            } else if (entity instanceof CraftZombie) {
                entity.remove();
            }
        }

        Bukkit.unloadWorld(arena.getWaitingRoomLoc().

                toBukkitLocation().

                getWorld(), false);
        final World arenaWorld = Bukkit.createWorld(new WorldCreator(arena.getName()));
        arenaWorld.setAutoSave(false);
    }

    public boolean isGameActive(final Arena arena) {
        return arena.getArenaState() == ArenaState.INGAME ||
                arena.getArenaState() == ArenaState.END ||
                arena.getArenaState() == ArenaState.COUNTDOWN;
    }

    public void loadArenas() {
        final File folder = new File(plugin.getDataFolder() + "/arenas");

        if (folder.listFiles() == null) {
            Bukkit.getLogger().info("&3No arenas has been found!");
            return;
        }

        for (final File file : Objects.requireNonNull(folder.listFiles())) {
            final Arena arena = createArena(file.getName().replace(".yml", ""));
            final FileConfiguration cfg = arena.getArenaConfig();
            arena.setWaitingRoomLoc(CustomLocation.fromBukkitLocation(new Location(
                    Bukkit.getWorld(cfg.getString("LobbySpawn.world")),
                    cfg.getDouble("LobbySpawn.x"),
                    cfg.getDouble("LobbySpawn.y"),
                    cfg.getDouble("LobbySpawn.z"),
                    (float) cfg.getDouble("LobbySpawn.pitch"),
                    (float) cfg.getDouble("LobbySpawn.yaw"))));

            arena.setSpawnLocation(CustomLocation.fromBukkitLocation(new Location(
                    Bukkit.getWorld(cfg.getString("SpawnLocation.world")),
                    cfg.getDouble("SpawnLocation.x"),
                    cfg.getDouble("SpawnLocation.y"),
                    cfg.getDouble("SpawnLocation.z"),
                    (float) cfg.getDouble("SpawnLocation.pitch"),
                    (float) cfg.getDouble("SpawnLocation.yaw"))));

            final int minPlayers = cfg.getInt("min-players");
            final int maxPlayers = cfg.getInt("max-players");
            final int lobbyCountdown = cfg.getInt("lobby-countdown");

            for (final String key : arena.getArenaConfig().getConfigurationSection("monster-spawn-locations").getKeys(false)) {
                arena.addMonsterSpawn(CustomLocation.deserialize(arena.getArenaConfig().getString("monster-spawn-locations." + key)));
            }

            for (final String key : arena.getArenaConfig().getConfigurationSection("perk-npc-locations").getKeys(false)) {
                arena.addShopNPC(CustomLocation.deserialize(arena.getArenaConfig().getString("perk-npc-locations." + key)));
            }

            arena.setMinPlayer(minPlayers);
            arena.setMaxPlayers(maxPlayers);
            arena.setLobbyCountdown(lobbyCountdown);
            Bukkit.getLogger().info("&aLoaded arena &e" + arena.getName());


        }
    }


    private void deleteMap(final File dir) {
        final File[] files = dir.listFiles();

        for (final File file : files) {
            if (file.isDirectory()) {
                this.deleteMap(file);
            }
            file.delete();
        }

        dir.delete();
    }

}
