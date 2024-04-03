package de.Der_Mark_.Manhunt.Listener;

import de.Der_Mark_.Manhunt.ManhuntMain;
import org.bukkit.Bukkit;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import static de.Der_Mark_.Manhunt.WichtigeDaten.*;

public class EnderdrachenTodListener implements Listener {
    ManhuntMain plugin;
    public EnderdrachenTodListener(ManhuntMain plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
    }

    @EventHandler
    public void onEnderdrachenTod(EntityDeathEvent event) {
        //Code-Abbruch, wenn Spiel schon entschieden ist
        if(siegFürSpeedrunner != null) {
            return;
        }
        LivingEntity livingEntity = event.getEntity();
        //Code-Abbruch, wenn gestorbenes Wesen kein Enderdrache ist
        if(!(livingEntity instanceof EnderDragon)) {
            return;
        }
        setSiegFürSpeedrunner(true);
        Bukkit.broadcastMessage(ManhuntMain.GLOBALE_NACHRICHT_NORMAL + "Der Enderdrache ist tot, somit haben die Speedrunner gewonnen!!!");
    }
}
