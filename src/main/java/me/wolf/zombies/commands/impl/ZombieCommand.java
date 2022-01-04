package me.wolf.zombies.commands.impl;

import me.wolf.zombies.ZombiePlugin;
import me.wolf.zombies.arena.Arena;
import me.wolf.zombies.commands.BaseCommand;
import me.wolf.zombies.constants.Messages;
import me.wolf.zombies.game.GameState;
import me.wolf.zombies.utils.CustomLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ZombieCommand extends BaseCommand {

    private final ZombiePlugin plugin;
    private final List<String> argsList = Arrays.asList("setspawn", "setlobby", "createarena", "deletearena", "setmonsterspawn", "deletemonsterspawn");

    public ZombieCommand(final ZombiePlugin plugin) {
        super("zombies");
        this.plugin = plugin;
    }

    @Override
    protected void run(CommandSender sender, String[] args) {

        final Player player = (Player) sender;

        if (args.length < 1 || args.length > 2) {
            tell(Messages.HELP);
        }

        if (args.length == 1) {
            if (argsList.contains(args[0])) {
                tell("&bPlease specify an arena!");
            }
            if (args[0].equalsIgnoreCase("admin")) {
                if (isAdmin()) tell(Messages.ADMIN_HELP);
            } else if (args[0].equalsIgnoreCase("setworldspawn")) {
                if (isAdmin()) {
                    plugin.getConfig().set("WorldSpawn", player.getLocation());
                    plugin.saveConfig();
                    tell(Messages.SET_WORLD_SPAWN);
                }
            } else if (args[0].equalsIgnoreCase("leave")) {
                plugin.getGameManager().removePlayer(player);
            } else if (args[0].equalsIgnoreCase("join")) {
                tell("&bPlease specify an arena!");
            } else if (args[0].equalsIgnoreCase("help")) {
                tell(Messages.HELP);
            }

        } else if (args.length == 2) {
            final String arenaName = args[1];
            if (args[0].equalsIgnoreCase("createarena")) {
                if (isAdmin()) {
                    if (plugin.getArenaManager().getArena(arenaName) == null) {
                        tell("&aCreating arena world...");
                        plugin.getArenaManager().createArena(arenaName);
                        player.teleport(new Location(Bukkit.getWorld(arenaName), 0, 80, 0));
                        tell(Messages.ARENA_CREATED.replace("{arena}", arenaName));
                    } else tell(Messages.ARENA_EXISTS);
                }
            } else if (args[0].equalsIgnoreCase("deletearena")) {
                if (isAdmin()) {
                    if (plugin.getArenaManager().getArena(arenaName) != null) {
                        plugin.getArenaManager().deleteArena(arenaName);
                        tell(Messages.ARENA_DELETED.replace("{arena}", arenaName));
                    } else tell(Messages.ARENA_NOT_FOUND);
                }
            } else if (args[0].equalsIgnoreCase("setlobby")) {
                if (isAdmin()) setArenaLobby(player, arenaName);

            } else if (args[0].equalsIgnoreCase("setspawn")) {
                if (isAdmin()) setGameSpawn(player, arenaName);

            } else if (args[0].equalsIgnoreCase("setmonsterspawn")) {
                if (isAdmin()) setMonsterSpawn(player, arenaName);

            } else if (args[0].equalsIgnoreCase("deletemonsterspawn")) {
                if (isAdmin()) deleteMonsterSpawn(player, plugin.getArenaManager().getArena(arenaName));

            } else if (args[0].equalsIgnoreCase("setperknpc")) {
                if (isAdmin()) setPerkNPC(player, arenaName);

            } else if (args[0].equalsIgnoreCase("removeperknpc")) {
                if (isAdmin()) removePerkNPC(player, plugin.getArenaManager().getArena(arenaName));

            } else if (args[0].equalsIgnoreCase("forcestart")) {
                if (isAdmin())
                    plugin.getGameManager().setGameState(GameState.LOBBY_COUNTDOWN, plugin.getArenaManager().getArena(arenaName));

            } else if (args[0].equalsIgnoreCase("tp")) {
                if (isAdmin()) {
                    if (plugin.getArenaManager().getArena(arenaName) != null) {
                        player.teleport(plugin.getArenaManager().getArena(arenaName).getWaitingRoomLoc().toBukkitLocation());
                        tell(Messages.TELEPORTED_TO_ARENA);
                    } else tell(Messages.ARENA_NOT_FOUND);
                }
            } else if (args[0].equalsIgnoreCase("join")) {
                final Arena arena = plugin.getArenaManager().getArena(arenaName);
                if (arena != null) {
                    plugin.getGameManager().addPlayer(player, plugin.getArenaManager().getArena(arenaName));
                } else tell("&cThis arena does not exist");
            }
        }

    }

    // setting game spawns and saving them in the arena config
    private void setGameSpawn(final Player player, final String arenaName) {
        if (plugin.getArenaManager().getArena(arenaName) == null) {
            tell(Messages.ARENA_NOT_FOUND);
        } else {
            final Arena arena = plugin.getArenaManager().getArena(arenaName);
            if (plugin.getArenaManager().isGameActive(arena)) {
                tell(Messages.CAN_NOT_MODIFY);
            } else {

                arena.getArenaConfig().set("SpawnLocation", player.getLocation().serialize());
                arena.setSpawnLocation(CustomLocation.fromBukkitLocation(player.getLocation()));

                tell(Messages.SET_GAME_SPAWN);

                saveConfig(arena);
            }
        }
    }

    // setting an arena's waiting room lobby location
    private void setArenaLobby(final Player player, final String arenaName) {
        if (plugin.getArenaManager().getArena(arenaName) == null) {
            tell(Messages.ARENA_NOT_FOUND);
        } else {
            final Arena arena = plugin.getArenaManager().getArena(arenaName);
            if (plugin.getArenaManager().isGameActive(arena)) {
                tell(Messages.CAN_NOT_MODIFY);
            } else {
                arena.getArenaConfig().set("LobbySpawn", player.getLocation().serialize());
                arena.setWaitingRoomLoc(CustomLocation.fromBukkitLocation(player.getLocation()));

                saveConfig(arena);
                tell(Messages.SET_LOBBY_SPAWN);
            }
        }
    }

    // setting game spawns for monstrs and saving them in the arena config
    private void setMonsterSpawn(final Player player, final String arenaName) {
        if (plugin.getArenaManager().getArena(arenaName) != null) {
            final Arena arena = plugin.getArenaManager().getArena(arenaName);
            if (!plugin.getArenaManager().isGameActive(arena)) {
                arena.getMonsterSpawns().add(CustomLocation.fromBukkitLocation(player.getLocation()));
                int i = 1;
                for (final CustomLocation location : plugin.getArenaManager().getArena(arenaName).getMonsterSpawns()) {
                    plugin.getArenaManager().getArena(arenaName).getArenaConfig().set("monster-spawn-locations." + i, location.serialize());
                    i++;
                }
                tell(Messages.SET_MONSTER_SPAWN);
                saveConfig(arena);

            } else {
                tell(Messages.CAN_NOT_MODIFY);
            }
        } else tell(Messages.ARENA_NOT_FOUND);

    }

    // method that takes care of deleting spawnpoints
    private void deleteMonsterSpawn(final Player player, final Arena arena) {
        if (!arena.getMonsterSpawns().contains(CustomLocation.fromBukkitLocation(player.getLocation()))) {
            player.sendMessage(Messages.NO_MONSTER_SPAWN_FOUND);
        } else {

            arena.removeMonsterSpawn(CustomLocation.fromBukkitLocation(player.getLocation()));
            arena.getArenaConfig().set("monster-spawn-locations", null); // deleting it so it can be re-written
            int i = 1;
            for (final CustomLocation location : arena.getMonsterSpawns()) {
                arena.getArenaConfig().set("monster-spawn-locations." + i, location.serialize());
                i++;
            }

            saveConfig(arena);
            tell(Messages.DELETED_MONSTER_SPAWN);
        }
    }

    private void removePerkNPC(final Player player, final Arena arena) {
        if (!arena.getPerkShopNPCs().contains(CustomLocation.fromBukkitLocation(player.getLocation()))) {
            player.sendMessage(Messages.NO_PERN_NPC_SPAWN_FOUND);
        } else {
            arena.removePerkShopNPC(CustomLocation.fromBukkitLocation(player.getLocation()));
            arena.getArenaConfig().set("perk-npc-locations", null); // deleting it so it can be re-written
            int i = 1;
            for (final CustomLocation location : arena.getPerkShopNPCs()) {
                arena.getArenaConfig().set("perk-npc-locations." + i, location.serialize());
                i++;
            }
            saveConfig(arena);
            tell(Messages.PERK_NPC_SPAWN_DELETED);
        }
    }

    // setting game spawns for monsters and saving them in the arena config
    private void setPerkNPC(final Player player, final String arenaName) {
        if (plugin.getArenaManager().getArena(arenaName) == null) tell(Messages.ARENA_NOT_FOUND);
        final Arena arena = plugin.getArenaManager().getArena(arenaName);
        if (!plugin.getArenaManager().isGameActive(arena)) {
            arena.getPerkShopNPCs().add(CustomLocation.fromBukkitLocation(player.getLocation()));
            int i = 1;
            for (final CustomLocation location : plugin.getArenaManager().getArena(arenaName).getPerkShopNPCs()) {
                plugin.getArenaManager().getArena(arenaName).getArenaConfig().set("perk-npc-locations." + i, location.serialize());
                i++;
            }
            tell(Messages.SET_PERK_NPC_SPOT);
            saveConfig(arena);

        } else {
            tell(Messages.CAN_NOT_MODIFY);
        }
    }

    private void saveConfig(final Arena arena) {
        try {
            arena.getArenaConfig().save(arena.getArenaConfigFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
