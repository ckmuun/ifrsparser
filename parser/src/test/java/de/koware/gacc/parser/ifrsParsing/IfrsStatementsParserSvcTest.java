package de.koware.gacc.parser.ifrsParsing;

import de.koware.gacc.parser.pdfParsing.BasicPdfOperationsSvc;
import de.koware.gacc.parser.pdfParsing.PreprocessedDocument;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;

@SpringBootTest
public class IfrsStatementsParserSvcTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(IfrsStatementsParserSvcTest.class);


    @Autowired
    private IfrsStatementsParserSvc ifrsStatementsParserSvc;

    @Autowired
    private BasicPdfOperationsSvc basicPdfOperationsSvc;

    @Test
    public void testAnnotateDocument() throws IOException {

        PreprocessedDocument preprocessedDocument = basicPdfOperationsSvc.extractText(
                PDDocument.load(new File("src/test/resources/adidas-cropped.pdf"))
        );

        IfrsPdfDocument ifrsPdfDocument = ifrsStatementsParserSvc.annotateDocument(preprocessedDocument);

        assert null != ifrsPdfDocument;
        assert 9 == ifrsPdfDocument.getPages().size();
        assert 5 == ifrsPdfDocument.getPages().get(0).getTypesDetectedOnPage().size();
    }
}
