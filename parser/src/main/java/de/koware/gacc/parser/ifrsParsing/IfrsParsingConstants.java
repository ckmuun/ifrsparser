package de.koware.gacc.parser.ifrsParsing;

import java.util.EnumMap;
import java.util.regex.Pattern;

public class IfrsParsingConstants {

    private IfrsParsingConstants() {
    }

    public static EnumMap<IfrsComponentType, Pattern[]> ifrsComponentsRegexes() {
        EnumMap<IfrsComponentType, Pattern[]> map = new EnumMap<IfrsComponentType, Pattern[]>(IfrsComponentType.class);

        /*
            convention is one pattern per supported language
         */

        map.put(IfrsComponentType.BALANCE_SHEET, new Pattern[]{
                Pattern.compile("((konzern)?[-]?(bilanz[^a-z]))|([^a-z]aktiva[^a-z])|([^a-z]passiva[^a-z])"),
        });

        map.put(IfrsComponentType.PROFIT_AND_LOSS_STATEMENT, new Pattern[]{
                Pattern.compile("((konzern)?[-]?gewinn-[ ]*und[- ]*verlust[-]?(rechnung\\s*))|(konzern[\\s-_.]+gewinn[\\s-_.]+und[\\s-_.]+verlust[\\s-_.]+rechnung)"),
        });

        map.put(IfrsComponentType.CASHFLOW_STATEMENT, new Pattern[]{
                Pattern.compile("(konzern)?[-]?kapitalfluss[-]?rechnung")
        });
        map.put(IfrsComponentType.OTHER_COMPREHENSIVE_INCOME, new Pattern[]{
                Pattern.compile("((konzern)?[-]?gesamtergebnis[-]?rechnung)|(im eigenkapital erfasste erträge und aufwendungen)"),
        });

        map.put(IfrsComponentType.EQUITY_CHANGES_STATEMENT, new Pattern[]{
                Pattern.compile("((konzern)?[-]?eigenkapital[-]?ver[(ae)ä]nderungsrechnung)|(entwicklung des eigenkapitals)"),
        });


        return map;
    }


    public static EnumMap<IfrsComponentType, Pattern[]> ifrsComponentColumnIndicators() {
        EnumMap<IfrsComponentType, Pattern[]> map = new EnumMap<IfrsComponentType, Pattern[]>(IfrsComponentType.class);

        map.put(IfrsComponentType.BALANCE_SHEET, new Pattern[]{
                Pattern.compile("erl[(ae)ä]uterung"),
                Pattern.compile("ver[(ae)ä]nderung"),
                Pattern.compile("(20(\\d{2}))|(1.Jan(uar)?.*31.Dez(ember)?)")
        });

        map.put(IfrsComponentType.PROFIT_AND_LOSS_STATEMENT, new Pattern[]{
                Pattern.compile("erl[(ae)ä]uterung"),
                Pattern.compile("ver[(ae)ä]nderung"),
                Pattern.compile("20(\\d{2})")
        });

        map.put(IfrsComponentType.CASHFLOW_STATEMENT, new Pattern[]{
                Pattern.compile("erl[(ae)ä]uterung"),
                Pattern.compile("ver[(ae)ä]nderung"),
                Pattern.compile("20(\\d{2})")
        });
        map.put(IfrsComponentType.OTHER_COMPREHENSIVE_INCOME, new Pattern[]{
                Pattern.compile("erl[(ae)ä]uterung"),
                Pattern.compile("ver[(ae)ä]nderung"),
                Pattern.compile("20(\\d{2})")
        });

        map.put(IfrsComponentType.EQUITY_CHANGES_STATEMENT, new Pattern[]{
                Pattern.compile("erl[(ae)ä]uterung"),
                Pattern.compile("ver[(ae)ä]nderung"),
                Pattern.compile("20(\\d{2})")
        });


        return map;
    }

}
