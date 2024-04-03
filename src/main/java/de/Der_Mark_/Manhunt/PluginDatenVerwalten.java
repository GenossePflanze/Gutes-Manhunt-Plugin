package de.Der_Mark_.Manhunt;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static de.Der_Mark_.Manhunt.WichtigeDaten.*;

public class PluginDatenVerwalten {
    public static String datenOrdnerPfad;

    public static void speichereDaten() {
        // Speichern der Daten in der YAML-Datei
        spielerDatenSpeichern();

        wichtigeBoolscheWerteSpeichern();

        speichereListe(welcherBlockWarBevorLeitsteinHier, "welcherBlockWarBevorLeitsteinHier");
    }

    public static void spielerDatenSpeichern() {
        speichereListe(speedrunnerListe, "speedrunnerListe");
        speichereListe(hunterListe, "hunterListe");
        speichereListe(gestorbeneSpeedrunnerListe, "gestorbeneSpeedrunnerListe");

        speichereListe(wessenKompassZeigtAufWenGerade, "wessenKompassZeigtAufWenGerade");

        speichereListe(letztePostitionDesSpeedrunnersInOberwelt, "letztePostitionDesSpeedrunnersInOberwelt");
        speichereListe(letztePostitionDesSpeedrunnersImNether, "letztePostitionDesSpeedrunnersImNether");
        speichereListe(letztePostitionDesSpeedrunnersImEnde, "letztePostitionDesSpeedrunnersImEnde");
        speichereListe(zugangsPostitionDesHuntersInOberwelt, "zugangsPostitionDesHuntersInOberwelt");
        speichereListe(zugangsPostitionDesHuntersInNether, "zugangsPostitionDesHuntersInNether");
        speichereListe(zugangsPostitionDesHuntersInEnde, "zugangsPostitionDesHuntersInEnde");
    }

    public static void wichtigeBoolscheWerteSpeichern() {
        HashMap<String, Boolean> wichtigeBoolscheWerte = new HashMap<>();
        wichtigeBoolscheWerte.put("siegFürSpeedrunner", siegFürSpeedrunner);
        wichtigeBoolscheWerte.put("endeWurdeBetreten", endeWurdeBetreten);
        speichereListe(wichtigeBoolscheWerte,"wichtigeBoolscheWerte");
    }

    public static void speichereListe(Object listenObjekt, String listenObjektName) {
        if (listenObjekt instanceof ArrayList ||
                listenObjekt instanceof HashSet ||
                listenObjekt instanceof HashMap
        ) {
            String dateiName = datenOrdnerPfad + listenObjektName + ".yml";
            File datenDatei = new File(dateiName);
            // Speichern der Daten in der separaten YAML-Datei
            FileConfiguration datenConfig = YamlConfiguration.loadConfiguration(datenDatei);
            datenConfig.set(listenObjektName, listenObjekt);
            try {
                datenConfig.save(datenDatei);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void ladeDaten(Server server) {
        // Überprüfe, ob die Daten in der Datei vorhanden sind, andernfalls erstelle neue Listen/Klasse
        speedrunnerListe = ladeArrayList("speedrunnerListe");
        hunterListe = ladeArrayList("hunterListe");
        gestorbeneSpeedrunnerListe = ladeArrayList("gestorbeneSpeedrunnerListe");

        wessenKompassZeigtAufWenGerade = ladeHashMapMitStringsAlsSchlüssel("wessenKompassZeigtAufWenGerade");

        letztePostitionDesSpeedrunnersInOberwelt = ladeHashMapMitStringsAlsSchlüssel("letztePostitionDesSpeedrunnersInOberwelt");
        letztePostitionDesSpeedrunnersImNether = ladeHashMapMitStringsAlsSchlüssel("letztePostitionDesSpeedrunnersImNether");
        letztePostitionDesSpeedrunnersImEnde = ladeHashMapMitStringsAlsSchlüssel("letztePostitionDesSpeedrunnersImEnde");
        zugangsPostitionDesHuntersInOberwelt = ladeHashMapMitStringsAlsSchlüssel("zugangsPostitionDesHuntersInOberwelt");
        zugangsPostitionDesHuntersInNether = ladeHashMapMitStringsAlsSchlüssel("zugangsPostitionDesHuntersInNether");
        zugangsPostitionDesHuntersInEnde = ladeHashMapMitStringsAlsSchlüssel("zugangsPostitionDesHuntersInEnde");

        HashMap<String, Boolean> wichtigeBoolscheWerte = ladeHashMapMitStringsAlsSchlüssel("wichtigeBoolscheWerte");
        siegFürSpeedrunner = wichtigeBoolscheWerte.get("siegFürSpeedrunner");
        endeWurdeBetreten = wichtigeBoolscheWerte.get("endeWurdeBetreten");
        if (endeWurdeBetreten == null) {
            endeWurdeBetreten = false;
        }

        welcherBlockWarBevorLeitsteinHier = ladeHashMapMitLocationAlsSchlüssel("welcherBlockWarBevorLeitsteinHier");
    }

    private static  <T> ArrayList<T> ladeArrayList(String arrayListName) {
        String dateiName = datenOrdnerPfad + arrayListName + ".yml";
        File datenDatei = new File(dateiName);
        if (datenDatei.exists()) {
            FileConfiguration datenConfig = YamlConfiguration.loadConfiguration(datenDatei);
            if (datenConfig.contains(arrayListName)) {
                ArrayList<T> list = (ArrayList<T>) datenConfig.getList(arrayListName);
                if (list != null) {
                    return list;
                }
            }
        }
        return new ArrayList<>();
    }

    private static <T> HashSet<T> ladeHashSet(String hashSetName) {
        String dateiName = datenOrdnerPfad + hashSetName + ".yml";
        File datenDatei = new File(dateiName);
        if (datenDatei.exists()) {
            FileConfiguration datenConfig = YamlConfiguration.loadConfiguration(datenDatei);
            if (datenConfig.contains(hashSetName)) {
                ArrayList<T> list = (ArrayList<T>) datenConfig.getList(hashSetName);
                if (list != null) {
                    return new HashSet<>(list);
                }
            }
        }
        return new HashSet<>();
    }

    private static  <V> HashMap<String, V> ladeHashMapMitStringsAlsSchlüssel(String hashMapName) {
        String dateiName = datenOrdnerPfad + hashMapName + ".yml";
        File datenDatei = new File(dateiName);
        if (datenDatei.exists()) {
            FileConfiguration datenConfig = YamlConfiguration.loadConfiguration(datenDatei);
            ConfigurationSection memorySection = datenConfig.getConfigurationSection(hashMapName);
            if (memorySection != null) {
                HashMap<String, V> map = new HashMap<>();
                for (String mapKey : memorySection.getKeys(false)) {
                    V value = (V) memorySection.get(mapKey);
                    map.put(mapKey, value);
                }
                return map;
            }
        }
        return new HashMap<>();
    }

    private static  <V> HashMap<Location, V> ladeHashMapMitLocationAlsSchlüssel(String hashMapName) {
        String dateiName = datenOrdnerPfad + hashMapName + ".yml";
        File datenDatei = new File(dateiName);
        if (datenDatei.exists()) {
            FileConfiguration datenConfig = YamlConfiguration.loadConfiguration(datenDatei);
            ConfigurationSection memorySection = datenConfig.getConfigurationSection(hashMapName);
            if (memorySection != null) {
                HashMap<Location, V> map = new HashMap<>();
                for (String mapKey : memorySection.getKeys(false)) {
                    V value = (V) memorySection.get(mapKey);
                    map.put(stringAusYmlToLocation(mapKey), value);
                }
                return map;
            }
        }
        return new HashMap<>();
    }

    public static Location stringAusYmlToLocation(String str) {
        if (str == null || str.equals("null")) {
            return null;
        }
        str = str.replace(",",".");
        String[] parts = str.split("; ");
        if (parts.length != 6) {
            throw new IllegalArgumentException("Ungültiger Location-String: " + str);
        }
        String worldStr = parts[0].substring(parts[0].indexOf(": ") + 2);
        World world = Bukkit.getWorld(worldStr);
        double x = Double.parseDouble(parts[1].substring(parts[1].indexOf(": ") + 2));
        double y = Double.parseDouble(parts[2].substring(parts[2].indexOf(": ") + 2));
        double z = Double.parseDouble(parts[3].substring(parts[3].indexOf(": ") + 2));
        float yaw = Float.parseFloat(parts[4].substring(parts[4].indexOf(": ") + 2));
        float pitch = Float.parseFloat(parts[5].substring(parts[5].indexOf(": ") + 2, parts[5].length() - 1));
        return new Location(world, x, y, z, yaw, pitch);
    }
}
