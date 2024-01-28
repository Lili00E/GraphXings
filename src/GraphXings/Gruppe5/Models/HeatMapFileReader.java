package GraphXings.Gruppe5.Models;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import GraphXings.Gruppe5.Utils.WeightedNumberGenerator;

public class HeatMapFileReader {

    public ArrayList<HeatMap> readAllFromDir(String dirPath) throws RuntimeException {
        File heatMapDir = new File(dirPath);

        File[] files = heatMapDir.listFiles();

        var heatMaps = new ArrayList<HeatMap>();
        if (files != null) {
            for (File file : files) {
                try {

                    heatMaps.add(new HeatMapFileReader().readFromFile(file.getPath()));
                } catch (FileNotFoundException e) {
                    throw new RuntimeException("Error reading heatmap file");
                }
            }
        } else {

            throw new RuntimeException("Error Reading Dir");
        }
        return heatMaps;
    }

    public HeatMap readFromFile(String fileName) throws FileNotFoundException {
        final double epsilon = 0.01;
        try {
            File file = new File(fileName);
            Scanner scanner = new Scanner(file);
            var weightList = new ArrayList<Double>();
            int width = 0, height = 0;
            double sum = 0;

            while (scanner.hasNextLine()) {
                String data = scanner.nextLine();
                var numbers = data.split(" ");
                for (String numString : numbers) {
                    var num = Double.parseDouble(numString);
                    if (num == 0) {
                        num = epsilon;
                    }
                    weightList.add(num);
                    sum += num;
                }
                if (numbers.length > 0)
                    width = numbers.length;
                height++;
            }
            double[] weights = new double[width * height];
            for (int i = 0; i < weightList.size(); i++) {
                weights[i] = weightList.get(i) / sum;

            }
            scanner.close();
            return new HeatMap(new WeightedNumberGenerator(weights), width, height, fileName);

        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
            throw new FileNotFoundException("The Heatmap you loaded, doesn't exist under " + fileName);
        }
    }

}
