package dev.twme.pointCylinder.exception;

public class NonConvexPolyhedralRegionException extends Exception {
    public NonConvexPolyhedralRegionException() {
        super("The selected region is not convex polyhedral.");
    }
}
