package de.koware.gacc.parser.pdfParsing;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;

@SpringBootTest
public class CroppingTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(CroppingTest.class);

    @Test
    public void testPageLocation() throws IOException {


        PDDocument daimler = PDDocument.load(new File("src/test/resources/daimler.pdf"));
        PDDocument crop = new PDDocument();

        crop.addPage(
                daimler.getPage(154)
        );

        crop.save("src/test/resources/guv_daimler.pdf");
    }
}
