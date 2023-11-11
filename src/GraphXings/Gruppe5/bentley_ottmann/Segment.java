package GraphXings.Gruppe5.bentley_ottmann;

/**
 * Created by valen_000 on 14. 5. 2017.
 */

public class Segment {

    private Point p_1;
    private Point p_2;
    double value;

    public Segment(Point p_1, Point p_2) {
        this.p_1 = p_1;
        this.p_2 = p_2;
        this.calculate_value(this.first().getXCoord());
    }

    public Point first() {
        if (p_1.getXCoord() <= p_2.getXCoord()) {
            return p_1;
        } else {
            return p_2;
        }
    }

    public Point second() {
        if (p_1.getXCoord() <= p_2.getXCoord()) {
            return p_2;
        } else {
            return p_1;
        }
    }

    public void calculate_value(double value) {
        double x1 = this.first().getXCoord();
        double x2 = this.second().getXCoord();
        double y1 = this.first().getYCoord();
        double y2 = this.second().getYCoord();
        this.value = y1 + (((y2 - y1) / (x2 - x1)) * (value - x1));
    }

    public void set_value(double value) {
        this.value = value;
    }

    public double getValue() {
        return this.value;
    }

}
