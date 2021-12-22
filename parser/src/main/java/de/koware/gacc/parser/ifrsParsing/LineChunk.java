package de.koware.gacc.parser.ifrsParsing;

public class LineChunk {

    final int leftBound;
    final int lowerBound;
    int width;

    private String text;

    public LineChunk(int leftBound, int lowerBound) {
        this.leftBound = leftBound;
        this.lowerBound = lowerBound;
        this.text = "";
        this.width = 0;
    }

    public void addText(String textToAdd, int widthOfText) {
        this.text = this.text.concat(textToAdd);
        this.width += widthOfText;
    }

    public String getText() {
        return this.text;
    }

    @Override
    public String toString() {
        return "Line Chunk: \n\tLeftBound: " + leftBound + "\n\tRightBound: " + (this.leftBound + width)
                + "\n\tLowerBound: " + this.lowerBound + "\n\t: Text: " + this.text;
    }
}
