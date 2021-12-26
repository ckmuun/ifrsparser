package de.koware.gacc.parser.ifrsParsing;

import java.util.List;

public class IfrsComponent {

    private final IfrsComponentType type;
    private final List<String[][]> tables;


    public IfrsComponent(IfrsComponentType type, List<String[][]> tables) {
        this.type = type;
        this.tables = tables;
    }

    public IfrsComponentType getType() {
        return type;
    }

    public List<String[][]> getTables() {
        return tables;
    }

    public String prettyPrintTables() {
        StringBuilder tables = new StringBuilder();

        tables.append("type: ");
        tables.append(this.type.name());
        tables.append('\n');

        for(String[][] table: this.tables) {

            for (String[] strings : table) {
                tables.append(" | ");
                for (String string : strings) {
                    tables.append(string);
                    tables.append(" | ");
                }
                tables.append('\n');
            }
        }
        return tables.toString();
    }
}
