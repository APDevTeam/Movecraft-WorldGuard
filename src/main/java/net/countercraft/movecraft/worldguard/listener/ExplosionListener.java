package net.countercraft.movecraft.worldguard.listener;

import com.sk89q.worldguard.protection.flags.Flags;
import net.countercraft.movecraft.events.ExplosionEvent;
import net.countercraft.movecraft.worldguard.MovecraftWorldGuard;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ExplosionListener implements Listener {
    @EventHandler
    public void onExplosion(ExplosionEvent e) {
        switch (MovecraftWorldGuard.getInstance().getWGUtils().getState(
                null, e.getExplosionLocation(), Flags.OTHER_EXPLOSION)) {
            case ALLOW:
                break; // Other-explosion is allowed
            case DENY:
                // Other-explosion is not allowed
                e.setCancelled(true);
                return;
            default:
                break;
        }
    }
}
