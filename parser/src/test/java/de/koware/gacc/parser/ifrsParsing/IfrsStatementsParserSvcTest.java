package de.koware.gacc.parser.ifrsParsing;

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
    private PdfIrfsCroppingSvc pdfIrfsCroppingSvc;


    @Test
    public void testPdfCroppingBasf() throws IOException {

        PDDocument croppedBasf = this.pdfIrfsCroppingSvc.extractIfrsRelevantPages(PDDocument.load(new File(
                "src/test/resources/basf_full_2020.pdf"
        )));

        croppedBasf.save("src/test/resources/basf_cropped_2020.pdf");

        assert 6 == croppedBasf.getNumberOfPages();
        croppedBasf.close();
    }

    @Test
    public void testCroppingDw() throws IOException {
        PDDocument croppedDw = this.pdfIrfsCroppingSvc.extractIfrsRelevantPages(PDDocument.load(new File(
                "src/test/resources/dw_2020_full.pdf"
        )));

        croppedDw.save("src/test/resources/dw_cropped_2020.pdf");
        assert 8 == croppedDw.getNumberOfPages();
        croppedDw.close();

    }

    @Test
    public void testCroppingTelekom() throws IOException {
        PDDocument croppedDt = this.pdfIrfsCroppingSvc.extractIfrsRelevantPages(PDDocument.load(new File(
                "src/test/resources/d-telko_2020_full.pdf"
        )));

        croppedDt.save("src/test/resources/d-telko_cropped_2020.pdf");
        croppedDt.close();
    }

    @Test
    public void testCroppingBmw() throws IOException {
        PDDocument croppedDt = this.pdfIrfsCroppingSvc.extractIfrsRelevantPages(PDDocument.load(new File(
                "src/test/resources/bmw_2020_full.pdf"
        )));

        croppedDt.save("src/test/resources/bmw_cropped_2020.pdf");
        croppedDt.close();
    }

    @Test
    public void testCroppingDeliveryH() throws IOException {
        PDDocument croppedDt = this.pdfIrfsCroppingSvc.extractIfrsRelevantPages(PDDocument.load(new File(
                "src/test/resources/delivery-hero.pdf"
        )));

        croppedDt.save("src/test/resources/dhero_cropped_2020.pdf");
        croppedDt.close();
    }

}
