package net.countercraft.movecraft.worldguard.listener;

import net.countercraft.movecraft.combat.event.CombatReleaseEvent;
import net.countercraft.movecraft.worldguard.MovecraftWorldGuard;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CombatReleaseListener implements Listener {
    @EventHandler
    public void onCombatRelease(CombatReleaseEvent e) {
        if(e.getCraft().getHitBox().isEmpty())
            return;

        // If in a region with TNT or PVP denied, cancel the combat release
        if(!MovecraftWorldGuard.getInstance().getWGUtils().isPVPAllowed(e.getCraft().getW(), e.getCraft().getHitBox()))
            e.setCancelled(true);
        else if(!MovecraftWorldGuard.getInstance().getWGUtils().isTNTAllowed(e.getCraft().getW(), e.getCraft().getHitBox()))
            e.setCancelled(true);
    }
}
