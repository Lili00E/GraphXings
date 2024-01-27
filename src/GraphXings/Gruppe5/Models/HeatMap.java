package GraphXings.Gruppe5.Models;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import GraphXings.Data.Coordinate;
import GraphXings.Gruppe5.Utils.WeightedNumberGenerator;

public class HeatMap {

  private WeightedNumberGenerator generator;

  private int width;
  private int height;

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public HeatMap(WeightedNumberGenerator generator, int width, int height) {
    this.generator = generator;
    this.width = width;
    this.height = height;

    if (width == 0 || height == 0) {
      System.out.println("ERROR invalid HeatMap detected...");
    }

    if (this.width * this.height != this.generator.getWeights().length) {
      System.out.println("ERROR invalid HeatMap detected...");
    }
  }

  public Coordinate chooseWeightedCoord() {
    var index = generator.getNextNumber();
    return new Coordinate(index % width, index / width);
  }

  public void writeToFile(String filePath) {
    try {
      File file = new File(filePath);
      file.createNewFile();
      FileWriter writer = new FileWriter(filePath);

      String content = "";

      var weights = generator.getWeights();
      for (int i = 0; i < weights.length; i++) {
        content += weights[i];

        if ((i + 1) % width == 0) {
          content += "\n";
        } else {
          content += " ";
        }
      }

      writer.append(content);
      writer.close();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
