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
        event.setCancelled(MovecraftWorldGuard.getInstance().getWGUtils().isProtectedFromBreak(event.getBlock()));
    }

    @EventHandler
    public void onBurn(BlockBurnEvent event) {
        event.setCancelled(MovecraftWorldGuard.getInstance().getWGUtils().isProtectedFromBreak(event.getBlock()));
    }
}
