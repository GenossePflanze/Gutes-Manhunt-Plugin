package de.Der_Mark_.Manhunt.Listener;

import de.Der_Mark_.Manhunt.ManhuntMain;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

import static de.Der_Mark_.Manhunt.WichtigeDaten.*;

public class LetztesPortalEinesSpielersSpeichern implements Listener {
    ManhuntMain plugin;
    public LetztesPortalEinesSpielersSpeichern(ManhuntMain plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerTeleportsInAnotherDimension(PlayerTeleportEvent event) {
        if(!(event.getCause().equals(PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) ||
                event.getCause().equals(PlayerTeleportEvent.TeleportCause.END_PORTAL)
        )) {return; }
        Location altePos = event.getFrom();
        Player player = event.getPlayer();
        if (altePos.getWorld().equals(event.getTo().getWorld())) {
            Bukkit.broadcastMessage(ManhuntMain.GLOBALE_NACHRICHT_FEHLSCHLAG + "onPlayerTeleportsInAnotherDimension: Spieler " +
                    player.getName() + " teleportierte sich durch Portal in selbe Welt.");
            return;
        }
        //PreTeleport-Position von Speedrunnern speichern:
        if (speedrunnerListe.contains(player.getName())) {
            switch (altePos.getWorld().getEnvironment()) {
                case NORMAL:
                    letztePostitionDesSpeedrunnersInOberwelt_put(player.getName(), altePos);
                    break;
                case NETHER:
                    letztePostitionDesSpeedrunnersImNether_put(player.getName(), altePos);
                    break;
                case THE_END:
                    letztePostitionDesSpeedrunnersImEnde_put(player.getName(), altePos);
                    break;
            }
        }
        //PostTeleport-Position von Huntern speichern:
        if (hunterListe.contains(player.getName())) {
            switch (event.getTo().getWorld().getEnvironment()) {
                case NORMAL:
                    zugangsPostitionDesHuntersInOberwelt_put(player.getName(), event.getTo());
                    break;
                case NETHER:
                    zugangsPostitionDesHuntersInNether_put(player.getName(), event.getTo());
                    break;
                case THE_END:
                    zugangsPostitionDesHuntersInEnde_put(player.getName(), event.getTo());
                    break;
            }
        }
    }
}
