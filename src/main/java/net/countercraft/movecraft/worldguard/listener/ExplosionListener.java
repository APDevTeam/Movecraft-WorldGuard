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
            MovecraftLocation loc = MathUtils.bukkit2MovecraftLoc(block.getLocation());
            Craft craft = MathUtils.fastNearestCraftToLoc(
                    CraftManager.getInstance().getCraftsInWorld(block.getWorld()),
                    block.getLocation()
            );
            switch (MovecraftWorldGuard.getInstance().getWGUtils().getState(
                    null, block.getLocation(), CustomFlags.ALLOW_CRAFT_COMBAT)) {
                case ALLOW:
                    if (craft == null) {
                        protectedBlocks.add(block);
                        continue;
                    }
                    if (!craft.getHitBox().contains(loc)) {
                        protectedBlocks.add(block);
                    }
                case NONE:
                    break;
                case DENY:
                    if (craft == null) {
                        continue;
                    }
                    if (craft.getHitBox().contains(loc)) {
                        protectedBlocks.add(block);
                    }
                default:
                    break;
            }
        }
        return protectedBlocks;
    }
}
