package de.koware.gacc.parser.ifrsParsing;

import org.apache.pdfbox.pdmodel.PDPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;
import technology.tabula.*;
import technology.tabula.extractors.BasicExtractionAlgorithm;

import java.awt.Rectangle;
import java.util.*;
import java.util.regex.Pattern;


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

    public List<Integer> detectColums(List<TextElement> textElements) {

        List<Integer> colums = new ArrayList<>();

        for (TextElement textElement : textElements) {

        }
        return colums;
    }


    public List<List<TextElement>> creatTextElementLInes(List<TextElement> textElements) {
        List<List<TextElement>> lines = new ArrayList<>();

        int currentY = 0;
        List<TextElement> currentLine = new ArrayList<>();
        for (TextElement textElement : textElements) {
            Rectangle bounds = textElement.getBounds();

            // track new line, add and reset if y changes
            if (bounds.y != currentY || currentY == 0) {
                currentY = bounds.y;
                lines.add(currentLine);
                currentLine = new ArrayList<>();
            }
            currentLine.add(textElement);
        }
        lines.remove(0);
        lines.add(currentLine);
        return lines;
    }


    public List<List<Tuple2<Float, Float>>> getXPositionsOfTextElements(List<List<TextElement>> textElements) {
        List<List<Tuple2<Float, Float>>> xPositions = new ArrayList<>();
        List<Tuple2<Float, Float>> currentLineXPos = new ArrayList<>();

        // iterate over whole document
        for (List<TextElement> lineElements : textElements) {
            int currentX = 0;

            // iterate over one line
            for (TextElement element : lineElements) {

                int distance = element.getBounds().x - currentX;
                if (distance >= 1) {
                    currentLineXPos.add(
                            Tuples.of(element.getLeft(), element.getRight())
                    );
                }
                currentX = Math.round(element.getRight());
            }
            xPositions.add(currentLineXPos);
            currentLineXPos = new ArrayList<>();
        }

        return xPositions;
    }

    // Tuple contains left and right positions of a text element
    public Tuple2<List<Integer>, List<Integer>> getColumnSeparationPositions(List<List<Tuple2<Float, Float>>> xPositions, int sensitivity) {
        List<Integer> leftBoundCols = new ArrayList<>();
        List<Integer> rightBoundCols = new ArrayList<>();


        // save how many lines contain a textelement with that float bound
        HashMap<Integer, Integer> leftBoundCache = new HashMap<>();
        HashMap<Integer, Integer> rightBoundCache = new HashMap<>();

        // fill caches
        for (List<Tuple2<Float, Float>> lineXPositions : xPositions) {

            for (Tuple2<Float, Float> lrPositions : lineXPositions) {
                int left = Math.round(lrPositions.getT1());
                int right = Math.round(lrPositions.getT2());

                // check how often it has already been recorded
                int nrLeft = leftBoundCache.getOrDefault(left, 0);
                nrLeft++;
                int nrRight = rightBoundCache.getOrDefault(right, 0);
                nrRight++;

                // update the value and put it into the cache
                leftBoundCache.put(left, nrLeft);
                rightBoundCache.put(right, nrRight);
            }
        }
        // analyze caches
        leftBoundCache.forEach((x, nr) -> {
            if (nr >= sensitivity) {
                leftBoundCols.add(x);
            }
        });

        rightBoundCache.forEach((x, nr) -> {
            if (nr >= sensitivity) {
                rightBoundCols.add(x);
            }
        });

        return Tuples.of(leftBoundCols, rightBoundCols);
    }

    public List<List<LineChunk>> parseLineChunks(List<TextElement> textElements) {
        int currentY = 0;
        int currentX = 0;

        List<List<LineChunk>> lines = new ArrayList<>();

        List<LineChunk> currentLine = new ArrayList<>();
        LineChunk currentLineChunk = new LineChunk(0, 0);

        for (TextElement textElement : textElements) {
            Rectangle bounds = textElement.getBounds();

            // track new line, add and reset if y changes
            if (bounds.y != currentY || currentY == 0) {
                currentY = bounds.y;
                currentLine.add(currentLineChunk);
                lines.add(currentLine);
                currentLine = new ArrayList<>();
                currentLineChunk = new LineChunk(0, 0);
                currentX = 0;
            }
            LOGGER.info("current X: {}", currentX);
            LOGGER.info("bounds X: {}", bounds.x);
            int distance = bounds.x - currentX;
            LOGGER.info("x distance: {}", distance);
            if (distance >= 1) {
                LOGGER.info("creating new line chunk");
                if (!currentLineChunk.getText().equals("")) {
                    currentLine.add(currentLineChunk);
                }
                currentX = currentLineChunk.leftBound + currentLineChunk.width;
                currentLineChunk = new LineChunk(bounds.x, bounds.y);
            }

            currentLineChunk.addText(textElement.getText(), bounds.width);
            currentX += bounds.width;
            currentX += distance;
        }
        return lines;
    }

    public String[][] parseTableFromLineChunks(List<List<LineChunk>> lineChunks) {

        Pattern numerical = Pattern.compile("(-)?(\\[)?\\d+(])?([.,]?]\\d*)");

        int maxChunksPerLine = 0;

        List<Integer[]> chunkPositions = new ArrayList<>();

        // step 0 get most chunks in a line
        for (List<LineChunk> line : lineChunks) {
            if (line.size() >= maxChunksPerLine) {
                maxChunksPerLine = line.size();
//                int[] brackets = new int[maxChunksPerLine];
//                for(int i =1; i< line.size(); i++) {
//
//                    LineChunk currentChunk = line.get(i);
//                    LineChunk previousChunk = line.get(i-1);
//                    // differ right and left-alignged
//                    if(Pattern.matches(numerical.pattern(), currentChunk.getText())) {
//                        LOGGER.info("right-aligned numerical column");
//                    }
//
//
//                    brackets[i-1] = line.get(i).leftBound;
//                }
            }
        }
        LOGGER.info("max chunks per line: {}", maxChunksPerLine);

        String[][] table = new String[lineChunks.size()][maxChunksPerLine];


        for (int i = 0; i <= lineChunks.size() - 1; i++) {
            String[] lineArr = new String[maxChunksPerLine];
            List<LineChunk> currentLine = lineChunks.get(i);


            // easy case, just add all chunks to the array
            if(currentLine.size() == maxChunksPerLine) {
                for(int y = 0; y <= maxChunksPerLine-1; y++) {
                    lineArr[y] = currentLine.get(y).getText();
                }
                continue;
            }

            // complex case




            // put line array into table matrix
            table[i] = lineArr;
        }

        return table;
    }

    public List<String> parseTableLines(List<TextElement> textElements) {
        LOGGER.info("parsing tables");
        List<String> lines = new ArrayList<>();

        int currentY = 0;
        int currentX = 0;
        StringBuilder currentLine = new StringBuilder();


        for (TextElement textElement : textElements) {
            Rectangle bounds = textElement.getBounds();

            if (bounds.y <= 50) {
                continue;
            }

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
            if (distance >= 1) {
                currentLine.append('|');
            }

            while (distance >= 1) {
                LOGGER.info("appending padding space");
                currentLine.append(' ');
                currentX += 5;
                distance -= 5;
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
        lines.add(currentLine.toString());
        return lines;
    }


}
