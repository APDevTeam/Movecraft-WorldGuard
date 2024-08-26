package net.countercraft.movecraft.worldguard;

import net.countercraft.movecraft.combat.MovecraftCombat;
import net.countercraft.movecraft.repair.MovecraftRepair;
import net.countercraft.movecraft.worldguard.config.Config;
import net.countercraft.movecraft.worldguard.listener.*;
import net.countercraft.movecraft.worldguard.localisation.I18nSupport;
import net.countercraft.movecraft.worldguard.utils.WorldGuardUtils;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Level;

public final class MovecraftWorldGuard extends JavaPlugin {
    private static MovecraftWorldGuard instance;

    public static MovecraftWorldGuard getInstance() {
        return instance;
    }



    private WorldGuardUtils wgUtils;


    @Override
    public void onLoad() {
        instance = this;

        CustomFlags.register();
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();

        // TODO other languages
        String[] languages = {"en"};
        for (String s : languages) {
            if (!new File(getDataFolder()  + "/localisation/movecraftworldguardlang_"+ s +".properties").exists()) {
                saveResource("localisation/movecraftworldguardlang_"+ s +".properties", false);
            }
        }
        Config.Locale = getConfig().getString("Locale", "en");
        I18nSupport.init();

        Plugin wgPlugin = getServer().getPluginManager().getPlugin("WorldGuard");
        wgUtils = new WorldGuardUtils();
        if(wgPlugin == null || !wgUtils.init(wgPlugin)) {
            getLogger().log(Level.SEVERE, "Movecraft-WorldGuard did not find a compatible version of WorldGuard. Shutting down.");
            getServer().shutdown();
            return;
        }
        getLogger().log(Level.INFO, "Found a compatible version of WorldGuard. Enabling WorldGuard integration.");

        Plugin movecraftCombat = getServer().getPluginManager().getPlugin("Movecraft-Combat");
        if(movecraftCombat instanceof MovecraftCombat) {
            getServer().getPluginManager().registerEvents(new CombatReleaseListener(), this);
            getLogger().info("Found a compatible version of Movecraft-Combat. Enabling Movecraft-Combat integration.");
        }

        Plugin movecraftRepair = getServer().getPluginManager().getPlugin("Movecraft-Repair");
        if(movecraftRepair instanceof MovecraftRepair) {
            getServer().getPluginManager().registerEvents(new ProtoRepairCreatedListener(), this);
            getLogger().info("Found a compatible version of Movecraft-Repair. Enabling Movecraft-Repair integration.");
        }

        getServer().getPluginManager().registerEvents(new CraftDetectListener(), this);
        getServer().getPluginManager().registerEvents(new CraftDirectListener(), this);
        getServer().getPluginManager().registerEvents(new CraftRotateListener(), this);
        getServer().getPluginManager().registerEvents(new CraftSinkListener(), this);
        getServer().getPluginManager().registerEvents(new CraftTranslateListener(), this);
        getServer().getPluginManager().registerEvents(new ExplosionListener(), this);
        getServer().getPluginManager().registerEvents(new BurnListener(), this);
    }

    public WorldGuardUtils getWGUtils() {
        return wgUtils;
    }
}
