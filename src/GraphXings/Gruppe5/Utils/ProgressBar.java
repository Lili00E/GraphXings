package GraphXings.Gruppe5.Utils;

public class ProgressBar {

    int lineWidth;
    char progressChar;

    public ProgressBar(int lineWidth, char progressChar) {
        this.lineWidth = lineWidth;
        this.progressChar = progressChar;
    }

    public ProgressBar() {
        this.lineWidth = 20;
        this.progressChar = '=';
    }

    public void printProgressDiscrete(int currentNum, int totalNum) {

        var progress = (float) (currentNum) / (float) totalNum;

        int numChars = Math.round((progress * (float) this.lineWidth));

        var output = "|";

        for (int i = 0; i < numChars; i++) {
            output += progressChar;
        }
        for (int i = 0; i < this.lineWidth - numChars; i++) {
            output += " ";
        }

        output += "| " + (currentNum) + "/" + totalNum + "\r";

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
