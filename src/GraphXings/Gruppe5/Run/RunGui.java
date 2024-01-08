//package GraphXings.Gruppe5Algo.Run;
//
//import java.io.FileNotFoundException;
//
//import GraphXings.Game.NewGameResult;
//import GraphXings.Gruppe5Algo.GUI.GuiGame;
//import GraphXings.Gruppe5Algo.Models.HeatMap;
//import GraphXings.Gruppe5Algo.Models.HeatMapFileReader;
//import GraphXings.Gruppe5Algo.Players.PointChoicePlayer;
//import GraphXings.Gruppe5Algo.PointStrategies.HeatMapChoiceStrategy;
//import GraphXings.Gruppe5Algo.PointStrategies.RandomPointChoiceStrategy;
//import GraphXings.Gruppe5Algo.Utils.SpecificRandomCycleFactory;
//
//public class RunGui {
//
//        public static void main(String[] args) {
//
//                int numNodes = 50;
//                int width = 1000;
//                int height = 1000;
//                var randomFactory = new SpecificRandomCycleFactory(numNodes, width, height);
//
//                var gameInstance = randomFactory.getGameInstance();
//
//                System.out.println("Generated game with " + gameInstance.getG().getN() + "x" + gameInstance.getWidth()
//                                + "x"
//                                + gameInstance.getHeight());
//
//                HeatMap maxHeatMap, minHeatMap;
//                try {
//                        maxHeatMap = new HeatMapFileReader()
//                                        .readFromFile("./src/GraphXings/Gruppe5Algo/PointStrategies/HeatMaps/SmallHeatMapMax.txt");
//                        minHeatMap = new HeatMapFileReader().readFromFile(
//                                        "./src/GraphXings/Gruppe5Algo/PointStrategies/HeatMaps/SmallHeatMapMax.txt");
//
//                } catch (FileNotFoundException e) {
//                        System.out.println(e.toString());
//                        return;
//                }
//
//                var player1 = new PointChoicePlayer("My Player", new HeatMapChoiceStrategy(minHeatMap),
//                                new HeatMapChoiceStrategy(maxHeatMap), 2000);
//
//                var player2 = new PointChoicePlayer("RC 20", new RandomPointChoiceStrategy(20),
//                                new RandomPointChoiceStrategy(20),
//                                2000);
//
//                System.out.println("Starting matchup " + player1.getName() + " vs. " + player2.getName());
//
//                GuiGame game = new GuiGame(gameInstance.getG(), gameInstance.getHeight(), gameInstance.getWidth(),
//                                player1, player2);
//                NewGameResult res = game.play();
//                ;
//
//                System.out.println(res.announceResult());
//
//        }
//
//}
