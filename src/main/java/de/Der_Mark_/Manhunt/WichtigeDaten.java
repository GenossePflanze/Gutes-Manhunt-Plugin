package de.Der_Mark_.Manhunt;

import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.HashMap;

import static de.Der_Mark_.Manhunt.PluginDatenVerwalten.speichereListe;
import static de.Der_Mark_.Manhunt.PluginDatenVerwalten.wichtigeBoolscheWerteSpeichern;

public class WichtigeDaten {
    public static ArrayList<String> speedrunnerListe = new ArrayList<>();
    public static ArrayList<String> hunterListe = new ArrayList<>();
    public static ArrayList<String> gestorbeneSpeedrunnerListe = new ArrayList<>();

    public static HashMap<String, String> wessenKompassZeigtAufWenGerade = new HashMap<>();

    public static HashMap<String, Location> letztePostitionDesSpeedrunnersInOberwelt = new HashMap<>();
    public static HashMap<String, Location> letztePostitionDesSpeedrunnersImNether = new HashMap<>();
    public static HashMap<String, Location> letztePostitionDesSpeedrunnersImEnde = new HashMap<>();
    public static HashMap<String, Location> zugangsPostitionDesHuntersInOberwelt = new HashMap<>();
    public static HashMap<String, Location> zugangsPostitionDesHuntersInNether = new HashMap<>();
    public static HashMap<String, Location> zugangsPostitionDesHuntersInEnde = new HashMap<>();

    public static Boolean siegFürSpeedrunner;
    public static Boolean endeWurdeBetreten = false;

    //wird nur beim Serverstopp gespeichert und geht beim Absturz verloren... müsste sonst aber zu oft gespeichert werden
    public static HashMap<Location, Material> welcherBlockWarBevorLeitsteinHier = new HashMap<>();

    public static void speedrunnerListe_add(String spielerName) {
        speedrunnerListe.add(spielerName);
        speichereListe(speedrunnerListe, "speedrunnerListe");
    }

    public static void speedrunnerListe_remove(String spielerName) {
        speedrunnerListe.remove(spielerName);
        speichereListe(speedrunnerListe, "speedrunnerListe");
    }

    public static void hunterListe_add(String spielerName) {
        hunterListe.add(spielerName);
        speichereListe(hunterListe, "hunterListe");
    }

    public static void hunterListe_remove(String spielerName) {
        hunterListe.remove(spielerName);
        speichereListe(hunterListe, "hunterListe");
    }

    public static void gestorbeneSpeedrunnerListe_add(String spielerName) {
        gestorbeneSpeedrunnerListe.add(spielerName);
        speichereListe(gestorbeneSpeedrunnerListe, "gestorbeneSpeedrunnerListe");
    }

    public static void gestorbeneSpeedrunnerListe_remove(String spielerName) {
        gestorbeneSpeedrunnerListe.remove(spielerName);
        speichereListe(gestorbeneSpeedrunnerListe, "gestorbeneSpeedrunnerListe");
    }

    public static void wessenKompassZeigtAufWenGerade_put(String hunter, String speedrunner) {
        wessenKompassZeigtAufWenGerade.put(hunter, speedrunner);
        speichereListe(wessenKompassZeigtAufWenGerade, "wessenKompassZeigtAufWenGerade");
    }

    public static void letztePostitionDesSpeedrunnersInOberwelt_put(String spielerName, Location altePos) {
        letztePostitionDesSpeedrunnersInOberwelt.put(spielerName, altePos);
        speichereListe(letztePostitionDesSpeedrunnersInOberwelt, "letztePostitionDesSpeedrunnersInOberwelt");
    }

    public static void letztePostitionDesSpeedrunnersImNether_put(String spielerName, Location altePos) {
        letztePostitionDesSpeedrunnersImNether.put(spielerName, altePos);
        speichereListe(letztePostitionDesSpeedrunnersImNether, "letztePostitionDesSpeedrunnersImNether");
    }

    public static void letztePostitionDesSpeedrunnersImEnde_put(String spielerName, Location altePos) {
        letztePostitionDesSpeedrunnersImEnde.put(spielerName, altePos);
        speichereListe(letztePostitionDesSpeedrunnersImEnde, "letztePostitionDesSpeedrunnersImEnde");
    }

    public static void zugangsPostitionDesHuntersInOberwelt_put(String spielerName, Location altePos) {
        zugangsPostitionDesHuntersInOberwelt.put(spielerName, altePos);
        speichereListe(zugangsPostitionDesHuntersInOberwelt, "zugangsPostitionDesHuntersInOberwelt");
    }

    public static void zugangsPostitionDesHuntersInNether_put(String spielerName, Location altePos) {
        zugangsPostitionDesHuntersInNether.put(spielerName, altePos);
        speichereListe(zugangsPostitionDesHuntersInNether, "zugangsPostitionDesHuntersInNether");
    }

    public static void zugangsPostitionDesHuntersInEnde_put(String spielerName, Location altePos) {
        zugangsPostitionDesHuntersInEnde.put(spielerName, altePos);
        speichereListe(zugangsPostitionDesHuntersInEnde, "zugangsPostitionDesHuntersInEnde");
    }

    public static void setSiegFürSpeedrunner(Boolean speedrunnerHabenGewonnen) {
        siegFürSpeedrunner = speedrunnerHabenGewonnen;
        wichtigeBoolscheWerteSpeichern();
    }

    public static void setEndeWurdeBetreten(Boolean endeWurdeGeradeBetreten) {
        endeWurdeBetreten = endeWurdeGeradeBetreten;
        wichtigeBoolscheWerteSpeichern();
    }
}
