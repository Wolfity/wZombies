package me.wolf.zombies.game;

import me.wolf.zombies.ZombiePlugin;
import me.wolf.zombies.arena.Arena;
import me.wolf.zombies.arena.ArenaState;
import me.wolf.zombies.constants.Messages;
import me.wolf.zombies.gun.Gun;
import me.wolf.zombies.monsters.ZombieMonster;
import me.wolf.zombies.perks.PerkNPC;
import me.wolf.zombies.player.ZombiePlayer;
import me.wolf.zombies.utils.CustomLocation;
import me.wolf.zombies.utils.Utils;
import net.minecraft.server.v1_16_R3.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;
import java.util.Random;

@SuppressWarnings("ConstantConditions")
public class GameManager {

    private final ZombiePlugin plugin;
    private GameState gameState;

    public GameManager(final ZombiePlugin plugin) {
        this.plugin = plugin;
    }

    public void setGameState(final GameState gameState, final Arena arena) {
        this.gameState = gameState;
        switch (gameState) {
            case RECRUITING:
                arena.setArenaState(ArenaState.READY);
                enoughPlayers(arena);
                break;
            case LOBBY_COUNTDOWN:
                arena.setArenaState(ArenaState.COUNTDOWN);
                lobbyCountdown(arena);
                break;
            case ACTIVE:
                arena.setArenaState(ArenaState.INGAME);
                spawnPerkNPC(arena);
                teleportToGameSpawn(arena);
                initSigns(arena);
                giveBeginGun(arena);
                spawnMonsters(arena);
                gameTimer(arena);
                break;
            case END:
                arena.setArenaState(ArenaState.END);
                sendGameEndNotification(arena);
                Bukkit.getScheduler().runTaskLater(plugin, () -> endGame(arena), 200L);
                break;
        }
    }

    // handles the lobby countdown timer, if this ends the gamestate will be set to active and players will get notified
    private void lobbyCountdown(final Arena arena) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (gameState != GameState.LOBBY_COUNTDOWN) {
                    this.cancel();
                }
                if (arena.getLobbyCountdown() > 0) {
                    arena.decrementLobbyCountdown();
                    arena.getArenaMembers().stream().filter(Objects::nonNull).forEach(zombiePlayer -> {
                        final Player player = Bukkit.getPlayer(zombiePlayer.getUuid());
                        player.sendMessage(Messages.LOBBY_COUNTDOWN.replace("{countdown}", String.valueOf(arena.getLobbyCountdown())));
                    });
                } else {
                    this.cancel();
                    arena.resetLobbyCountdown();
                    arena.getArenaMembers().forEach(zombiePlayer -> {
                        zombiePlayer.sendMessage(Messages.GAME_STARTED);
                        plugin.getScoreboard().gameScoreboard(zombiePlayer.getBukkitPlayer(), arena);
                    });
                    setGameState(GameState.ACTIVE, arena);
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    // Making sure all values update properly while the game is going on
    private void gameTimer(final Arena arena) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (gameState != GameState.ACTIVE) {
                    this.cancel();
                }
                arena.getArenaMembers().forEach(zombiePlayer -> {
                    final Player player = Bukkit.getPlayer(zombiePlayer.getUuid());
                    plugin.getScoreboard().gameScoreboard(player, arena);
                    updateGuns(zombiePlayer);
                });
                roundChecking(arena);
            }
        }.runTaskTimer(plugin, 0L, 20L);

    }

    // ending the game
    private void endGame(final Arena arena) {

        arena.getArenaMembers().stream().filter(Objects::nonNull).forEach(zombiePlayer -> {

            final Player player = Bukkit.getPlayer(zombiePlayer.getUuid());
            player.getInventory().clear();
            teleportToWorld(arena);

            // setting gamemode, clearing inventories
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            player.setGameMode(GameMode.SURVIVAL);

            plugin.getPlayerManager().removeZombiePlayer(player.getUniqueId());

            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            // clearing effects
            for (final PotionEffect activeEffect : player.getActivePotionEffects()) {
                player.removePotionEffect(activeEffect.getType());
            }
            zombiePlayer.setDown(false);
        });
        plugin.getArenaManager().clearArena(arena);
        arena.resetLobbyCountdown();
        arena.getArenaMembers().clear();
        arena.setRound(0);

        Bukkit.getScheduler().runTaskLater(plugin, () -> setGameState(GameState.RECRUITING, arena), 200); // allow the world to fully regenerate first, then set it to recruiting

    }

    private void teleportToWorld(final Arena arena) {
        arena.getArenaMembers().forEach(zombiePlayer -> {

            final Location worldLoc = (Location) plugin.getConfig().get("WorldSpawn");
            zombiePlayer.teleport(worldLoc);
            plugin.getPlayerManager().removeZombiePlayer(zombiePlayer.getUuid());
        });
    }

    private void teleportToGameSpawn(final Arena arena) {
        arena.getArenaMembers().stream().filter(Objects::nonNull).forEach(zombiePlayer
                -> Bukkit.getPlayer(zombiePlayer.getUuid()).teleport(arena.getSpawnLocation().toBukkitLocation()));
    }

    public void teleportToLobby(final Player player, final Arena arena) {
        player.teleport(arena.getWaitingRoomLoc().toBukkitLocation());
    }

    // Adds a new player to a specific arena, creates the Custom Player object.
    public void addPlayer(final Player player, final Arena arena) {
        if (arena.getArenaMembers().contains(plugin.getPlayerManager().getZombiePlayer(player.getUniqueId()))) {
             player.sendMessage(Messages.ALREADY_IN_ARENA);
        } else if (plugin.getArenaManager().isGameActive(arena)) {
            player.sendMessage(Messages.GAME_IN_PROGRESS);
        } else {
            if (arena.getArenaMembers().isEmpty()) {
                setGameState(GameState.RECRUITING, arena);
            }

            if (arena.getArenaMembers().size() <= arena.getMaxPlayers()) {
                plugin.getPlayerManager().addZombiePlayer(player.getUniqueId());
                final ZombiePlayer zombiePlayer = plugin.getPlayerManager().getZombiePlayer(player.getUniqueId());
                zombiePlayer.setGuns(plugin.getGunManager().getGuns()); // give the player his guns
                arena.getArenaMembers().add(zombiePlayer);

                // teleporting + changing their level, health and food
                plugin.getScoreboard().lobbyScoreboard(player, arena);
                teleportToLobby(player, arena);
                player.getInventory().clear();
                player.setLevel(0);
                player.setFoodLevel(20);
                player.setHealth(20);

                arena.getArenaMembers().stream().filter(Objects::nonNull).forEach(arenaMembers -> {
                    plugin.getScoreboard().lobbyScoreboard(arenaMembers.getBukkitPlayer(), arena);
                    arenaMembers.sendMessage(Messages.PLAYER_JOINED_GAME.replace("{player}", player.getDisplayName()));
                });
                enoughPlayers(arena);
                player.sendMessage(Messages.JOINED_ARENA.replace("{arena}", arena.getName()));

            } else player.sendMessage(Messages.ARENA_IS_FULL);
        }
    }


    // remove a player from the game, teleport them, clear the custom player object
    public void removePlayer(final Player player) {
        final ZombiePlayer zombiePlayer = plugin.getPlayerManager().getZombiePlayer(player.getUniqueId());
        if (zombiePlayer == null) return;
        final Arena arena = plugin.getArenaManager().getArenaByPlayer(zombiePlayer);
        if (arena == null) return;

        if (plugin.getConfig().get("WorldSpawn") == null) {
            player.sendMessage(Utils.colorize("&cSomething went wrong, no world spawn set!"));
        } else {

            arena.getArenaMembers().remove(zombiePlayer);
            plugin.getPlayerManager().removeZombiePlayer(player.getUniqueId());

            player.teleport((Location) plugin.getConfig().get("WorldSpawn"));
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            player.getInventory().clear();
            player.sendMessage(Messages.LEFT_ARENA.replace("{arena}", arena.getName()));
            player.getInventory().clear();
            leaveGameCheck(arena);
            player.sendMessage(Messages.LEFT_ARENA);
            arena.getArenaMembers()
                    .stream().filter(Objects::nonNull)
                    .forEach(arenaMember -> Bukkit.getPlayer(arenaMember.getUuid()).sendMessage(Messages.PLAYER_LEFT_GAME.replace("{player}", player.getDisplayName())));

        }
    }


    // only resetting if there is no player left ingame
    private void leaveGameCheck(final Arena arena) {
        if (gameState == GameState.ACTIVE) {
            if (arena.getArenaMembers().size() < 1) {
                setGameState(GameState.END, arena);
                arena.resetLobbyCountdown();
            }
        } else if (gameState == GameState.LOBBY_COUNTDOWN) {
            if (arena.getArenaMembers().size() < 1) {
                setGameState(GameState.RECRUITING, arena);
                arena.resetLobbyCountdown();
            }
        }
    }

    // message that will be shown at the end of the game
    private void sendGameEndNotification(final Arena arena) {
        arena.getArenaMembers().stream().filter(Objects::nonNull).forEach(zombiePlayer -> {
            zombiePlayer.sendMessage(Messages.GAME_ENDED.replace("{round}", String.valueOf(arena.getRound())));
        });
    }

    // checking if there are enough players to start
    private void enoughPlayers(final Arena arena) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (gameState == GameState.RECRUITING) {
                    if (arena.getArenaMembers().size() >= arena.getMinPlayer()) {
                        setGameState(GameState.LOBBY_COUNTDOWN, arena);
                    } else this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    // method for spawning monsters
    private void spawnMonsters(final Arena arena) {
        for (double i = 0; i < zombiesToSpawn(arena.getRound()); i++) {
            final int randomIndex = new Random().nextInt(arena.getMonsterSpawns().size());
            final CustomLocation monsterSpawn = arena.getMonsterSpawns().get(randomIndex); // getting a random monster spawn, and spawning one there

            final WorldServer world = ((CraftWorld) monsterSpawn.toBukkitLocation().getWorld()).getHandle();
            final ZombieMonster zombie = new ZombieMonster(world, monsterSpawn.toBukkitLocation(), arena.getRound() + 9f * 1.9f, arena.getRound() + 0.01);

            arena.addArenaMonster(zombie);
        }
    }

    // checking if a new round has to start
    private void roundChecking(final Arena arena) {
        if (arena.getArenaMonsterList().isEmpty()) {
            arena.incrementRound();
            spawnMonsters(arena);
            respawnDownedPlayers(arena);
        }
    }

    private double zombiesToSpawn(final double round) {
        return (round + 5) * 1.8;
    }

    private void giveBeginGun(final Arena arena) {
        arena.getArenaMembers().forEach(zombiePlayer -> {
            final Player player = Bukkit.getPlayer(zombiePlayer.getUuid());
            final Gun pistol = plugin.getGunManager().getGunByIdentifier("Pistol");
            player.getInventory().addItem(Utils.createItem(pistol.getIcon(), pistol.getName(), 1));
        });
    }

    private void updateGuns(final ZombiePlayer zombiePlayer) {
        final Player player = Bukkit.getPlayer(zombiePlayer.getUuid());
        plugin.getGunManager().getGuns().forEach(gun -> {
            if (player.getInventory().getItemInMainHand().getType() == gun.getIcon()) {
                player.setLevel(gun.getAmmoAmount());
            }
        });
    }

    // looping through the signs, checking if any of them need to be changed game-signs
    private void initSigns(final Arena arena) {
        for (final Chunk chunk : arena.getSpawnLocation().toBukkitLocation().getWorld().getLoadedChunks()) {
            for (BlockState blockState : chunk.getTileEntities()) {
                if (blockState instanceof Sign) {
                    final Sign sign = (Sign) blockState;
                    plugin.getGunManager().getGuns().forEach(gun -> {
                        if (!sign.getLine(0).equalsIgnoreCase(gun.getIdentifier())) return;
                        sign.setLine(0, Utils.colorize(gun.getName()));
                        sign.setLine(1, Utils.colorize("&c" + gun.getPrice()));
                        sign.setLine(2, Utils.colorize("&bClick To Buy"));
                        sign.update();
                        // setting the lines according to the gun that identifies it.
                    });

                }
            }
        }
    }

    // respawn players at the end of the round
    private void respawnDownedPlayers(final Arena arena) {
        arena.getArenaMembers().stream().filter(ZombiePlayer::isDown).forEach(zombiePlayer -> {
            final Player player = Bukkit.getPlayer(zombiePlayer.getUuid());
            player.teleport(arena.getSpawnLocation().toBukkitLocation());
            player.sendTitle(Utils.colorize("&aYou Respawned"), "", 20, 80, 20);
            player.setGameMode(GameMode.SURVIVAL);
            player.setHealth(20);
            zombiePlayer.setDown(false);
        });
    }

    private void spawnPerkNPC(final Arena arena) {
        arena.getPerkShopNPCs().forEach(location -> new PerkNPC(location.toBukkitLocation()));
    }
}
