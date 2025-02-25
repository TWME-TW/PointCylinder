package dev.twme.pointCylinder.exception;

public class LessThenThreePointsException extends Exception {
    public LessThenThreePointsException() {
        super("Less than three points are selected.");
    }
}
