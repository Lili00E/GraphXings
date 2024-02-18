package GraphXings.Gruppe5.Run;

import GraphXings.Algorithms.NewPlayer;
import GraphXings.Game.GameInstance.GameInstanceFactory;
import GraphXings.Game.GameInstance.PlanarGameInstanceFactory;
import GraphXings.Game.GameInstance.RandomCycleFactory;
import GraphXings.Game.League.NewLeague;
import GraphXings.Game.League.NewLeagueResult;
import GraphXings.Game.Match.NewMatch;
import GraphXings.Gruppe10.LighthousePlayer;
import GraphXings.GraphXings;
import GraphXings.Gruppe5.Models.HeatMap;
import GraphXings.Gruppe5.Models.HeatMapFileReader;
import GraphXings.Gruppe5.Players.PointChoicePlayer;
import GraphXings.Gruppe5.PointStrategies.HeatMapChoiceStrategy;
import GraphXings.Gruppe5.Utils.CSVWriter;
import stuffUnused.Gruppe8.EfficientWinningPlayer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.HashMap;
import java.util.logging.FileHandler;

import static GraphXings.GraphXings.runLeague;

import java.io.File;

public class RunTest {

  public static void main(String[] args) throws IOException {
    long timeLimit = 300000000000l;
    long seed = 2571;
    int bestOf = 3;
    java.util.logging.Logger logger = java.util.logging.Logger.getLogger("TestRun");
    FileHandler fileHandler = new FileHandler("status.log");
    logger.addHandler(fileHandler);

    var csvWriter = new CSVWriter(new ArrayList<String>() {
      {
        add("our_player_name");
        add("other_player_name");
        add("graph_width");
        add("graph_height");
        add("num_nodes");
        add("factory");
        add("match_type");
        add("final_score"); // ex: "2:1" my player first
      }
    });

    var reader = new HeatMapFileReader();
    var minHeatMaps = reader.readAllFromDir("./src/GraphXings/Gruppe5/PointStrategies/HeatMaps/HeatMap_Min");
    var maxHeatMaps = reader.readAllFromDir("./src/GraphXings/Gruppe5/PointStrategies/HeatMaps/HeatMap_Max");

    var numHeatMaps = Math.max(minHeatMaps.size(), maxHeatMaps.size());
    ArrayList<NewPlayer> ourPlayers = new ArrayList<>();
    for (var i = 0; i < numHeatMaps; i++) {
      var minHeatMap = minHeatMaps.get(Math.min(minHeatMaps.size() - 1, i));
      var maxHeatMap = maxHeatMaps.get(Math.min(maxHeatMaps.size() - 1, i));
      var player = new PointChoicePlayer(minHeatMap.name + ":" + maxHeatMap.name, new HeatMapChoiceStrategy(minHeatMap),
          new HeatMapChoiceStrategy(maxHeatMap));
      ourPlayers.add(player);
    }

    // other Players
    ArrayList<NewPlayer> gameGruppe8 = new ArrayList<>();
    gameGruppe8.add(0, new EfficientWinningPlayer("Gruppe 8"));

    // ArrayList<NewPlayer> gameGruppe10 = new ArrayList<>();
    // gameGruppe10.add(0, new LighthousePlayer());

    ArrayList<ArrayList<NewPlayer>> competitorGroups = new ArrayList<>();
    competitorGroups.add(gameGruppe8);
    // competitorGroups.add(gameGruppe10);

    // Match Types
    ArrayList<NewMatch.MatchType> matchTypes = new ArrayList<>();

    NewMatch.MatchType angles = NewMatch.MatchType.CROSSING_ANGLE;
    NewMatch.MatchType crossings = NewMatch.MatchType.CROSSING_NUMBER;

    TimeZone tz = TimeZone.getTimeZone("UTC");
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
    df.setTimeZone(tz);
    String nowAsISO = df.format(new Date());
    matchTypes.add(angles);
    matchTypes.add(crossings);

    HashMap<String, GameInstanceFactory> factories = new HashMap<>();

    PlanarGameInstanceFactory planarTriangulation = new PlanarGameInstanceFactory(seed);
    RandomCycleFactory cycleNoMatching = new RandomCycleFactory(seed, false);
    RandomCycleFactory cycleMatching = new RandomCycleFactory(seed, true);
    factories.put("planarTriangulation", planarTriangulation);
    factories.put("cycleMatching", cycleMatching);
    factories.put("cycleNoMatching", cycleNoMatching);

    for (ArrayList<NewPlayer> competitorGroup : competitorGroups) {
      for (NewPlayer competitorPlayer : competitorGroup) {
        System.out.println("Competitor: " + competitorPlayer.getName());

        for (NewPlayer ourPlayer : ourPlayers) {

          for (String factory : factories.keySet()) {
            System.out.println("Current Factory: " + factory);
            var game = factories.get(factory).getGameInstance();

            for (NewMatch.MatchType matchType : matchTypes) {
              System.out.println("MatchType: " + matchType.name());
              System.out.println(" ");
              System.out.println(game.getG().getN() + " nodes");
              var players = new ArrayList<NewPlayer>();
              players.add(ourPlayer);
              players.add(competitorPlayer);
              var factoryInstance = factories.get(factory);
              NewLeague l = new NewLeague(players, bestOf, timeLimit, factoryInstance, matchType, seed);
              NewLeagueResult lr = l.runLeague();

              String[] row = {
                  ourPlayer.getName(),
                  competitorPlayer.getName(),
                  String.valueOf(game.getWidth()),
                  String.valueOf(game.getHeight()),
                  String.valueOf(game.getG().getN()),
                  factory,
                  matchType.name(),
                  lr.getScore(ourPlayer) + ":" + lr.getScore(competitorPlayer),
              };
              csvWriter.write(row);
            }
          }
        }
      }
    }
    csvWriter.writeToFile("./out_" + nowAsISO + ".csv");
  }

}
