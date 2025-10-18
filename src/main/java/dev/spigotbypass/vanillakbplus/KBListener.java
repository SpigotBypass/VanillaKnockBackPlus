package dev.spigotbypass.vanillakbplus;
import org.bukkit.Location;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import net.minecraft.server.v1_8_R3.EntityLiving;
public class KBListener implements Listener{
    private final VanillaKnockbackPlus pl;
    public KBListener(VanillaKnockbackPlus p){this.pl=p;}
    @EventHandler(priority=EventPriority.MONITOR)
    public void onDamage(EntityDamageByEntityEvent e){
        if(e.isCancelled())return;
        Entity d=e.getDamager(),v=e.getEntity();
        if(pl.getMode()==VanillaKnockbackPlus.Mode.PVP&&(!(d instanceof Player)||!(v instanceof Player)))return;
        int kb=0;
        if(d instanceof Player){
            Player p=(Player)d;
            if(p.getItemInHand()!=null){
                Integer lvl=p.getItemInHand().getEnchantmentLevel(Enchantment.KNOCKBACK);
                if(lvl!=null)kb+=lvl;
            }
            if(p.isSprinting())kb+=1;
        }else if(pl.getMode()==VanillaKnockbackPlus.Mode.PVP)return;
        if(kb<=0)return;
        try{
            EntityLiving victim=(EntityLiving)((CraftEntity)v).getHandle();
            EntityLiving damager=(EntityLiving)((CraftEntity)d).getHandle();
            double dx=d.getLocation().getX()-v.getLocation().getX();
            double dz=d.getLocation().getZ()-v.getLocation().getZ();
            victim.a(damager,kb*0.5F,dx,dz);
        }catch(Throwable t){
            fallback(v,d);
        }
    }
    private void fallback(Entity v,Entity d){
        Location a=d.getLocation(),b=v.getLocation();
        double dx=b.getX()-a.getX(),dz=b.getZ()-a.getZ(),len=Math.sqrt(dx*dx+dz*dz);
        if(len<1e-4){dx=(Math.random()-Math.random())*0.01;dz=(Math.random()-Math.random())*0.01;len=Math.sqrt(dx*dx+dz*dz);}
        Vector vel=v.getVelocity();
        vel.setX(vel.getX()/2-dx/len*0.4);
        vel.setZ(vel.getZ()/2-dz/len*0.4);
        vel.setY(vel.getY()/2+0.4);
        if(vel.getY()>0.4)vel.setY(0.4);
        v.setVelocity(vel);
    }
}