package dev.twme.pointCylinder.command;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extension.factory.PatternFactory;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.extension.platform.Actor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PointCylinderTabCompleter implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if (!(commandSender instanceof Player player)) {
            return completions;
        }

        if (!player.hasPermission("worldedit.generation.pointcylinder")) {
            return completions;
        }

        if (args.length == 1) {
            // FAWE provides access to block types
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

        if (args.length > 1) {
            completions.add("-d");
        }

        return completions;
    }
}
