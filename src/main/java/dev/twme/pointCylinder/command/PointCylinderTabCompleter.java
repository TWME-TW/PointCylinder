package dev.twme.pointCylinder.command;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extension.factory.PatternFactory;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.extension.platform.Actor;
import dev.twme.pointCylinder.util.TabCompleterUtil;
import dev.twme.pointCylinder.util.WeUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

public class PointCylinderTabCompleter implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        List<String> completions = new LinkedList<>();

        if (!(commandSender instanceof Player player)) {
            return completions;
        }

        if (!player.hasPermission("worldedit.generation.pointcylinder")) {
            return completions;
        }

        if (args.length == 1) {
            completions.add("<block_type> [height] [thickness] [-d]");

            completions.addAll(WeUtil.getPatternSuggestions(args[0], player));
        }

        if (args.length == 2) {
            completions.addAll(TabCompleterUtil.numberSuggestions(args[1], true));
        }

        if (args.length > 1) {
            completions.add("-d");
        }

        return completions;
    }
}
