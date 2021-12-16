package de.koware.gacc.parser.pdfParsing;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class PdfParserSvc {


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
