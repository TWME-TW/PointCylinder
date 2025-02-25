package dev.twme.pointCylinder.command;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.regions.RegionSelector;
import com.sk89q.worldedit.regions.selector.ConvexPolyhedralRegionSelector;
import dev.twme.pointCylinder.exception.LessThenThreePointsException;
import dev.twme.pointCylinder.exception.NonConvexPolyhedralRegionException;
import dev.twme.pointCylinder.exception.ThreePointsCollinearException;
import dev.twme.pointCylinder.util.PlayerCylinderData;
import dev.twme.pointCylinder.util.WeUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PointHollowCylinderCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage("Only players can use this command.");
            return true;
        }

        if (!player.hasPermission("worldedit.generation.pointhollowcylinder")) {
            player.sendMessage("You do not have permission to use this command.");
            return true;
        }

        if (args.length < 1) {
            return false;
        }

        BukkitPlayer bukkitPlayer = BukkitAdapter.adapt(player);

        String blockTypeString = args[0];

        Pattern blockPattern = WeUtil.getPattern(blockTypeString, player);

        if (blockPattern == null) {
            player.sendMessage("Invalid block type: " + blockTypeString);
            return true;
        }

        // 預設參數
        float height = 1;
        boolean down = false; // 預設不以最低點平面生成
        int thickness = 1; // 預設厚度

        int currentIndex = 1;

        // 解析高度參數 (如果有這個選項)
        if (currentIndex < args.length && !args[currentIndex].equalsIgnoreCase("-d")) {
            try {
                height = Float.parseFloat(args[currentIndex]);
                if (height < 1 && height > -1) {
                    player.sendMessage("Height must be at least 1.");
                    return true;
                }
                currentIndex++;
            } catch (NumberFormatException e) {
                player.sendMessage("Invalid height: " + args[currentIndex]);
                return true;
            }
        }

        // 解析外圍厚度參數 (如果有這個選項)
        if (currentIndex < args.length && !args[currentIndex].equalsIgnoreCase("-d")) {
            try {
                thickness = Integer.parseInt(args[currentIndex]);
                if (thickness < 1) {
                    player.sendMessage("Thickness must be at least 1.");
                    return true;
                }
                currentIndex++;
            } catch (NumberFormatException e) {
                player.sendMessage("Invalid thickness: " + args[currentIndex]);
                return true;
            }
        }

        // 檢查是否從最低點水平面開始生成
        if (currentIndex < args.length && args[currentIndex].equalsIgnoreCase("-d")) {
            down = true;
            currentIndex++;
        }

        // 檢查是否有多餘的參數
        if (currentIndex < args.length) {
            player.sendMessage("Too many arguments.");
            return true;
        }

        PlayerCylinderData playerCylinderData;

        try {
            playerCylinderData = PlayerCylinderData.generate(player, blockPattern, down);
        } catch (NonConvexPolyhedralRegionException e) {
            player.sendMessage("Please select a convex region.");
            return true;
        } catch (LessThenThreePointsException e) {
            player.sendMessage("Please select at least three points.");
            return true;
        } catch (ThreePointsCollinearException e) {
            player.sendMessage("The selected points are collinear.");
            return true;
        }

        // 生成圓柱
        WeUtil.generateHollowCylinder(bukkitPlayer, playerCylinderData, height, thickness);

        return true;
    }
}
