package de.koware.gacc.parser.pdfParsing;

import org.apache.pdfbox.pdmodel.PDDocument;

public class PreprocessedDocument {

    private final PDDocument document;

    private String runningHead;
    private String footer;

    public PreprocessedDocument(PDDocument document) {
        this.document = document;
    }


    public PDDocument getDocument() {
        return document;
    }

    public int getNumberOfPages() {
        return this.document.getNumberOfPages();
    }
}
