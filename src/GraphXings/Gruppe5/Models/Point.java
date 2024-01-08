package GraphXings.Gruppe5.Models;

public class Point {

    private double x;
    private double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getXCoord() {
        return this.x;
    }

    public void setXCoord(double x) {
        this.x = x;
    }

    public double getYCoord() {
        return this.y;
    }

    public void setYCoord(double y) {
        this.y = y;
    }

}
