package GraphXings.Gruppe5.Utils;

import java.util.ArrayList;

public class CSVWriter {
  private ArrayList<String> colNames;
  private ArrayList<String[]> data;

  public CSVWriter(ArrayList<String> colNames) {
    this.colNames = colNames;
  }

  public void append(String[] row) {

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

  public void writeToFile(String path) {
  }

}
