package net.countercraft.movecraft.worldguard.listener;

import net.countercraft.movecraft.MovecraftLocation;
import net.countercraft.movecraft.craft.CraftManager;
import net.countercraft.movecraft.util.MathUtils;
import net.countercraft.movecraft.worldguard.CustomFlags;
import net.countercraft.movecraft.worldguard.MovecraftWorldGuard;
import net.countercraft.movecraft.worldguard.utils.WorldGuardUtils;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;

public class BurnListener implements Listener {
    @EventHandler
    public void onBurn(BlockBurnEvent e) {
        Location location = e.getBlock().getLocation();

        var wgUtils = MovecraftWorldGuard.getInstance().getWGUtils();
        WorldGuardUtils.State onlyDamagePilotedCrafts = wgUtils.getState(null, location, CustomFlags.ONLY_DAMAGE_PILOTED_CRAFTS);
        if (onlyDamagePilotedCrafts != WorldGuardUtils.State.ALLOW) {
            return;
        }

        MovecraftLocation movecraftLocation = MathUtils.bukkit2MovecraftLoc(location);
        boolean burningCraft = CraftManager.getInstance().getCraftsInWorld(location.getWorld()).stream().anyMatch(it -> it.getHitBox().contains(movecraftLocation));

        e.setCancelled(!burningCraft);
    }
}
