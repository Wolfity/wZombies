package me.wolf.zombies;

import me.wolf.zombies.arena.Arena;
import me.wolf.zombies.arena.ArenaManager;
import me.wolf.zombies.commands.impl.ZombieCommand;
import me.wolf.zombies.files.FileManager;
import me.wolf.zombies.game.GameListeners;
import me.wolf.zombies.game.GameManager;
import me.wolf.zombies.gun.GunManager;
import me.wolf.zombies.listeners.*;
import me.wolf.zombies.perks.PerkManager;
import me.wolf.zombies.player.ZombiePlayer;
import me.wolf.zombies.scoreboard.Scoreboards;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;

public class ZombiePlugin extends JavaPlugin {

    private ArenaManager arenaManager;
    private GameManager gameManager;
    private Scoreboards scoreboard;
    private FileManager fileManager;
    private GunManager gunManager;
    private PerkManager perkManager;


    private final Set<Arena> arenas = new HashSet<>();
    private final Map<UUID, ZombiePlayer> zombiePlayers = new HashMap<>();

    @Override
    public void onEnable() {
        final ZombiePlugin plugin = this;

        File folder = new File(this.getDataFolder() + "/arenas");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        registerCommands();
        registerListeners();
        registerManagers();

        getConfig().options().copyDefaults();
        saveDefaultConfig();
    }

    @Override
    public void onDisable() {
        arenaManager.saveArenas();


    }

    private void registerCommands() {
        Collections.singletonList(
                new ZombieCommand(this)
        ).forEach(this::registerCommand);

    }

    private void registerListeners() {
        Arrays.asList(
                new GameListeners(this),
                new PlayerQuit(this),
                new FoodChange(this),
                new BlockPlace(this),
                new BlockBreak(this),
                new EntityDamage(this),
                new ItemDrop(this)
        ).forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, this));
    }

    private void registerManagers() {
        this.fileManager = new FileManager(this);
        this.arenaManager = new ArenaManager(this);
        this.arenaManager.loadArenas();
        this.gameManager = new GameManager(this);
        this.scoreboard = new Scoreboards(this);
        this.gunManager = new GunManager(this);
        this.perkManager = new PerkManager(this);

        gunManager.initGuns();
        perkManager.initPerks();
    }

    private void registerCommand(final Command command) {
        try {
            final Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);

            final CommandMap commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
            commandMap.register(command.getLabel(), command);

        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public Scoreboards getScoreboard() {
        return scoreboard;
    }

    public Set<Arena> getArenas() {
        return arenas;
    }

    public Map<UUID, ZombiePlayer> getZombiePlayers() {
        return zombiePlayers;
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public GunManager getGunManager() {
        return gunManager;
    }

    public PerkManager getPerkManager() {
        return perkManager;
    }
}
