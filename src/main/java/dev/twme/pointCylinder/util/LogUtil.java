package dev.twme.pointCylinder.util;

import org.bukkit.entity.Player;
import dev.twme.pointCylinder.PointCylinder;

public class LogUtil {

    private static final String PREFIX = "[PointCylinder] ";

    public static void info(String message) {
        PointCylinder.getInstance().getLogger().info(PREFIX + message);
    }

    public static void warning(String message) {
        PointCylinder.getInstance().getLogger().info(PREFIX + message);
    }

    public static void severe(String message) {
        PointCylinder.getInstance().getLogger().info(PREFIX + message);
    }

    public static void playerInfo(Player player, String message) {
        player.sendMessage(PREFIX + message);
    }

    public static void playerWarning(Player player, String message) {
        player.sendMessage(PREFIX + message);
    }
}
