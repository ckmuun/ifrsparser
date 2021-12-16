package de.koware.gacc.parser.pdfParsing;

public class PdfPage implements PdfContent {

    private final String text;


    public PdfPage(String text) {
        this.text = text;
    }


    public String getText() {
        return text;
    }
}
