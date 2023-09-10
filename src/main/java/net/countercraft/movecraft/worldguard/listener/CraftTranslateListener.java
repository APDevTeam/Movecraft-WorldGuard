package net.countercraft.movecraft.worldguard.listener;

import com.sk89q.worldguard.protection.flags.Flags;

import net.countercraft.movecraft.MovecraftLocation;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.PilotedCraft;
import net.countercraft.movecraft.events.CraftTranslateEvent;
import net.countercraft.movecraft.util.hitboxes.HitBox;
import net.countercraft.movecraft.worldguard.CustomFlags;
import net.countercraft.movecraft.worldguard.MovecraftWorldGuard;
import net.countercraft.movecraft.worldguard.localisation.I18nSupport;
import net.countercraft.movecraft.worldguard.utils.WorldGuardUtils;
import net.countercraft.movecraft.worldguard.utils.WorldGuardUtils.State;

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
        HitBox hitBox = e.getNewHitBox();
        if (!(craft instanceof PilotedCraft) || hitBox.isEmpty())
            return;

        WorldGuardUtils wgUtils = MovecraftWorldGuard.getInstance().getWGUtils();
        World w = craft.getWorld();
        Player p = ((PilotedCraft) craft).getPilot();

        MovecraftWorldGuard.getInstance().getLogger()
                .info("Checking hitbox for " + p.getDisplayName() + " at <" + hitBox.getMinX() + "," + hitBox.getMinY()
                        + "," + hitBox.getMinZ() + "> to <" + hitBox.getMaxX() + "," + hitBox.getMaxY() + ","
                        + hitBox.getMaxZ() + ">");
        // Check custom flag
        switch (wgUtils.getState(p, w, hitBox, CustomFlags.ALLOW_CRAFT_TRANSLATE)) {
            case ALLOW:
                MovecraftWorldGuard.getInstance().getLogger().info("Allowed");
                return; // Craft is allowed to translate
            case DENY:
                MovecraftWorldGuard.getInstance().getLogger().info("Denied");
                // Craft is not allowed to translate
                e.setCancelled(true);
                for (MovecraftLocation ml : hitBox) {
                    Location loc = ml.toBukkit(w);
                    if (wgUtils.getState(p, loc, CustomFlags.ALLOW_CRAFT_TRANSLATE) == State.DENY) {
                        // Found first denied location, set fail message and return
                        e.setFailMessage(I18nSupport.getInternationalisedString(
                            "CustomFlags - Translation Failed"
                            ) + String.format(" @ %d,%d,%d", ml.getX(), ml.getY(), ml.getZ()));
                        return;
                    }
                }
                // No denied locations found, set fail message and return
                e.setFailMessage(I18nSupport.getInternationalisedString(
                    "CustomFlags - Translation Failed"
                    ) + String.format(" @ %d,%d,%d",
                        hitBox.getMidPoint().getX(), hitBox.getMidPoint().getY(), hitBox.getMidPoint().getZ()));
                return;
            case NONE:
                MovecraftWorldGuard.getInstance().getLogger().info("None");
            default:
                MovecraftWorldGuard.getInstance().getLogger().info("Default");
                break;
        }

        // Check build flag
        switch (wgUtils.getState(p, w, hitBox, Flags.BUILD)) {
            case ALLOW:
                MovecraftWorldGuard.getInstance().getLogger().info("Allowed");
                break; // Craft is allowed to build
            case NONE:
                MovecraftWorldGuard.getInstance().getLogger().info("None");
            case DENY:
                MovecraftWorldGuard.getInstance().getLogger().info("Denied");
                // Craft is not allowed to build
                e.setCancelled(true);
                for (MovecraftLocation ml : hitBox) {
                    Location loc = ml.toBukkit(w);
                    if (wgUtils.getState(p, loc, Flags.BUILD) != State.ALLOW) {
                        // Found first denied location, set fail message and return
                        e.setFailMessage(I18nSupport.getInternationalisedString(
                            "Translation - WorldGuard - Not Permitted To Build"
                            ) + String.format(" @ %d,%d,%d", ml.getX(), ml.getY(), ml.getZ()));
                        return;
                    }
                }
                // No denied locations found, set fail message and return
                e.setFailMessage(I18nSupport.getInternationalisedString(
                    "Translation - WorldGuard - Not Permitted To Build"
                    ) + String.format(" @ %d,%d,%d",
                        hitBox.getMidPoint().getX(), hitBox.getMidPoint().getY(), hitBox.getMidPoint().getZ()));
                return;
            default:
                MovecraftWorldGuard.getInstance().getLogger().info("Default");
                break;
        }
    }
}
