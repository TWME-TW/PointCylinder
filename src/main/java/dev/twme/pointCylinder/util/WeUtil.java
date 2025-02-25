package dev.twme.pointCylinder.util;

import com.fastasyncworldedit.core.math.MutableVector3;
import com.fastasyncworldedit.core.util.TaskManager;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.extension.factory.PatternFactory;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.world.block.BaseBlock;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

public class WeUtil {

    public static final Vector3 offset = new MutableVector3(0.5, 0.5, 0.5);

    // 獲取方塊模式建議
    public static List<String> getPatternSuggestions(String blockTypeString, Player player) {
        ParserContext context = new ParserContext();
        context.setActor(BukkitAdapter.adapt(player));
        context.setWorld(BukkitAdapter.adapt(player.getWorld()));
        PatternFactory patternFactory = WorldEdit.getInstance().getPatternFactory();
        return patternFactory.getSuggestions(blockTypeString, context);
    }

    // 獲取方塊模式
    public static Pattern getPattern(String blockTypeString, Player player) {
        Pattern blockPattern;
        try {
            ParserContext context = new ParserContext();
            context.setActor(BukkitAdapter.adapt(player));
            context.setWorld(BukkitAdapter.adapt(player.getWorld()));
            PatternFactory patternFactory = WorldEdit.getInstance().getPatternFactory();
            blockPattern = patternFactory.parseFromInput(blockTypeString, context);
        } catch (Exception e) {
            return null;
        }
        return blockPattern;
    }

    @Deprecated
    public static void generateCircleCylinder(Player player, LocalSession session, Vector3 v1, Vector3 v2, Vector3 v3, Pattern blockPattern, float height, boolean filled, int thickness) {

        // 計算由三個點定義的圓的法向量
        Vector3 normal = CircleUtil.calculateNormal(v1, v2, v3);

        // 計算圓心和半徑
        Vector3 center = CircleUtil.calculateCircleCenter(v1, v2, v3);
        if (center == null) {
            // 三點共線
            player.sendMessage("The selected points are collinear.");
            return;
        }

        // 計算半徑
        double radius = center.subtract(v1).length();

        // 獲取世界
        World world = player.getWorld();

        // 使用非同步任務防止伺服器卡頓
        TaskManager.taskManager().async(() -> {
            try (EditSession editSession = session.createEditSession(BukkitAdapter.adapt(player))) {

                if (height <= -1) {
                    // 根據高度，在每一層上建造圓形
                    for (int h = 0; h > height; h--) {
                        Vector3 layerCenter = center.add(normal.multiply(h));
                        // 在該層建構圓形
                        buildCircle(editSession, layerCenter, normal, radius, blockPattern, filled, thickness);
                    }
                } else {
                    // 根據高度，在每一層上建造圓形
                    for (int h = 0; h < height; h++) {
                        Vector3 layerCenter = center.add(normal.multiply(h));
                        // 在該層建構圓形
                        buildCircle(editSession, layerCenter, normal, radius, blockPattern, filled, thickness);
                    }
                }
                session.remember(editSession);
                // 更新並關閉 editSession，完成更改
                editSession.close();
            } catch (Exception e) {
                player.sendMessage("An error occurred while building the circle: " + e.getMessage());
                LogUtil.warning(e.getMessage());
            }
        });
    }

    public static void generateCylinder(PlayerCylinderData circle, float height) {

        // 使用非同步任務防止伺服器卡頓
        TaskManager.taskManager().async(() -> {
            try (EditSession editSession = circle.session.createEditSession(circle.getBukkitPlayer())) {

                if (height <= -1) {
                    // 根據高度，在每一層上建造圓形
                    for (int h = 0; h > height; h--) {
                        circle.center = circle.center.add(circle.normal.multiply(h));
                        // 在該層建構圓形
                        buildCircle(editSession, circle);
                    }
                } else {
                    // 根據高度，在每一層上建造圓形
                    for (int h = 0; h < height; h++) {
                        circle.center = circle.center.add(circle.normal.multiply(h));
                        // 在該層建構圓形
                        buildCircle(editSession, circle);
                    }
                }
                circle.session.remember(editSession);
            } catch (Exception e) {
                circle.player.sendMessage("An error occurred while building the circle: " + e.getMessage());
                LogUtil.warning(e.getMessage());
            }
        });
    }

    public static void generateHollowCylinder(BukkitPlayer bukkitPlayer, PlayerCylinderData circle, float height, int thickness) {

        // 使用非同步任務防止伺服器卡頓
        TaskManager.taskManager().async(() -> {
            try (EditSession editSession = circle.session.createEditSession(bukkitPlayer)) {

                if (height <= -1) {
                    // 根據高度，在每一層上建造圓形
                    for (int h = 0; h > height; h--) {
                        Vector3 layerCenter = circle.center.add(circle.normal.multiply(h));
                        // 在該層建構圓形
                        buildHollowCircle(editSession, circle, thickness);
                    }
                } else {
                    // 根據高度，在每一層上建造圓形
                    for (int h = 0; h < height; h++) {
                        Vector3 layerCenter = circle.center.add(circle.normal.multiply(h));
                        // 在該層建構圓形
                        buildHollowCircle(editSession, circle, thickness);                    }
                }
                circle.session.remember(editSession);
            } catch (Exception e) {
                bukkitPlayer.getPlayer().sendMessage("An error occurred while building the circle: " + e.getMessage());
                LogUtil.warning(e.getMessage());
            }
        });
    }

    @Deprecated
    public static void buildCircle(EditSession editSession, Vector3 center, Vector3 normal, double radius, Pattern blockPattern, boolean filled, int thickness) {
        int points = (int) (2 * Math.PI * radius * 2); // 調整此值以更改密度

        if (filled) {
            // 生成實心圓
            double step = 1.0 / radius; // 調整步長以控制填充密度
            for (double r = 0; r <= radius; r += step) {
                int innerPoints = (int) (2 * Math.PI * r * 2);
                fillCircle(editSession, center, normal, blockPattern, innerPoints, r);
            }
        } else {
            // 生成空心圓
            double outerRadius = radius;
            double innerRadius = radius - thickness + 1;
            if (innerRadius < 0) {
                innerRadius = 0;
            }

            double step = 0.1; // 調整步長以控制填充密度
            for (double r = innerRadius; r <= outerRadius; r += step) {
                fillCircle(editSession, center, normal, blockPattern, points, r);
            }
        }
    }

    public static void buildCircle(EditSession editSession, PlayerCylinderData data) {
        double step = 1.0 / data.radius; // 調整步長以控制填充密度
        for (double r = 0; r <= data.radius; r += step) {
            int innerPoints = (int) (2 * Math.PI * r * 2);
            fillCircle(editSession, data, innerPoints, r);
        }
    }

    public static void buildHollowCircle(EditSession editSession, PlayerCylinderData data, int thickness) {
        double outerRadius = data.radius;
        double innerRadius = data.radius - thickness + 1;
        if (innerRadius < 0) {
            innerRadius = 0;
        }

        double step = 0.1; // 調整步長以控制填充密度
        for (double r = innerRadius; r <= outerRadius; r += step) {
            fillCircle(editSession, data, (int) (2 * Math.PI * r * 2), r);
        }
    }

    @Deprecated
    public static void fillCircle(EditSession editSession, Vector3 center, Vector3 normal, Pattern blockPattern, int points, double r) {
        for (int i = 0; i < points; i++) {
            double angle = 2 * Math.PI * i / points;
            Vector3 point = CircleUtil.calculatePointOnCircle(center, normal, r, angle);
            // 獲取方塊狀態
            BaseBlock state = blockPattern.applyBlock(point.toBlockPoint());
            //  設置方塊
            editSession.smartSetBlock(point.toBlockPoint(), state);
        }
    }

    public static void fillCircle(EditSession editSession, PlayerCylinderData data, int points, double r) {
        for (int i = 0; i < points; i++) {
            double angle = 2 * Math.PI * i / points;
            Vector3 point = CircleUtil.calculatePointOnCircle(data.center, data.normal, r, angle);
            // 獲取方塊狀態
            BaseBlock state = data.pattern.applyBlock(point.toBlockPoint());
            //  設置方塊
            editSession.smartSetBlock(point.toBlockPoint(), state);
        }
    }

    public static void fillCircleByExpression(PlayerCylinderData data, int points, double r) {
        for (int i = 0; i < points; i++) {
            double angle = 2 * Math.PI * i / points;
            Vector3 point = CircleUtil.calculatePointOnCircle(data.center, data.normal, r, angle);
            // 獲取方塊狀態
            BaseBlock state = data.pattern.applyBlock(point.toBlockPoint());
            //  設置方塊
            data.session.mak
        }
    }
}
