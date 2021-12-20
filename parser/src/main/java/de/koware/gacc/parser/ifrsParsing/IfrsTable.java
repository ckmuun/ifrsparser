package de.koware.gacc.parser.ifrsParsing;

import java.util.LinkedHashMap;
import java.util.List;

public class IfrsTable {
    private IfrsComponentType type;

    private List<String> columns;
    private LinkedHashMap<String, List<String>> rows;

    public IfrsTable(IfrsComponentType type, List<String> columns) {
        this.type = type;
        this.columns = columns;
        this.rows = new LinkedHashMap<>();
    }

    public void addRow(String rowHead, List<String> rowColumValues) {
        this.rows.put(rowHead, rowColumValues);
    }

    public IfrsComponentType getType() {
        return type;
    }

    public List<String> getColumns() {
        return columns;
    }

    public LinkedHashMap<String, List<String>> getRows() {
        return rows;
    }
}
