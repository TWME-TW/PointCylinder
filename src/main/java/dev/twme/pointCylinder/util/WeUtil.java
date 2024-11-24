package dev.twme.pointCylinder.util;

import com.fastasyncworldedit.core.util.TaskManager;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.world.block.BaseBlock;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class WeUtil {

    public static void generateCircleCylinder(Player player, LocalSession session, Vector3 v1, Vector3 v2, Vector3 v3, Pattern blockPattern, float height, boolean filled, int thickness) {

        // 計算由三個點定義的圓的法向量
        Vector3 normal = (v2.subtract(v1)).cross(v3.subtract(v1)).normalize();

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
}
