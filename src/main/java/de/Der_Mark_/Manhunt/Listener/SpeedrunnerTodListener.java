package de.Der_Mark_.Manhunt.Listener;

import de.Der_Mark_.Manhunt.ManhuntMain;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

import static de.Der_Mark_.Manhunt.WichtigeDaten.*;

public class SpeedrunnerTodListener implements Listener {
    ManhuntMain plugin;
    public SpeedrunnerTodListener(ManhuntMain plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
    }

    @EventHandler
    public void onSpeedrunnerDeath(PlayerDeathEvent event) {
        //Code-Abbruch, wenn Spiel schon entschieden ist
        if(siegFürSpeedrunner != null) {
            return;
        }
        final Player player = event.getEntity();
        //Code-Abbruch, wenn gestorbener Spieler kein Speedrunner ist
        if(!speedrunnerListe.contains(player.getName())) {
            return;
        }
        //Code-Abbruch, wenn Spieler schon einmal gestorben ist
        if(gestorbeneSpeedrunnerListe.contains(player.getName())) {
            return;
        }

        gestorbeneSpeedrunnerListe_add(player.getName());
        final int anzahlVerbleibendeSpeedrunner = speedrunnerListe.size() - gestorbeneSpeedrunnerListe.size();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (alleSpeedrunnerMüssenBesiegtWerden()) {
                    if (anzahlVerbleibendeSpeedrunner != 0) {
                        if (!ManhuntMain.EndeWurdeBetreten()) {
                            Bukkit.broadcastMessage(ManhuntMain.GLOBALE_NACHRICHT_NORMAL + player.getName() + " ist nun gestorben und kann somit nicht mehr als erstes das Ende betreten. " +
                                    anzahlVerbleibendeSpeedrunner + " Speedrunner verbleiben.");
                        } else {
                            Bukkit.broadcastMessage(ManhuntMain.GLOBALE_NACHRICHT_NORMAL + player.getName() + " ist nun gestorben. " +
                                    anzahlVerbleibendeSpeedrunner + " Speedrunner verbleiben.");
                        }
                    } else {
                        setSiegFürSpeedrunner(false);
                        Bukkit.broadcastMessage(ManhuntMain.GLOBALE_NACHRICHT_NORMAL + player.getName() + " ist als letzter verbleibender Speedrunner gestorben, somit haben die Hunter gewonnen!!!");
                    }
                } else if (ManhuntMain.ANZAHL_TOTE_SPEEDRUNNER_FÜR_HUNTER_SIEG == 1) {
                    setSiegFürSpeedrunner(false);
                    Bukkit.broadcastMessage(ManhuntMain.GLOBALE_NACHRICHT_NORMAL + "Speedrunner " + player.getName() + " gestorben, somit haben die Hunter gewonnen!!!");
                } else {
                    int anzahlNochZuBesiegendeSpeedrunner = ManhuntMain.ANZAHL_TOTE_SPEEDRUNNER_FÜR_HUNTER_SIEG - gestorbeneSpeedrunnerListe.size();
                    if (anzahlNochZuBesiegendeSpeedrunner <= 0) {
                        setSiegFürSpeedrunner(false);
                        Bukkit.broadcastMessage(ManhuntMain.GLOBALE_NACHRICHT_NORMAL + "Der " + ManhuntMain.ANZAHL_TOTE_SPEEDRUNNER_FÜR_HUNTER_SIEG +
                                ". Speedrunner " + player.getName() + " ist gestorben, somit haben die Hunter gewonnen!!!");
                    } else {
                        Bukkit.broadcastMessage(ManhuntMain.GLOBALE_NACHRICHT_NORMAL +
                                player.getName() + " ist nun gestorben und kann somit nicht mehr als erstes das Ende betreten. " +
                                anzahlVerbleibendeSpeedrunner + " Speedrunner verbleiben. " +
                                "Für den Sieg der Hunter müssen noch " + anzahlNochZuBesiegendeSpeedrunner + " ausgeschaltet werden.");
                    }
                }
                this.cancel();
            }
        }.runTaskTimer(plugin,0,1);
    }

    private boolean alleSpeedrunnerMüssenBesiegtWerden() {
        return ManhuntMain.ALLE_SPEEDRUNNER_MÜSSEN_BESIEGT_WERDEN || ManhuntMain.ANZAHL_TOTE_SPEEDRUNNER_FÜR_HUNTER_SIEG >= speedrunnerListe.size();
    }
}
