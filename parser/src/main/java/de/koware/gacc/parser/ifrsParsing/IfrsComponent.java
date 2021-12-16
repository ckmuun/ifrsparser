package de.koware.gacc.parser.ifrsParsing;

public class IfrsComponent {

    private final IfrsComponentType  type;


    public IfrsComponent(IfrsComponentType type) {
        this.type = type;
    }


    public IfrsComponentType getType() {
        return type;
    }
}
