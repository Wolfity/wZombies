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

import java.util.Arrays;
import java.util.List;

public class ZombieCommand extends BaseCommand {

    private final ZombiePlugin plugin;

    public ZombieCommand(final ZombiePlugin plugin) {
        super("zombies");
        this.plugin = plugin;
    }

    private final List<String> argsList = Arrays.asList("setspawn", "setlobby", "createarena", "deletearena", "setmonsterspawn", "deletemonsterspawn");

    @Override
    protected void run(CommandSender sender, String[] args) {

        final Player player = (Player) sender;

        if (args.length < 1 || args.length > 2) {
            tell(Messages.HELP);
        }
        if (isAdmin()) {
            if (args.length == 1) {
                if (argsList.contains(args[0])) {
                    tell("&bPlease specify an arena!");
                }
                if (args[0].equalsIgnoreCase("admin")) {
                    tell(Messages.ADMIN_HELP);
                } else if (args[0].equalsIgnoreCase("setworldspawn")) {
                    plugin.getConfig().set("WorldSpawn", player.getLocation());
                    plugin.saveConfig();
                    tell(Messages.SET_WORLD_SPAWN);
                }
            } else if (args.length == 2) {
                final String arenaName = args[1];
                if (args[0].equalsIgnoreCase("createarena")) {
                    plugin.getArenas().forEach(arena -> {
                        if(!arena.getName().equalsIgnoreCase(arenaName)) {
                            tell("&aCreating arena world...");
                            plugin.getArenaManager().createArena(arenaName);
                            player.teleport(new Location(Bukkit.getWorld(arenaName), 0, 80, 0));
                            tell(Messages.ARENA_CREATED.replace("{arena}", arenaName));
                        } else tell(Messages.ARENA_EXISTS);
                    });
                } else if (args[0].equalsIgnoreCase("deletearena")) {
                    if (plugin.getArenaManager().getArena(arenaName) != null) {
                        plugin.getArenaManager().deleteArena(arenaName);
                        tell(Messages.ARENA_DELETED.replace("{arena}", arenaName));
                    } else tell(Messages.ARENA_NOT_FOUND);
                } else if (args[0].equalsIgnoreCase("setlobby")) {
                    setArenaLobby(player, arenaName);
                } else if (args[0].equalsIgnoreCase("setspawn")) {
                    setGameSpawn(player, arenaName);
                } else if (args[0].equalsIgnoreCase("setmonsterspawn")) {
                    setMonsterSpawn(player, arenaName);
                } else if (args[0].equalsIgnoreCase("deletemonsterspawn")) {
                    deleteMonsterSpawn(player, plugin.getArenaManager().getArena(arenaName));
                } else if (args[0].equalsIgnoreCase("setperknpc")) {
                    setPerkNPC(player, arenaName);
                } else if(args[0].equalsIgnoreCase("removeperknpc")) {
                    removePerkNPC(player, plugin.getArenaManager().getArena(arenaName));
                } else if (args[0].equalsIgnoreCase("forcestart")) {
                    plugin.getGameManager().setGameState(GameState.LOBBY_COUNTDOWN, plugin.getArenaManager().getArena(arenaName));
                } else if (args[0].equalsIgnoreCase("tp")) {
                    if (plugin.getArenaManager().getArena(arenaName) != null) {
                        player.teleport(plugin.getArenaManager().getArena(arenaName).getWaitingRoomLoc().toBukkitLocation());
                        tell(Messages.TELEPORTED_TO_ARENA);
                    } else tell(Messages.ARENA_NOT_FOUND);
                }
            }
        }

        if (args.length == 2) {
            final String arenaName = args[1];
            if (args[0].equalsIgnoreCase("join")) {
                if (plugin.getArenaManager().getArena(arenaName) != null) {
                    plugin.getGameManager().addPlayer(player, plugin.getArenaManager().getArena(arenaName));
                }
            }
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("leave")) {
                plugin.getGameManager().removePlayer(player);
            } else if (args[0].equalsIgnoreCase("join")) {
                tell("&bPlease specify an arena!");
            } else if (args[0].equalsIgnoreCase("help")) {
                tell(Messages.HELP);
            }
        }
    }

    // setting game spawns and saving them in the arena config
    private void setGameSpawn(final Player player, final String arenaName) {
        if (plugin.getArenaManager().getArena(arenaName) == null) {
            tell(Messages.ARENA_NOT_FOUND);
        }
        final Arena arena = plugin.getArenaManager().getArena(arenaName);
        if (plugin.getArenaManager().isGameActive(arena)) {
            tell(Messages.CAN_NOT_MODIFY);
        }

        arena.getArenaConfig().set("SpawnLocation", player.getLocation().serialize());
        arena.setSpawnLocation(CustomLocation.fromBukkitLocation(player.getLocation()));

        tell(Messages.SET_GAME_SPAWN);
        player.getWorld().save();
        arena.saveArena();
    }

    // setting an arena's waiting room lobby location
    private void setArenaLobby(final Player player, final String arenaName) {
        if (plugin.getArenaManager().getArena(arenaName) == null) {
            tell(Messages.ARENA_NOT_FOUND);
        }
        final Arena arena = plugin.getArenaManager().getArena(arenaName);
        if (plugin.getArenaManager().isGameActive(arena)) {
            tell(Messages.CAN_NOT_MODIFY);
        }
        arena.getArenaConfig().set("LobbySpawn", player.getLocation().serialize());
        arena.setWaitingRoomLoc(CustomLocation.fromBukkitLocation(player.getLocation()));
        player.getWorld().save();
        arena.saveArena();
        tell(Messages.SET_LOBBY_SPAWN);
    }

    // setting game spawns for monstrs and saving them in the arena config
    private void setMonsterSpawn(final Player player, final String arenaName) {
        if (plugin.getArenaManager().getArena(arenaName) == null) tell(Messages.ARENA_NOT_FOUND);
        final Arena arena = plugin.getArenaManager().getArena(arenaName);
        if (!plugin.getArenaManager().isGameActive(arena)) {
            arena.getMonsterSpawns().add(CustomLocation.fromBukkitLocation(player.getLocation()));
            int i = 1;
            for (final CustomLocation location : plugin.getArenaManager().getArena(arenaName).getMonsterSpawns()) {
                plugin.getArenaManager().getArena(arenaName).getArenaConfig().set("monster-spawn-locations." + i, location.serialize());
                i++;
            }
            tell(Messages.SET_MONSTER_SPAWN);
            arena.saveArena();
            player.getWorld().save();
        } else {
            tell(Messages.CAN_NOT_MODIFY);
        }
    }
    // method that takes care of deleting spawnpoints
    private void deleteMonsterSpawn(final Player player, final Arena arena) {
        if (!arena.getMonsterSpawns().contains(CustomLocation.fromBukkitLocation(player.getLocation()))) {
            player.sendMessage(Messages.NO_MONSTER_SPAWN_FOUND);
        }

        arena.removeMonsterSpawn(CustomLocation.fromBukkitLocation(player.getLocation()));
        arena.getArenaConfig().set("monster-spawn-locations", null); // deleting it so it can be re-written
        int i = 1;
        for (final CustomLocation location : arena.getMonsterSpawns()) {
            arena.getArenaConfig().set("monster-spawn-locations." + i, location.serialize());
            i++;
        }

        arena.saveArena();
        tell(Messages.DELETED_MONSTER_SPAWN);
    }

    private void removePerkNPC(final Player player, final Arena arena) {
        if (!arena.getPerkShopNPCs().contains(CustomLocation.fromBukkitLocation(player.getLocation()))) {
            player.sendMessage(Messages.NO_PERN_NPC_SPAWN_FOUND);
        }
        arena.removePerkShopNPC(CustomLocation.fromBukkitLocation(player.getLocation()));
        arena.getArenaConfig().set("perk-npc-locations", null); // deleting it so it can be re-written
        int i = 1;
        for (final CustomLocation location : arena.getPerkShopNPCs()) {
            arena.getArenaConfig().set("perk-npc-locations." + i, location.serialize());
            i++;
        }
        arena.saveArena();
        tell(Messages.PERK_NPC_SPAWN_DELETED);
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
            arena.saveArena();
            player.getWorld().save();
        } else {
            tell(Messages.CAN_NOT_MODIFY);
        }
    }


}
