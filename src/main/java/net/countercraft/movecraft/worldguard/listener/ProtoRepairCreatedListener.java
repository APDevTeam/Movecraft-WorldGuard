package net.countercraft.movecraft.worldguard.listener;

import com.sk89q.worldguard.protection.flags.Flags;
import net.countercraft.movecraft.exception.EmptyHitBoxException;
import net.countercraft.movecraft.repair.events.ProtoRepairCreateEvent;
import net.countercraft.movecraft.repair.types.ProtoRepair;
import net.countercraft.movecraft.util.hitboxes.HitBox;
import net.countercraft.movecraft.worldguard.CustomFlags;
import net.countercraft.movecraft.worldguard.MovecraftWorldGuard;
import net.countercraft.movecraft.worldguard.localisation.I18nSupport;
import net.countercraft.movecraft.worldguard.utils.WorldGuardUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class ProtoRepairCreatedListener implements Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onProtoRepairCreate(@NotNull ProtoRepairCreateEvent e) {
        WorldGuardUtils wgUtils = MovecraftWorldGuard.getInstance().getWGUtils();
        ProtoRepair protoRepair = e.getProtoRepair();
        Player p = Bukkit.getPlayer(protoRepair.playerUUID());
        World w = protoRepair.getWorld();
        HitBox hitBox = protoRepair.getHitBox();

        try {
            // Check custom flag
            switch (wgUtils.getState(p, w, hitBox, CustomFlags.ALLOW_CRAFT_REPAIR)) {
                case ALLOW:
                    return; // Is allowed to repair
                case DENY:
                    // Is not allowed to repair
                    e.setCancelled(true);
                    e.setFailMessage(I18nSupport.getInternationalisedString("CustomFlags - Repair Failed"));
                    return;
                case NONE:
                default:
                    break;
            }

            // Check build flag
            switch (wgUtils.getState(p, w, hitBox, Flags.BUILD)) {
                case ALLOW:
                    break; // Is allowed to build
                case NONE:
                case DENY:
                    // Is not allowed to build
                    e.setCancelled(true);
                    e.setFailMessage(I18nSupport.getInternationalisedString(
                            "Repair - WorldGuard - Not Permitted To Build"));
                    return;
                default:
                    break;
            }
        } catch (EmptyHitBoxException ignored) {
        }
    }
}
