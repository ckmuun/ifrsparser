package de.koware.gacc.parser.ifrsParsing;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class TableToXslxSvc {

    private static final Logger LOGGER = LoggerFactory.getLogger(TableToXslxSvc.class);

    public XSSFWorkbook convertStringTableToXslx(List<IfrsComponent> components) {
        LOGGER.info("creating excel workbook");
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook();


        for (IfrsComponent component : components) {
            LOGGER.info("creating excel sheet for ifrs component");
            XSSFSheet sheet = xssfWorkbook.createSheet(component.getType().name());

            String[][] allTable = concatTables(component.getTables());

            for (int i = 0; i < allTable.length; i++) {
                String[] line = allTable[i];

                Row xslxRow = sheet.createRow(i);

                for (int k = 0; k < line.length; k++) {
                    xslxRow.createCell(k).setCellValue(line[k]);
                }
            }
        }

        return xssfWorkbook;
    }

    private String[][] concatTables(List<String[][]> tables) {
        int allLength = 0;
        int width = 0;

        ArrayList<String[]> allLines = new ArrayList<>();
        for (String[][] table : tables) {
            allLength += table.length;
            allLines.addAll(Arrays.asList(table));
        }


        String[][] all = new String[allLength][width];

        for (int i = 0; i < allLength; i++) {

            all[i] = allLines.get(i);
        }

        return all;

    }
}
