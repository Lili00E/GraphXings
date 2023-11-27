package GraphXings.Competitors.Group08;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import GraphXings.Data.Coordinate;
import GraphXings.Data.Edge;
import GraphXings.Data.Graph;
import GraphXings.Data.Segment;
import GraphXings.Data.Vertex;

public class SweepLineIntersection {
    static class Point implements Comparable<Point> {
        int x, y;
        boolean start;

        public Point(int x, int y, boolean start) {
            this.x = x;
            this.y = y;
            this.start = start;
        }

        @Override
        public int compareTo(Point p) {
            if (this.x != p.x) {
                return this.x - p.x;
            } else if (this.start && !p.start) {
                return 1; 
            } else if (!this.start && p.start) {
                return -1; 
            } else {
                return this.y - p.y;
            }
        }
    }

    public int computeCrossingNumber(Graph g, HashMap<Vertex,Coordinate> vertexCoordinates) {
        List<Point> points = new ArrayList<>();

        for (Edge edge : g.getEdges()) {
            if (!vertexCoordinates.containsKey(edge.getS()) || !vertexCoordinates.containsKey(edge.getT()))
            {
                continue;
            }
            Segment segment = new Segment(vertexCoordinates.get(edge.getS()),vertexCoordinates.get(edge.getT()));

            points.add(new Point(segment.getStartX().getP() / segment.getStartX().getQ(), segment.getStartY().getP() / segment.getStartY().getQ(), true));
            points.add(new Point(segment.getEndX().getP() / segment.getEndX().getQ(), segment.getEndY().getP() / segment.getEndY().getQ(), false));
        }

        Collections.sort(points);

        int count = 0;
        int activeSegments = 0;

        for (Point point : points) {
            if (point.start) {
                activeSegments++;
            } else {
                activeSegments--;
            }
            if (activeSegments > 0) {
                count++;
            }
        }
        return count / 2;
    }
}
