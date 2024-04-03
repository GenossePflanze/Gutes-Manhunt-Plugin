package de.Der_Mark_.Manhunt.Listener;

import de.Der_Mark_.Manhunt.ManhuntMain;
import de.Der_Mark_.Manhunt.WichtigeDaten;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class KompassTracktErstenGejointenSpeedrunner implements Listener {
    ManhuntMain plugin;
    public KompassTracktErstenGejointenSpeedrunner(ManhuntMain plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
    }

    @EventHandler
    public void onSpeedrunnerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        //Code-Abbruch, wenn Spieler kein Speedrunner ist
        if(!WichtigeDaten.speedrunnerListe.contains(player.getName())) {
            return;
        }
        if (schonAndereSpeedrunnerOnline(player)) {return; }
        for (String hunterName : WichtigeDaten.hunterListe) {
            Player hunter = plugin.getServer().getPlayer(hunterName);
            if (hunter != null) {
                WichtigeDaten.wessenKompassZeigtAufWenGerade_put(hunter.getName(), player.getName());
                //Nachricht:
                ManhuntMain.aufWenZeigtKompassNachricht(hunter.getName(), player.getName());
            }
        }
    }

    private boolean schonAndereSpeedrunnerOnline(Player speedrunner) {
        for (String srName : WichtigeDaten.speedrunnerListe) {
            if (!srName.equals(speedrunner.getName())) {
                return true;
            }
        }
        return false;
    }
}
