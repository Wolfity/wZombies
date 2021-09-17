package me.wolf.zombies.monsters;

import me.wolf.zombies.utils.Utils;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.event.entity.CreatureSpawnEvent;

@SuppressWarnings("ConstantConditions")
public class ZombieMonster extends EntityZombie {

    public ZombieMonster(final World world, final Location location, final float health, final double damage) {
        super(world);
        this.setBaby(false);
        this.setPosition(location.getX(), location.getY(), location.getZ());
        this.setCustomNameVisible(true);
        this.setCustomName(new ChatComponentText(Utils.colorize("&cMonster")));
        world.addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
        this.setMaxHealth(health);
        this.setDamage(damage);
    }

    @Override
    protected void initPathfinder() {
        super.initPathfinder();
        this.goalSelector.a(0, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 10.0F));
        this.targetSelector.a(1, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, true));


    }
    public void setMaxHealth(final float amount) {
        this.craftAttributes.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(amount);
        this.setHealth(amount);
    }

    public void setDamage(final double amount) {
        this.craftAttributes.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(amount);
    }


    @Override
    public float getHealth() {
        return super.getHealth();
    }
}
