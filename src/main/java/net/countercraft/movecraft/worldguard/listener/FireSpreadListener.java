package net.countercraft.movecraft.worldguard.listener;

import net.countercraft.movecraft.MovecraftLocation;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.CraftManager;
import net.countercraft.movecraft.util.MathUtils;
import net.countercraft.movecraft.worldguard.CustomFlags;
import net.countercraft.movecraft.worldguard.MovecraftWorldGuard;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockSpreadEvent;

public class FireSpreadListener implements Listener {
    @EventHandler
    public void onIgnite(BlockIgniteEvent event) {
        event.setCancelled(fireCheck(event.getBlock()));
    }

    @EventHandler
    public void onBurn(BlockBurnEvent event) {
        event.setCancelled(fireCheck(event.getBlock()));
    }

    private boolean fireCheck(Block block) {
        MovecraftLocation loc = MathUtils.bukkit2MovecraftLoc(block.getLocation());
        Craft craft = MathUtils.fastNearestCraftToLoc(
                CraftManager.getInstance().getCraftsInWorld(block.getWorld()),
                block.getLocation()
        );
        switch (MovecraftWorldGuard.getInstance().getWGUtils().getState(
                null, block.getLocation(), CustomFlags.ALLOW_CRAFT_COMBAT)) {
            case ALLOW:
                // Protect the area outside the craft
                if (craft == null) {
                    return true;
                }
                if (!craft.getHitBox().contains(loc)) {
                    System.out.println("Protected!");
                    return true;
                }
                break;
            case DENY:
                // Protect the block if fire occurs inside craft
                if (craft == null) {
                    break;
                }
                if (craft.getHitBox().contains(loc)) {
                    System.out.println("Protected!");
                    return true;
                }
                break;
            default:
                break;
        }
        System.out.println("Not protected");
        return false;
    }
}
