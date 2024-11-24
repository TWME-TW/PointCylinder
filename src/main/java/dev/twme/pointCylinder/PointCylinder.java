package dev.twme.pointCylinder;

import dev.twme.pointCylinder.command.PointCylinderCommand;
import dev.twme.pointCylinder.command.PointCylinderTabCompleter;
import dev.twme.pointCylinder.command.PointHollowCylinderCommand;
import dev.twme.pointCylinder.command.PointHollowCylinderTabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

public final class PointCylinder extends JavaPlugin {

    private static PointCylinder instance;

    @Override
    public void onEnable() {
        instance = this;
        this.getCommand("/pcyl").setExecutor(new PointCylinderCommand());
        this.getCommand("/pcyl").setTabCompleter(new PointCylinderTabCompleter());

        this.getCommand("/phcyl").setExecutor(new PointHollowCylinderCommand());
        this.getCommand("/phcyl").setTabCompleter(new PointHollowCylinderTabCompleter());
    }

    @Override
    public void onDisable() {
    }

    public static PointCylinder getInstance() {
        return instance;
    }
}
