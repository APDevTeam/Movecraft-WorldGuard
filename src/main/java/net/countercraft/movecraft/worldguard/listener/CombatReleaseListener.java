package net.countercraft.movecraft.worldguard.listener;

import net.countercraft.movecraft.combat.features.combat.events.CombatReleaseEvent;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.PilotedCraft;
import net.countercraft.movecraft.exception.EmptyHitBoxException;
import net.countercraft.movecraft.util.hitboxes.HitBox;
import net.countercraft.movecraft.worldguard.CustomFlags;
import net.countercraft.movecraft.worldguard.MovecraftWorldGuard;
import net.countercraft.movecraft.worldguard.utils.WorldGuardUtils;

import com.sk89q.worldguard.protection.flags.Flags;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class CombatReleaseListener implements Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onCombatRelease(@NotNull CombatReleaseEvent e) {
        Craft craft = e.getCraft();
        HitBox hitBox = craft.getHitBox();
        if(!(craft instanceof PilotedCraft) || hitBox.isEmpty())
            return;

        WorldGuardUtils wgUtils = MovecraftWorldGuard.getInstance().getWGUtils();
        World w = craft.getWorld();
        Player p = ((PilotedCraft) craft).getPilot();

        try {
            // Check custom flag
            switch (wgUtils.getState(p, w, hitBox, CustomFlags.ALLOW_COMBAT_RELEASE)) {
                case ALLOW:
                    // Craft is allowed to combat release
                    e.setCancelled(true);
                    return;
                case DENY:
                    return; // Craft is not allowed to combat release
                case NONE:
                default:
                    break;
            }

            // Check PVP flag
            switch (wgUtils.getState(p, w, hitBox, Flags.PVP)) {
                case ALLOW:
                    break; // PVP is allowed
                case DENY:
                    // PVP is not allowed
                    e.setCancelled(true);
                    return;
                case NONE:
                default:
                    break;
            }

            // Check TNT flag
            switch (wgUtils.getState(p, w, hitBox, Flags.TNT)) {
                case ALLOW:
                    break; // TNT is allowed
                case DENY:
                    // TNT is not allowed
                    e.setCancelled(true);
                    return;
                case NONE:
                default:
                    break;
            }
        } catch (EmptyHitBoxException ignored) {
        }
    }
}
