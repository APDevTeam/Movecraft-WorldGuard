package net.countercraft.movecraft.worldguard.listener;

import com.sk89q.worldguard.protection.flags.Flags;
import net.countercraft.movecraft.MovecraftLocation;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.PilotedCraft;
import net.countercraft.movecraft.events.CraftTranslateEvent;
import net.countercraft.movecraft.worldguard.MovecraftWorldGuard;
import net.countercraft.movecraft.worldguard.config.Config;
import net.countercraft.movecraft.worldguard.localisation.I18nSupport;
import net.countercraft.movecraft.worldguard.utils.CustomFlags;
import net.countercraft.movecraft.worldguard.utils.WorldGuardUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class CraftTranslateListener implements Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onCraftTranslate(@NotNull CraftTranslateEvent e) {
        Craft craft = e.getCraft();
        if(!(craft instanceof PilotedCraft) || e.getNewHitBox().isEmpty())
            return;

        WorldGuardUtils wgUtils = MovecraftWorldGuard.getInstance().getWGUtils();
        World w = craft.getWorld();
        Player p = ((PilotedCraft) craft).getPilot();
        boolean canBuild = wgUtils.allowedTo(p, w, e.getNewHitBox(), Flags.BUILD);
        boolean canTranslate = wgUtils.allowedTo(p, w, e.getNewHitBox(), CustomFlags.ALLOW_TRANSLATE);
        if(canBuild || canTranslate)
            return; // return if the player is allowed to translate in the new location

        // Find the first offending location and notify the player
        MovecraftLocation location = e.getNewHitBox().getMidPoint();
        for(MovecraftLocation ml : e.getNewHitBox()) {
            Location loc = ml.toBukkit(w);
            if(!wgUtils.allowedTo(p, loc, Flags.BUILD)) {
                location = ml;
                break;
            }
            if(!wgUtils.allowedTo(p, loc, CustomFlags.ALLOW_TRANSLATE)) {
                location = ml;
                break;
            }
        }

        e.setCancelled(true);
        e.setFailMessage(String.format(I18nSupport.getInternationalisedString( "Translation - WorldGuard - Not Permitted To Build" ) + " @ %d,%d,%d", location.getX(), location.getY(), location.getZ()));
    }
}
