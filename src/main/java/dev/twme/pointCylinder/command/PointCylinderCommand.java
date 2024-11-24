package dev.twme.pointCylinder.command;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.extension.factory.PatternFactory;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.regions.RegionSelector;
import com.sk89q.worldedit.regions.selector.ConvexPolyhedralRegionSelector;
import dev.twme.pointCylinder.util.WeUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PointCylinderCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage("Only players can use this command.");
            return true;
        }

        if (!player.hasPermission("worldedit.generation.pointcylinder")) {
            player.sendMessage("You do not have permission to use this command.");
            return true;
        }

        if (args.length < 1) {
            return false;
        }

        Actor actor = BukkitAdapter.adapt(player);
        BukkitPlayer bukkitPlayer = BukkitAdapter.adapt(player);

        String blockTypeString = args[0];

        Pattern blockPattern;
        try {
            ParserContext context = new ParserContext();
            context.setActor(actor);
            context.setWorld(BukkitAdapter.adapt(player.getWorld()));
            PatternFactory patternFactory = WorldEdit.getInstance().getPatternFactory();
            blockPattern = patternFactory.parseFromInput(blockTypeString, context);
        } catch (Exception e) {
            player.sendMessage("Invalid block type: " + blockTypeString);
            return true;
        }

        // 預設參數
        float height = 1;
        boolean down = false; // 預設不以最低點平面生成
        int thickness = 1; // 預設厚度

        // 目前解析到的參數索引
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

        // 獲取玩家的選取區域
        LocalSession session = WorldEdit.getInstance().getSessionManager().get(actor);
        RegionSelector selector = session.getRegionSelector(bukkitPlayer.getWorld());

        if (selector instanceof ConvexPolyhedralRegionSelector) {
            ConvexPolyhedralRegionSelector cps = (ConvexPolyhedralRegionSelector) selector;
            List<BlockVector3> points = cps.getVertices();

            if (points.size() < 3) {
                player.sendMessage("Please select at least 3 points.");
                return true;
            }

            // 獲取最後三個點
            Vector3 v1 = points.get(points.size() - 3).toVector3();
            Vector3 v2 = points.get(points.size() - 2).toVector3();
            Vector3 v3 = points.get(points.size() - 1).toVector3();

            if (down) {
                double minY = Math.min(v1.y(), Math.min(v2.y(), v3.y()));
                v1 = v1.setComponents(v1.x(), minY, v1.z());
                v2 = v2.setComponents(v2.x(), minY, v2.z());
                v3 = v3.setComponents(v3.x(), minY, v3.z());
            }

            // 生成圓柱
            WeUtil.generateCircleCylinder(player, session, v1, v2, v3, blockPattern, height, true, thickness);

        } else {
            player.sendMessage("Please use convex selection mode.");
            return true;
        }

        return true;
    }
}
