package de.koware.gacc.parser.ifrsParsing;

import de.koware.gacc.parser.pdfParsing.PreprocessedDocument;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuples;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class IfrsStatementsParserSvc {

    private static final Logger LOGGER = LoggerFactory.getLogger(IfrsStatementsParserSvc.class);

    // document pruning method
    public PDDocument extractIfrsRelevantPages(PDDocument raw) throws IOException {

        EnumMap<IfrsComponentType, Pattern[]> map = IfrsParsingConstants.ifrsComponentsRegexes();

        PDFTextStripper textStripper = new PDFTextStripper();

        // page nr + Tuple3 with most-mentioned type, no. of mentions for type, difference to next-often mentioned
        LinkedHashMap<Integer, Tuple3<IfrsComponentType, Integer, Integer>> tfIdfReport = new LinkedHashMap<>();

        for (int i = 1; i < raw.getNumberOfPages(); i++) {
            LOGGER.info("processing page no: {}", i);
            textStripper.setStartPage(i);
            textStripper.setEndPage(i);

            String pageText = textStripper.getText(raw).toLowerCase();

            HashMap<IfrsComponentType, Integer> detectedComponents = new HashMap<>();
            detectedComponents.put(IfrsComponentType.BALANCE_SHEET, 0);
            detectedComponents.put(IfrsComponentType.OTHER_COMPREHENSIVE_INCOME, 0);
            detectedComponents.put(IfrsComponentType.CASHFLOW_STATEMENT, 0);
            detectedComponents.put(IfrsComponentType.EQUITY_CHANGES_STATEMENT, 0);
            detectedComponents.put(IfrsComponentType.PROFIT_AND_LOSS_STATEMENT, 0);


            // type detection iteration, determine how often a term is found on a page
            for (Map.Entry<IfrsComponentType, Pattern[]> entry : map.entrySet()) {
                for (Pattern pattern : entry.getValue()) {
                    if (pattern.matcher(pageText).find()) {
                        LOGGER.info("found key: {}", entry.getKey());
                        detectedComponents.put(
                                entry.getKey(),
                                detectedComponents.get(entry.getKey()) + 1
                        );
                    }
                }
            }


            IfrsComponentType mostMentionedType = IfrsComponentType.NOP;
            int mostMentions = -1;
            int nextMostMentions = -1;


            for (Map.Entry<IfrsComponentType, Integer> entry : detectedComponents.entrySet()) {
                if (mostMentions <= entry.getValue()) {
                    mostMentionedType = entry.getKey();
                    nextMostMentions = mostMentions;

                    mostMentions = entry.getValue();
                    continue;
                }
                if (nextMostMentions <= entry.getValue()) {
                    nextMostMentions = entry.getValue();
                }
            }


            tfIdfReport.put(i - 1, Tuples.of(mostMentionedType, mostMentions, mostMentions - nextMostMentions));
        }

        return determineIfrsStatementsPageCluster(
                tfIdfReport,
                raw
        );
    }

    private PDDocument determineIfrsStatementsPageCluster(LinkedHashMap<Integer, Tuple3<IfrsComponentType, Integer, Integer>> tfidf, PDDocument raw) {
        PDDocument cropped = new PDDocument();


        return cropped;
    }


    private void getDigitTextCharThreshValue(int i, String pageText) {
        // second filter -- the ifrs component tables contain more digits than text
        int numberOfDigitChars = 0;
        int numberOfTextChars = 0;
        for (char c : pageText.toCharArray()) {
            if (Character.isDigit(c)) {
                numberOfDigitChars++;
                continue;
            }
            numberOfTextChars++;
        }
        LOGGER.info("number of text chars: {}", numberOfTextChars);
        LOGGER.info("number of digit chars: {}", numberOfDigitChars);

        if (numberOfDigitChars * 15 >= numberOfTextChars) {
            LOGGER.info("adding page no: {}", i);
        }
    }


    // elaborate method
    public IfrsPdfDocument annotateDocument(PreprocessedDocument document) {

        List<IfrsPdfPage> annotatedPages = new ArrayList<>(document.getNumberOfPages());

        EnumMap<IfrsComponentType, Pattern[]> map = IfrsParsingConstants.ifrsComponentsRegexes();

        // page iteration, yes it starts from 1
        for (Map.Entry<PDPage, String> pageWithText : document.getPageTexts().entrySet()) {
            LOGGER.info("processing page");

            LOGGER.info("page text: {}", pageWithText.getValue());
            LOGGER.info("###############");
            Set<IfrsComponentType> detectedComponents = new HashSet<>();

            // type detection iteration
            for (Map.Entry<IfrsComponentType, Pattern[]> entry : map.entrySet()) {
                for (Pattern pattern : entry.getValue()) {
                    if (pattern.matcher(pageWithText.getValue()).find()) {
                        LOGGER.info("found key: {}", entry.getKey());
                        detectedComponents.add(entry.getKey());
                    }
                }
            }

            IfrsPdfPage ifrsPdfPage = new IfrsPdfPage(pageWithText.getKey(), detectedComponents);
            annotatedPages.add(ifrsPdfPage);
        }

        return new IfrsPdfDocument(annotatedPages);
    }


}
