package me.wolf.zombies.game;

import me.wolf.zombies.ZombiePlugin;
import me.wolf.zombies.arena.Arena;
import me.wolf.zombies.arena.ArenaState;
import me.wolf.zombies.constants.Messages;
import me.wolf.zombies.gun.Gun;
import me.wolf.zombies.perks.Perk;
import me.wolf.zombies.player.ZombiePlayer;
import me.wolf.zombies.utils.Utils;
import net.minecraft.server.v1_16_R3.ChatComponentText;
import net.minecraft.server.v1_16_R3.EntityMonster;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityDestroy;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftMonster;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftZombie;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("ConstantConditions")
public class GameListeners implements Listener {

    private final ZombiePlugin plugin;
    final Map<Gun, Integer> cooldownMap = new HashMap<>();

    public GameListeners(final ZombiePlugin plugin) {
        this.plugin = plugin;
    }

    // listener to check if a player shoots a gun or not
    @EventHandler
    public void onShoot(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final ZombiePlayer zombiePlayer = plugin.getPlayerManager().getZombiePlayer(player.getUniqueId());
        if (zombiePlayer == null) return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR) return;

        zombiePlayer.getGuns().forEach(gun -> {
            if (player.getInventory().getItemInMainHand().getType() == gun.getIcon()) {
                if (canShoot(zombiePlayer)) {
                    launchBullet(player, gun);
                }
            }
        });
    }

    @EventHandler
    public void onCombust(final EntityCombustEvent event) {
        if (event.getEntity() instanceof CraftZombie) { // dont allow CraftZombies to catch fire during the day
            event.setCancelled(true);
        }
    }

    // clicking the perk shop NPC
    @EventHandler
    public void onPerkShopOpen(final PlayerInteractAtEntityEvent event) {
        final Player player = event.getPlayer();
        final ZombiePlayer zombiePlayer = plugin.getPlayerManager().getZombiePlayer(player.getUniqueId());
        if (zombiePlayer == null) return;

        final Arena arena = plugin.getArenaManager().getArenaByPlayer(zombiePlayer);
        if (arena.getArenaState() == ArenaState.INGAME) {
            if (event.getRightClicked() instanceof Villager) {
                openPerkGUI(player);
            }
        }

    }

    // creating the signs for refilling ammo and upgrading weapons
    @EventHandler
    public void createSigns(final SignChangeEvent event) {
        if (event.getLine(0).equalsIgnoreCase("ammo")) {
            event.setLine(0, Utils.colorize("&a[&bClick Me&a]"));
            event.setLine(1, Utils.colorize("&cTo Refill"));
            event.setLine(2, Utils.colorize("&bCost: " + plugin.getConfig().getInt("ammo-refill-price")));
        } else if (event.getLine(0).equalsIgnoreCase("upgrade")) {
            event.setLine(0, Utils.colorize("&b[Upgrade]"));
            event.setLine(1, Utils.colorize("&3Cost: &e" + plugin.getConfig().getInt("gun-upgrade-price")));
        }
    }

    // checking if a sign is clicked, which sign is clicked, and then performing the actions.
    @EventHandler
    public void onSignClick(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final ZombiePlayer zombiePlayer = plugin.getPlayerManager().getZombiePlayer(player.getUniqueId());
        if (zombiePlayer == null) return;
        if (event.getClickedBlock() == null) return;
        if (!(event.getClickedBlock().getState() instanceof Sign)) return;

        final Sign sign = (Sign) event.getClickedBlock().getState();

        if (sign.getLine(0).equalsIgnoreCase(Utils.colorize("&a[&bClick Me&a]"))) {
            final int price = plugin.getConfig().getInt("ammo-refill-price"); //refilling amo
            if (canAfford(zombiePlayer, price)) {
                zombiePlayer.removeCoins(price);
                zombiePlayer.getGuns().forEach(gun -> {
                    if (player.getInventory().getItemInMainHand().getType() != gun.getIcon()) return;
                    gun.setAmmo(gun.getDefaultAmount());
                });
            }
            // dealing with upgrading the gun
        } else if (sign.getLine(0).equalsIgnoreCase(Utils.colorize("&b[Upgrade]"))) {
            final int price = plugin.getConfig().getInt("gun-upgrade-price");
            zombiePlayer.getGuns().forEach(gun -> {
                if (player.getInventory().getItemInMainHand().getType() != gun.getIcon()) return;
                if (gun.getLevel() <= gun.getMaxLevel()) {
                    if (canAfford(zombiePlayer, price)) {
                        zombiePlayer.removeCoins(price);
                        gun.setDamage(gun.getNewDamage());
                        gun.incrementLevel();
                        zombiePlayer.sendMessage("&bUpgrade\n" +
                                "&3- &bLevel " + (gun.getLevel() - 1) + " &3-> &b" + (gun.getLevel()) + "\n" +
                                "&3- &bDamage " + Math.round(gun.getDamage()) + " &3-> &b" + Math.round(gun.getNewDamage()) + "\n");
                        gun.setDamage(gun.getNewDamage());
                    }
                } else zombiePlayer.sendMessage(Messages.MAX_LEVEL);
            });

        }
        // the part that takes care of buying the gun, checking if the price can be afforded, etc
        zombiePlayer.getGuns().forEach(gun -> {
            if (sign.getLine(0).equalsIgnoreCase(Utils.colorize(gun.getName()))) {
                if (player.getInventory().contains(gun.getIcon())) {
                    player.sendMessage(Messages.ALREADY_HAVE_WEAPON);
                    return;
                }
                if (canAfford(zombiePlayer, gun.getPrice())) {
                    zombiePlayer.removeCoins(gun.getPrice());
                    player.getInventory().addItem(Utils.createItem(gun.getIcon(), gun.getName(), 1));
                }
            }
        });

    }

    // setting zombie drops to nothing
    @EventHandler
    public void onMonsterDeath(final EntityDeathEvent event) {
        // checking if the entity is the custom zombie
        if (!(event.getEntity() instanceof Monster)) return;
        final EntityMonster entityMonster = ((CraftMonster) event.getEntity()).getHandle();
        if (entityMonster.getCustomName() == null) return;
        if (entityMonster.getCustomName().equals(new ChatComponentText(Utils.colorize("&cMonster")))) {
            event.setDroppedExp(0);
            event.getDrops().clear();
        }
    }

    // Checking when a player dies, setting them to spectator. If he was the last non-spectator, end game
    @EventHandler
    public void onPlayerDeath(final EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        final Player player = (Player) event.getEntity();
        final ZombiePlayer zombiePlayer = plugin.getPlayerManager().getZombiePlayer(player.getUniqueId());
        if (zombiePlayer == null) return;
        final Arena arena = plugin.getArenaManager().getArenaByPlayer(zombiePlayer);
        if (arena == null) return;

        if (event.getDamage() > player.getHealth()) {
            setSpectator(zombiePlayer);
            arena.getArenaMembers().forEach(arenaMember -> Bukkit.getPlayer(arenaMember.getUuid()).sendMessage(Messages.PLAYER_DOWN.replace("{dead}", player.getDisplayName())));

            // all arena members are down, end game
            if (arena.getArenaMembers().stream().filter(ZombiePlayer::isDown).count() == arena.getArenaMembers().size()) {
                plugin.getGameManager().setGameState(GameState.END, arena);
            }
        }
    }

    // checking if a zombie gets hit, then doing things such as playing a sound, dealing the damage, etc
    @EventHandler
    public void onMonsterHit(final EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Monster && event.getDamager() instanceof Snowball) {
            final EntityMonster entityMonster = ((CraftMonster) event.getEntity()).getHandle();
            if (entityMonster.getCustomName() == null) return;

            if (!entityMonster.getCustomName().equals(new ChatComponentText(Utils.colorize("&cMonster")))) return;
            final Player shooter = (Player) ((Snowball) event.getDamager()).getShooter();
            final ZombiePlayer zombieShooter = plugin.getPlayerManager().getZombiePlayer(shooter.getUniqueId());

            if (zombieShooter == null) return;
            final Arena arena = plugin.getArenaManager().getArenaByPlayer(zombieShooter);
            if (arena == null) return;

            shooter.playSound(shooter.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.3f, 0.3f);
            zombieShooter.getGuns().forEach(gun -> {
                if (event.getDamager().getCustomName().equalsIgnoreCase(String.valueOf(gun.getAmmoID()))) { // checking if the snowball has the entity ID from a gun
                    entityMonster.setHealth((float) (entityMonster.getHealth() - gun.getDamage())); // subtracting the monster's HP with the gun ammo
                    ((CraftMonster) event.getEntity()).setMaximumNoDamageTicks(gun.getFireRate()); //setting the hit delay according to the fire rate
                }
            });

            zombieShooter.addCoins(15); // adding 15 coins per hit
            if (entityMonster.getHealth() <= 0) {
                arena.getArenaMonsterList().remove(0); // if the monsters health is below 0, remove it from the list
            }
        }
        if (event.getEntity() instanceof Monster) { // checking if a player is the damager (hitting with the weapon/fist), canceling that
            if (plugin.getPlayerManager().getZombiePlayer(event.getDamager().getUniqueId()) != null) {
                event.setCancelled(true);
            }
        }
    }

    // checking if a player is purchasing anything from the perk shop
    @EventHandler
    public void onPurchasePerk(final InventoryClickEvent event) {
        if (event.getCurrentItem() == null) return;
        if (event.getCurrentItem().getItemMeta() == null) return;
        if (event.getCurrentItem().getItemMeta().getLore() == null) return;
        final Player player = (Player) event.getWhoClicked();
        final ZombiePlayer zombiePlayer = plugin.getPlayerManager().getZombiePlayer(player.getUniqueId());
        if (zombiePlayer == null) return;

        if (!event.getView().getTitle().equalsIgnoreCase(Utils.colorize("&cPerk Shop"))) return;
        plugin.getPerkManager().getPerks().forEach(perk -> {
            if (event.getCurrentItem().getType() != perk.getIcon()) return;
            if (perk.getLevel() < perk.getMaxLevel() + 1) {
                if (canAfford(zombiePlayer, perk.getPrice())) {
                    applyPerk(zombiePlayer, perk.getIdentifier());
                    zombiePlayer.removeCoins(perk.getPrice());
                    zombiePlayer.sendMessage("&bPerk Upgrade bought "+ perk.getName() + ": " + perk.getLevel() + "&3/&b" + perk.getMaxLevel());
                    perk.incrementLevel();
                    perk.setPrice(perk.getPrice() * perk.getPriceMultiplier());

                }
            } else zombiePlayer.sendMessage("&cThis perk is already at the max level!");
        });

        event.setCancelled(true);

    }

    // method for launching the bullet
    private void launchBullet(final Player shooter, final Gun gun) {
        if (gun.getAmmoAmount() != 0) {
            final Snowball bullet = shooter.launchProjectile(Snowball.class); // spawning a snowball (bullet)
            bullet.setVelocity(bullet.getVelocity().multiply(3)); // making it shoot further
            bullet.setCustomName(String.valueOf(gun.getAmmoID()));
            bullet.setShooter(shooter);
            gun.decrementAmmo(); // removing 1 ammo out of the clip

            new BukkitRunnable() {
                private int i = 0;

                @Override
                public void run() {
                    i++;
                    bullet.getWorld().spawnParticle(Particle.ASH, bullet.getLocation(), 1);
                    if (i > 5) this.cancel();
                }
            }.runTaskTimer(plugin, 0L, 1L);
            // "destroying" the snowball so the bullets are invisible
            final PacketPlayOutEntityDestroy destroyPacket = new PacketPlayOutEntityDestroy(bullet.getEntityId());
            ((CraftPlayer) shooter).getHandle().playerConnection.sendPacket(destroyPacket);
        } else shooter.sendMessage(Messages.NO_AMMO);
    }

    private boolean canShoot(final ZombiePlayer player) {
        // player shot a bullet
        player.getGuns().forEach(gun -> {
            if (player.getBukkitPlayer().getInventory().getItemInMainHand().equals(Utils.createItem(gun.getIcon(), gun.getName()))) {
                if (!cooldownMap.containsKey(gun)) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (gun.getFireRate() > 0) {
                                cooldownMap.put(gun, gun.getFireRate());
                                gun.decrementFireRate();
                            } else { // cooldown over
                                this.cancel();
                                gun.setFireRate(plugin.getFileManager().getGunsConfig().getConfig().getInt("guns." + gun.getIdentifier().toLowerCase() + ".firerate"));
                                cooldownMap.remove(gun);
                            }
                        }
                    }.runTaskTimer(plugin, 0L, 1L);
                }
            }
        });
        return cooldownMap.keySet().stream().noneMatch(gun -> player.getGuns().contains(gun)); // check if the gun isn't in the map

    }


    // checking if a player can afford something with their coins
    private boolean canAfford(final ZombiePlayer zombiePlayer, final double amount) {
        final Player player = Bukkit.getPlayer(zombiePlayer.getUuid());
        if (zombiePlayer.getCoins() >= amount) {
            player.sendMessage(Messages.PURCHASED);
            return true;
        } else {
            player.sendMessage(Messages.CANT_AFFORD);
            return false;
        }
    }

    // setting the player to spectator/downed mode
    private void setSpectator(final ZombiePlayer zombiePlayer) {
        final Player player = Bukkit.getPlayer(zombiePlayer.getUuid());
        zombiePlayer.setDown(true);
        player.setGameMode(GameMode.SPECTATOR);
        player.setHealth(20);
    }

    // opening the perk GUI
    private void openPerkGUI(final Player player) {
        final Inventory inventory = Bukkit.createInventory(null, 9, Utils.colorize("&cPerk Shop"));

        plugin.getPerkManager().getPerks().stream().filter(Perk::isEnabled).forEach(perk -> inventory.addItem(Utils.createItem(perk.getIcon(), perk.getName() + " &e(" + perk.getPrice() + ")", "&bLevel &3" + perk.getLevel())));

        player.openInventory(inventory);
    }

    // applying the perk to a player.
    private void applyPerk(final ZombiePlayer zombiePlayer, final String identifier) {
        final Perk perk = plugin.getPerkManager().getPerkByIdentifier(identifier);

        switch (identifier) {
            case "Speed":
                zombiePlayer.getBukkitPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, perk.getLevel() - 1));
                break;
            case "Resistance":
                zombiePlayer.getBukkitPlayer().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, perk.getLevel() - 1));
                break;
            case "Jump":
                zombiePlayer.getBukkitPlayer().addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, perk.getLevel() - 1));
                break;
        }
    }
}
