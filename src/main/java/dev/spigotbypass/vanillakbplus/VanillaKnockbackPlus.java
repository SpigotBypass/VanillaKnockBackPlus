package dev.spigotbypass.vanillakbplus;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
public class VanillaKnockbackPlus extends JavaPlugin{
    public enum Mode{PVP,ALL}
    private Mode mode;
    @Override public void onEnable(){
        saveDefaultConfig();
        FileConfiguration c=getConfig();
        mode="all".equalsIgnoreCase(c.getString("mode","pvp"))?Mode.ALL:Mode.PVP;
        getServer().getPluginManager().registerEvents(new KBListener(this),this);
        Bukkit.getLogger().info("VanillaKnockbackPlus enabled (mode="+mode+")");
    }
    @Override public void onDisable(){
        Bukkit.getLogger().info("VanillaKnockbackPlus disabled");
    }
    public Mode getMode(){return mode;}
}