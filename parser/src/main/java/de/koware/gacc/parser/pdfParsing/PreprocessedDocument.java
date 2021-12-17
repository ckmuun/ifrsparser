package de.koware.gacc.parser.pdfParsing;


import org.apache.pdfbox.pdmodel.PDPage;

import java.util.LinkedHashMap;
import java.util.Map;

public class PreprocessedDocument {

    private final LinkedHashMap<PDPage, String> pageTexts;
    private String runningHead;
    private String footer;

    public PreprocessedDocument(LinkedHashMap<PDPage, String> pageTexts) {
        this.pageTexts = pageTexts;
    }


    public int getNumberOfPages() {
        return this.pageTexts.size();
    }

    public String getRunningHead() {
        return runningHead;
    }

    public String getFooter() {
        return footer;
    }


    public LinkedHashMap<PDPage, String> getPageTexts() {
        return pageTexts;
    }
}
