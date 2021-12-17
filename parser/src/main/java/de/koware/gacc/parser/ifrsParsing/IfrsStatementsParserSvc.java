package de.koware.gacc.parser.ifrsParsing;

import de.koware.gacc.parser.pdfParsing.PreprocessedDocument;
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

        PDPageTree pages = document.getDocument().getPages();

        EnumMap<IfrsComponentType, Pattern[]> map = IfrsParsingConstants.ifrsComponentsRegexes();

        // page iteration, yes it starts from 1
        for (int i = 1; i <= pages.getCount(); i++) {
            LOGGER.info("processing page");

            textStripper.setStartPage(i);
            textStripper.setEndPage(i);

            String pageText = textStripper
                    .getText(document.getDocument())
                    .toLowerCase();



            LOGGER.info("page text: {}", pageText);
            LOGGER.info("###############");
            Set<IfrsComponentType> detectedComponents = new HashSet<>();

            // type detection iteration
            for (Map.Entry<IfrsComponentType, Pattern[]> entry : map.entrySet()) {
                for (Pattern pattern : entry.getValue()) {
                    if (pattern.matcher(pageText).find()) {
                        LOGGER.info("found key: {}", entry.getKey());
                        detectedComponents.add(entry.getKey());
                    }
                }
            }

            IfrsPdfPage ifrsPdfPage = new IfrsPdfPage(pages.get(i-1), detectedComponents);
            annotatedPages.add(ifrsPdfPage);
        }

        return new IfrsPdfDocument(annotatedPages);
    }


}
