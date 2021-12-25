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
}
