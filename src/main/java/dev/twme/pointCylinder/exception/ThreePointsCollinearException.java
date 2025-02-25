package dev.twme.pointCylinder.exception;

public class ThreePointsCollinearException extends Exception {
    public ThreePointsCollinearException() {
        super("Three points are collinear.");
    }
}
