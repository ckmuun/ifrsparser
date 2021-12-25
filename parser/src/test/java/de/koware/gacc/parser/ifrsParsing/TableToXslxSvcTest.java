package de.koware.gacc.parser.ifrsParsing;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TableToXslxSvcTest {

     @Autowired
     private TableToXslxSvc tableToXslxSvc;

    @Test
    void convertStringTableToXslx() {
    }

    @Test
    public void testToXslxConversion() throws IOException {

        List<String[][]> tables = new ArrayList<>();

        String[] line = new String[]{"alpha", "bravo", "charlie"};
        String[] line1 = new String[]{"delta", "echo", "foxtrot"};

        String[][] table = new String[][] {
                line,
                line1
        };

        tables.add(table);
        IfrsComponent component = new IfrsComponent(IfrsComponentType.BALANCE_SHEET, tables);

        XSSFWorkbook wb  = tableToXslxSvc.convertStringTableToXslx(Collections.singletonList(component));

        FileOutputStream fos = new FileOutputStream("src/test/resources/test.xlsx");

        wb.write(fos);

    }
}
