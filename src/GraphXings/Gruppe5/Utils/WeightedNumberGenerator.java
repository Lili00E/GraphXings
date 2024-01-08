package GraphXings.Gruppe5.Utils;

import java.util.Random;

public class WeightedNumberGenerator {

    private double[] weights;
    private double[] cumulativeProbabilities;
    private Random random;

    public WeightedNumberGenerator(double[] weights) {
        this.weights = weights;
        this.cumulativeProbabilities = calculateCumulativeProbabilities(weights);
        this.random = new Random();
    }

    public double[] getWeights() {
        return this.weights;
    }

    private double[] calculateCumulativeProbabilities(double[] weights) {
        double[] cumulativeProbabilities = new double[weights.length];
        cumulativeProbabilities[0] = weights[0];

        for (int i = 1; i < weights.length; i++) {
            cumulativeProbabilities[i] = cumulativeProbabilities[i - 1] + weights[i];
        }

        return cumulativeProbabilities;
    }

    public int getNextNumber() {
        double randomValue = random.nextDouble();

        for (int i = 0; i < cumulativeProbabilities.length; i++) {
            if (randomValue < cumulativeProbabilities[i]) {
                return i;
            }
        }

        // This should not happen if the weights are correctly normalized.
        return -1;
    }

}
