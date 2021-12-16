package de.koware.gacc.parser.pdfParsing;

import java.util.List;

public class Table implements PdfContent {

    private List<String> columns;
    private List<String> rows;


    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public List<String> getRows() {
        return rows;
    }

    public void setRows(List<String> rows) {
        this.rows = rows;
    }
}
