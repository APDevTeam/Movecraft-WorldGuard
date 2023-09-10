package net.countercraft.movecraft.worldguard.utils;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import net.countercraft.movecraft.MovecraftLocation;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.util.Pair;
import net.countercraft.movecraft.util.hitboxes.HitBox;
import net.countercraft.movecraft.worldguard.MovecraftWorldGuard;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

public class WorldGuardUtils {
    public enum State {
        DENY,
        ALLOW,
        NONE
    }

    private WorldGuardPlugin wgPlugin;

    public boolean init(@NotNull Plugin plugin) {
        if (!(plugin instanceof WorldGuardPlugin))
            return false;

        wgPlugin = (WorldGuardPlugin) plugin;
        return true;
    }

    /**
     * Get a flag state for the corners of a hitbox
     * 
     * @param p      Player (null for no player)
     * @param w      World
     * @param hitBox HitBox to check
     * @param flag   Flag to check
     * 
     * @return Flag state
     */
    @NotNull
    public State getState(@Nullable Player p, @NotNull World w, @NotNull HitBox hitBox, @NotNull StateFlag flag) {
        State result = State.NONE; // None
        for (MovecraftLocation ml : getHitboxCorners(hitBox)) {
            switch (getState(p, ml.toBukkit(w), flag)) {
                case ALLOW: // Allow overrides None
                    MovecraftWorldGuard.getInstance().getLogger().info("\t- " + ml + ": Allowed (" + flag.getName() + ")");
                    result = State.ALLOW;
                    break;
                case DENY: // Deny overrides all
                    MovecraftWorldGuard.getInstance().getLogger().info("\t- " + ml + ": Denied (" + flag.getName() + ")");
                    return State.DENY;
                default: // None, no change
                    MovecraftWorldGuard.getInstance().getLogger().info("\t- " + ml + ": None (" + flag.getName() + ")");
                    break;
            }
        }
        return result;
    }

    /**
     * Get a flag state at a location
     * 
     * @param p    Player (null for no player)
     * @param loc  Location to check
     * @param flag Flag to check
     * 
     * @return Flag state
     */
    @NotNull
    public State getState(@Nullable Player p, @NotNull Location loc, @NotNull StateFlag flag) {
        RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
        LocalPlayer localPlayer = null;
        if (p != null) {
            localPlayer = wgPlugin.wrapPlayer(p);
        }
        StateFlag.State state = query.queryState(BukkitAdapter.adapt(loc), localPlayer, flag);
        if (state == null)
            return State.NONE;
        switch (state) {
            case ALLOW:
                return State.ALLOW;
            case DENY:
                return State.DENY;
            default:
                return State.NONE;
        }
    }

    // TODO: move all below into a separate file
    /**
     * Movecraft-Warfare Features
     */

    // Siege Features

    public boolean craftFullyInRegion(String regionName, World w, Craft craft) {
        ProtectedRegion r = getRegion(regionName, w);
        if (r == null)
            return false;

        for (MovecraftLocation ml : getHitboxCorners(craft.getHitBox())) {
            if (!r.contains(ml.getX(), ml.getY(), ml.getZ()))
                return false;
        }
        return true;
    }

    public void clearAndSetOwnership(String regionName, World w, UUID owner) {
        ProtectedRegion region = getRegion(regionName, w);
        if (region == null)
            return;

        DefaultDomain newOwners = new DefaultDomain();
        newOwners.addPlayer(owner);
        region.setOwners(newOwners);
        region.setMembers(newOwners);
    }

    public Set<String> getRegions(Location loc) {
        ApplicableRegionSet regionSet = getApplicableRegions(loc);
        HashSet<String> stringSet = new HashSet<>();
        for (ProtectedRegion r : regionSet) {
            stringSet.add(r.getId());
        }
        return stringSet;
    }

    // Assault Features

    public boolean isInRegion(Location loc) {
        return getApplicableRegions(loc).size() > 0;
    }

    public boolean regionExists(String regionName, World w) {
        return getRegion(regionName, w) != null;
    }

    public boolean ownsAssaultableRegion(Player p) {
        LocalPlayer lp = wgPlugin.wrapPlayer(p);
        RegionManager manager = WorldGuard.getInstance().getPlatform().getRegionContainer()
                .get(BukkitAdapter.adapt(p.getWorld()));
        if (manager == null)
            return false;

        for (ProtectedRegion r : manager.getRegions().values()) {
            if (r.isOwner(lp) && r.getFlag(Flags.TNT) == StateFlag.State.DENY)
                return true;
        }
        return false;
    }

    @Nullable
    public String getAssaultableRegion(Location loc, HashSet<String> exclusions) {
        for (ProtectedRegion r : getApplicableRegions(loc)) {
            if (r.getFlag(Flags.TNT) != StateFlag.State.DENY || r.getOwners().size() == 0)
                continue;

            if (exclusions.contains(r.getId().toUpperCase()))
                continue;

            return r.getId();
        }
        return null;
    }

    @Nullable
    public Set<UUID> getUUIDOwners(String regionName, World w) {
        ProtectedRegion r = getRegion(regionName, w);
        if (r == null)
            return null;

        return r.getOwners().getUniqueIds();
    }

    @Nullable
    public Set<UUID> getUUIDMembers(String regionName, World w) {
        ProtectedRegion r = getRegion(regionName, w);
        if (r == null)
            return null;

        return r.getMembers().getUniqueIds();
    }

    public void setTNTAllow(String regionName, World w) {
        ProtectedRegion r = getRegion(regionName, w);
        if (r == null)
            return;

        r.setFlag(Flags.TNT, StateFlag.State.ALLOW);
    }

    public void setTNTDeny(String regionName, World w) {
        ProtectedRegion r = getRegion(regionName, w);
        if (r == null)
            return;

        r.setFlag(Flags.TNT, StateFlag.State.DENY);
    }

    public void clearOwners(String regionName, World w) {
        ProtectedRegion r = getRegion(regionName, w);
        if (r == null)
            return;

        r.getOwners().clear();
    }

    @Nullable
    public String getRegionOwnerList(String regionName, World w) {
        ProtectedRegion r = getRegion(regionName, w);
        if (r == null)
            return null;

        StringBuilder output = new StringBuilder();
        boolean first = true;
        for (UUID uuid : r.getOwners().getUniqueIds()) {
            if (!first)
                output.append(", ");
            else
                first = false;

            OfflinePlayer ofp = Bukkit.getOfflinePlayer(uuid);
            if (ofp == null)
                output.append(uuid);
            else
                output.append(ofp.getName());
        }
        for (String player : r.getOwners().getPlayers()) {
            if (!first)
                output.append(", ");
            else
                first = false;

            output.append(player);
        }
        return output.toString();
    }

    public boolean addOwners(String regionName, World w, Set<UUID> owners) {
        ProtectedRegion r = getRegion(regionName, w);
        if (r == null)
            return false;

        DefaultDomain regionOwners = r.getOwners();
        for (UUID owner : owners) {
            regionOwners.addPlayer(owner);
        }
        return true;
    }

    public boolean isMember(String regionName, World w, Player p) {
        ProtectedRegion r = getRegion(regionName, w);
        if (r == null)
            return false;

        LocalPlayer lp = wgPlugin.wrapPlayer(p);
        return r.isMember(lp) || r.isOwner(lp);
    }

    public boolean isTNTDenied(String regionName, World w) {
        ProtectedRegion r = getRegion(regionName, w);
        if (r == null)
            return false;

        return r.getFlag(Flags.TNT) == StateFlag.State.DENY;
    }

    @Nullable
    public MovecraftLocation getMinLocation(String regionName, World w) {
        ProtectedRegion r = getRegion(regionName, w);
        if (r == null)
            return null;

        return vectorToMovecraftLocation(r.getMinimumPoint());
    }

    @Nullable
    public MovecraftLocation getMaxLocation(String regionName, World w) {
        ProtectedRegion r = getRegion(regionName, w);
        if (r == null)
            return null;

        return vectorToMovecraftLocation(r.getMaximumPoint());
    }

    @Nullable
    public Queue<Pair<Integer, Integer>> getChunksInRegion(String regionName, World w) {
        ProtectedRegion region = getRegion(regionName, w);
        if (region == null)
            return null;

        Queue<Pair<Integer, Integer>> chunks = new LinkedList<>();
        for (int x = (int) Math.floor(region.getMinimumPoint().getBlockX() / 16.0); x < Math
                .floor(region.getMaximumPoint().getBlockX() / 16.0) + 1; x++) {
            for (int z = (int) Math.floor(region.getMinimumPoint().getBlockZ() / 16.0); z < Math
                    .floor(region.getMaximumPoint().getBlockZ() / 16.0) + 1; z++) {
                chunks.add(new Pair<>(x, z));
            }
        }
        return chunks;
    }

    @Nullable
    public IsInRegion getIsInRegion(String regionName, World w) {
        ProtectedRegion r = getRegion(regionName, w);
        if (r == null)
            return null;

        return new IsInRegion(r);
    }

    public boolean regionContains(String regionName, @NotNull Location l) {
        ProtectedRegion r = getRegion(regionName, l.getWorld());
        if (r == null)
            return false;

        return r.contains(l.getBlockX(), l.getBlockY(), l.getBlockZ());
    }

    /**
     * Generic features
     */

    @Nullable
    private ProtectedRegion getRegion(String regionName, World w) {
        RegionManager regions = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(w));
        if (regions == null)
            return null;

        return regions.getRegion(regionName);
    }

    @NotNull
    private ApplicableRegionSet getApplicableRegions(Location loc) {
        RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
        return query.getApplicableRegions(BukkitAdapter.adapt(loc));
    }

    @NotNull
    private MovecraftLocation vectorToMovecraftLocation(@NotNull BlockVector3 v) {
        return new MovecraftLocation(v.getBlockX(), v.getBlockY(), v.getBlockZ());
    }

    /**
     * @param hitbox HitBox to check
     * @return ~27 "corners" of the hitbox. This drastically reduces the workload
     *         for checking a large craft's hitbox.
     *         For tiny crafts, this may be smaller than 27 due to overlaps.
     */
    @NotNull
    private Set<MovecraftLocation> getHitboxCorners(@NotNull HitBox hitbox) {
        Set<MovecraftLocation> corners = new HashSet<>();
        MovecraftLocation midPoint = hitbox.getMidPoint();
        for (int x : new int[] { hitbox.getMinX(), midPoint.getX(), hitbox.getMaxX() }) {
            for (int y : new int[] { hitbox.getMinY(), midPoint.getY(), hitbox.getMaxY() }) {
                for (int z : new int[] { hitbox.getMinZ(), midPoint.getZ(), hitbox.getMaxZ() }) {
                    corners.add(new MovecraftLocation(x, y, z));
                }
            }
        }
        return corners;
    }
}
