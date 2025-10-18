package dev.spigotbypass.vanillakbplus;

import dev.spigotbypass.vanillakbplus.VanillaKnockbackPlus.Mode;
import net.minecraft.server.v1_8_R3.EntityLiving;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

public class KBListener implements Listener {

    private final VanillaKnockbackPlus plugin;

    public KBListener(VanillaKnockbackPlus plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDamage(EntityDamageByEntityEvent e) {
        if (e.isCancelled()) return;

        Entity damager = e.getDamager();
        Entity victim = e.getEntity();

        if (plugin.getMode() == Mode.PVP &&
                (!(damager instanceof Player) || !(victim instanceof Player))) return;

        int kb = 0;

        if (damager instanceof Player) {
            Player p = (Player) damager;
            if (p.getItemInHand() != null) {
                kb += p.getItemInHand().getEnchantmentLevel(Enchantment.KNOCKBACK);
            }
            if (p.isSprinting()) {
                kb += 1;
            }
        } else if (plugin.getMode() == Mode.PVP) {
            return;
        }

        if (kb <= 0) return;

        try {
            EntityLiving nmsVictim = (EntityLiving) ((CraftEntity) victim).getHandle();
            EntityLiving nmsDamager = (EntityLiving) ((CraftEntity) damager).getHandle();

            double dx = damager.getLocation().getX() - victim.getLocation().getX();
            double dz = damager.getLocation().getZ() - victim.getLocation().getZ();

            nmsVictim.a(nmsDamager, (float) kb, dx, dz);
        } catch (Throwable ignored) {
            fallback(victim, damager);
        }
    }

    private void fallback(Entity victim, Entity damager) {
        Location a = damager.getLocation();
        Location b = victim.getLocation();

        double dx = b.getX() - a.getX();
        double dz = b.getZ() - a.getZ();
        double len = Math.sqrt(dx * dx + dz * dz);

        if (len < 1e-4) {
            dx = (Math.random() - Math.random()) * 0.01;
            dz = (Math.random() - Math.random()) * 0.01;
            len = Math.sqrt(dx * dx + dz * dz);
        }

        Vector vel = victim.getVelocity();
        vel.setX(vel.getX() / 2 - dx / len * 0.4);
        vel.setZ(vel.getZ() / 2 - dz / len * 0.4);
        vel.setY(vel.getY() / 2 + 0.4);

        if (vel.getY() > 0.4) {
            vel.setY(0.4);
        }

        victim.setVelocity(vel);
    }
}
