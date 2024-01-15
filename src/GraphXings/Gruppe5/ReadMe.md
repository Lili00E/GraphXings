Bitte PointChoicePlayer nehmen, RecursiveSearchPlayer ist noch nicht fertig!

Erstellung der Heatmap in GraphXings: (Pfad ggf. anpassen)
        var smallHeatMapMax = new HeatMapFileReader()
                .readFromFile("./src/GraphXings/Gruppe5Algo/PointStrategies/HeatMaps/SmallHeatMapMax.txt");
        var minHeatMap = new HeatMapFileReader()
                .readFromFile("./src/GraphXings/Gruppe5Algo/PointStrategies/HeatMaps/UniformHeatMap.txt");

        var myPlayer = new PointChoicePlayer("Gruppe 5", new HeatMapChoiceStrategy(minHeatMap),
                new HeatMapChoiceStrategy(smallHeatMapMax));