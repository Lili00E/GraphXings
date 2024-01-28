package GraphXings.Gruppe5.Run;

import GraphXings.Algorithms.NewPlayer;
import GraphXings.Game.GameInstance.GameInstanceFactory;
import GraphXings.Game.GameInstance.PlanarGameInstanceFactory;
import GraphXings.Game.GameInstance.RandomCycleFactory;
import GraphXings.Game.Match.NewMatch;
import GraphXings.GraphXings;
import GraphXings.Gruppe10.LighthousePlayer;
import GraphXings.Gruppe5.Models.HeatMap;
import GraphXings.Gruppe5.Models.HeatMapFileReader;
import GraphXings.Gruppe5.Players.PointChoicePlayer;
import GraphXings.Gruppe5.PointStrategies.HeatMapChoiceStrategy;
import GraphXings.Gruppe8.EfficientWinningPlayer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.FileHandler;

import static GraphXings.GraphXings.runLeague;

public class RunTest {
    public static void main(String[] args) throws IOException {
        long timeLimit = 300000000000l;
        long seed = 27081884;
        int bestOf = 3;
        java.util.logging.Logger logger = java.util.logging.Logger.getLogger("TestRun");
        FileHandler fileHandler = new FileHandler("status.log");
        logger.addHandler(fileHandler);
        logger.setUseParentHandlers(false);
        logger.info("seed: " + seed);

        // Heatmaps
        HashMap<Integer, HeatMap> minMaps = new HashMap<>();
        HashMap<Integer, HeatMap> maxMaps = new HashMap<>();

        var Max_HeatMap_1 = new HeatMapFileReader()
                .readFromFile("./GraphXings/Gruppe5/PointStrategies/HeatMaps/Max_HeatMap_1.txt");
        var Min_HeatMap_2 = new HeatMapFileReader()
                .readFromFile("./GraphXings/Gruppe5/PointStrategies/HeatMaps/Min_HeatMap_2.txt");
        var Max_HeatMap_3 = new HeatMapFileReader()
                .readFromFile("./GraphXings/Gruppe5/PointStrategies/HeatMaps/Max_HeatMap_3.txt");
        var Max_HeatMap_4 = new HeatMapFileReader()
                .readFromFile("./GraphXings/Gruppe5/PointStrategies/HeatMaps/Max_HeatMap_4.txt");
        var Min_HeatMap_5 = new HeatMapFileReader()
                .readFromFile("./GraphXings/Gruppe5/PointStrategies/HeatMaps/Min_HeatMap_5.txt");
        var Both_HeatMap_6 = new HeatMapFileReader()
                .readFromFile("./GraphXings/Gruppe5/PointStrategies/HeatMaps/Both_HeatMap_6.txt");
        var Max_Heatmap_7 = new HeatMapFileReader()
                .readFromFile("./GraphXings/Gruppe5/PointStrategies/HeatMaps/Max_Heatmap_7.txt");
        var Max_Heatmap_8 = new HeatMapFileReader()
                .readFromFile("./GraphXings/Gruppe5/PointStrategies/HeatMaps/Max_Heatmap_8.txt");
        var Min_Heatmap_9 = new HeatMapFileReader()
                .readFromFile("./GraphXings/Gruppe5/PointStrategies/HeatMaps/Min_Heatmap_9.txt");
        var Min_Heatmap_10 = new HeatMapFileReader()
                .readFromFile("./GraphXings/Gruppe5/PointStrategies/HeatMaps/Min_Heatmap_10.txt");
        var Max_Heatmap_11 = new HeatMapFileReader()
                .readFromFile("./GraphXings/Gruppe5/PointStrategies/HeatMaps/Max_Heatmap_11.txt");
        var Min_Heatmap_12 = new HeatMapFileReader()
                .readFromFile("./GraphXings/Gruppe5/PointStrategies/HeatMaps/Min_Heatmap_12.txt");
        var Min_Heatmap_13 = new HeatMapFileReader()
                .readFromFile("./GraphXings/Gruppe5/PointStrategies/HeatMaps/Min_Heatmap_13.txt");
        var Min_Heatmap_14 = new HeatMapFileReader()
                .readFromFile("./GraphXings/Gruppe5/PointStrategies/HeatMaps/Min_Heatmap_14.txt");
        var Max_Heatmap_15 = new HeatMapFileReader()
                .readFromFile("./GraphXings/Gruppe5/PointStrategies/HeatMaps/Max_Heatmap_15.txt");

        minMaps.put(2, Min_HeatMap_2);
        minMaps.put(5, Min_HeatMap_5);
        minMaps.put(6, Both_HeatMap_6);
        minMaps.put(9, Min_Heatmap_9);
        minMaps.put(10, Min_Heatmap_10);
        minMaps.put(12, Min_Heatmap_12);
        minMaps.put(13, Min_Heatmap_13);
        minMaps.put(14, Min_Heatmap_14);

        maxMaps.put(3, Max_HeatMap_3);
        maxMaps.put(1, Max_HeatMap_1);
        maxMaps.put(4, Max_HeatMap_4);
        maxMaps.put(7, Max_Heatmap_7);
        maxMaps.put(8, Max_Heatmap_8);
        maxMaps.put(11, Max_Heatmap_11);
        maxMaps.put(15, Max_Heatmap_15);
        maxMaps.put(6, Both_HeatMap_6);

        // other Players
        ArrayList<NewPlayer> gameGruppe8 = new ArrayList<>();
        gameGruppe8.add(0, new EfficientWinningPlayer("Gruppe 8"));

        ArrayList<NewPlayer> gameGruppe10 = new ArrayList<>();
        gameGruppe10.add(0, new LighthousePlayer());

        ArrayList<ArrayList<NewPlayer>> listOfCompetitors = new ArrayList<>();
        listOfCompetitors.add(gameGruppe8);
        listOfCompetitors.add(gameGruppe10);

        // Match Types
        ArrayList<NewMatch.MatchType> matchTypes = new ArrayList<>();

        NewMatch.MatchType angles = NewMatch.MatchType.CROSSING_ANGLE;
        NewMatch.MatchType crossings = NewMatch.MatchType.CROSSING_NUMBER;

        matchTypes.add(angles);
        matchTypes.add(crossings);

        // Factories
        HashMap<String, GameInstanceFactory> factories = new HashMap<>();

        PlanarGameInstanceFactory planarTriangulation = new PlanarGameInstanceFactory(seed);
        RandomCycleFactory cycleNoMatching = new RandomCycleFactory(seed, false);
        RandomCycleFactory cycleMatching = new RandomCycleFactory(seed, true);

        factories.put("planarTriangulation", planarTriangulation);
        factories.put("cycleMatching", cycleMatching);
        factories.put("cycleNoMatching", cycleNoMatching);

        // Running Games
        for (ArrayList<NewPlayer> players : listOfCompetitors) {
            System.out.println("###################################################");
            System.out.println("Competitor: " + players.get(0).getName());
            logger.info("Competitor: " + players.get(0).getName());
            System.out.println("###################################################");

            for (int min : minMaps.keySet()) {
                System.out.println("Min Heatmap " + min);
                logger.info("Min: " + min);
                for (int max: maxMaps.keySet()){
                    System.out.println("Max Heatmap " + max);
                    logger.info("Max: " + max);
                    players.add(1, new PointChoicePlayer("Our Player", new HeatMapChoiceStrategy(minMaps.get(min)),
                            new HeatMapChoiceStrategy(maxMaps.get(max))));

                    for (String factory : factories.keySet()) {
                        System.out.println("Current Factory: " + factory);
                        System.out.println("Game with " + factories.get(factory).getGameInstance().getG().getN() + "x" + factories.get(factory).getGameInstance().getWidth() + "x"
                                + factories.get(factory).getGameInstance().getHeight());
                        logger.info(factory);
                        logger.info(factories.get(factory).getGameInstance().getG().getN() + "x" + factories.get(factory).getGameInstance().getWidth() + "x"
                                + factories.get(factory).getGameInstance().getHeight());

                        for (NewMatch.MatchType matchType : matchTypes) {
                            System.out.println("MatchType: " + matchType.name());
                            logger.info(matchType.name());
                            System.out.println(" ");
                            runLeague(players, bestOf, timeLimit, factories.get(factory), matchType, seed);
                        }
                    }
                }
                players.remove(1);
            }
            System.out.println("###################################################");
        }
    }

}
