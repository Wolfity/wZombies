package me.wolf.zombies.scoreboard;

import me.wolf.zombies.ZombiePlugin;
import me.wolf.zombies.arena.Arena;
import me.wolf.zombies.player.ZombiePlayer;
import me.wolf.zombies.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class Scoreboards {

    private final ZombiePlugin plugin;
    public Scoreboards(final ZombiePlugin plugin) {
        this.plugin = plugin;
    }

    public void lobbyScoreboard(final Player player, final Arena arena) {
        final int maxPlayers = arena.getArenaConfig().getInt("max-players");
        final String name = arena.getName();
        final int currentPlayers = arena.getArenaMembers().size();

        final ScoreboardManager scoreboardManager = plugin.getServer().getScoreboardManager();
        org.bukkit.scoreboard.Scoreboard scoreboard = scoreboardManager.getNewScoreboard();

        final Objective objective = scoreboard.registerNewObjective("zombie", "zombie");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(Utils.colorize("&c&lZombies Waiting Room"));

        final Team players = scoreboard.registerNewTeam("players");
        players.addEntry(Utils.colorize("&bPlayers: "));
        players.setPrefix("");
        players.setSuffix(Utils.colorize("&b" + currentPlayers + "&3/&b" + maxPlayers));
        objective.getScore(Utils.colorize("&bPlayers: ")).setScore(1);

        final Team empty1 = scoreboard.registerNewTeam("empty1");
        empty1.addEntry(" ");
        empty1.setPrefix("");
        empty1.setSuffix("");
        objective.getScore(" ").setScore(2);

        final Team map = scoreboard.registerNewTeam("map");
        map.addEntry(Utils.colorize("&bMap: &2"));
        map.setPrefix("");
        map.setSuffix(Utils.colorize(name));
        objective.getScore(Utils.colorize("&bMap: &2")).setScore(3);

        final Team empty2 = scoreboard.registerNewTeam("empty2");
        empty2.addEntry("  ");
        empty2.setPrefix("");
        empty2.setSuffix("");
        objective.getScore("  ").setScore(4);


        player.setScoreboard(scoreboard);
    }

    public void gameScoreboard(final Player player, final Arena arena) {
        final int maxPlayers = arena.getArenaConfig().getInt("max-players");
        final String name = arena.getName();
        final int currentPlayers = arena.getArenaMembers().size();
        final ZombiePlayer zombiePlayer = plugin.getZombiePlayers().get(player.getUniqueId());

        final ScoreboardManager scoreboardManager = plugin.getServer().getScoreboardManager();
        Scoreboard scoreboard = scoreboardManager.getNewScoreboard();

        final Objective objective = scoreboard.registerNewObjective("zombiegame", "zombiegame");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(Utils.colorize("&c&lZombies"));

        final Team players = scoreboard.registerNewTeam("players");
        players.addEntry(Utils.colorize("&bPlayers: "));
        players.setPrefix("");
        players.setSuffix(Utils.colorize("&b" + currentPlayers + "&3/&b" + maxPlayers));
        objective.getScore(Utils.colorize("&bPlayers: ")).setScore(1);

        final Team empty1 = scoreboard.registerNewTeam("empty1");
        empty1.addEntry(" ");
        empty1.setPrefix("");
        empty1.setSuffix("");
        objective.getScore(" ").setScore(2);

        final Team coins = scoreboard.registerNewTeam("coins");
        coins.addEntry(Utils.colorize("&eCoins: &e"));
        coins.setPrefix("");
        coins.setSuffix(String.valueOf(zombiePlayer.getCoins()));
        objective.getScore(Utils.colorize("&eCoins: &e")).setScore(3);

        final Team empty2 = scoreboard.registerNewTeam("empty2");
        empty2.addEntry("  ");
        empty2.setPrefix("");
        empty2.setSuffix("");
        objective.getScore("  ").setScore(4);

        final Team zombiesLeft = scoreboard.registerNewTeam("zombiesLeft");
        zombiesLeft.addEntry(Utils.colorize("&cZombies Left: &e"));
        zombiesLeft.setPrefix("");
        zombiesLeft.setSuffix(String.valueOf(arena.getArenaMonsterList().size()));
        objective.getScore(Utils.colorize("&cZombies Left: &e")).setScore(5);


        final Team empty3 = scoreboard.registerNewTeam("empty3");
        empty3.addEntry("   ");
        empty3.setPrefix("");
        empty3.setSuffix("");
        objective.getScore("   ").setScore(6);

        final Team round = scoreboard.registerNewTeam("round");
        round.addEntry(Utils.colorize("&cRound: &e"));
        round.setPrefix("");
        round.setSuffix(String.valueOf(arena.getRound()));
        objective.getScore(Utils.colorize("&cRound: &e")).setScore(7);

        final Team empty4 = scoreboard.registerNewTeam("empty4");
        empty4.addEntry("    ");
        empty4.setPrefix("");
        empty4.setSuffix("");
        objective.getScore("    ").setScore(8);


        final Team map = scoreboard.registerNewTeam("map");
        map.addEntry(Utils.colorize("&bMap: &2"));
        map.setPrefix("");
        map.setSuffix(Utils.colorize(name));
        objective.getScore(Utils.colorize("&bMap: &2")).setScore(9);

        player.setScoreboard(scoreboard);
    }

}
