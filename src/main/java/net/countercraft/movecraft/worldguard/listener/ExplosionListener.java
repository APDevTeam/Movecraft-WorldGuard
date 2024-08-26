package net.countercraft.movecraft.worldguard.listener;

import com.sk89q.worldguard.protection.flags.Flags;
import net.countercraft.movecraft.MovecraftLocation;
import net.countercraft.movecraft.craft.CraftManager;
import net.countercraft.movecraft.events.ExplosionEvent;
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

        switch (wgUtils.getState(null, explosionLocation, Flags.OTHER_EXPLOSION)) {
            case DENY:
                // Other-explosion is not allowed
                e.setCancelled(true);
                return;
            case ALLOW: // Other-explosion is allowed
            case NONE:
                break;
        }

        WorldGuardUtils.State onlyDamagePilotedCrafts = wgUtils.getState(null, explosionLocation, CustomFlags.ONLY_DAMAGE_PILOTED_CRAFTS);
        if (onlyDamagePilotedCrafts != WorldGuardUtils.State.ALLOW) {
            return;
        }

        MovecraftLocation movecraftLocation = new MovecraftLocation(explosionLocation.getBlockX(), explosionLocation.getBlockY(), explosionLocation.getBlockZ());
        boolean explodingOnCraft = CraftManager.getInstance().getCrafts().stream().anyMatch(it -> {
            if (it.getWorld() != explosionLocation.getWorld()) {
                return false;
            }

            return it.getHitBox().contains(movecraftLocation);
        });

        e.setCancelled(!explodingOnCraft);
    }
}
