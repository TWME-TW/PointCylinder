package dev.twme.pointCylinder.command;

import com.fastasyncworldedit.core.FaweAPI;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.command.util.annotation.PatternList;
import com.sk89q.worldedit.extension.factory.PatternFactory;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.world.block.BlockCategories;
import com.sk89q.worldedit.world.block.BlockType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PointHollowCylinderTabCompleter implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if (!(commandSender instanceof Player player)) {
            return completions;
        }

        if (!player.hasPermission("worldedit.generation.pointhollowcylinder")) {
            return completions;
        }

        if (args.length == 1) {
            // 獲取 Pattern 建議列表
            Actor actor = BukkitAdapter.adapt(commandSender);
            ParserContext context = new ParserContext();
            context.setActor(actor);
            context.setWorld(BukkitAdapter.adapt(player.getWorld()));
            PatternFactory patternFactory = WorldEdit.getInstance().getPatternFactory();
            completions.addAll(patternFactory.getSuggestions(args[0],context));
        }

        if (args.length == 2) {
            completions.add("[height]");
        }

        if (args.length == 3) {
            completions.add("[thickness]");
        }

        if (args.length > 1) {
            completions.add("-d");
        }

        return completions;
    }
}
