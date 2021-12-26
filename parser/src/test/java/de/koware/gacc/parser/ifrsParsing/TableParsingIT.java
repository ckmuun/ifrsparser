package de.koware.gacc.parser.ifrsParsing;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.util.function.Tuples;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@SpringBootTest
public class TableParsingIT {
    @Autowired
    private TableToXslxSvc tableToXslxSvc;

    @Autowired
    private PdfIrfsCroppingSvc croppingSvc;

    @Autowired
    private PdfTableParsingSvc tableParsingSvc;


    @Test
    public void testCropping() {

    }


    @Test
    public void testPrecroppedDocument() throws IOException {
        PDDocument document = PDDocument.load(new File("src/test/resources/dw_cropped_2020.pdf"));

        HashMap<Integer, IfrsComponentType> dwCroppedMap = new HashMap<>();
        dwCroppedMap.put(1, IfrsComponentType.BALANCE_SHEET);
        dwCroppedMap.put(2, IfrsComponentType.BALANCE_SHEET);
        dwCroppedMap.put(3, IfrsComponentType.PROFIT_AND_LOSS_STATEMENT);
        dwCroppedMap.put(4, IfrsComponentType.OTHER_COMPREHENSIVE_INCOME);
        dwCroppedMap.put(5, IfrsComponentType.CASHFLOW_STATEMENT);
        dwCroppedMap.put(6, IfrsComponentType.EQUITY_CHANGES_STATEMENT);
        dwCroppedMap.put(7, IfrsComponentType.EQUITY_CHANGES_STATEMENT);

        List<IfrsComponent> ifrsComponentList = tableParsingSvc.parseTablesFromPdf(Tuples.of(document, dwCroppedMap));

        for(IfrsComponent ifrsComponent : ifrsComponentList) {
            System.out.println(ifrsComponent.prettyPrintTables());
        }

        XSSFWorkbook workbook = tableToXslxSvc.convertStringTableToXslx(
                ifrsComponentList
        );

        FileOutputStream fos = new FileOutputStream("src/test/resources/dw_cropped_2020.xlsx");
        workbook.write(fos);
        fos.close();

    }

    @Test
    public void testFullRun() {

    }
}
