package de.Der_Mark_.Manhunt.Listener;

import de.Der_Mark_.Manhunt.ManhuntMain;
import de.Der_Mark_.Manhunt.WichtigeDaten;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

import static de.Der_Mark_.Manhunt.WichtigeDaten.*;

public class GestorbeneSpeedrunnerNichtAlsErstesInsEnde implements Listener {
    ManhuntMain plugin;
    public GestorbeneSpeedrunnerNichtAlsErstesInsEnde(ManhuntMain plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
    }

    @EventHandler
    public void onSpielerWillEndeBetreten(PlayerTeleportEvent event) {
        //Code-Abbruch, wenn Teleport kein Teleport ins Ende wäre
        if(event.getCause() != PlayerTeleportEvent.TeleportCause.END_PORTAL) {
            return;
        }
        Player player = event.getPlayer();
        //Ende-Wurde-Betreten-Zustand wird gespeichert:
        if(speedrunnerListe.contains(player.getName())) {
            if(!gestorbeneSpeedrunnerListe.contains(player.getName())) {
                setEndeWurdeBetreten(true);
            }
        }
        if(hunterListe.contains(player.getName())) {
            setEndeWurdeBetreten(true);
        }
        //Code-Abbruch, wenn Spieler kein gestorbener Speedrunner ist
        if(!gestorbeneSpeedrunnerListe.contains(player.getName())) {
            return;
        }
        //Code-Abbruch, wenn Ende schon betreten wurde
        if(endeWurdeBetreten) {
            return;
        }
        //Code-Abbruch, wenn Spiel schon entschieden ist
        if(siegFürSpeedrunner != null) {
            return;
        }
        //Event-Abbruch
        event.setCancelled(true);
        player.sendMessage(ManhuntMain.PRIVATE_NACHRICHT_FEHLSCHLAG + "Du kannst das Ende nicht als Erster betreten, da du Speedrunner bist und bereits gestorben bist. " +
                "Du dienst nur noch dazu, die verbleibenden Speedrunner zu unterstützen.");
    }
}
