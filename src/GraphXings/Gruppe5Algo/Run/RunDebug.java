package GraphXings.Gruppe5Algo.Run;

import GraphXings.Algorithms.NewPlayer;
import GraphXings.Game.NewGame;
import GraphXings.Game.NewGameResult;
import GraphXings.Gruppe5Algo.Models.HeatMapFileReader;
import GraphXings.Gruppe5Algo.Players.PointChoicePlayer;
import GraphXings.Gruppe5Algo.PointStrategies.HeatMapChoiceStrategy;
import GraphXings.Gruppe5Algo.PointStrategies.RandomPointChoiceStrategy;
import GraphXings.Gruppe5Algo.Utils.ProgressBar;
import GraphXings.Gruppe5Algo.Utils.SpecificRandomCycleFactory;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.ArrayList;

public class RunDebug {
    public static void main(String[] args) {

        int numGames = 10;
        int numNodes = 1000;
        int width = 10000;
        int height = 10000;
        var randomFactory = new SpecificRandomCycleFactory(numNodes, width, height);

        var gameInstance = randomFactory.getGameInstance();

        System.out.println("Generated game with " + gameInstance.getG().getN() + "x" + gameInstance.getWidth() + "x"
                + gameInstance.getHeight());

        var winners = new HashMap<String, Integer>();

        var progressBar = new ProgressBar(100, '=');

        // var heatMapSize = 100;
        // var totalSize = heatMapSize * heatMapSize;
        // double[] weights = new double[totalSize];

        // double p = 1.0 / (double) totalSize;

        // System.out.println(p);

        // for (int i = 0; i < weights.length; i++) {
        // weights[i] = p;
        // }
        // var generator = new WeightedNumberGenerator(weights);

        // var heatMap = new HeatMap(generator, heatMapSize, heatMapSize);
        var maxHeatMap = new HeatMapFileReader()
                .readFromFile("./src/GraphXings/Gruppe5Algo/PointStrategies/HeatMaps/ManualHeatMapMini.txt");
        var minHeatMap = new HeatMapFileReader()
                .readFromFile("./src/GraphXings/Gruppe5Algo/PointStrategies/HeatMaps/ManualHeatMap.txt");

        var myPlayer = new PointChoicePlayer("My Player", new HeatMapChoiceStrategy(minHeatMap),
                new HeatMapChoiceStrategy(maxHeatMap), 2000);

        var competitors = new ArrayList<NewPlayer>() {
            {
                // add(new NewRandomPlayer("Random (Control)"));
                // add(new RandomChoicePlayer("RC 20", 20, 1000));
                // add(new RandomChoicePlayer("RC 5", 5, 1000));
                add(new PointChoicePlayer("RC 25", new RandomPointChoiceStrategy(25), new RandomPointChoiceStrategy(20),
                        2000));
                // add(new PointChoicePlayer("Gridmaster 81", new GridPointChoiceStrategy(10),
                // new GridPointChoiceStrategy(10),
                // 2000));
                // add(new RandomChoicePlayerTest("Minimize with edges", 20, 2000));
            }
        };

        for (NewPlayer competitor : competitors) {
            var player1 = myPlayer;
            var player2 = competitor;

            System.out.println("Starting matchup " + player1.getName() + " vs. " + player2.getName());
            for (int i = 0; i < numGames; i++) {
                NewGame game = new NewGame(gameInstance.getG(), gameInstance.getHeight(), gameInstance.getWidth(),
                        player1, player2);
                NewGameResult res = game.play();

                progressBar.printProgressDiscrete(i + 1, numGames);

                var winnerPlayer = res.getWinner();
                if (winnerPlayer == null) {
                    continue;
                }
                var winner = winnerPlayer.getName();
                if (winners.containsKey(winner)) {
                    winners.put(winner, winners.get(winner) + 1);
                } else {
                    winners.put(winner, 1);
                }

            }

            progressBar.clearOutput();

            System.out.println("Win distribution for " + player1.getName() + " vs. " + player2.getName());
            progressBar.printProgressDiscrete(winners.get(player1.getName()), numGames);

            System.out.println();
            winners = new HashMap<>();

        }

    }

    /**
     * Output to csv file.
     */
    public static void csvReport(int[] gridMax, int[] gridMin) {
        try {
            String dest = "./histograms";
            File file = new File(dest);
            file.mkdir();
            FileWriter histMax = new FileWriter(dest + "/" + "MaxGridValues.csv");
            FileWriter histMin = new FileWriter(dest + "/" + "MinGridValues.csv");

            histMax.append("GridNumber ; CoordinatesChosen\n");
            histMin.append("GridNumber ; CoordinatesChosen\n");

            String max = "";
            String min = "";

            for (int i = 1; i < gridMax.length; i++) {
                max += (i + ";" + gridMax[i] + "\n");
                min += (i + ";" + gridMin[i] + "\n");

            }

            histMax.append(max);
            histMin.append(min);

            histMax.close();
            histMin.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
