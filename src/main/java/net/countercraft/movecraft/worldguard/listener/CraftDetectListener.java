package net.countercraft.movecraft.worldguard.listener;

import com.sk89q.worldguard.protection.flags.Flags;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.PilotedCraft;
import net.countercraft.movecraft.events.CraftDetectEvent;
import net.countercraft.movecraft.util.hitboxes.HitBox;
import net.countercraft.movecraft.worldguard.CustomFlags;
import net.countercraft.movecraft.worldguard.MovecraftWorldGuard;
import net.countercraft.movecraft.worldguard.localisation.I18nSupport;
import net.countercraft.movecraft.worldguard.utils.WorldGuardUtils;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class CraftDetectListener implements Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onCraftDetect(@NotNull CraftDetectEvent e) {
        Craft craft = e.getCraft();
        HitBox hitBox = craft.getHitBox();
        if (!(craft instanceof PilotedCraft) || hitBox.isEmpty())
            return;

        WorldGuardUtils wgUtils = MovecraftWorldGuard.getInstance().getWGUtils();
        World w = craft.getWorld();
        Player p = ((PilotedCraft) craft).getPilot();

        // Check custom flag
        switch (wgUtils.getState(p, w, hitBox, CustomFlags.ALLOW_CRAFT_PILOT)) {
            case ALLOW:
                return; // Craft is allowed to pilot
            case DENY:
                // Craft is not allowed to pilot
                e.setCancelled(true);
                e.setFailMessage(I18nSupport.getInternationalisedString("CustomFlags - Detection Failed"));
                return;
            default:
                break;
        }

        // Check build flag
        switch (wgUtils.getState(p, w, hitBox, Flags.BUILD)) {
            case ALLOW:
                break; // Craft is allowed to build
            case DENY:
                // Craft is not allowed to build
                e.setCancelled(true);
                e.setFailMessage(I18nSupport.getInternationalisedString(
                    "Detection - WorldGuard - Not Permitted To Build"));
                return;
            default:
                break;
        }
    }
}
