package de.koware.gacc.parser.ifrsParsing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;
import technology.tabula.*;
import technology.tabula.extractors.BasicExtractionAlgorithm;

import java.awt.Rectangle;
import java.io.Serializable;
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

    public LinkedHashMap<Integer, Integer> concatLhmToNewLhm(LinkedHashMap<Integer, Integer> a, LinkedHashMap<Integer, Integer> b) {

        LinkedHashMap<Integer, Integer> concat = new LinkedHashMap<>();
        for (Map.Entry<Integer, Integer> entryOfA : a.entrySet()) {
            concat.put(entryOfA.getKey(), entryOfA.getValue());
        }
        for (Map.Entry<Integer, Integer> entryOfB : b.entrySet()) {
            int valueOfA = concat.getOrDefault(entryOfB.getKey(), 0);
            if(entryOfB.getValue() >= valueOfA+1) {
                concat.put(entryOfB.getKey(), entryOfB.getValue());
            }
        }

        return concat;
    }

    public static <K, V extends Comparable<? super V>> Comparator<Map.Entry<K, V>> comparingByValueDesc() {
        return (Comparator<Map.Entry<K, V>> & Serializable)
                (c1, c2) -> c2.getValue().compareTo(c1.getValue());
    }

    public String[][] parseTableFromLineChunks(List<List<LineChunk>> lineChunks) {

        Pattern numerical = Pattern.compile("(-)?(\\[)?\\d+(])?([.,]?]\\d*)");

        int maxChunksPerLine = 0;


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

        // cartography
        List<List<Integer>> chunkLeftXPositions = new ArrayList<>();
        List<List<Integer>> chunkRightXPositions = new ArrayList<>();
        LinkedHashMap<Integer, Integer> nrsLeft = new LinkedHashMap<>();
        LinkedHashMap<Integer, Integer> nrsRight = new LinkedHashMap<>();
        for (List<LineChunk> line : lineChunks) {

            List<Integer> leftPositions = new ArrayList<>();
            List<Integer> rightPositions = new ArrayList<>();

            for (LineChunk chunk : line) {

                int left = chunk.leftBound;
                int right = chunk.getRightBound();

                left += 10 - (left % 10);
                right += 10 - (right % 10);

                leftPositions.add(left);


                nrsLeft.put(left, nrsLeft.getOrDefault(
                        left, 0)
                        + 1
                );

                nrsRight.put(right, nrsRight.getOrDefault(
                        right, 0)
                        + 1
                );

                rightPositions.add(right);
            }
            chunkLeftXPositions.add(leftPositions);
            chunkRightXPositions.add(rightPositions);
        }

        // detect column separations, then match with l/r list whether it's a left- or right-bound column
        List<Integer> leftXCols = new ArrayList<>();
        List<Integer> rightXcols = new ArrayList<>();

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

//        // analyze caches
//        nrsLeft.forEach((x, nr) -> {
//            if (nr >= 10) {
//                leftXCols.add(x);
//            }
//        });
//
//        nrsRight.forEach((x, nr) -> {
//            if (nr >= 10) {
//                rightXcols.add(x);
//            }
//        });
//        Collections.sort(leftXCols);
//        Collections.sort(rightXcols);
//
//        LOGGER.info("max chunks per line: {}", maxChunksPerLine);
//
//        // TODO unify leftX and rightX cols into one column list
//
//        //List<Integer> allXcols = new ArrayList<>(leftXCols.size() + rightXcols.size());
//        //allXcols.addAll(leftXCols);
//        //allXcols.addAll(rightXcols);
//        //Collections.sort(allXcols);

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

                for (int k = 0; k <= cols.size()-2; k++) {
                    int currentCol = cols.get(k);
                    int nextCol = cols.get(k+1);

                    // leftmost
                    if( chunk.leftBound <= currentCol) {
                        lineArr[k] = chunk.getText();
                        break;
                    }

                    if(chunk.getRightBound() <= nextCol ) {
                        lineArr[k+1] = chunk.getText();
                        break;
                    }

                    if(chunk.getRightBound() >= nextCol && k== cols.size()-2) {
                        lineArr[cols.size()-1] = chunk.getText();
                    }
                }
            }


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
