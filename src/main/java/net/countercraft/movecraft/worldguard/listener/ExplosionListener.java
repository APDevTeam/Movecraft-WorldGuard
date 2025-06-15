package net.countercraft.movecraft.worldguard.listener;

import com.sk89q.worldguard.protection.flags.Flags;
import net.countercraft.movecraft.MovecraftLocation;
import net.countercraft.movecraft.craft.CraftManager;
import net.countercraft.movecraft.events.ExplosionEvent;
import net.countercraft.movecraft.util.MathUtils;
import net.countercraft.movecraft.worldguard.CustomFlags;
import net.countercraft.movecraft.worldguard.MovecraftWorldGuard;
import net.countercraft.movecraft.worldguard.utils.WorldGuardUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.Location;

public class ExplosionListener implements Listener {
    @EventHandler
    public void onExplosion(ExplosionEvent e) {
        var wgUtils = MovecraftWorldGuard.getInstance().getWGUtils();
        Location explosionLocation = e.getExplosionLocation();

        switch (wgUtils.getState(null, e.getExplosionLocation(), Flags.OTHER_EXPLOSION)) {
            case ALLOW:
            case NONE:
                break; // Other-explosion is allowed
            case DENY:
                // Other-explosion is not allowed
                e.setCancelled(true);
                return;
            default:
                break;
        }

        WorldGuardUtils.State onlyDamagePilotedCrafts = wgUtils.getState(null, explosionLocation, CustomFlags.ONLY_DAMAGE_PILOTED_CRAFTS);
        if (onlyDamagePilotedCrafts != WorldGuardUtils.State.ALLOW) {
            return;
        }

        MovecraftLocation movecraftLocation = MathUtils.bukkit2MovecraftLoc(explosionLocation);
        boolean explodingOnCraft = CraftManager.getInstance().getCraftsInWorld(explosionLocation.getWorld()).stream().anyMatch(it -> it.getHitBox().contains(movecraftLocation));

        e.setCancelled(!explodingOnCraft);
    }
}
