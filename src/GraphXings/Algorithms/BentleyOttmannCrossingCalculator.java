package GraphXings.Algorithms;

import GraphXings.Data.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

class Event implements Comparable<Event> {
    Rational x;
    Rational y;
    Segment segment;
    int type;

    public Event(Rational x, Rational y, Segment segment, int type) {
        this.x = x;
        this.y = y;
        this.segment = segment;
        this.type = type;
    }

    @Override
    public int compareTo(Event other) {
        if (this.x.equals(other.x)) {
            var result = Rational.minus(x, other.x);
            return result.getP() / result.getQ();
        }
        return this.type - other.type;
    }
}

/**
 * A class for computing the number of crossings of a partially embedded graph.
 */
public class BentleyOttmannCrossingCalculator implements CrossingCalculatorAlgorithm {

    @Override
    public int computeCrossingNumber(Graph g, HashMap<Vertex, Coordinate> vertexCoordinates) {

        int crossingNumber = 0;

        List<Event> events = new ArrayList<>();

        ArrayList<Segment> segments = new ArrayList<>();

        for (var e : g.getEdges()) {
            segments.add(new Segment(vertexCoordinates.get(e.getS()), vertexCoordinates.get(e.getT())));
        }

        for (Segment segment : segments) {
            events.add(new Event(segment.getStartX(), segment.getEndX(), segment, 1)); // 1 represents the start event
            events.add(new Event(segment.getEndX(), segment.getEndY(), segment, -1)); // -1 represents the end event
        }

        TreeSet<Segment> activeSegments = new TreeSet<>((seg1, seg2) -> {
            if (!seg1.getEndY().equals(seg2.getEndY()) && Rational.lesserEqual(seg1.getEndY(), seg2.getEndY())) {
                return -1;
            } else if (!seg1.getEndY().equals(seg2.getEndY()) && Rational.lesserEqual(seg2.getEndY(), seg1.getEndY())) {
                return 1;
            }
            var result = Rational.minus(seg1.getStartX(), seg2.getStartX());
            return result.getP() / result.getQ();

        });

        events.sort(Event::compareTo);

        for (Event event : events) {
            if (event.type == 1) { // Start event
                for (Segment seg : activeSegments) {
                    if (event.segment != seg) {
                        if (Segment.intersect(event.segment, seg)) {
                            crossingNumber++;
                        }
                    }
                }
                activeSegments.add(event.segment);
            } else if (event.type == -1) { // End event
                activeSegments.remove(event.segment);
            }
        }

        return crossingNumber;
    }

}
