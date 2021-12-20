package de.koware.gacc.parser.ifrsParsing;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.Matrix;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import technology.tabula.Rectangle;
import technology.tabula.TextElement;
import technology.tabula.TextStripper;
import technology.tabula.Utils;

import java.io.File;
import java.io.IOException;
import java.util.*;

@SpringBootTest
public class TableParsingTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(TableParsingTest.class);

    @Autowired
    private PdfTableParsingSvc tableParsingSvc;

    @Test
    public void simpleParsingTest() throws IOException {

        //PDDocument document = PDDocument.load(new File("src/test/resources/basf_cropped_2020.pdf"));
        PDDocument document = PDDocument.load(new File("src/test/resources/dw_cropped_2020.pdf"));
        //PDDocument document = PDDocument.load(new File("src/test/resources/adidas-cropped.pdf"));

        TextStripper tabulaTs = new TextStripper(document, 1);


        tabulaTs.setAddMoreFormatting(true);
        tabulaTs.process();

        List<TextElement> textElements = tabulaTs.getTextElements();


        Utils.sort(tabulaTs.getTextElements(), Rectangle.ILL_DEFINED_ORDER);
        for(TextElement textElement : textElements) {
            LOGGER.info("text element bounds: {}", textElement.getBounds());
            LOGGER.info("text element text: {}", textElement.getText());
        }
        LOGGER.info("###############################################################");

        List<String> lines = tableParsingSvc.parseTable(textElements);
        for(String line: lines) {
            System.out.println(line);
        }
    }

    @Test
    public void testTableParsing() throws IOException {
        PreprocessedDocument prepdoc = new PreprocessedDocument();
        prepdoc.setCroppedPdf(PDDocument.load(new File("src/test/resources/basf_cropped_2020.pdf")));
        final HashMap<IfrsComponentType, List<Integer>> ifrsCompPageIndices = new HashMap<>(5);

        // 1-based
        ifrsCompPageIndices.put(IfrsComponentType.PROFIT_AND_LOSS_STATEMENT, Collections.singletonList(1));
        ifrsCompPageIndices.put(IfrsComponentType.OTHER_COMPREHENSIVE_INCOME, Collections.singletonList(2));
        ifrsCompPageIndices.put(IfrsComponentType.BALANCE_SHEET, Arrays.asList(3, 4));
        ifrsCompPageIndices.put(IfrsComponentType.CASHFLOW_STATEMENT, Collections.singletonList(5));
        ifrsCompPageIndices.put(IfrsComponentType.EQUITY_CHANGES_STATEMENT, Collections.singletonList(6));


        prepdoc.setPageIndicesForIfrsComp(ifrsCompPageIndices);

        IfrsPdfDocument ifrsPdfDocument = tableParsingSvc.process(prepdoc);

    }
}
