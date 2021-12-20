package de.koware.gacc.parser.ifrsParsing;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import technology.tabula.ObjectExtractor;
import technology.tabula.detectors.SpreadsheetDetectionAlgorithm;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
public class PdfPreprocessingSvc {
    private static final Logger LOGGER = LoggerFactory.getLogger(PdfPreprocessingSvc.class);


    public PreprocessedDocument extractText(PDDocument document) throws IOException {
        PDFTextStripper textStripper = new PDFTextStripper();

        LinkedHashMap<Integer, String> pageTexts = new LinkedHashMap<>(document.getNumberOfPages());

        ObjectExtractor oe = new ObjectExtractor(document);
        SpreadsheetDetectionAlgorithm sda = new SpreadsheetDetectionAlgorithm();


        // text stripper is 1-based
        for (int i = 1; i <= document.getPages().getCount(); i++) {
            LOGGER.info("processing page");

            textStripper.setStartPage(i);
            textStripper.setEndPage(i);

            String text = textStripper
                    .getText(document)
                    .toLowerCase()
                    .replaceAll("\u00AD", "-") // this removes some weird unicode dashes
                    .replaceAll("\n", " ");

            // document page index is 0-based
            //pageTexts.put(document.getPage(i - 1), text);
            pageTexts.put(i - 1, text);
        }

        return new PreprocessedDocument(pageTexts);
    }

    /*
        If a line exists on 90% of all pages then it si considered a static content
        (e.g.) a page running head or a footer.
        Not 100% because certain slides don't have them.
     */
    public PreprocessedDocument removeRunningHeadAndFooter(PreprocessedDocument document) {
        final double threshold = 0.9;


        return null;
    }


    public PDDocument getPdocFromFile(String filepath) {

        try {
            return PDDocument.load(new File(filepath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<PDDocument> pageSplit(PDDocument document) throws IOException {
        if (1 == document.getNumberOfPages()) {
            return Collections.singletonList(document);
        }

        ArrayList<PDDocument> pagesAsDocuments = new ArrayList<>(document.getNumberOfPages());

        document.getPages().forEach(
                page -> {
                    try {
                        pagesAsDocuments.add(PDDocument.load(
                                page.getContents()
                        ));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        );

        return pagesAsDocuments;
    }
}