package de.koware.gacc.parser.ifrsParsing;

import org.apache.pdfbox.pdmodel.PDPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import technology.tabula.*;
import technology.tabula.extractors.BasicExtractionAlgorithm;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/*
    TODO Currently only one type per page is supported
    for horizontal-formatted two tables on one page, the method needs to be able to deal with extracting the same
    page two times.
 */

@Service
public class PdfTableParsingSvc {
    private static final Logger LOGGER = LoggerFactory.getLogger(PdfTableParsingSvc.class);


    public IfrsPdfDocument process(PreprocessedDocument preprocessedDocument) {

        BasicExtractionAlgorithm bea = new BasicExtractionAlgorithm();
        ObjectExtractor oe = new ObjectExtractor(preprocessedDocument.getCroppedPdf());


        final HashMap<IfrsComponentType, List<Table>> ifrsTables = new HashMap<>();

        for (Map.Entry<IfrsComponentType, List<Integer>> ifrsCompsPageIndices : preprocessedDocument.getPageIndicesForIfrsComp().entrySet()) {
            IfrsComponentType type = ifrsCompsPageIndices.getKey();

            List<Table> tablesForIfrsComp = new ArrayList<>();
            for (int pageindex : ifrsCompsPageIndices.getValue()) {

                Page page = oe.extract(pageindex);
                tablesForIfrsComp.addAll(bea.extract(page));
            }

            for (Table tables : tablesForIfrsComp) {
                LOGGER.info("Table with {} rows and {} columns:", tables.getRowCount(), tables.getColCount());

                List<List<RectangularTextContainer>> rows = tables.getRows();
                String[][] table = new String[tables.getRowCount()][tables.getColCount()];

                for (List<RectangularTextContainer> cells : rows) {

                    for (RectangularTextContainer cell : cells) {
                        System.out.print(cell.getText() + " | ");
                    }

                    System.out.println();
                }
            }

            ifrsTables.put(type, tablesForIfrsComp);
            System.out.println("########################################################################");
        }

        return new IfrsPdfDocument();
    }

    public List<String> parseTable(List<TextElement> textElements) {
        LOGGER.info("parsing tables");
        List<String> lines = new ArrayList<>();

        int currentY = 0;
        int currentX = 0;
        StringBuilder currentLine = new StringBuilder();
        for (TextElement textElement : textElements) {
            Rectangle bounds = textElement.getBounds();

            // track new line, add and reset if y changes
            if (bounds.y != currentY || currentY == 0) {
                currentY = bounds.y;
                lines.add(currentLine.toString());
                currentLine = new StringBuilder();
                currentX = 0;
            }


            int distance = bounds.x - currentX;
            LOGGER.info("current X : {} in line {}", currentX, currentLine.toString());
            LOGGER.info("Bound of next x: {}", bounds.x);
            LOGGER.info("x distance: {}", distance);
            while (distance >= 0) {
                LOGGER.info("appending padding space");
                currentLine.append(' ');
                currentX += 5;
                distance -=5;
            }

//            int temp = currentX;
//            while (currentX < temp + bounds.width) {
//                currentLine.append(' ');
//                currentX += 5;
//            }
//            temp = 0;

            currentLine.append(textElement.getText());
            currentX += bounds.width;


        }
        return lines;
    }


}
