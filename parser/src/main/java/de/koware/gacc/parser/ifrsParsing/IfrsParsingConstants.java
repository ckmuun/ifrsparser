package de.koware.gacc.parser.ifrsParsing;

import java.util.EnumMap;
import java.util.regex.Pattern;

public class IfrsParsingConstants {

    private IfrsParsingConstants() {
    }

    public static EnumMap<IfrsComponentType, Pattern[]> ifrsComponentsRegexes() {
        EnumMap<IfrsComponentType, Pattern[]> map = new EnumMap<IfrsComponentType, Pattern[]>(IfrsComponentType.class);

        map.put(IfrsComponentType.BALANCE_SHEET, new Pattern[]{
                Pattern.compile("(konzern)?[-]?bilanz"),
        });

        map.put(IfrsComponentType.PROFIT_AND_LOSS_STATEMENT, new Pattern[]{
                Pattern.compile("(konzern)?[-]?gewinn-[ ]*und[- ]*verlust[-]?rechnung")
        });

        map.put(IfrsComponentType.CASHFLOW_STATEMENT, new Pattern[]{
                Pattern.compile("(konzern)?[-]?kapitalfluss[-]?rechnung")
        });
        map.put(IfrsComponentType.OTHER_COMPREHENSIVE_INCOME, new Pattern[]{
                Pattern.compile("(konzern)?[-]?gesamtergebnis[-]?rechnung"),
        });

        map.put(IfrsComponentType.EQUITY_CHANGES_STATEMENT, new Pattern[]{
                Pattern.compile("(konzern)?[-]?eigenkapital[-]?ver[(ae)ä]nderungsrechnung"),
        });


        return map;
    }
}
