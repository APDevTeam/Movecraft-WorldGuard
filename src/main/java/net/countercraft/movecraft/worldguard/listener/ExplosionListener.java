package net.countercraft.movecraft.worldguard.listener;

import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import net.countercraft.movecraft.MovecraftLocation;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.CraftManager;
import net.countercraft.movecraft.events.ExplosionEvent;
import net.countercraft.movecraft.util.MathUtils;
import net.countercraft.movecraft.worldguard.CustomFlags;
import net.countercraft.movecraft.worldguard.MovecraftWorldGuard;
import net.countercraft.movecraft.worldguard.utils.WorldGuardUtils;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.jetbrains.annotations.NotNull;

import javax.swing.text.html.parser.Entity;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ExplosionListener implements Listener {
    @EventHandler
    public void onExplosion(ExplosionEvent e) {
        switch (MovecraftWorldGuard.getInstance().getWGUtils().getState(
                null, e.getExplosionLocation(), Flags.OTHER_EXPLOSION)) {
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
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityExplosion(EntityExplodeEvent e) {
        e.blockList().removeAll(getProtectedBlocks(e.blockList()));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onTorpedoExplosion(BlockExplodeEvent e) {
        e.blockList().removeAll(getProtectedBlocks(e.blockList()));
    }

    private List<Block> getProtectedBlocks(List<Block> blocks) {
        List<Block> protectedBlocks = new ArrayList<>();
        for (Block block : blocks) {
            WorldGuardUtils.State state = MovecraftWorldGuard.getInstance().getWGUtils().getState(
                    null, block.getLocation(), CustomFlags.ALLOW_CRAFT_COMBAT);
            if (state == WorldGuardUtils.State.ALLOW) {
                MovecraftLocation loc = MathUtils.bukkit2MovecraftLoc(block.getLocation());
                Craft craft = fastNearestCraftToLoc(block.getLocation());
                if (craft == null) {
                    protectedBlocks.add(block);
                    System.out.println("Protected block added!");
                    continue;
                }
                if (!craft.getHitBox().contains(loc)) {
                    protectedBlocks.add(block);
                    System.out.println("Protected block added!");
                }
            } else if (state == WorldGuardUtils.State.DENY) {
                MovecraftLocation loc = MathUtils.bukkit2MovecraftLoc(block.getLocation());
                Craft craft = fastNearestCraftToLoc(block.getLocation());
                if (craft == null) {
                    continue;
                }
                if (craft.getHitBox().contains(loc)) {
                    protectedBlocks.add(block);
                    System.out.println("Protected block added!");
                }
            }
        }
        return protectedBlocks;
    }

    private Craft fastNearestCraftToLoc(@NotNull Location source) {
        MovecraftLocation loc = MathUtils.bukkit2MovecraftLoc(source);
        Craft closest = null;
        long closestDistSquared = Long.MAX_VALUE;
        for (Craft other : CraftManager.getInstance()) {
            if (other.getWorld() != source.getWorld())
                continue;

            long distSquared = other.getHitBox().getMidPoint().distanceSquared(loc);
            if (distSquared < closestDistSquared) {
                closestDistSquared = distSquared;
                closest = other;
            }
        }
        return closest;
    }
}
