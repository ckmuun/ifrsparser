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
        LinkedHashMap<Integer, Map<IfrsComponentType, Integer>> tfIdfReport = new LinkedHashMap<>();

        for (int i = 1; i < raw.getNumberOfPages(); i++) {
            analyzePage(raw, textStripper, tfIdfReport, i);
        }

        return determineIfrsStatementsPageCluster(
                tfIdfReport,
                raw
        );
    }


    private void analyzePage(PDDocument raw, PDFTextStripper textStripper,
                             LinkedHashMap<Integer, Map<IfrsComponentType, Integer>> tfIdfReport, int i) throws IOException {
        LOGGER.info("processing page no: {}", i);
        textStripper.setStartPage(i);
        textStripper.setEndPage(i);

        String pageText = textStripper
                .getText(raw)
                .toLowerCase()
                .replaceAll("\u00AD", "-") // this removes some weird unicode dashes
                .replaceAll("\n", " ");

        tfIdfReport.put(i - 1, detectComponentsInPageTextString(pageText));
    }


    private Tuple3<IfrsComponentType, Integer, Integer> getMentions(Map<IfrsComponentType, Integer> mentions) {
        LOGGER.info("getting mentions");
        IfrsComponentType mostMentionedType = IfrsComponentType.NOP;
        int mostMentions = 0;
        int nextMostMentions = 0;


        for (Map.Entry<IfrsComponentType, Integer> entry : mentions.entrySet()) {
            if (mostMentions < entry.getValue()) {
                mostMentionedType = entry.getKey();
                nextMostMentions = mostMentions;

                mostMentions = entry.getValue();
                continue;
            }
            if (nextMostMentions < entry.getValue()) {
                nextMostMentions = entry.getValue();
            }
        }
        return Tuples.of(mostMentionedType, mostMentions, nextMostMentions);
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

    // the LinkedHashMap contains the page numbers with the keyword analysis results for every ifrs component type
    private PDDocument determineIfrsStatementsPageCluster(LinkedHashMap<Integer, Map<IfrsComponentType, Integer>> tfidf, PDDocument raw) throws IOException {
        LOGGER.info("selecting pages for the cropped document");
        PDDocument cropped = new PDDocument();

        // step 0 find the chapter slide
        int chapterSlideNr = -1;

        for (Map.Entry<Integer, Map<IfrsComponentType, Integer>> entry : tfidf.entrySet()) {

            // the chapter slide is usually not directly at the beginning of the document
            if (entry.getKey() <= 20 || chapterSlideNr != -1) {
                continue;
            }

            // value 0 found -> all ifrs components mentioned on that slide -> chapter slide
            if (!entry.getValue().containsValue(0)) {
                chapterSlideNr = entry.getKey();
                LOGGER.info("found chapter slide nr: {}", chapterSlideNr);
            }
        }
        LOGGER.info("now doing mention-based page insertion");


        final HashMap<IfrsComponentType, List<Integer>> ifrsComponentsWithPages = new HashMap<>();

        ifrsComponentsWithPages.put(IfrsComponentType.PROFIT_AND_LOSS_STATEMENT, new ArrayList<>());
        ifrsComponentsWithPages.put(IfrsComponentType.OTHER_COMPREHENSIVE_INCOME, new ArrayList<>());
        ifrsComponentsWithPages.put(IfrsComponentType.BALANCE_SHEET, new ArrayList<>());
        ifrsComponentsWithPages.put(IfrsComponentType.CASHFLOW_STATEMENT, new ArrayList<>());
        ifrsComponentsWithPages.put(IfrsComponentType.EQUITY_CHANGES_STATEMENT, new ArrayList<>());


        // step1 use the pages with the mentions
        int maxOffset = 15;

        for (int offsetFromChaperSlide = 1; offsetFromChaperSlide < maxOffset; offsetFromChaperSlide++) {
            Tuple3<IfrsComponentType, Integer, Integer> pageAnalysis = getMentions(tfidf.get(offsetFromChaperSlide + chapterSlideNr));
            int mostMentions = pageAnalysis.getT2();
            int nextMostMentions = pageAnalysis.getT3();

            if ((mostMentions == 0 && nextMostMentions == 0)
                    || getDigitTextCharThreshValue(chapterSlideNr + offsetFromChaperSlide + 1, raw)) {
                maxOffset++;
                continue;
            }

            LOGGER.info("Page Nr: {} has mostMentions: {} and next mostMentions: {} and most mentioned type is: {}",
                    offsetFromChaperSlide, mostMentions, nextMostMentions, pageAnalysis.getT1());


            if ((mostMentions >= 2 && nextMostMentions <= 1) || (mostMentions == 1 && ifrsComponentsWithPages.get(pageAnalysis.getT1()).size() == 0)) {
                LOGGER.info("adding page: {}", offsetFromChaperSlide + chapterSlideNr);

                ifrsComponentsWithPages.get(pageAnalysis.getT1()).add(offsetFromChaperSlide + chapterSlideNr);

                cropped.addPage(raw.getPage(offsetFromChaperSlide + chapterSlideNr));
            }
        }

        return cropped;
    }


    private boolean getDigitTextCharThreshValue(int pageIndex, PDDocument document) throws IOException {
        PDFTextStripper textStripper = new PDFTextStripper();
        textStripper.setStartPage(pageIndex);
        textStripper.setEndPage(pageIndex);
        String pageText = textStripper.getText(document);

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

        return numberOfDigitChars * 20 <= numberOfTextChars;
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
