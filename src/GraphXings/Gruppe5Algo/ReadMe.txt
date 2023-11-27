Erstellung der Heatmap in GraphXings:
var maxHeatMap = new HeatMapFileReader()
                .readFromFile("./GraphXings/src/GraphXings/Gruppe5Algo/PointStrategies/HeatMaps/SimpleHeatMap.txt");
var minHeatMap = new HeatMapFileReader()
                .readFromFile("./GraphXings/src/GraphXings/Gruppe5Algo/PointStrategies/HeatMaps/UniformHeatMap.txt");


var myPlayer = new PointChoicePlayer("My Player", new HeatMapChoiceStrategy(minHeatMap, 10),
                new HeatMapChoiceStrategy(maxHeatMap, 10), 20000);