package GraphXings;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;

import GraphXings.Algorithms.NewPlayer;
import GraphXings.Data.Edge;
import GraphXings.Data.Graph;
import GraphXings.Data.Vertex;
import GraphXings.Game.GameInstance.GameInstanceFactory;
import GraphXings.Game.GameInstance.PlanarGameInstanceFactory;
import GraphXings.Game.League.NewLeague;
import GraphXings.Game.League.NewLeagueResult;
import GraphXings.Game.Match.NewMatch;
import GraphXings.Game.Match.NewMatchResult;
import GraphXings.Gruppe10.LighthousePlayer;
import GraphXings.Gruppe5.Models.HeatMapFileReader;
import GraphXings.Gruppe5.Players.PointChoicePlayer;
import GraphXings.Gruppe5.Players.PointChoicePlayer;
import GraphXings.Gruppe5.PointStrategies.HeatMapChoiceStrategy;
import stuffUnused.Gruppe8.EfficientWinningPlayer;

public class GraphXings {
        public static void main(String[] args) throws FileNotFoundException {
                // Heatmaps
                var Max_HeatMap_1 = new HeatMapFileReader()
                                .readFromFile("./GraphXings/Gruppe5/PointStrategies/HeatMaps/HeatMap_Max/Max_HeatMap_1.txt");
                var Min_HeatMap_2 = new HeatMapFileReader()
                                .readFromFile("./GraphXings/Gruppe5/PointStrategies/HeatMaps/HeatMap_Min/Min_HeatMap_2.txt");
                var Max_HeatMap_3 = new HeatMapFileReader()
                                .readFromFile("./GraphXings/Gruppe5/PointStrategies/HeatMaps/HeatMap_Max/Max_HeatMap_3.txt");
                var Max_HeatMap_4 = new HeatMapFileReader()
                                .readFromFile("./GraphXings/Gruppe5/PointStrategies/HeatMaps/HeatMap_Max/Max_HeatMap_4.txt");
                var Min_HeatMap_5 = new HeatMapFileReader()
                                .readFromFile("./GraphXings/Gruppe5/PointStrategies/HeatMaps/HeatMap_Min/Min_HeatMap_5.txt");
                var Both_HeatMap_6 = new HeatMapFileReader()
                                .readFromFile("./GraphXings/Gruppe5/PointStrategies/HeatMaps/HeatMap_Max/Both_HeatMap_6.txt");
                var Max_Heatmap_7 = new HeatMapFileReader()
                                .readFromFile("./GraphXings/Gruppe5/PointStrategies/HeatMaps/HeatMap_Max/Max_Heatmap_7.txt");
                var Max_Heatmap_8 = new HeatMapFileReader()
                                .readFromFile("./GraphXings/Gruppe5/PointStrategies/HeatMaps/HeatMap_Max/Max_Heatmap_8.txt");
                var Min_Heatmap_9 = new HeatMapFileReader()
                                .readFromFile("./GraphXings/Gruppe5/PointStrategies/HeatMaps/HeatMap_Min/Min_Heatmap_9.txt");
                var Min_Heatmap_10 = new HeatMapFileReader()
                                .readFromFile("./GraphXings/Gruppe5/PointStrategies/HeatMaps/HeatMap_Min/Min_Heatmap_10.txt");
                var Max_Heatmap_11 = new HeatMapFileReader()
                                .readFromFile("./GraphXings/Gruppe5/PointStrategies/HeatMaps/HeatMap_Max/Max_Heatmap_11.txt");
                var Min_Heatmap_12 = new HeatMapFileReader()
                                .readFromFile("./GraphXings/Gruppe5/PointStrategies/HeatMaps/HeatMap_Min/Min_Heatmap_12.txt");
                var Min_Heatmap_13 = new HeatMapFileReader()
                                .readFromFile("./GraphXings/Gruppe5/PointStrategies/HeatMaps/HeatMap_Min/Min_Heatmap_13.txt");
                var Min_Heatmap_14 = new HeatMapFileReader()
                                .readFromFile("./GraphXings/Gruppe5/PointStrategies/HeatMaps/HeatMap_Min/Min_Heatmap_14.txt");
                var Max_Heatmap_15 = new HeatMapFileReader()
                                .readFromFile("./GraphXings/Gruppe5/PointStrategies/HeatMaps/HeatMap_Max/Max_Heatmap_15.txt");

                ArrayList<NewPlayer> players = new ArrayList<>();
                players.add(new PointChoicePlayer("Max Heatmap 3", new HeatMapChoiceStrategy(Min_HeatMap_5),
                                new HeatMapChoiceStrategy(Max_HeatMap_3)));
                players.add(new PointChoicePlayer("Max Heatmap 1", new HeatMapChoiceStrategy(Min_HeatMap_5),
                                new HeatMapChoiceStrategy(Max_HeatMap_1)));
                players.add(new PointChoicePlayer("Max Heatmap 7", new HeatMapChoiceStrategy(Min_HeatMap_5),
                                new HeatMapChoiceStrategy(Max_Heatmap_7)));
                players.add(new PointChoicePlayer("Max Heatmap 8", new HeatMapChoiceStrategy(Min_HeatMap_5),
                                new HeatMapChoiceStrategy(Max_Heatmap_8)));
                players.add(new PointChoicePlayer("Max Heatmap 15", new HeatMapChoiceStrategy(Min_HeatMap_5),
                                new HeatMapChoiceStrategy(Max_Heatmap_15)));
                // players.add(new EfficientWinningPlayer("Gruppe 8"));
                // players.add(new LighthousePlayer());
                // players.add(new RecursiveSearchPlayer("Recursive Search", 5, 100, 100,
                // 20000));
                long timeLimit = 300000000000l;
                long seed = 28061914;
                int bestOf = 3;
                NewMatch.MatchType matchType = NewMatch.MatchType.CROSSING_ANGLE;
                PlanarGameInstanceFactory factory = new PlanarGameInstanceFactory(seed);
                System.out.println("Generated game with " + factory.getGameInstance().getG().getN() + "x"
                                + factory.getGameInstance().getWidth() + "x"
                                + factory.getGameInstance().getHeight());
                runLeague(players, bestOf, timeLimit, factory, matchType, seed);
                // runRemainingMatches(player,players,bestOf,timeLimit,factory);
        }

        private static Graph inializeGraph(int NumNodes) {
                Vertex first = null;
                Vertex last = null;

                Graph g = new Graph();
                for (int i = 0; i < NumNodes; i++) {
                        Vertex v = new Vertex((i + 1) + "_Node");
                        g.addVertex(v);
                        if (i == 0) {
                                first = v;
                                last = v;
                        } else if (i == (NumNodes - 1)) {
                                Edge e = new Edge(v, first);
                                g.addEdge(e);
                        } else {
                                Edge e = new Edge(last, v);
                                g.addEdge(e);
                        }
                        last = v;
                }
                return g;
        }

        public static void runLeague(ArrayList<NewPlayer> players, int bestOf, long timeLimit,
                        GameInstanceFactory factory,
                        NewMatch.MatchType matchType, long seed) {
                NewLeague l = new NewLeague(players, bestOf, timeLimit, factory, matchType, seed);
                NewLeagueResult lr = l.runLeague();
                System.out.println(lr.announceResults());
        }

        public static void runRemainingMatches(NewPlayer p1, ArrayList<NewPlayer> opponents, int bestOf, long timeLimit,
                        GameInstanceFactory factory, NewMatch.MatchType matchType, long seed) {
                int i = 1;
                for (NewPlayer opponent : opponents) {
                        NewMatch m = new NewMatch(p1, opponent, factory, bestOf, timeLimit, matchType, seed);
                        NewMatchResult mr = m.play();
                        System.out.println("Match " + i++ + ": " + mr.announceResult());
                }
        }
}