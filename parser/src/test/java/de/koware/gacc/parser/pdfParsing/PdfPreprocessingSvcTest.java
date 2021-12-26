package de.koware.gacc.parser.pdfParsing;


import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import technology.tabula.*;
import technology.tabula.extractors.BasicExtractionAlgorithm;
import technology.tabula.extractors.SpreadsheetExtractionAlgorithm;

import java.io.File;
import java.io.IOException;
import java.util.List;

@SpringBootTest
public class PdfPreprocessingSvcTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(PdfPreprocessingSvcTest.class);


    // from https://stackoverflow.com/questions/52866639/how-can-tabula-jar-be-called-from-java
    @Test
    public void testTabulaSimple() throws IOException {
        final String FILENAME="src/test/resources/adidas-cropped.pdf";

        PDDocument pd = PDDocument.load(new File(FILENAME));

        int totalPages = pd.getNumberOfPages();
        System.out.println("Total Pages in Document: "+totalPages);

        ObjectExtractor oe = new ObjectExtractor(pd);


        SpreadsheetExtractionAlgorithm sea = new SpreadsheetExtractionAlgorithm();
        BasicExtractionAlgorithm bea = new BasicExtractionAlgorithm();

        Page page = oe.extract(4);

        // extract text from the table after detecting
        List<Table> table = bea.extract(page);
        for(Table tables: table) {
            System.out.println("Table: \n");

            List<List<RectangularTextContainer>> rows = tables.getRows();

            for (List<RectangularTextContainer> cells : rows) {

                for (RectangularTextContainer cell : cells) {
                    System.out.print(cell.getText() + " | " );
                }

                 System.out.println();
            }
        }

    }

}
