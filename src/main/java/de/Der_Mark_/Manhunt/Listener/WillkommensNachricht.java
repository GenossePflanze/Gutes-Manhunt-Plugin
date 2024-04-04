package de.Der_Mark_.Manhunt.Listener;

import de.Der_Mark_.Manhunt.ManhuntMain;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import static de.Der_Mark_.Manhunt.WichtigeDaten.*;
import static de.Der_Mark_.Manhunt.ZuweisungsBefehle.teilnehmerAufzählung;

public class WillkommensNachricht implements Listener {
    ManhuntMain plugin;
    public WillkommensNachricht(ManhuntMain plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
    }

    @EventHandler
    public void onSpielerBetrittServer(PlayerJoinEvent event) {
        String siegesNachricht = "";
        if (siegFürSpeedrunner != null && siegFürSpeedrunner) {
            siegesNachricht = "Die Speedrunner haben bereits gewonnen.";
        }
        if (siegFürSpeedrunner != null && !siegFürSpeedrunner) {
            siegesNachricht = "Die Hunter haben bereits gewonnen.";
        }
        if (siegFürSpeedrunner == null && endeWurdeBetreten != null && endeWurdeBetreten) {
            siegesNachricht = "Das Ende wurde bereits betreten, aber noch ist kein Sieg entschieden.";
        }
        event.getPlayer().sendMessage(ManhuntMain.PRIVATE_NACHRICHT_FEHLSCHLAG + "\n" + teilnehmerAufzählung() + "\n" + siegesNachricht);
        if (manhuntPause) {
            event.getPlayer().sendMessage(ManhuntMain.PRIVATE_NACHRICHT_FEHLSCHLAG + "Spielgeschehen pausiert.");
        }
    }
}
