package GraphXings.Algorithms;

import bentley_ottmann.Event;
import bentley_ottmann.Point;
import bentley_ottmann.Segment;
import GraphXings.Data.Coordinate;
import GraphXings.Data.Graph;
import GraphXings.Data.Vertex;

import java.util.*;

/**
 * A class for computing the number of crossings of a partially embedded graph.
 */
public class BentleyOttmannCrossingCalculator implements CrossingCalculatorAlgorithm {

    private Queue<Event> Q;
    private NavigableSet<Segment> T;
    public ArrayList<Point> X;
    public ArrayList<Segment> segments;
    public ArrayList<Point> intersections;
    public ArrayList<Segment> intersectingSegment;
    public ArrayList<Event> usedEventPoints;
    public Event lastEvent;
    public boolean doneStepComputation = true;
    public Segment compA;
    public Segment compB;

    public BentleyOttmannCrossingCalculator() {
        this.Q = new PriorityQueue<>(new EventComparator());
        this.T = new TreeSet<>(new SegmentComparator());
        this.X = new ArrayList<>();
        segments = new ArrayList<>();
        usedEventPoints = new ArrayList<>();
        intersectingSegment = new ArrayList<>();
        lastEvent = null;
    }

    private boolean reportIntersection(Segment s1, Segment s2, double L) {
        double x1 = s1.first().getXCoord();
        double y1 = s1.first().getYCoord();
        double x2 = s1.second().getXCoord();
        double y2 = s1.second().getYCoord();
        double x3 = s2.first().getXCoord();
        double y3 = s2.first().getYCoord();
        double x4 = s2.second().getXCoord();
        double y4 = s2.second().getYCoord();

        boolean segmentIsPoint = (x1 == x2 && y1 == y2) || (x3 == x4 && y3 == y4);
        boolean segmentsOverlap = (x1 == x3 && y1 == y3) || (x1 == x4 && y1 == y4) ||
                (x2 == x3 && y2 == y3)
                || (x2 == x4 && y2 == y4);
        if (segmentIsPoint || segmentsOverlap) {

            return false;
        }

        double r = (x2 - x1) * (y4 - y3) - (y2 - y1) * (x4 - x3);
        if (r != 0) {
            double t = ((x3 - x1) * (y4 - y3) - (y3 - y1) * (x4 - x3)) / r;
            double u = ((x3 - x1) * (y2 - y1) - (y3 - y1) * (x2 - x1)) / r;
            if (t >= 0 && t <= 1 && u >= 0 && u <= 1) {
                double x_c = x1 + t * (x2 - x1);
                double y_c = y1 + t * (y2 - y1);
                if (x_c > L) {
                    this.Q.add(new Event(new Point(x_c, y_c), new ArrayList<>(Arrays.asList(s1, s2)), 2));
                    X.add(new Point(x_c, y_c));
                    return true;
                }
            }
        }
        return false;
    }

    private boolean removeFutureSegemnts(Segment s1, Segment s2) {
        for (Event e : this.Q) {
            if (e.getType() == 2) {
                if ((e.getSegements().get(0) == s1 && e.getSegements().get(1) == s2)
                        || (e.getSegements().get(0) == s2 && e.getSegements().get(1) == s1)) {
                    this.Q.remove(e);
                    return true;
                }
            }
        }
        return false;
    }

    private void swap(Segment s1, Segment s2) {
        this.T.remove(s1);
        this.T.remove(s2);
        double value = s1.getValue();
        s1.set_value(s2.getValue());
        s2.set_value(value);
        this.T.add(s1);
        this.T.add(s2);
    }

    private void recalculate(double L) {
        Iterator<Segment> iter = this.T.iterator();
        while (iter.hasNext()) {
            iter.next().calculate_value(L);
        }
    }

    public Point computeSingleIntersectionStep() {

        if (!this.Q.isEmpty()) {
            lastEvent = this.Q.poll();
            System.out.println(
                    "x " + lastEvent.getPoint().getXCoord() + ",y: " + lastEvent.getPoint().getYCoord()
                            + ", type: " + lastEvent.getType());
            usedEventPoints.add(lastEvent);
            double L = lastEvent.getValue();
            boolean intersection = false;
            switch (lastEvent.getType()) {
                case 0:
                    for (Segment s : lastEvent.getSegements()) {
                        this.recalculate(L);
                        this.T.add(s);
                        if (this.T.lower(s) != null) {
                            Segment r = this.T.lower(s);
                            intersection = this.reportIntersection(r, s, L);
                            compA = r;
                            compB = s;
                        }
                        if (this.T.higher(s) != null) {
                            Segment t = this.T.higher(s);
                            intersection = this.reportIntersection(t, s, L);
                            compA = t;
                            compB = s;
                        }
                        if (this.T.lower(s) != null && this.T.higher(s) != null) {
                            Segment r = this.T.lower(s);
                            Segment t = this.T.higher(s);
                            this.removeFutureSegemnts(r, t);
                        }
                    }
                    break;
                case 1:
                    for (Segment s : lastEvent.getSegements()) {
                        if (this.T.lower(s) != null && this.T.higher(s) != null) {
                            Segment r = this.T.lower(s);
                            Segment t = this.T.higher(s);
                            intersection = this.reportIntersection(r, t, L);
                            compA = r;
                            compB = t;
                        }
                        this.T.remove(s);
                    }
                    break;
                case 2:
                    Segment s1 = lastEvent.getSegements().get(0);
                    Segment s2 = lastEvent.getSegements().get(1);
                    this.swap(s1, s2);
                    if (s1.getValue() < s2.getValue()) {
                        if (this.T.higher(s1) != null) {
                            Segment t = this.T.higher(s1);
                            intersection = this.reportIntersection(t, s1, L);
                            this.removeFutureSegemnts(t, s2);
                            compA = t;
                            compB = s1;
                        }
                        if (this.T.lower(s2) != null) {
                            Segment r = this.T.lower(s2);
                            intersection = this.reportIntersection(r, s2, L);
                            this.removeFutureSegemnts(r, s1);
                            compA = r;
                            compB = s2;
                        }
                    } else {
                        if (this.T.higher(s2) != null) {
                            Segment t = this.T.higher(s2);
                            intersection = this.reportIntersection(t, s2, L);
                            this.removeFutureSegemnts(t, s1);
                            compA = t;
                            compB = s2;
                        }
                        if (this.T.lower(s1) != null) {
                            Segment r = this.T.lower(s1);
                            intersection = this.reportIntersection(r, s1, L);
                            this.removeFutureSegemnts(r, s2);
                            compA = r;
                            compB = s1;
                        }
                    }

                    lastEvent.getSegements().forEach((seg) -> {
                        this.intersectingSegment.add(seg);
                    });

                    return lastEvent.getPoint();

            }

        } else {
            doneStepComputation = true;
        }
        return null;
    }

    public int computeIntersections() {
        int numIntersections = 0;
        intersectingSegment = new ArrayList<>();
        usedEventPoints = new ArrayList<>();
        while (!this.Q.isEmpty()) {
            Event e = this.Q.poll();
            System.out.println(
                    "x " + e.getPoint().getXCoord() + ",y: " + e.getPoint().getYCoord() + ", type: " + e.getType());
            usedEventPoints.add(e);
            double L = e.getValue();
            boolean intersection = false;
            switch (e.getType()) {
                case 0:
                    for (Segment s : e.getSegements()) {
                        this.recalculate(L);
                        this.T.add(s);
                        if (this.T.lower(s) != null) {
                            Segment r = this.T.lower(s);
                            intersection = this.reportIntersection(r, s, L);
                        }
                        if (this.T.higher(s) != null) {
                            Segment t = this.T.higher(s);
                            intersection = this.reportIntersection(t, s, L);
                        }
                        if (this.T.lower(s) != null && this.T.higher(s) != null) {
                            Segment r = this.T.lower(s);
                            Segment t = this.T.higher(s);
                            this.removeFutureSegemnts(r, t);
                        }
                    }
                    break;
                case 1:
                    for (Segment s : e.getSegements()) {
                        if (this.T.lower(s) != null && this.T.higher(s) != null) {
                            Segment r = this.T.lower(s);
                            Segment t = this.T.higher(s);
                            intersection = this.reportIntersection(r, t, L);
                        }
                        this.T.remove(s);
                    }
                    break;
                case 2:
                    Segment s1 = e.getSegements().get(0);
                    Segment s2 = e.getSegements().get(1);
                    this.swap(s1, s2);
                    if (s1.getValue() < s2.getValue()) {
                        if (this.T.higher(s1) != null) {
                            Segment t = this.T.higher(s1);
                            intersection = this.reportIntersection(t, s1, L);
                            this.removeFutureSegemnts(t, s2);
                        }
                        if (this.T.lower(s2) != null) {
                            Segment r = this.T.lower(s2);
                            intersection = this.reportIntersection(r, s2, L);
                            this.removeFutureSegemnts(r, s1);
                        }
                    } else {
                        if (this.T.higher(s2) != null) {
                            Segment t = this.T.higher(s2);
                            intersection = this.reportIntersection(t, s2, L);
                            this.removeFutureSegemnts(t, s1);
                        }
                        if (this.T.lower(s1) != null) {
                            Segment r = this.T.lower(s1);
                            intersection = this.reportIntersection(r, s1, L);
                            this.removeFutureSegemnts(r, s2);
                        }
                    }

                    e.getSegements().forEach((seg) -> {
                        this.intersectingSegment.add(seg);
                    });
                    X.add(e.getPoint());
                    numIntersections++;
                    break;
            }

        }
        return numIntersections;
    }

    class EventComparator implements Comparator<Event> {
        @Override
        public int compare(Event e1, Event e2) {
            if (e1.getValue() > e2.getValue()) {
                return 1;
            }
            if (e1.getValue() < e2.getValue()) {
                return -1;
            }
            return 0;
        }
    }

    public class SegmentComparator implements Comparator<Segment> {
        @Override
        public int compare(Segment s1, Segment s2) {
            if (s1.getValue() < s2.getValue()) {
                return 1;
            }
            if (s1.getValue() > s2.getValue()) {
                return -1;
            }
            return 0;
        }
    }

    public void initCrossingStepComputation(Graph g, HashMap<Vertex, Coordinate> vertexCoordinates) {
        segments = new ArrayList<>();
        usedEventPoints = new ArrayList<>();
        intersectingSegment = new ArrayList<>();
        lastEvent = null;
        doneStepComputation = false;

        for (var e : g.getEdges()) {

            var startCoord = vertexCoordinates.get(e.getS());
            var endCoord = vertexCoordinates.get(e.getT());

            if (startCoord == null || endCoord == null) {
                continue;
            }

            segments.add(new Segment(new Point(startCoord.getX(), startCoord.getY()),
                    new Point(endCoord.getX(), endCoord.getY())));
        }

        this.Q = new PriorityQueue<>(new EventComparator());
        this.T = new TreeSet<>(new SegmentComparator());
        this.X = new ArrayList<>();
        for (Segment s : segments) {
            this.Q.add(new Event(s.first(), s, 0));
            this.Q.add(new Event(s.second(), s, 1));
        }

    }

    @Override
    public int computeCrossingNumber(Graph g, HashMap<Vertex, Coordinate> vertexCoordinates) {

        // init segments
        segments = new ArrayList<>();

        for (var e : g.getEdges()) {

            var startCoord = vertexCoordinates.get(e.getS());
            var endCoord = vertexCoordinates.get(e.getT());

            if (startCoord == null || endCoord == null) {
                continue;
            }

            segments.add(new Segment(new Point(startCoord.getX(), startCoord.getY()),
                    new Point(endCoord.getX(), endCoord.getY())));
        }

        this.Q = new PriorityQueue<>(new EventComparator());
        this.T = new TreeSet<>(new SegmentComparator());
        this.X = new ArrayList<>();
        for (Segment s : segments) {
            this.Q.add(new Event(s.first(), s, 0));
            this.Q.add(new Event(s.second(), s, 1));
        }

        int numCrossings = computeIntersections();

        // int checkCrossings = new CrossingCalculator(g,
        // vertexCoordinates).computeCrossingNumber();
        // if (numCrossings != checkCrossings) {
        // throw new IllegalArgumentException("fakls");

        // }
        return numCrossings;

    }

}
