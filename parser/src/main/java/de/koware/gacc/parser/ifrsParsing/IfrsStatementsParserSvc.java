package de.koware.gacc.parser.ifrsParsing;

import de.koware.gacc.parser.pdfParsing.PreprocessedDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class IfrsStatementsParserSvc {

    private static final Logger LOGGER = LoggerFactory.getLogger(IfrsStatementsParserSvc.class);

    public IfrsPdfDocument annotateDocument(PreprocessedDocument document) throws IOException {

        List<IfrsPdfPage> annotatedPages = new ArrayList<>(document.getNumberOfPages());

        PDFTextStripper textStripper = new PDFTextStripper();

        //PDFTextStripperByArea textStripperByArea = new PDFTextStripperByArea();


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
