package dev.twme.pointCylinder.util;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.regions.RegionSelector;
import dev.twme.pointCylinder.exception.LessThenThreePointsException;
import dev.twme.pointCylinder.exception.NonConvexPolyhedralRegionException;
import dev.twme.pointCylinder.exception.ThreePointsCollinearException;
import org.bukkit.entity.Player;

import java.util.List;

public class PlayerCylinderData {

    public final Player player;
    public final LocalSession session;
    public Vector3 center;
    public Vector3 normal;
    public double radius;
    public Pattern pattern;

    public PlayerCylinderData(Player player, LocalSession localSession, Vector3 center, Vector3 normal, double radius, Pattern pattern) {
        this.player = player;
        this.session = localSession;
        this.center = center;
        this.normal = normal;
        this.radius = radius;
        this.pattern = pattern;
    }

    public BukkitPlayer getBukkitPlayer() {
        return BukkitAdapter.adapt(player);
    }

    public static PlayerCylinderData generate(Player player, Pattern pattern, boolean down) throws LessThenThreePointsException, NonConvexPolyhedralRegionException, ThreePointsCollinearException {

        LocalSession localSession = WorldEdit.getInstance().getSessionManager().get(BukkitAdapter.adapt(player));
        RegionSelector selector = localSession.getRegionSelector(BukkitAdapter.adapt(player.getWorld()));

        List<BlockVector3> points = selector.getVertices();

        if (points.size() < 3) {
            throw new LessThenThreePointsException();
        }

        // 獲取最後三個點
        Vector3 v1 = points.get(points.size() - 3).toVector3().add(WeUtil.offset);
        Vector3 v2 = points.get(points.size() - 2).toVector3().add(WeUtil.offset);
        Vector3 v3 = points.getLast().toVector3().add(WeUtil.offset);

        if (down) {
            double minY = Math.min(v1.y(), Math.min(v2.y(), v3.y()));
            v1 = v1.setComponents(v1.x(), minY, v1.z());
            v2 = v2.setComponents(v2.x(), minY, v2.z());
            v3 = v3.setComponents(v3.x(), minY, v3.z());
        }

        // 計算由三個點定義的圓的法向量
        Vector3 normal = CircleUtil.calculateNormal(v1, v2, v3);

        // 計算圓心和半徑
        Vector3 center = CircleUtil.calculateCircleCenter(v1, v2, v3);
        if (center == null) {
            // 三點共線
            throw new ThreePointsCollinearException();
        }

        // 計算半徑
        double radius = center.subtract(v1).length();

        return new PlayerCylinderData(player, localSession, center, normal, radius, pattern);
    }
}
