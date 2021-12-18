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
            analyzePage(raw, map, textStripper, tfIdfReport, i);
        }

        return determineIfrsStatementsPageCluster(
                tfIdfReport,
                raw
        );
    }

    private PDDocument getIfrsPartsByChapterslide(PDDocument raw) {
        PDDocument cropped = new PDDocument();


        return cropped;
    }

    private HashMap<IfrsComponentType, Integer> detectComponentsInPageTextString(String pageText) {
        HashMap<IfrsComponentType, Integer> detectedComponents = new HashMap<>();
        detectedComponents.put(IfrsComponentType.BALANCE_SHEET, 0);
        detectedComponents.put(IfrsComponentType.OTHER_COMPREHENSIVE_INCOME, 0);
        detectedComponents.put(IfrsComponentType.CASHFLOW_STATEMENT, 0);
        detectedComponents.put(IfrsComponentType.EQUITY_CHANGES_STATEMENT, 0);
        detectedComponents.put(IfrsComponentType.PROFIT_AND_LOSS_STATEMENT, 0);

        // type detection iteration, determine how often a term is found on a page
        // relies on the soft convention that there is only one regex pattern per supported language
        for (Map.Entry<IfrsComponentType, Pattern[]> entry : IfrsParsingConstants.ifrsComponentsRegexes().entrySet()) {
            for (Pattern pattern : entry.getValue()) {
                pattern.matcher(pageText).results().forEach(
                            matchResult -> {
                                LOGGER.info("found key: {} at: {}", entry.getKey(), matchResult.group());
                                detectedComponents.put(
                                        entry.getKey(),
                                        detectedComponents.get(entry.getKey()) + 1
                                );
                            }
                    );
            }
        }

        return detectedComponents;
    }

    private void analyzePage(PDDocument raw, EnumMap<IfrsComponentType, Pattern[]> map, PDFTextStripper textStripper,
                             LinkedHashMap<Integer, Tuple3<IfrsComponentType, Integer, Integer>> tfIdfReport, int i) throws IOException {
        LOGGER.info("processing page no: {}", i);
        textStripper.setStartPage(i);
        textStripper.setEndPage(i);

        String pageText = textStripper.getText(raw).toLowerCase();


        IfrsComponentType mostMentionedType = IfrsComponentType.NOP;
        int mostMentions = -1;
        int nextMostMentions = -1;


        for (Map.Entry<IfrsComponentType, Integer> entry : detectComponentsInPageTextString(pageText).entrySet()) {
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


        tfIdfReport.put(i - 1, Tuples.of(mostMentionedType, mostMentions, nextMostMentions));
    }

    private PDDocument determineIfrsStatementsPageCluster(LinkedHashMap<Integer, Tuple3<IfrsComponentType, Integer, Integer>> tfidf, PDDocument raw) {
        LOGGER.info("selecting pages for the cropped document");
        PDDocument cropped = new PDDocument();

        for (Map.Entry<Integer, Tuple3<IfrsComponentType, Integer, Integer>> pageAnalysis : tfidf.entrySet()) {
            int pagenr = pageAnalysis.getKey();
            int mostMentions = pageAnalysis.getValue().getT2();
            int nextMostMentions = pageAnalysis.getValue().getT3();

            LOGGER.info("Page Nr: {} has mostMentions: {} and next mostMentions: {} and most mentioned type is: {}",
                    pagenr, mostMentions, nextMostMentions, pageAnalysis.getValue().getT1());

            // standard case
            if (mostMentions >= 2 && nextMostMentions <= 1) {
                LOGGER.info("adding page: {}", pagenr);
                cropped.addPage(raw.getPage(pagenr));
            }
        }

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
