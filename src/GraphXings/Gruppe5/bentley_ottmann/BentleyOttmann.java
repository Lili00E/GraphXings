package GraphXings.Gruppe5.bentley_ottmann;

import java.util.*;

/**
 * Created by valen_000 on 14. 5. 2017.
 */

public class BentleyOttmann {

    private Queue<Event> Q;
    private NavigableSet<Segment> T;
    private ArrayList<Point> X;

    public BentleyOttmann(ArrayList<Segment> input_data) {
        this.Q = new PriorityQueue<>(new EventComparator());
        this.T = new TreeSet<>(new SegmentComparator());
        this.X = new ArrayList<>();
        for (Segment s : input_data) {
            this.Q.add(new Event(s.first(), s, 0));
            this.Q.add(new Event(s.second(), s, 1));
        }
    }

    public int find_intersections() {
        int numIntersections = 0;
        while (!this.Q.isEmpty()) {
            Event e = this.Q.poll();
            double L = e.getValue();
            boolean intersection = false;
            switch (e.getType()) {
                case 0:
                    for (Segment s : e.getSegements()) {
                        this.recalculate(L);
                        this.T.add(s);
                        if (this.T.lower(s) != null) {
                            Segment r = this.T.lower(s);
                            intersection = this.report_intersection(r, s, L);
                        }
                        if (this.T.higher(s) != null) {
                            Segment t = this.T.higher(s);
                            intersection = this.report_intersection(t, s, L);
                        }
                        if (this.T.lower(s) != null && this.T.higher(s) != null) {
                            Segment r = this.T.lower(s);
                            Segment t = this.T.higher(s);
                            this.remove_future(r, t);
                        }
                    }
                    break;
                case 1:
                    for (Segment s : e.getSegements()) {
                        if (this.T.lower(s) != null && this.T.higher(s) != null) {
                            Segment r = this.T.lower(s);
                            Segment t = this.T.higher(s);
                            intersection = this.report_intersection(r, t, L);
                        }
                        this.T.remove(s);
                    }
                    break;
                case 2:
                    Segment s_1 = e.getSegements().get(0);
                    Segment s_2 = e.getSegements().get(1);
                    this.swap(s_1, s_2);
                    if (s_1.getValue() < s_2.getValue()) {
                        if (this.T.higher(s_1) != null) {
                            Segment t = this.T.higher(s_1);
                            intersection = this.report_intersection(t, s_1, L);
                            this.remove_future(t, s_2);
                        }
                        if (this.T.lower(s_2) != null) {
                            Segment r = this.T.lower(s_2);
                            intersection = this.report_intersection(r, s_2, L);
                            this.remove_future(r, s_1);
                        }
                    } else {
                        if (this.T.higher(s_2) != null) {
                            Segment t = this.T.higher(s_2);
                            intersection = this.report_intersection(t, s_2, L);
                            this.remove_future(t, s_1);
                        }
                        if (this.T.lower(s_1) != null) {
                            Segment r = this.T.lower(s_1);
                            intersection = this.report_intersection(r, s_1, L);
                            this.remove_future(r, s_2);
                        }
                    }

                    this.X.add(e.getPoint());
                    break;
            }
            if (intersection) {
                numIntersections++;
            }
        }
        return numIntersections;
    }

    private boolean report_intersection(Segment s_1, Segment s_2, double L) {
        double x1 = s_1.first().getXCoord();
        double y1 = s_1.first().getYCoord();
        double x2 = s_1.second().getXCoord();
        double y2 = s_1.second().getYCoord();
        double x3 = s_2.first().getXCoord();
        double y3 = s_2.first().getYCoord();
        double x4 = s_2.second().getXCoord();
        double y4 = s_2.second().getYCoord();
        double r = (x2 - x1) * (y4 - y3) - (y2 - y1) * (x4 - x3);
        if (r != 0) {
            double t = ((x3 - x1) * (y4 - y3) - (y3 - y1) * (x4 - x3)) / r;
            double u = ((x3 - x1) * (y2 - y1) - (y3 - y1) * (x2 - x1)) / r;
            if (t >= 0 && t <= 1 && u >= 0 && u <= 1) {
                double x_c = x1 + t * (x2 - x1);
                double y_c = y1 + t * (y2 - y1);
                if (x_c > L) {
                    this.Q.add(new Event(new Point(x_c, y_c), new ArrayList<>(Arrays.asList(s_1, s_2)), 2));
                    return true;
                }
            }
        }
        return false;
    }

    private boolean remove_future(Segment s_1, Segment s_2) {
        for (Event e : this.Q) {
            if (e.getType() == 2) {
                if ((e.getSegements().get(0) == s_1 && e.getSegements().get(1) == s_2)
                        || (e.getSegements().get(0) == s_2 && e.getSegements().get(1) == s_1)) {
                    this.Q.remove(e);
                    return true;
                }
            }
        }
        return false;
    }

    private void swap(Segment s_1, Segment s_2) {
        this.T.remove(s_1);
        this.T.remove(s_2);
        double value = s_1.getValue();
        s_1.set_value(s_2.getValue());
        s_2.set_value(value);
        this.T.add(s_1);
        this.T.add(s_2);
    }

    private void recalculate(double L) {
        Iterator<Segment> iter = this.T.iterator();
        while (iter.hasNext()) {
            iter.next().calculate_value(L);
        }
    }

    public void print_intersections() {
        for (Point p : this.X) {
            System.out.println("(" + p.getXCoord() + ", " + p.getYCoord() + ")");
        }
    }

    public ArrayList<Point> get_intersections() {
        return this.X;
    }

    public class EventComparator implements Comparator<Event> {
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
}