package net.countercraft.movecraft.worldguard.listener;

import net.countercraft.movecraft.worldguard.MovecraftWorldGuard;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

public class ProjectileHitListener implements Listener {
    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getHitBlock() == null) {
            return;
        }
        event.setCancelled(MovecraftWorldGuard.getInstance().getWGUtils().isProtectedFromBreak(event.getHitBlock()));
    }
}
