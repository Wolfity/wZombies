package me.wolf.zombies.arena;


import me.wolf.zombies.ZombiePlugin;
import me.wolf.zombies.monsters.ZombieMonster;
import me.wolf.zombies.player.ZombiePlayer;
import me.wolf.zombies.utils.CustomLocation;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Arena {

    private final ZombiePlugin plugin;

    private final String name;
    private final Set<ZombiePlayer> arenaMembers = new HashSet<>();
    private final List<CustomLocation> monsterSpawns = new ArrayList<>();
    private final List<ZombieMonster> arenaMonsterList = new ArrayList<>();
    private final List<CustomLocation> perkShopNPCs = new ArrayList<>();
    private  int minPlayers;
    private  int maxPlayers;
    private File arenaConfigFile;
    private ArenaState arenaState = ArenaState.READY;
    private CustomLocation waitingRoomLoc;
    private FileConfiguration arenaConfig;
    private CustomLocation spawnLocation; // I will temporarily assign the arena spawn to the player's spawn point so it won't throw a null
    private int lobbyCountdown;
    private int round;

    protected Arena(final String name, final int lobbyCountdown, final int minPlayer, final int maxPlayers, final ZombiePlugin plugin) {
        this.plugin = plugin;
        this.name = name;
        createConfig(name);
        this.lobbyCountdown = lobbyCountdown;
        this.minPlayers = minPlayer;
        this.maxPlayers = maxPlayers;
        this.round = 1;

    }


    public void createConfig(final String cfgName) {
        arenaConfigFile = new File(plugin.getDataFolder() + "/arenas", cfgName.toLowerCase() + ".yml");
        arenaConfig = new YamlConfiguration();
        try {
            arenaConfig.load(arenaConfigFile);
            arenaConfig.save(arenaConfigFile);
        } catch (IOException | InvalidConfigurationException ignore) {

        }
        if (!arenaConfigFile.exists()) {
            arenaConfigFile.getParentFile().mkdirs();
            try {
                arenaConfigFile.createNewFile();
                arenaConfig.load(arenaConfigFile);
                arenaConfig.set("min-players", 1);
                arenaConfig.set("max-players", 4);
                arenaConfig.set("lobby-countdown", 10);
                arenaConfig.save(arenaConfigFile);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        }
    }


    public void decrementLobbyCountdown() {
        lobbyCountdown--;
    }

    public void resetLobbyCountdown() {
        this.lobbyCountdown = arenaConfig.getInt("lobby-countdown");
    }

    public void incrementRound() {
        round++;
    }

    public List<CustomLocation> getPerkShopNPCs() {
        return perkShopNPCs;
    }

    public void addShopNPC(final CustomLocation location) {
        if (!perkShopNPCs.contains(location)) {
            perkShopNPCs.add(location);
        }
    }

    public void removePerkShopNPC(final CustomLocation location) {
        perkShopNPCs.remove(location);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Arena arena = (Arena) o;
        return name.equals(arena.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public String getName() {
        return name;
    }

    public ArenaState getArenaState() {
        return arenaState;
    }

    public void setArenaState(final ArenaState arenaState) {
        this.arenaState = arenaState;
    }

    public CustomLocation getWaitingRoomLoc() {
        return waitingRoomLoc;
    }

    public void setWaitingRoomLoc(final CustomLocation waitingRoomLoc) {
        this.waitingRoomLoc = waitingRoomLoc;
    }

    public FileConfiguration getArenaConfig() {
        return arenaConfig;
    }

    public CustomLocation getSpawnLocation() {
        return spawnLocation;
    }

    public void setSpawnLocation(final CustomLocation customLocation) {
        this.spawnLocation = customLocation;
    }

    public Set<ZombiePlayer> getArenaMembers() {
        return arenaMembers;
    }

    public int getLobbyCountdown() {
        return lobbyCountdown;
    }

    public File getArenaConfigFile() {
        return arenaConfigFile;
    }

    public int getRound() {
        return round;
    }

    public void setRound(final int round) {
        this.round = round;
    }

    public List<CustomLocation> getMonsterSpawns() {
        return monsterSpawns;
    }

    public void addMonsterSpawn(final CustomLocation location) {
        if (!monsterSpawns.contains(location)) {
            monsterSpawns.add(location);
        }
    }

    public void setLobbyCountdown(int lobbyCountdown) {
        this.lobbyCountdown = lobbyCountdown;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getMinPlayer() {
        return minPlayers;
    }

    public void setMinPlayer(int minPlayer) {
        this.minPlayers = minPlayer;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public void removeMonsterSpawn(final CustomLocation location) {
        monsterSpawns.remove(location);
    }

    public List<ZombieMonster> getArenaMonsterList() {
        return arenaMonsterList;
    }

    public void addArenaMonster(final ZombieMonster zombieMonster) {
        if (!arenaMonsterList.contains(zombieMonster)) {
            arenaMonsterList.add(zombieMonster);
        }
    }
}