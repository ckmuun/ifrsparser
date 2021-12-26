package de.koware.gacc.parser.ifrsParsing;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;
import technology.tabula.*;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.*;

import static de.koware.gacc.parser.Utils.comparingByValueDesc;


/*
    Proposal: Currently only one type per page is supported
    for horizontal-formatted two tables on one page, the method needs to be able to deal with extracting the same
    page two times.
 */

@Service
public class PdfTableParsingSvc {
    private static final Logger LOGGER = LoggerFactory.getLogger(PdfTableParsingSvc.class);


    public List<IfrsComponent> parseTablesFromPdf(Tuple2<PDDocument, HashMap<Integer, IfrsComponentType>> documentHashMapTuple2) {
        List<IfrsComponent> ifrsComponents = new ArrayList<>();

        HashMap<IfrsComponentType, List<Integer>> ifrsCompPages = invertHm(documentHashMapTuple2.getT2());

        TextStripper tabulaTs = null;
        try {

            for (Map.Entry<IfrsComponentType, List<Integer>> entry : ifrsCompPages.entrySet()) {
                List<String[][]> componentTables = new ArrayList<>();
                for (int i = 0; i <= entry.getValue().size() - 1; i++) {

                    tabulaTs = new TextStripper(documentHashMapTuple2.getT1(), entry.getValue().get(i));

                    tabulaTs.setAddMoreFormatting(true);
                    tabulaTs.process();

                    List<TextElement> textElements = tabulaTs.getTextElements();
                    LOGGER.info("number of text elements on page {}, is {}", entry.getValue().get(i), textElements.size());

                    Utils.sort(tabulaTs.getTextElements(), technology.tabula.Rectangle.ILL_DEFINED_ORDER);

                    List<List<LineChunk>> lineChunks = parseLineChunks(textElements);
                    String[][] table = parseTableFromLineChunks(lineChunks);
                    componentTables.add(table);
                }
                ifrsComponents.add(new IfrsComponent(entry.getKey(), componentTables));
            }

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return ifrsComponents;
    }

    private HashMap<IfrsComponentType, List<Integer>> invertHm(HashMap<Integer, IfrsComponentType> orgMapping) {
        HashMap<IfrsComponentType, List<Integer>> inverted = new HashMap<>();

        for (Map.Entry<Integer, IfrsComponentType> entry : orgMapping.entrySet()) {
            if (null == inverted.get(entry.getValue())) {
                List<Integer> pagenrs = new ArrayList<>();
                pagenrs.add(entry.getKey());
                inverted.put(entry.getValue(), pagenrs);
                continue;
            }

            List<Integer> pagenrs = inverted.get(entry.getValue());
            pagenrs.add(entry.getKey());
            inverted.put(entry.getValue(), pagenrs);
        }

        return inverted;
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

    private LinkedHashMap<Integer, Integer> concatLhmToNewLhm(LinkedHashMap<Integer, Integer> a, LinkedHashMap<Integer, Integer> b) {

        LinkedHashMap<Integer, Integer> concat = new LinkedHashMap<>();
        for (Map.Entry<Integer, Integer> entryOfA : a.entrySet()) {
            concat.put(entryOfA.getKey(), entryOfA.getValue());
        }
        for (Map.Entry<Integer, Integer> entryOfB : b.entrySet()) {
            int valueOfA = concat.getOrDefault(entryOfB.getKey(), 0);
            if (entryOfB.getValue() >= valueOfA + 1) {
                concat.put(entryOfB.getKey(), entryOfB.getValue());
            }
        }

        return concat;
    }


    public String[][] parseTableFromLineChunks(List<List<LineChunk>> lineChunks) {

        int maxChunksPerLine = 0;

        // step 0 get most chunks in a line
        for (List<LineChunk> line : lineChunks) {
            if (line.size() >= maxChunksPerLine) {
                maxChunksPerLine = line.size();
            }
        }

        // cartography
        LinkedHashMap<Integer, Integer> nrsLeft = new LinkedHashMap<>();
        LinkedHashMap<Integer, Integer> nrsRight = new LinkedHashMap<>();
        for (List<LineChunk> line : lineChunks) {


            for (LineChunk chunk : line) {

                int left = chunk.leftBound;
                int right = chunk.getRightBound();

                left += 10 - (left % 10);
                right += 10 - (right % 10);

                nrsLeft.put(left, nrsLeft.getOrDefault(
                        left, 0)
                        + 1
                );

                nrsRight.put(right, nrsRight.getOrDefault(
                        right, 0)
                        + 1
                );
            }
        }

        // detect column separations, then match with l/r list whether it's a left- or right-bound column
        LinkedHashMap<Integer, Integer> allNrs = concatLhmToNewLhm(
                nrsLeft,
                nrsRight
        );
        List<Integer> cols = new ArrayList<>(maxChunksPerLine);

        allNrs.entrySet().stream()
                .sorted(comparingByValueDesc())
                .limit(maxChunksPerLine)
                .forEach(entry -> cols.add(entry.getKey()));

        Collections.sort(cols);

        String[][] table = new String[lineChunks.size()][maxChunksPerLine];


        for (int i = 0; i <= lineChunks.size() - 1; i++) {
            String[] lineArr = new String[maxChunksPerLine];
            List<LineChunk> currentLine = lineChunks.get(i);

            // easy case, just add all chunks to the array
            if (currentLine.size() == maxChunksPerLine) {
                for (int y = 0; y <= maxChunksPerLine - 1; y++) {
                    lineArr[y] = currentLine.get(y).getText();
                    table[i] = lineArr;
                }
                continue;
            }

            // complex case
            for (LineChunk chunk : currentLine) {

                for (int k = 0; k <= cols.size() - 2; k++) {
                    int currentCol = cols.get(k);
                    int nextCol = cols.get(k + 1);

                    // leftmost
                    if (chunk.leftBound <= currentCol) {
                        lineArr[k] = chunk.getText();
                        break;
                    }

                    if (chunk.getRightBound() <= nextCol) {
                        lineArr[k + 1] = chunk.getText();
                        break;
                    }

                    if (chunk.getRightBound() >= nextCol && k == cols.size() - 2) {
                        lineArr[cols.size() - 1] = chunk.getText();
                    }
                }
            }


            // put line array into table matrix
            table[i] = lineArr;
        }

        return table;
    }


    /*
    #########################################################################


        DEPRECATED TRIAL CODE BELOW


    ##########################################################################
     */

    @Deprecated
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
            if (bounds.y != currentY) {
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

            currentLine.append(textElement.getText());
            currentX += bounds.width;

        }
        lines.add(currentLine.toString());
        return lines;
    }


    @Deprecated
    public List<List<TextElement>> creatTextElementLines(List<TextElement> textElements) {
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


    @Deprecated
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
    @Deprecated
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


}
