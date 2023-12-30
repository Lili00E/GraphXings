
package GraphXings.Gruppe5Algo.Utils;

public class VsBar {

  int lineWidth;
  char aChar;
  char bChar;

  public VsBar(int lineWidth, char aChar, char bChar) {
    this.lineWidth = lineWidth;
    this.aChar = aChar;
    this.bChar = bChar;
  }

  public void printProgressDiscrete(int aWins, int bWins, int totalNum) {

    var aProgress = (float) (aWins) / (float) totalNum;
    var bProgress = (float) (bWins) / (float) totalNum;

    int numAChars = Math.round((aProgress * (float) this.lineWidth));
    int numBChars = Math.round((bProgress * (float) this.lineWidth));
    int numSpaceChars = Math.round(((1 - (aProgress + bProgress)) * (float) this.lineWidth));

    var output = "|";

    for (int i = 0; i < numAChars; i++) {
      output += aChar;
    }
    for (int i = 0; i < numSpaceChars; i++) {
      output += " ";
    }
    for (int i = 0; i < numBChars; i++) {
      output += bChar;
    }

    output += "| " + (aWins + bWins) + "/" + totalNum + "\r";

    System.out.print(output);

  }

  public void clearOutput() {

    var output = "";

    for (int i = 0; i < this.lineWidth * 2; i++) {
      output += " ";
    }

    System.err.println(output);
  }

}
