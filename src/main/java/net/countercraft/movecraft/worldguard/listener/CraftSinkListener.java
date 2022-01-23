package net.countercraft.movecraft.worldguard.listener;

import com.sk89q.worldguard.protection.flags.Flags;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.PilotedCraft;
import net.countercraft.movecraft.events.CraftSinkEvent;
import net.countercraft.movecraft.util.hitboxes.HitBox;
import net.countercraft.movecraft.worldguard.MovecraftWorldGuard;
import net.countercraft.movecraft.worldguard.localisation.I18nSupport;
import net.countercraft.movecraft.worldguard.utils.CustomFlags;
import net.countercraft.movecraft.worldguard.utils.WorldGuardUtils;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class CraftSinkListener implements Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onCraftSink(@NotNull CraftSinkEvent e) {
        Craft craft = e.getCraft();
        HitBox hitBox = craft.getHitBox();
        if (!(craft instanceof PilotedCraft) || hitBox.isEmpty())
            return;

        WorldGuardUtils wgUtils = MovecraftWorldGuard.getInstance().getWGUtils();
        World w = craft.getWorld();
        if (!wgUtils.isFlagDenied(w, hitBox, CustomFlags.ALLOW_CRAFT_SINK))
            return; // Craft is allowed to sink

        e.setCancelled(true);
        Player p = ((PilotedCraft) craft).getPilot();
        if (wgUtils.isFlagDenied(w, hitBox, Flags.PVP))
            p.sendMessage(I18nSupport.getInternationalisedString(
                    "Player - Craft should sink but PVP is not allowed in this WorldGuard region"));
        else
            p.sendMessage(I18nSupport.getInternationalisedString(
                    "CustomFlags - Sinking a craft is not allowed in this WorldGuard region"));
    }
}
