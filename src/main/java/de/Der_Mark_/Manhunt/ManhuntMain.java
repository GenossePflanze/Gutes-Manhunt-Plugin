package de.Der_Mark_.Manhunt;

import de.Der_Mark_.Manhunt.Listener.*;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static de.Der_Mark_.Manhunt.PluginDatenVerwalten.ladeDatenUndSetzeSpielmodus;
import static de.Der_Mark_.Manhunt.PluginDatenVerwalten.speichereDaten;
import static de.Der_Mark_.Manhunt.WichtigeDaten.welcherBlockWarBevorLeitsteinHier;
import static de.Der_Mark_.Manhunt.WichtigeDaten.wessenKompassZeigtAufWenGerade;

public class ManhuntMain extends JavaPlugin {
    public static int ANZAHL_TOTE_SPEEDRUNNER_FÜR_HUNTER_SIEG = 1;
    public static boolean ALLE_SPEEDRUNNER_MÜSSEN_BESIEGT_WERDEN = true;
    public static boolean KOMPASS_ZEIGT_ZU_PORTAL = true;

    public static ManhuntMain plugin;
    ConfigurationManager configManager;
    public static long seed;

    public static String PLUGIN_PREFIX = ChatColor.DARK_RED + "[" + ChatColor.GREEN + "Manhunt-Plugin" + ChatColor.DARK_RED + "] ";
    public static String PRIVATE_NACHRICHT_NORMAL =  PLUGIN_PREFIX + ChatColor.GREEN;
    public static String PRIVATE_NACHRICHT_FEHLSCHLAG = PLUGIN_PREFIX + ChatColor.RED;
    public static String GLOBALE_NACHRICHT_NORMAL = PLUGIN_PREFIX + ChatColor.GOLD;
    public static String GLOBALE_NACHRICHT_FEHLSCHLAG = PLUGIN_PREFIX + ChatColor.DARK_RED;

    @Override
    public void onEnable() {
        plugin = this;
        configManager = new ConfigurationManager(getDataFolder(), "config.yml", this);
        configManager.load();
        seed = this.getServer().getWorlds().get(0).getSeed();
        PluginDatenVerwalten.datenOrdnerPfad = "plugins/GutesManhuntPlugin/WorldSeed" + seed + "/";

        listenerRegistrieren();

        befehleRegistrieren();

        parseValues();

        ladeDatenUndSetzeSpielmodus(plugin.getServer());

        kompasseAktualisieren();
    }

    private void befehleRegistrieren() {
        ZuweisungsBefehle zuweisungsBefehle = new ZuweisungsBefehle(this);
        this.getCommand("speedrunner_add").setExecutor(zuweisungsBefehle);
        this.getCommand("hunter_add").setExecutor(zuweisungsBefehle);
        this.getCommand("speedrunner_remove").setExecutor(zuweisungsBefehle);
        this.getCommand("hunter_remove").setExecutor(zuweisungsBefehle);
        this.getCommand("gestorbener_speedrunner_add").setExecutor(zuweisungsBefehle);
        this.getCommand("gestorbener_speedrunner_remove").setExecutor(zuweisungsBefehle);
        this.getCommand("switch_kompasszeigtzuportal").setExecutor(zuweisungsBefehle);
        this.getCommand("change_anzahltotespeedrunnerfürhuntersieg").setExecutor(zuweisungsBefehle);
        this.getCommand("spielernamen_austauschen").setExecutor(zuweisungsBefehle);
        this.getCommand("start_pause").setExecutor(zuweisungsBefehle);
    }

    private void listenerRegistrieren() {
        new SpeedrunnerTodListener(this);
        new EnderdrachenTodListener(this);
        new GestorbeneSpeedrunnerNichtAlsErstesInsEnde(this);
        new KompassZielWechseln(this);
        new AntiLeitsteinZerstörung(this);
        new KompassFürRespawnteHunter(this);
        new KompassFürNeuGejointeHunter(this);
        new KompassTracktErstenGejointenSpeedrunner(this);
        new LetztesPortalEinesSpielersSpeichern(this);
        new WillkommensNachricht(this);
        new ManhuntPausenListener(this);
    }

    private void kompasseAktualisieren() {
        new BukkitRunnable() {
            @Override
            public void run() {
                //Alte Leitsteine entfernen:
                AlteLeitsteineEntfernen();

                for (String hunterName : WichtigeDaten.hunterListe) {
                    Player hunter = plugin.getServer().getPlayer(hunterName);
                    boolean hunterOnline = hunter != null;
                    if(hunterOnline) {
                        String anvisierterSpeedrunnerName = wessenKompassZeigtAufWenGerade.get(hunterName);
                        Player anvisierterSpeedrunner = null;
                        if(anvisierterSpeedrunnerName != null) {
                            anvisierterSpeedrunner = plugin.getServer().getPlayer(anvisierterSpeedrunnerName);
                        }
                        boolean anvisierterSpeedrunnerOnline = anvisierterSpeedrunner != null;

                        Location leitsteinLoc = null;
                        if(anvisierterSpeedrunnerOnline) {
                            World world = hunter.getWorld();
                            if (world == anvisierterSpeedrunner.getWorld() || KOMPASS_ZEIGT_ZU_PORTAL) {
                                //Neuen Leitstein setzen:
                                Location speedrunnerOderPortalLoc = speedrunnerOderPortalLoc(anvisierterSpeedrunner, hunter);
                                if (!world.getEnvironment().equals(World.Environment.THE_END)) {
                                    int y;
                                    if (world.getEnvironment().equals(World.Environment.NORMAL)) {
                                        y = -64;
                                    } else {
                                        y = 0;
                                    }
                                    leitsteinLoc = new Location(hunter.getWorld(), speedrunnerOderPortalLoc.getBlock().getX(), y, speedrunnerOderPortalLoc.getBlock().getZ());
                                    if (leitsteinLoc.getBlock().getType() == Material.BEDROCK) {
                                        welcherBlockWarBevorLeitsteinHier.put(leitsteinLoc, leitsteinLoc.getBlock().getType());
                                        leitsteinLoc.getBlock().setType(Material.LODESTONE);
                                    }
                                } else {
                                    if (speedrunnerOderPortalLoc.getY() < 128) {
                                        leitsteinLoc = new Location(hunter.getWorld(), speedrunnerOderPortalLoc.getBlock().getX(), 255, speedrunnerOderPortalLoc.getBlock().getZ());
                                        if (leitsteinLoc.getBlock().getType() == Material.AIR) {
                                            welcherBlockWarBevorLeitsteinHier.put(leitsteinLoc, leitsteinLoc.getBlock().getType());
                                            leitsteinLoc.getBlock().setType(Material.LODESTONE);
                                        }
                                    } else {
                                        leitsteinLoc = new Location(hunter.getWorld(), speedrunnerOderPortalLoc.getBlock().getX(), 0, speedrunnerOderPortalLoc.getBlock().getZ());
                                        if (leitsteinLoc.getBlock().getType() == Material.AIR ||
                                                leitsteinLoc.getBlock().getType() == Material.OBSIDIAN
                                        ) {
                                            welcherBlockWarBevorLeitsteinHier.put(leitsteinLoc, leitsteinLoc.getBlock().getType());
                                            leitsteinLoc.getBlock().setType(Material.LODESTONE);
                                        }
                                    }
                                }
                            }
                        }
                        for (int i = 0; i < 41; i++) {
                            if(hunter.getInventory().getItem(i) != null && hunter.getInventory().getItem(i).getType() == Material.COMPASS) {
                                ItemStack kompass = hunter.getInventory().getItem(i);
                                CompassMeta meta = (CompassMeta) kompass.getItemMeta();
                                meta.setLodestone(leitsteinLoc);
                                kompass.setItemMeta(meta);
                                hunter.getInventory().setItem(i, kompass);
                            }
                        }

                    }
                }
            }
        }.runTaskTimer(plugin,0,10);
    }

    private static Location speedrunnerOderPortalLoc (Player anvisierterSpeedrunner, Player hunter) {
        if (anvisierterSpeedrunner.getWorld().equals(hunter.getWorld())) {
            return anvisierterSpeedrunner.getLocation();
        }
        Location portalLoc = null;
        switch (hunter.getWorld().getEnvironment()) {
            case NORMAL:
                portalLoc =  WichtigeDaten.letztePostitionDesSpeedrunnersInOberwelt.get(anvisierterSpeedrunner.getName());
                break;
            case NETHER:
                portalLoc =  WichtigeDaten.letztePostitionDesSpeedrunnersImNether.get(anvisierterSpeedrunner.getName());
                break;
            case THE_END:
                portalLoc =  WichtigeDaten.letztePostitionDesSpeedrunnersImEnde.get(anvisierterSpeedrunner.getName());
                break;
        }
        if (portalLoc != null) {return portalLoc; }
        switch (hunter.getWorld().getEnvironment()) {
            case NORMAL: return WichtigeDaten.zugangsPostitionDesHuntersInOberwelt.get(hunter.getName());
            case NETHER: return WichtigeDaten.zugangsPostitionDesHuntersInNether.get(hunter.getName());
            case THE_END: return WichtigeDaten.zugangsPostitionDesHuntersInEnde.get(hunter.getName());
        }

        Bukkit.broadcastMessage(GLOBALE_NACHRICHT_FEHLSCHLAG + "ManhuntMain.speedrunnerOderPortalLoc: Dieser Fall sollte nicht eintreten. " +
                "hunter.getWorld().getEnvironment(): " + hunter.getWorld().getEnvironment()
        );
        return null;
    }

    public void AlteLeitsteineEntfernen() {
        Iterator it = welcherBlockWarBevorLeitsteinHier.entrySet().iterator();
        List<Location> löscheHashMapEinträge = new ArrayList<>();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            löscheHashMapEinträge.add((Location) pair.getKey());
        }
        for (Location tmp: löscheHashMapEinträge) {
            tmp.getBlock().setType(welcherBlockWarBevorLeitsteinHier.get(tmp));
            welcherBlockWarBevorLeitsteinHier.remove(tmp);
        }
    }

    public static boolean EndeWurdeBetreten() {
        Boolean endeWurdeBereitsBetreten = false;
        for(World world : plugin.getServer().getWorlds()) {
            if(world.getEnvironment() == World.Environment.THE_END) {
                endeWurdeBereitsBetreten = true;
            }
        }
        return endeWurdeBereitsBetreten;
    }

    public static void aufWenZeigtKompassNachricht(String hunterName, String speedrunnerName) {
        Player nunVerfolgterSpeedrunner = plugin.getServer().getPlayer(speedrunnerName);
        Player hunter = plugin.getServer().getPlayer(hunterName);
        if (hunter == null) {return; }
        if(nunVerfolgterSpeedrunner == null || nunVerfolgterSpeedrunner.isDead()) {
            hunter.sendMessage(ManhuntMain.PRIVATE_NACHRICHT_FEHLSCHLAG + "Dein Kompass würde jetzt auf " + speedrunnerName + " zeigen, " +
                    "aber " + speedrunnerName + " ist gerade nicht auf dem Server.");
        } else {
            if(nunVerfolgterSpeedrunner.getWorld() != hunter.getWorld() && !KOMPASS_ZEIGT_ZU_PORTAL) {
                hunter.sendMessage(ManhuntMain.PRIVATE_NACHRICHT_FEHLSCHLAG + "Dein Kompass würde jetzt auf " + speedrunnerName + " zeigen, " +
                        "aber " + speedrunnerName + " ist in einer anderen Dimension.");
            } else {
                hunter.sendMessage(ManhuntMain.PRIVATE_NACHRICHT_NORMAL + "Dein Kompass zeigt nun auf " + speedrunnerName + ".");
            }
        }
    }

    public void parseValues() {
        try {
            KOMPASS_ZEIGT_ZU_PORTAL = Boolean.parseBoolean(getConfig().getString("kompassZeigtZuPortal"));
            ALLE_SPEEDRUNNER_MÜSSEN_BESIEGT_WERDEN = Boolean.parseBoolean(getConfig().getString("alleSpeedrunnerMüssenBesiegtWerden"));
            ANZAHL_TOTE_SPEEDRUNNER_FÜR_HUNTER_SIEG = Integer.parseInt(getConfig().getString("anzahlToteSpeedrunnerFürHunterSieg"));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage("MESSAGE GHGJFESFES");
        }
    }

    @Override
    public void onDisable() {
        //Alte Leitsteine entfernen:
        AlteLeitsteineEntfernen();
        speichereDaten();
    }
}

//Wenn Spieler in unterschiedlicher Dimension und zuerst hunter zugewiesen wird, klappt nichts
