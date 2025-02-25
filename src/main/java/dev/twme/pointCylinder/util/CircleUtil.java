package dev.twme.pointCylinder.util;

import com.fastasyncworldedit.core.math.MutableVector3;
import com.sk89q.worldedit.math.Vector3;

public class CircleUtil {

    public static Vector3 calculateNormal(Vector3 v1, Vector3 v2, Vector3 v3) {
        return (v2.subtract(v1)).cross(v3.subtract(v1)).normalize();
    }

    public static Vector3 calculateCircleCenter(Vector3 A, Vector3 B, Vector3 C) {
        // 計算空間中三個點的圓的圓心
        Vector3 AB = B.subtract(A);
        Vector3 AC = C.subtract(A);

        Vector3 ABxAC = AB.cross(AC);

        double denominator = 2 * ABxAC.lengthSq();
        if (denominator == 0) {
            return null; //三點共線
        }

        Vector3 numerator = ABxAC.cross(AB.multiply(AC.dot(AC)).subtract(AC.multiply(AB.dot(AB))));

        return A.add(numerator.divide(denominator));
    }

    public static Vector3 calculatePointOnCircle(Vector3 center, Vector3 normal, double radius, double angle) {
        // 在給定角度上生成圓周上的一個點
        // 需要兩個與法向量正交的向量

        MutableVector3 arbitrary = new MutableVector3(1, 0, 0);
        if (Math.abs(normal.dot(arbitrary)) > 0.99) {
            arbitrary = new MutableVector3(0, 1, 0);
        }

        Vector3 u = normal.cross(arbitrary).normalize();
        Vector3 v = normal.cross(u).normalize();

        return center.add(u.multiply(radius * Math.cos(angle))).add(v.multiply(radius * Math.sin(angle)));
    }
}
