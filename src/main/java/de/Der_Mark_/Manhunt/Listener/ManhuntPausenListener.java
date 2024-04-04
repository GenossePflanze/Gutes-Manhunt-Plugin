package de.Der_Mark_.Manhunt.Listener;

import de.Der_Mark_.Manhunt.ManhuntMain;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import static de.Der_Mark_.Manhunt.WichtigeDaten.manhuntPause;

public class ManhuntPausenListener implements Listener {
    ManhuntMain plugin;
    public ManhuntPausenListener(ManhuntMain plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
    }

    @EventHandler
    public void onSpielerInteraktion(PlayerInteractEvent event) {
        if (!manhuntPause) {
            return;
        }
        if (event.getPlayer().isOp()) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onSpielerServerBeitritt(PlayerJoinEvent event) {
        event.getPlayer().setWalkSpeed(0.2f); //default
    }

    @EventHandler
    public void onSpielerBewegung(PlayerMoveEvent event) {
        if (!manhuntPause) {
            return;
        }
        if (event.getPlayer().isOp()) {
            return;
        }
        Location oldLoc = event.getFrom();
        Location newLoc = event.getTo().clone();
        newLoc.setX(oldLoc.getX());
        newLoc.setY(oldLoc.getY());
        newLoc.setZ(oldLoc.getZ());
        event.setTo(newLoc);
    }

    @EventHandler
    public void onSpielerNimmtSchaden(EntityDamageEvent event) {
        if (!manhuntPause) {
            return;
        }
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onSpielerMachtSchaden(EntityDamageByEntityEvent event) {
        if (!manhuntPause) {
            return;
        }
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        event.setCancelled(true);
    }

    public static void startManhuntPause(Server server) {
        World world = server.getWorlds().get(0);
        world.setTime(1000); //day
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.MOB_GRIEFING, false);
        world.setDifficulty(Difficulty.PEACEFUL);
    }

    public static void startManhuntSpiel(Server server) {
        World world = server.getWorlds().get(0);
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
        world.setGameRule(GameRule.MOB_GRIEFING, true);
        world.setTime(1000); //day
        world.setDifficulty(Difficulty.EASY);
    }
}
