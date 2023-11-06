package GraphXings.Algorithms;

import java.util.HashMap;

import GraphXings.Data.Coordinate;
import GraphXings.Data.Edge;
import GraphXings.Data.Graph;
import GraphXings.Data.Segment;
import GraphXings.Data.Vertex;

public class BasicCrossingCalculatorAlgorithm implements CrossingCalculatorAlgorithm {

    @Override
    public int computeCrossingNumber(Graph g, HashMap<Vertex, Coordinate> vertexCoordinates) {

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
                        if (Segment.intersect(s1, s2)) {
                            crossingNumber++;
                        }
                    }
                }
            }
        }
        return crossingNumber / 2;
    }

}
