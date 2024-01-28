package GraphXings.Gruppe5.Utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class CSVWriter {
  private ArrayList<String> colNames;
  private ArrayList<String[]> data;

  public CSVWriter(ArrayList<String> colNames) {
    this.colNames = colNames;
    this.data = new ArrayList<>();
  }

  public void write(String[] row) {

    assert row.length == colNames.size();
    data.add(row);
  }

  public void append(ArrayList<String> row) {
    assert row.size() == colNames.size();
    data.add(row.toArray(new String[0]));
  }

  public void delteRow(int index) {
    data.remove(index);
  }

  public void deleteCol(int index) {
    for (String[] row : data) {
      String[] newRow = new String[row.length - 1];
      for (int i = 0; i < row.length; i++) {
        if (i < index) {
          newRow[i] = row[i];
        } else if (i > index) {
          newRow[i - 1] = row[i];
        }
      }
      row = newRow;
    }
  }

  public void deleteCol(String colName) {
    int index = colNames.indexOf(colName);
    deleteCol(index);
  }

  public void addColumn(String colName, ArrayList<String> data) {
    assert data.size() == this.data.size();
    colNames.add(colName);
    for (int i = 0; i < data.size(); i++) {
      this.data.get(i)[colNames.size() - 1] = data.get(i);
    }
  }

  public void writeToFile(String path) throws IOException {
    var file = new File(path);
    file.createNewFile();
    var writer = new FileWriter(path);
    writer.append(String.join(";", colNames) + "\n");
    for (String[] row : data) {
      writer.append(String.join(";", row) + "\n");
    }
    writer.close();
  }

}
