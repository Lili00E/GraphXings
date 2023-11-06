package GraphXings.Gruppe5;

import GraphXings.Data.Rational;

public class RationalComputer {

    public static int compareRational(Rational x, Rational y) {
        var a = x.getP() * y.getQ();
        var b = x.getQ() * y.getP();
        return b - a;
    }

    public static double getValue(Rational r) {
        return r.getP() / r.getQ();
    }
}