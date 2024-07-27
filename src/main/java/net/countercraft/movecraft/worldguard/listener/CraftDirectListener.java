package net.countercraft.movecraft.worldguard.listener;

import com.sk89q.worldguard.protection.flags.Flags;
import net.countercraft.movecraft.combat.features.directors.Directors;
import net.countercraft.movecraft.combat.features.directors.events.CraftDirectEvent;
import net.countercraft.movecraft.exception.EmptyHitBoxException;
import net.countercraft.movecraft.worldguard.MovecraftWorldGuard;
import net.countercraft.movecraft.worldguard.localisation.I18nSupport;
import net.countercraft.movecraft.worldguard.utils.WorldGuardUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class CraftDirectListener implements Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onCraftDirect(@NotNull CraftDirectEvent e) {
        Player p = e.getPlayer();
        WorldGuardUtils wgUtils = MovecraftWorldGuard.getInstance().getWGUtils();
        try {
            // Check build flag
            switch (wgUtils.getState(p, p.getLocation(), Flags.BUILD)) {
                case ALLOW:
                    break; // Player is allowed to build
                case NONE:
                case DENY:
                    // Player is not allowed to build
                    e.setCancelled(true);
                    Directors.clearDirector(p);
                    p.sendMessage(I18nSupport.getInternationalisedString("Directing - WorldGuard - Not Permitted To Build"));
                    return;
                default:
                    break;
            }
        } catch (EmptyHitBoxException ignored) {
        }
    }
}
