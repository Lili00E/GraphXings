package GraphXings.Gruppe5.Run;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import GraphXings.Algorithms.NewPlayer;
import GraphXings.Algorithms.NewRandomPlayer;
import GraphXings.Game.NewGame;
import GraphXings.Game.NewGameResult;
import GraphXings.Game.GameInstance.PlanarGameInstanceFactory;
import GraphXings.Gruppe5.Models.HeatMapFileReader;
import GraphXings.Gruppe5.Players.PointChoicePlayer;
import GraphXings.Gruppe5.Players.RecursiveSearchPlayer;
import GraphXings.Gruppe5.PointStrategies.RandomPointChoiceStrategy;
import GraphXings.Gruppe5.Utils.SpecificRandomCycleFactory;
import GraphXings.Gruppe5.Utils.VsBar;

public class RunDebug {
  public static void main(String[] args) {

    int numGames = 10;
    int numNodes = 50;
    int width = 1000;
    int height = 1000;
    var randomFactory = new SpecificRandomCycleFactory(numNodes, width, height);
    long seed = 27081883;
    PlanarGameInstanceFactory factory = new PlanarGameInstanceFactory(seed);

    var gameInstance = factory.getGameInstance();

    System.out.println("Generated game with " + gameInstance.getG().getN() + "x" + gameInstance.getWidth() + "x"
        + gameInstance.getHeight());

    var winners = new HashMap<String, Integer>();

    var progressBar = new VsBar(80, '=', '*');

    try {

      // var maxHeatMap = new HeatMapFileReader()
      // .readFromFile("./GraphXings/Gruppe5Algo/PointStrategies/HeatMaps/ManualHeatMap.txt");
      // var minHeatMap = new HeatMapFileReader()
      // .readFromFile(".//Gruppe5/PointStrategies/HeatMaps/UniformHeatMap.txt");
      // var smallHeatMapMin = new HeatMapFileReader()
      // .readFromFile("./GraphXings/Gruppe5/PointStrategies/HeatMaps/SmallHeatMapMin.txt");
      var smallHeatMapMax = new HeatMapFileReader()
          .readFromFile("./src/GraphXings/Gruppe5/PointStrategies/HeatMaps/SmallHeatMapMax.txt");

      // var myPlayer = new PointChoicePlayer("My Player: Min as Min", new
      // HeatMapChoiceStrategy(minHeatMap),
      // new HeatMapChoiceStrategy(maxHeatMap), 2000);
      // var myPlayer = new PointChoicePlayer("My Player: Max as Min", new
      // HeatMapChoiceStrategy(smallHeatMapMax),
      // new HeatMapChoiceStrategy(smallHeatMapMin), 2000);
      // var myPlayer = new RecursiveSearchPlayer("My Player: Recursive Search", 0,
      // 10, 10, 20000);
      var myPlayer = new RecursiveSearchPlayer("RS Player", 0, 10, 10, 200);
      // var myPlayer = new
      // PointChoicePlayer("My Player", new
      // HeatMapChoiceStrategy(smallHeatMapMin),
      // new HeatMapChoiceStrategy(smallHeatMapMax), 2000);
      var competitors = new ArrayList<NewPlayer>() {
        {

          // add(new PointChoicePlayer("RC 150", new RandomPointChoiceStrategy(10),
          // new RandomPointChoiceStrategy(20),
          // 2000));
          add(new NewRandomPlayer("dummy"));
          // add(new PointChoicePlayer("My Player: only Max", new
          // HeatMapChoiceStrategy(smallHeatMapMin),
          // new HeatMapChoiceStrategy(smallHeatMapMax), 2000));
          // add(new GridPlayer("Group 10"));
          // add(new NewBetterFasterPlayer("Better Faster Player"));

        }
      };

      for (NewPlayer competitor : competitors) {
        var player1 = myPlayer;
        var player2 = competitor;

        if (!winners.containsKey(player1.getName())) {
          winners.put(player1.getName(), 0);
        }
        if (!winners.containsKey(player2.getName())) {
          winners.put(player2.getName(), 0);
        }

        System.out.println("Starting matchup " + player1.getName() + " vs. " + player2.getName());
        for (int i = 0; i < numGames; i++) {
          NewGame game = new NewGame(gameInstance.getG(), gameInstance.getHeight(), gameInstance.getWidth(),
              player1, player2, NewGame.Objective.CROSSING_ANGLE, 300000000000L);
          NewGameResult res = game.play();

          progressBar.printProgressDiscrete(winners.get(player1.getName()), winners.get(player2.getName()), numGames);

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
        progressBar.printProgressDiscrete(winners.get(player1.getName()), winners.get(player2.getName()), numGames);

        System.out.println();
        winners = new HashMap<>();

      }
    } catch (FileNotFoundException e) {
      System.out.println("HEATMAP File not found " + e.toString());
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
