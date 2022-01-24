package net.countercraft.movecraft.worldguard.utils;

import com.sk89q.worldguard.protection.flags.StateFlag;

public class CustomFlags {
    public static final StateFlag ALLOW_COMBAT_RELEASE = new StateFlag("allow-combat-release", false);
    public static final StateFlag ALLOW_CRAFT_PILOT = new StateFlag("allow-craft-pilot", false);
    public static final StateFlag ALLOW_CRAFT_ROTATE = new StateFlag("allow-craft-rotate", false);
    public static final StateFlag ALLOW_CRAFT_SINK = new StateFlag("allow-craft-sink", false);
    public static final StateFlag ALLOW_CRAFT_TRANSLATE = new StateFlag("allow-craft-translate", false);
}
