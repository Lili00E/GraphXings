package GraphXings.Algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import GraphXings.Data.Coordinate;
import GraphXings.Data.Edge;
import GraphXings.Data.Graph;
import GraphXings.Data.Segment;
import GraphXings.Data.Vertex;
import bentley_ottmann.Event;
import bentley_ottmann.Point;

public class BasicCrossingCalculatorAlgorithm implements CrossingCalculatorAlgorithm {

    public ArrayList<Point> intersectionPoints;

    public static Point reportIntersection(Segment s1, Segment s2) {
        double x1 = RationalComputer.getValue(s1.getStartX());
        double y1 = RationalComputer.getValue(s1.getStartY());
        double x2 = RationalComputer.getValue(s1.getEndX());
        double y2 = RationalComputer.getValue(s1.getEndY());
        double x3 = RationalComputer.getValue(s2.getStartX());
        double y3 = RationalComputer.getValue(s2.getStartY());
        double x4 = RationalComputer.getValue(s2.getEndX());
        double y4 = RationalComputer.getValue(s2.getEndY());

        boolean segmentIsPoint = (x1 == x2 && y1 == y2) || (x3 == x4 && y3 == y4);
        boolean segmentsOverlap = (x1 == x3 && y1 == y3) || (x1 == x4 && y1 == y4) ||
                (x2 == x3 && y2 == y3)
                || (x2 == x4 && y2 == y4);
        if (segmentIsPoint || segmentsOverlap) {

            return null;
        }

        double r = (x2 - x1) * (y4 - y3) - (y2 - y1) * (x4 - x3);
        if (r != 0) {
            double t = ((x3 - x1) * (y4 - y3) - (y3 - y1) * (x4 - x3)) / r;
            double u = ((x3 - x1) * (y2 - y1) - (y3 - y1) * (x2 - x1)) / r;
            if (t >= 0 && t <= 1 && u >= 0 && u <= 1) {
                double x_c = x1 + t * (x2 - x1);
                double y_c = y1 + t * (y2 - y1);

                return new Point(x_c, y_c);
            }
        }
        return null;
    }

    @Override
    public int computeCrossingNumber(Graph g, HashMap<Vertex, Coordinate> vertexCoordinates) {
        intersectionPoints = new ArrayList<>();
        int crossingNumber = 0;
        for (Edge e1 : g.getEdges()) {

            for (Edge e2 : g.getEdges()) {
                if (!e1.equals(e2)) {
                    if (!e1.isAdjacent(e2)) {

                        var startCoord1 = vertexCoordinates.get(e1.getS());
                        var endCoord1 = vertexCoordinates.get(e1.getT());
                        var startCoord2 = vertexCoordinates.get(e2.getS());
                        var endCoord2 = vertexCoordinates.get(e2.getT());

                        if (startCoord1 == null || startCoord2 == null || endCoord1 == null || endCoord2 == null) {
                            continue;
                        }

                        Segment s1 = new Segment(startCoord1, endCoord1);
                        Segment s2 = new Segment(startCoord2, endCoord2);
                        var intersectionPoint = BasicCrossingCalculatorAlgorithm.reportIntersection(s1, s2);
                        if (intersectionPoint != null) { // if(!Segment.intersect(s1, s2))
                            crossingNumber++;
                            intersectionPoints.add(intersectionPoint);
                        }
                    }
                }
            }
        }
        return crossingNumber / 2;
    }

}
