package de.Der_Mark_.Manhunt;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.UUID;

import static de.Der_Mark_.Manhunt.Nützliches.getKeysByValue;
import static de.Der_Mark_.Manhunt.WichtigeDaten.*;

public class ZuweisungsBefehle implements CommandExecutor {
    static ManhuntMain plugin;
    public ZuweisungsBefehle(ManhuntMain plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player && !sender.isOp()) {
            sender.sendMessage(ManhuntMain.PRIVATE_NACHRICHT_FEHLSCHLAG + "Für diesen Befehl benötigst du OP-Rechte.");
            return true;
        }
        if (command.getName().equalsIgnoreCase("speedrunner_add") ||
                command.getName().equalsIgnoreCase("hunter_add") ||
                command.getName().equalsIgnoreCase("speedrunner_remove") ||
                command.getName().equalsIgnoreCase("hunter_remove") ||
                command.getName().equalsIgnoreCase("gestorbener_speedrunner_add") ||
                command.getName().equalsIgnoreCase("gestorbener_speedrunner_remove")
        ) {
            if (args.length == 0) {
                sender.sendMessage(ManhuntMain.PRIVATE_NACHRICHT_FEHLSCHLAG + "Du hast keinen Spieler angegeben.");
                return true;
            }
            Player gefragterSpieler = plugin.getServer().getPlayer(args[0]);
            boolean gefragterSpielerOnline = gefragterSpieler != null;
            String gefragterSpielerName = gefragterSpielerName(args[0]);
            if (gefragterSpielerName == null) {
                sender.sendMessage(String.format(ManhuntMain.PRIVATE_NACHRICHT_FEHLSCHLAG + "Der Spieler %s existiert nicht.", args[0]));
                return true;
            }

            if (command.getName().equalsIgnoreCase("speedrunner_add")) {
                if (!speedrunnerListe.contains(gefragterSpielerName)) {
                    speedrunnerListe_add(gefragterSpielerName);
                    if (!hunterListe.contains(gefragterSpielerName)) {
                        Bukkit.broadcastMessage(ManhuntMain.GLOBALE_NACHRICHT_NORMAL + gefragterSpielerName + " als Speedrunner hinzugefügt.");
                    } else {
                        hunterListe_remove(gefragterSpielerName);
                        Bukkit.broadcastMessage(ManhuntMain.GLOBALE_NACHRICHT_NORMAL + gefragterSpielerName + " zum Speedrunner gewechselt.");
                    }
                    if (gefragterSpielerOnline) {
                        //Kompass von allen Huntern soll auf neuen Speedrunner zeigen, wenn noch kein Ziel vorhanden
                        for (String hunterName : hunterListe) {
                            if (!wessenKompassZeigtAufWenGerade.containsKey(hunterName)) { //Wenn der Hunter noch kein Ziel hat:
                                wessenKompassZeigtAufWenGerade_put(hunterName, gefragterSpielerName);
                                Player hunter = plugin.getServer().getPlayer(hunterName);
                                boolean hunterOnline = hunter != null;
                                if (hunterOnline) {
                                    ManhuntMain.aufWenZeigtKompassNachricht(hunterName, gefragterSpielerName);
                                }
                            }
                        }
                    }
                } else {
                    sender.sendMessage(ManhuntMain.PRIVATE_NACHRICHT_FEHLSCHLAG + gefragterSpielerName + " ist bereits Speedrunner.");
                }
            } else if (command.getName().equalsIgnoreCase("hunter_add")) {
                if (!hunterListe.contains(gefragterSpielerName)) {
                    hunterListe_add(gefragterSpielerName);

                    if (!speedrunnerListe.contains(gefragterSpielerName)) {
                        Bukkit.broadcastMessage(ManhuntMain.GLOBALE_NACHRICHT_NORMAL + gefragterSpielerName + " als Hunter hinzugefügt.");
                    } else {
                        speedrunnerListe_remove(gefragterSpielerName);
                        gestorbeneSpeedrunnerListe_remove(gefragterSpielerName);
                        Bukkit.broadcastMessage(ManhuntMain.GLOBALE_NACHRICHT_NORMAL + gefragterSpielerName + " zum Hunter gewechselt.");
                    }

                    if (gefragterSpielerOnline) {
                        if (speedrunnerListe.size() != 0) {
                            //Kompass von neuem Hunter soll auf ersten verfügbaren Speedrunner zeigen
                            Player speedrunner;
                            for (int i = 0; i < speedrunnerListe.size(); i++) {
                                speedrunner = plugin.getServer().getPlayer(speedrunnerListe.get(i));
                                if (speedrunner != null && speedrunner.getWorld() == gefragterSpieler.getWorld()) {
                                    if (!wessenKompassZeigtAufWenGerade.containsKey(gefragterSpielerName)) {
                                        wessenKompassZeigtAufWenGerade_put(gefragterSpielerName, speedrunner.getName());
                                        gefragterSpieler.sendMessage("Dein Kompass zeigt nun auf " + speedrunner.getName());
                                    }
                                }
                            }
                            //Wenn kein verfügbarer Speedrunner gefunden wurde, dann soll Kompass auf ersten nicht verfügbaren Speedrunner zeigen:
                            if (!wessenKompassZeigtAufWenGerade.containsKey(gefragterSpielerName)) {
                                for (int i = 0; i < speedrunnerListe.size(); i++) {
                                    if (!wessenKompassZeigtAufWenGerade.containsKey(gefragterSpielerName)) {
                                        String speedrunnerName = speedrunnerListe.get(i);
                                        speedrunner = plugin.getServer().getPlayer(speedrunnerName);
                                        wessenKompassZeigtAufWenGerade_put(gefragterSpielerName, speedrunner.getName());
                                        ManhuntMain.aufWenZeigtKompassNachricht(gefragterSpielerName, speedrunnerName);
                                    }
                                }
                            }
                        }

                        boolean hunterHatNochKeinenKompass = true;
                        for (int i = 0; i < 41; i++) {
                            if (gefragterSpieler.getInventory().getItem(i) != null && gefragterSpieler.getInventory().getItem(i).getType() == Material.COMPASS) {
                                hunterHatNochKeinenKompass = false;
                            }
                        }
                        if (hunterHatNochKeinenKompass) {
                            gefragterSpieler.getInventory().addItem(new ItemStack(Material.COMPASS, 1));
                        }
                    }
                } else {
                    sender.sendMessage(ManhuntMain.PRIVATE_NACHRICHT_FEHLSCHLAG + gefragterSpielerName + " ist bereits Hunter.");
                }
            } else if (command.getName().equalsIgnoreCase("speedrunner_remove")) {
                if (speedrunnerListe.contains(gefragterSpielerName)) {
                    speedrunnerListe_remove(gefragterSpielerName);
                    gestorbeneSpeedrunnerListe_remove(gefragterSpielerName);
                    Bukkit.broadcastMessage(ManhuntMain.GLOBALE_NACHRICHT_NORMAL + gefragterSpielerName + " ist nun kein Speedrunner mehr.");
                } else {
                    sender.sendMessage(ManhuntMain.PRIVATE_NACHRICHT_FEHLSCHLAG + args[0] + " ist garkein Speedrunner. Du kannst nur Speedrunner aus dem Team der Speedrunner entfernen.");
                }
            } else if (command.getName().equalsIgnoreCase("hunter_remove")) {
                if (hunterListe.contains(gefragterSpielerName)) {
                    hunterListe_remove(gefragterSpielerName);
                    Bukkit.broadcastMessage(ManhuntMain.GLOBALE_NACHRICHT_NORMAL + gefragterSpielerName + " ist nun kein Hunter mehr.");
                } else {
                    sender.sendMessage(ManhuntMain.PRIVATE_NACHRICHT_FEHLSCHLAG + args[0] + " ist garkein Hunter. Du kannst nur Hunter aus dem Team der Hunter entfernen.");
                }
            } else if (command.getName().equalsIgnoreCase("gestorbener_speedrunner_add")) {
                if (speedrunnerListe.contains(gefragterSpielerName)) {
                    if (!gestorbeneSpeedrunnerListe.contains(gefragterSpielerName)) {
                        gestorbeneSpeedrunnerListe_add(gefragterSpielerName);
                        Bukkit.broadcastMessage(ManhuntMain.GLOBALE_NACHRICHT_NORMAL + gefragterSpielerName +
                                " ist nun als gestorbener Speedrunner markiert und kann somit nicht als erster das Ende betreten.");
                    } else {
                        sender.sendMessage(ManhuntMain.PRIVATE_NACHRICHT_FEHLSCHLAG + gefragterSpielerName + " ist bereits als gestorbener Speedrunner markiert.");
                    }
                } else {
                    sender.sendMessage(ManhuntMain.PRIVATE_NACHRICHT_FEHLSCHLAG + args[0] + " ist kein Speedrunner. " +
                            "Also kann er auch nicht als gestorben markiert werden.");
                }
            } else if (command.getName().equalsIgnoreCase("gestorbener_speedrunner_remove")) {
                if (speedrunnerListe.contains(gefragterSpielerName)) {
                    if (gestorbeneSpeedrunnerListe.contains(gefragterSpielerName)) {
                        gestorbeneSpeedrunnerListe_remove(gefragterSpielerName);
                        Bukkit.broadcastMessage(ManhuntMain.GLOBALE_NACHRICHT_NORMAL + gefragterSpielerName + " gilt nun wieder als noch nicht gestorbener Speedrunner.");
                    } else {
                        sender.sendMessage(ManhuntMain.PRIVATE_NACHRICHT_FEHLSCHLAG + args[0] + " ist noch nicht gestorben. " +
                                "Also braucht auch nicht sein Tod ungeschehen gemacht werden.");
                    }
                } else {
                    sender.sendMessage(ManhuntMain.PRIVATE_NACHRICHT_FEHLSCHLAG + args[0] + " ist kein Speedrunner. " +
                            "Also braucht auch nicht sein Tod ungeschehen gemacht werden.");
                }
            }
            return true;
        } else {
            if (command.getName().equalsIgnoreCase("switch_kompasszeigtzuportal")) {
                if (ManhuntMain.KOMPASS_ZEIGT_ZU_PORTAL) {
                    plugin.getConfig().set("kompassZeigtZuPortal", false);
                    ManhuntMain.KOMPASS_ZEIGT_ZU_PORTAL = false;
                } else {
                    plugin.getConfig().set("kompassZeigtZuPortal", true);
                    ManhuntMain.KOMPASS_ZEIGT_ZU_PORTAL = true;
                }
                sender.sendMessage(ManhuntMain.PRIVATE_NACHRICHT_NORMAL + "kompassZeigtZuPortal: " + ManhuntMain.KOMPASS_ZEIGT_ZU_PORTAL + " gespeichert");
                plugin.saveConfig();
                plugin.reloadConfig();
                return true;
            } else if (command.getName().equalsIgnoreCase("change_anzahltotespeedrunnerfürhuntersieg")) {
                if (args.length == 0) {
                    sender.sendMessage(ManhuntMain.PRIVATE_NACHRICHT_FEHLSCHLAG + "Es fehlt die Angabe einer Zahl. anzahlToteSpeedrunnerFürHunterSieg ist aktuell " +
                            ManhuntMain.ANZAHL_TOTE_SPEEDRUNNER_FÜR_HUNTER_SIEG);
                    return true;
                }
                Integer anzahlSpeedrunner = null;
                try {
                    anzahlSpeedrunner = Integer.parseInt(args[0]);
                } catch (NumberFormatException throwables) { }
                if (anzahlSpeedrunner == null) {
                    sender.sendMessage(ManhuntMain.PRIVATE_NACHRICHT_FEHLSCHLAG + args[0] + " ist keine gültige Zahl.");
                    return true;
                }
                if (anzahlSpeedrunner <= 0) {
                    sender.sendMessage(ManhuntMain.PRIVATE_NACHRICHT_FEHLSCHLAG + "Zahl kann nicht negativ oder 0 sein.");
                    return true;
                }
                if (anzahlSpeedrunner != ManhuntMain.ANZAHL_TOTE_SPEEDRUNNER_FÜR_HUNTER_SIEG) {
                    plugin.getConfig().set("anzahlToteSpeedrunnerFürHunterSieg", anzahlSpeedrunner);
                    ManhuntMain.ANZAHL_TOTE_SPEEDRUNNER_FÜR_HUNTER_SIEG = anzahlSpeedrunner;
                    plugin.saveConfig();
                    plugin.reloadConfig();
                }
                sender.sendMessage(ManhuntMain.PRIVATE_NACHRICHT_NORMAL + "anzahlToteSpeedrunnerFürHunterSieg: " + ManhuntMain.ANZAHL_TOTE_SPEEDRUNNER_FÜR_HUNTER_SIEG + " gespeichert");
                return true;
            } else if (command.getName().equalsIgnoreCase("manhunt_spielernamen_austauschen")) {
                if (args.length < 2) {
                    sender.sendMessage(ManhuntMain.PRIVATE_NACHRICHT_FEHLSCHLAG + "Du musst zwei Spielernamen angeben.");
                    return true;
                }
                String neuerSpielerName = gefragterSpielerName(args[1]);
                if (neuerSpielerName == null) {
                    sender.sendMessage(String.format(ManhuntMain.PRIVATE_NACHRICHT_FEHLSCHLAG + "Der neue Spieler %s existiert nicht.", args[1]));
                    return true;
                }
                ArrayList alleSpielerNamenInAktuellerRunde = (ArrayList) speedrunnerListe.clone();
                alleSpielerNamenInAktuellerRunde.addAll(hunterListe);
                String alterSpielerName = args[0];
                if (alleSpielerNamenInAktuellerRunde.contains(alterSpielerName)) {
                    sender.sendMessage(String.format(ManhuntMain.PRIVATE_NACHRICHT_FEHLSCHLAG + "Der alte Spielername %s existiert nicht in der aktuellen Runde.", alterSpielerName) +
                            "\n " + teilnehmerAufzählung());
                    return true;
                }
                if (hunterListe.contains(alterSpielerName)) {
                    hunterListe_remove(alterSpielerName);
                    hunterListe_add(neuerSpielerName);

                    if (zugangsPostitionDesHuntersInOberwelt.containsKey(alterSpielerName)) {
                        Location value = zugangsPostitionDesHuntersInOberwelt.get(alterSpielerName);
                        zugangsPostitionDesHuntersInOberwelt_put(neuerSpielerName, value);
                    }
                    if (zugangsPostitionDesHuntersInNether.containsKey(alterSpielerName)) {
                        Location value = zugangsPostitionDesHuntersInNether.get(alterSpielerName);
                        zugangsPostitionDesHuntersInNether_put(neuerSpielerName, value);
                    }
                    if (zugangsPostitionDesHuntersInEnde.containsKey(alterSpielerName)) {
                        Location value = zugangsPostitionDesHuntersInEnde.get(alterSpielerName);
                        zugangsPostitionDesHuntersInEnde_put(neuerSpielerName, value);
                    }

                    sender.sendMessage(String.format(ManhuntMain.PRIVATE_NACHRICHT_FEHLSCHLAG + "Alten Hunter %s mit %s ausgetauscht.", alterSpielerName, neuerSpielerName));
                }
                if (speedrunnerListe.contains(alterSpielerName)) {
                    speedrunnerListe_remove(alterSpielerName);
                    speedrunnerListe_add(neuerSpielerName);

                    if (letztePostitionDesSpeedrunnersInOberwelt.containsKey(alterSpielerName)) {
                        Location value = letztePostitionDesSpeedrunnersInOberwelt.get(alterSpielerName);
                        letztePostitionDesSpeedrunnersInOberwelt_put(neuerSpielerName, value);
                    }
                    if (letztePostitionDesSpeedrunnersImNether.containsKey(alterSpielerName)) {
                        Location value = letztePostitionDesSpeedrunnersImNether.get(alterSpielerName);
                        letztePostitionDesSpeedrunnersImNether_put(neuerSpielerName, value);
                    }
                    if (letztePostitionDesSpeedrunnersImEnde.containsKey(alterSpielerName)) {
                        Location value = letztePostitionDesSpeedrunnersImEnde.get(alterSpielerName);
                        letztePostitionDesSpeedrunnersImEnde_put(neuerSpielerName, value);
                    }

                    sender.sendMessage(String.format(ManhuntMain.PRIVATE_NACHRICHT_FEHLSCHLAG + "Alten Speedrunner %s mit %s ausgetauscht.", alterSpielerName, neuerSpielerName));
                }
                if (gestorbeneSpeedrunnerListe.contains(alterSpielerName)) {
                    gestorbeneSpeedrunnerListe_remove(alterSpielerName);
                    gestorbeneSpeedrunnerListe_add(neuerSpielerName);
                }
                if (wessenKompassZeigtAufWenGerade.containsKey(alterSpielerName)) {
                    String value = wessenKompassZeigtAufWenGerade.get(alterSpielerName);
                    wessenKompassZeigtAufWenGerade_put(neuerSpielerName, value);
                }
                if (wessenKompassZeigtAufWenGerade.containsValue(alterSpielerName)) {
                    for (String key : getKeysByValue(wessenKompassZeigtAufWenGerade, alterSpielerName)) {
                        wessenKompassZeigtAufWenGerade_put(key, neuerSpielerName);
                    }
                }
            }
        }
        return true;
    }

    public static String teilnehmerAufzählung() {
        String ausgabe = "Speedrunner:  ";
        for (String speedrunnerName : speedrunnerListe) {
            ausgabe += speedrunnerName + ", ";
        }
        ausgabe = ausgabe.substring(0, ausgabe.length() - 2);
        ausgabe += "\nHunter:  ";
        for (String hunterName : hunterListe) {
            ausgabe += hunterName + ", ";
        }
        ausgabe = ausgabe.substring(0, ausgabe.length() - 2);
        return ausgabe;
    }

    static String gefragterSpielerName(String arg0) {
        UUID gemeinterSpielerUUID = gefragteSpielerUUID(arg0);
        String gemeinterSpielerName = null;
        if (plugin.getServer().getPlayer(gemeinterSpielerUUID) != null) {
            gemeinterSpielerName = plugin.getServer().getPlayer(gemeinterSpielerUUID).getName();
        } else {
            if (gemeinterSpielerUUID != null) {
                gemeinterSpielerName = plugin.getServer().getOfflinePlayer(gemeinterSpielerUUID).getName();
            }
        }
        return gemeinterSpielerName;
    }

    static UUID gefragteSpielerUUID(String arg0) {
        Player gefragterSpieler;
        OfflinePlayer gefragterOfflineSpieler;
        UUID uuid = null;
        if(arg0 != null) {
            gefragterSpieler = plugin.getServer().getPlayerExact(arg0);
            if (gefragterSpieler == null) {
                for(OfflinePlayer tmp : plugin.getServer().getOfflinePlayers()) {
                    if(tmp.getName().equals(arg0)) {
                        gefragterOfflineSpieler = tmp;
                        uuid = gefragterOfflineSpieler.getUniqueId();
                    }
                }
            } else {
                uuid = gefragterSpieler.getUniqueId();
            }
        }
        return uuid;
    }
}
