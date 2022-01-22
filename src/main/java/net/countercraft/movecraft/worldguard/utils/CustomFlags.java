package net.countercraft.movecraft.worldguard.utils;

import com.sk89q.worldguard.protection.flags.RegionGroup;
import com.sk89q.worldguard.protection.flags.StateFlag;

public class CustomFlags {
    public static final StateFlag ALLOW_TRANSLATE = new StateFlag("allow-translate", true, RegionGroup.MEMBERS);
    public static final StateFlag ALLOW_ROTATE = new StateFlag("allow-rotate", true, RegionGroup.MEMBERS);
    public static final StateFlag ALLOW_COMBAT_RELEASE = new StateFlag("allow-combat-release", true, RegionGroup.MEMBERS);
    // public static final StateFlag ALLOW_CRAFT_SINK = new StateFlag("allow-craft-sink",
    public static final StateFlag ALLOW_CRAFT_PILOT = new StateFlag("allow-craft-pilot", true, RegionGroup.MEMBERS);
}
