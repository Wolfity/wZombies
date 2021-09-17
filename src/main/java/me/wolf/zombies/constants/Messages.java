package me.wolf.zombies.constants;

import me.wolf.zombies.utils.Utils;

public final class Messages {

    public static final String ADMIN_HELP = Utils.colorize(
            "&7[-------&bZombies &cAdmin &bHelp&7-------]\n" +
                    "&b/zombies createarena <arena> &7- Creates a new arena \n" +
                    "&b/zombies deletearena <arena> &7- Deletes an arena\n" +
                    "&b/zombies setworldspawn &7- Sets the world spawn \n" +
                    "&b/zombies setmonsterspawn &7- Sets a monster spawn\n" +
                    "&b/zombies setspawn <arena> &7- Sets the spawn point for the arena\n" +
                    "&b/zombies tp <arena> &7- Teleports you to the arena\n" +
                    "&b/zombies setperknpc <arenaName> &7- Sets a Perk NPC spot\n" +
                    "&b/zombies removeperknpc <arenaName> &7- Removes a Perk NPC\n" +
                    "&b/zombies setlobby <arenaName> &7- Sets a lobby spawn point\n" +
                    "&b/zombies deletemonsterspawn <arenaName> &7- Removes a monster spawn point\n" +
                    "&b/zombies admin &7- Displays the admin help message\n" +
                    "&7[-------&bZombies &cAdmin &bHelp&7-------]");

    public static final String HELP = Utils.colorize(
            "&7[------- &bZombies Help &7-------]\n" +
                    "&b/zombies join <arena> &7- Join the arena\n" +
                    "&b/zombies leave &7- Leaves the arena\n" +
                    "&b/zombies help &7- Displays the help command\n" +
                    "&7[------- &bZombies Help &7-------]");

    public static final String ARENA_CREATED = Utils.colorize(
            "&aSuccessfully created the arena {arena}");

    public static final String ARENA_DELETED = Utils.colorize(
            "&cSuccessfully deleted the arena {arena}");

    public static final String SET_LOBBY_SPAWN = Utils.colorize(
            "&aSuccessfully set the lobby spawn");

    public static final String SET_WORLD_SPAWN = Utils.colorize(
            "&aSuccessfully set the world spawn");

    public static final String SET_GAME_SPAWN = Utils.colorize(
            "&aSuccessfully set a game spawn");

    public static final String JOINED_ARENA = Utils.colorize(
            "&aSuccessfully joined the arena &2{arena}");

    public static final String LEFT_ARENA = Utils.colorize(
            "&cSuccessfully left the arena &2{arena}");

    public static final String NOT_IN_ARENA = Utils.colorize(
            "&cYou are not in this arena!");

    public static final String ARENA_NOT_FOUND = Utils.colorize(
            "&cThis arena does not exist!");

    public static final String LOBBY_COUNTDOWN = Utils.colorize(
            "&bThe game will start in &3{countdown}&b seconds!");

    public static final String ARENA_IS_FULL = Utils.colorize(
            "&cThis arena is full!");

    public static final String ALREADY_IN_ARENA = Utils.colorize(
            "&cYou are already in this arena!");

    public static final String CAN_NOT_MODIFY = Utils.colorize(
            "&cThis game is going on, you can not modify the arena");

    public static final String ARENA_EXISTS = Utils.colorize(
            "&cThis arena already exists!");

    public static final String TELEPORTED_TO_ARENA = Utils.colorize(
            "&aTeleported to the arena");

    public static final String GAME_IN_PROGRESS = Utils.colorize(
            "&cThis game is in progress");

    public static final String PLAYER_LEFT_GAME = Utils.colorize(
            "&b{player} &ahas left the game!");

    public static final String PLAYER_JOINED_GAME = Utils.colorize(
            "&b{player} &ahas joined the game!");

    public static final String NO_PERMISSION = Utils.colorize(
            "&cSomething went wrong, you do not have the permissions!");


    public static final String GAME_STARTED = Utils.colorize(
            "  &a===============================================\n" +
                    "&a=             &a&lGame Started! \n" +
                    "&a=       Kill the zombies and stay alive!\n" +
                    "&a=              &2Good luck!\n" +
                    "&a===============================================");

    public static final String GAME_ENDED = Utils.colorize(
            "&a=========================================\n" +
                    "&a=          &a&lGame Ended!\n" +
                    "&a=         &6Rounds survived: {round}\n" +
                    "&a=======================================\n" +
                    "&2Players will be teleported out in 10 seconds!");


    public static final String SET_MONSTER_SPAWN = Utils.colorize("&aSuccessfully created a monster spawn point");

    public static final String NO_MONSTER_SPAWN_FOUND = Utils.colorize("&cNo monster spawn found in this location!\n" +
            "Make sure your x,y,z are the same!");

    public static final String DELETED_MONSTER_SPAWN = Utils.colorize("&aSuccessfully deleted a monster spawn point!");

    public static final String NO_AMMO = Utils.colorize("&cYou ran out of ammo, you can buy a refil at the shop!");

    public static final String CANT_AFFORD = Utils.colorize("&cYou can not afford this!");

    public static final String PURCHASED = Utils.colorize("&aPurchase Successfull!");

    public static final String MAX_LEVEL = Utils.colorize("&cThis gun reached the max level!");

    public static final String ALREADY_HAVE_WEAPON = Utils.colorize("&cYou can not buy this, you already own this weapon!");

    public static final String PERK_NPC_SPAWN_DELETED = Utils.colorize("&aSuccessfully deleted this perk npc spawn point!");

    public static final String NO_PERN_NPC_SPAWN_FOUND = Utils.colorize("&cNo perk npc spawn found in this location!\n" +
            "Make sure your x,y,z are the same!");

    public static final String SET_PERK_NPC_SPOT = Utils.colorize("&aSuccessfully set a perk npc shop");

    public static final String PLAYER_DOWN = Utils.colorize("&c[ALERT] &e{dead} &cgot killed");
}
