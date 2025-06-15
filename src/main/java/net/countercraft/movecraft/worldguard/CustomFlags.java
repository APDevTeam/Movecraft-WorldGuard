package net.countercraft.movecraft.worldguard;

import java.util.logging.Level;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;

public class CustomFlags {
    public static final StateFlag ALLOW_COMBAT_RELEASE = new StateFlag("allow-combat-release", false);
    public static final StateFlag ALLOW_CRAFT_PILOT = new StateFlag("allow-craft-pilot", false);
    public static final StateFlag ALLOW_CRAFT_ROTATE = new StateFlag("allow-craft-rotate", false);
    public static final StateFlag ALLOW_CRAFT_SINK = new StateFlag("allow-craft-sink", false);
    public static final StateFlag ALLOW_CRAFT_TRANSLATE = new StateFlag("allow-craft-translate", false);
    public static final StateFlag ALLOW_CRAFT_REPAIR = new StateFlag("allow-craft-repair", false);
    public static final StateFlag ONLY_DAMAGE_PILOTED_CRAFTS = new StateFlag("only-damage-piloted-crafts", false);

    public static void register() {
        try {
            FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
            registry.register(ALLOW_COMBAT_RELEASE);
            registry.register(ALLOW_CRAFT_PILOT);
            registry.register(ALLOW_CRAFT_ROTATE);
            registry.register(ALLOW_CRAFT_SINK);
            registry.register(ALLOW_CRAFT_TRANSLATE);
            registry.register(ALLOW_CRAFT_REPAIR);
            registry.register(ONLY_DAMAGE_PILOTED_CRAFTS);
        }
        catch (Exception e) {
            MovecraftWorldGuard.getInstance().getLogger().log(
                Level.WARNING, "Failed to register custom WorldGuard flags", e);
        }
    }
}
