# Zombies
Survive zombies coming at you in waves! 1.16.5 Minigame

### **Introduction**
In zombies, you get a default pistol. Zombies will come at you in waves.
You have to try to survive and kill all zombies. You get coins for killing zombies.
With these coins, you can buy weapons, perks, and weapon upgrades.

### **How to set it up**

**World Spawn** 

The first thing you have to do is create a world spawn. After the game ends, players will be teleported to this location; 
you can do that by typing the command [/zombies setworldspawn].

**Lobby and Game spawn**

Now you can set a lobby spawn. 
Players will be teleported to this location when they join the waiting room. 
You can set the spawn point using the command [/zombies setlobby arenaName]
Next up is creating a game spawn for players. 
You can do that by typing [/zombies setspawn arenaName]


**Signs and NPC**

You can set a perk shop spawn location by executing the command [/zombies setperknpc arenaName]. 

There are several available signs, ammo refill signs, gun upgrade signs, and gun purchase signs.

To create an ammo refill sign, you place a sign,
and in the first line of the sign, you type “ammo.” 
That’s it; that will create an ammo refill sign. 
To create a gun upgrade sign, you place a sign, and in the first line,
you type: “upgrade.”
To create gun purchase guns, you will have to type the gun’s 
identifier in the first line of the sign. You can check the identifier 
in the guns.yml file.

### **Guns**
In the guns.yml file, you can create your guns. 
You can even set a different fire rate for other guns;
the lower the fire rate, the faster the guns shoot. 
When creating new guns, make sure any other guns do not use the ammo-id.

### **Perks**
In the perk shop, you can buy several perks. 
You can choose the maximum level of your perks.

### **Commands**
`/zombies createarena <arenaName>` Creates a new arena\
`/zombies deletearena <arenaName>` Deletes an existing arena\
`/zombies join <arenaName>` Join an arena\
`/zombies leave` Leaves an arena\
`/zombies setlobby <arenaName>` Sets the lobby spawn point for an arena\
`/zombies setspawn <arenaName>` Sets a game spawn point for an arena\
`/zombies setworldspawn` Sets the world spawn, the spawn where players go to after the game ends, or they leave\
`/zombies help` Displays the help message\
`/zombies admin` Displays the admin message\
`/zombies setperknpc <arenaName>` Sets a perk NPC location\
`/zombies removeperknpc <arenaName>` Deletes a gold generator\
`/zombies tp <arenaName>` Teleports you to an arena\
`/zombies setmonsterspawn <arenaName>` Sets a monster spawn point\
`/zombies deletemonsterspawn <arenaName> ` Deletes a monster spawn point
