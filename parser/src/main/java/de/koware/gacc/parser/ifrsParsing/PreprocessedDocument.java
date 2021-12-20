package de.koware.gacc.parser.ifrsParsing;


import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class PreprocessedDocument {

    private LinkedHashMap<Integer, String> pageTexts;
    private List<Integer> chapterSlidesPageIndices;
    private String runningHead;
    private String footer;
    private PDPage[] pages;
    private PDDocument originalPdf;

    private HashMap<IfrsComponentType, List<Integer>> pageIndicesForIfrsComp;

    private PDDocument croppedPdf;


    public PreprocessedDocument() {
    }

    public PreprocessedDocument(LinkedHashMap<Integer, String> pageTexts) {
        this.pageTexts = pageTexts;
    }


    public HashMap<IfrsComponentType, List<Integer>> getPageIndicesForIfrsComp() {
        return pageIndicesForIfrsComp;
    }

    public void setPageIndicesForIfrsComp(HashMap<IfrsComponentType, List<Integer>> pageIndicesForIfrsComp) {
        this.pageIndicesForIfrsComp = pageIndicesForIfrsComp;
    }

    public String getRunningHead() {
        return runningHead;
    }

    public String getFooter() {
        return footer;
    }

    public LinkedHashMap<Integer, String> getPageTexts() {
        return pageTexts;
    }

    public List<Integer> getChapterSlidesPageIndices() {
        return chapterSlidesPageIndices;
    }

    public PDDocument getOriginalPdf() {
        return originalPdf;
    }

    public PDPage[] getPages() {

        return this.pages;
    }

    public PDDocument getCroppedPdf() {
        return croppedPdf;
    }

    public void setPageTexts(LinkedHashMap<Integer, String> pageTexts) {
        this.pageTexts = pageTexts;
    }

    public void setChapterSlidesPageIndices(List<Integer> chapterSlidesPageIndices) {
        this.chapterSlidesPageIndices = chapterSlidesPageIndices;
    }

    public void setRunningHead(String runningHead) {
        this.runningHead = runningHead;
    }

    public void setFooter(String footer) {
        this.footer = footer;
    }

    public void setPages(PDPage[] pages) {
        this.pages = pages;
    }

    public void setOriginalPdf(PDDocument originalPdf) {
        this.originalPdf = originalPdf;
    }

    public void setCroppedPdf(PDDocument croppedPdf) {
        this.croppedPdf = croppedPdf;
    }
}
