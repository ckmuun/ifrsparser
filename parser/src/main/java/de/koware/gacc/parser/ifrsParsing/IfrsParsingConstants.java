package de.koware.gacc.parser.ifrsParsing;

import java.util.EnumMap;
import java.util.regex.Pattern;

public class IfrsParsingConstants {

    private IfrsParsingConstants() {
    }

    public static Pattern[] chapterSlideIndicators() {
        return new Pattern[]{
            Pattern.compile("kon[-]?\\s*zern[-]?\\s*ab[-]?\\s*schluss")
        };
    }

    public static EnumMap<IfrsComponentType, Pattern[]> ifrsComponentsRegexes() {
        EnumMap<IfrsComponentType, Pattern[]> map = new EnumMap<IfrsComponentType, Pattern[]>(IfrsComponentType.class);

        /*
            convention is one pattern per supported language
         */

        map.put(IfrsComponentType.BALANCE_SHEET, new Pattern[]{
                Pattern.compile("((konzern)?[-]?\\s*(bilanz[^a-z]))|([^a-z]aktiva[^a-z])|([^a-z]passiva[^a-z])"),
        });

        map.put(IfrsComponentType.PROFIT_AND_LOSS_STATEMENT, new Pattern[]{
                Pattern.compile("((konzern)?[-]?gewinn[-]?\\s*und[-]?\\s*verlust[-]?\\s*(rechnung\\s*))|(konzern[\\s-_.]+gewinn[\\s-_.]+und[\\s-_.]+verlust[\\s-_.]+rechnung)"),
        });

        map.put(IfrsComponentType.CASHFLOW_STATEMENT, new Pattern[]{
                Pattern.compile("((konzern)?[-]?kapital\\s*fluss[-]?\\s*rech\\s*nung)|(kon[-]?\\s*zern[-]?\\s*kapital[-]?\\s*fluss[-]?\\s*rech[-]?\\s*nung)")
        });
        map.put(IfrsComponentType.OTHER_COMPREHENSIVE_INCOME, new Pattern[]{
                Pattern.compile("((konzern)?[-]?\\s*gesamtergebnis[-]?\\s*rechnung)|(im eigenkapital erfasste erträge und aufwendungen)|(kon[-\\s]*zern[-\\s]*gesamt[-\\s]*ergebnis[-\\s]*rech[-\\s]*nung)"),
        });

        map.put(IfrsComponentType.EQUITY_CHANGES_STATEMENT, new Pattern[]{
                Pattern.compile("((konzern)?[-]?\\s*eigenkapital[-]?\\s*ver[(ae)ä]nderungsrechnung)|(entwicklung\\s*des\\s*(konzern)?eigenkapitals)"),
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
