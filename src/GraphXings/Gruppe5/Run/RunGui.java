
//package GraphXings.Gruppe5Algo.Run;
//
//import java.io.FileNotFoundException;
//
import java.io.FileNotFoundException;

import GraphXings.Game.NewGameResult;
import GraphXings.Gruppe5.Models.HeatMap;
import GraphXings.Gruppe5.Models.HeatMapFileReader;
import GraphXings.Gruppe5.Players.PointChoicePlayer;
import GraphXings.Gruppe5.PointStrategies.HeatMapChoiceStrategy;
import GraphXings.Gruppe5.PointStrategies.RandomPointChoiceStrategy;
import GraphXings.Gruppe5.Utils.SpecificRandomCycleFactory;

public class RunGui {

        public static void main(String[] args) {

                int numNodes = 1000;
                int width = 1000;
                int height = 1000;
                var randomFactory = new SpecificRandomCycleFactory(numNodes, width, height);

                var gameInstance = randomFactory.getGameInstance();

                System.out.println("Generated game with " + gameInstance.getG().getN() + "x" + gameInstance.getWidth()
                                + "x"
                                + gameInstance.getHeight());
                try {
                        var smallHeatMapMin = new HeatMapFileReader()
                                        .readFromFile("./src/GraphXings/Gruppe5/PointStrategies/HeatMaps/HeatMap_Max/Max_Heatmap_15.txt");
                        var smallHeatMapMax = new HeatMapFileReader()
                                        .readFromFile("./src/GraphXings/Gruppe5/PointStrategies/HeatMaps/HeatMap_Max/Max_Heatmap_8.txt");

                        var player1 = new PointChoicePlayer("My Player", new HeatMapChoiceStrategy(smallHeatMapMin),
                                        new HeatMapChoiceStrategy(smallHeatMapMax));
                        var player2 = new PointChoicePlayer("My Player 2", new HeatMapChoiceStrategy(smallHeatMapMin),
                                        new HeatMapChoiceStrategy(smallHeatMapMax));

                        System.out.println("Starting matchup " + player1.getName() + " vs. " + player2.getName());
                        GuiGame game = new GuiGame(gameInstance.getG(), gameInstance.getHeight(),
                                        gameInstance.getWidth(),
                                        player1, player2);
                        NewGameResult res = game.play();
                        ;

                        System.out.println(res.announceResult());
                } catch (FileNotFoundException e) {
                        System.out.println("HEATMAP File not found " + e.toString());
                }
        }

}
