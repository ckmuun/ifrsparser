package de.koware.gacc.parser.pdfParsing;

import de.koware.gacc.parser.ifrsParsing.IfrsComponentType;
import de.koware.gacc.parser.ifrsParsing.IfrsParsingConstants;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

public class PatternTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(PatternTest.class);

    @Test
    public void testPatternGuv() {

        Pattern pattern = IfrsParsingConstants
                .ifrsComponentsRegexes()
                .get(IfrsComponentType.PROFIT_AND_LOSS_STATEMENT)[0];


        String[] permutations = new String[]{
                "konzern-gewinn- und verlustrechnung",
                "gewinn- und verlustrechnung",
                "gewinn-und-verlust-rechnung",
                "konzern-gewinn- und verlustrechnung",
        };

        for (String perm : permutations) {
            LOGGER.info("perm: {}", perm);
            assert perm.matches(pattern.pattern());
        }
    }

    @Test
    public void testPatternOci() {
        Pattern pattern = IfrsParsingConstants
                .ifrsComponentsRegexes()
                .get(IfrsComponentType.OTHER_COMPREHENSIVE_INCOME)[0];

        String[] permutations = new String[]{
                "konzern-gesamtergebnisrechnung",
                "gesamtergebnisrechnung",
                "gesamtergebnis-rechnung",
                "konzern-gesamtergebnis-rechnung",
        };
        for (String perm : permutations) {
            LOGGER.info("perm: {}", perm);
            assert perm.matches(pattern.pattern());
        }
    }

    @Test
    public void testPatternCashflowStmt()  {
        Pattern pattern = IfrsParsingConstants
                .ifrsComponentsRegexes()
                .get(IfrsComponentType.CASHFLOW_STATEMENT)[0];

        String[] permutations = new String[]{
                "kapitalflussrechnung",
                "konzern-kapitalflussrechnung",
                "kapitalfluss-rechnung",
                "konzern-kapitalfluss-rechnung",
        };
        for (String perm : permutations) {
            LOGGER.info("perm: {}", perm);
            assert perm.matches(pattern.pattern());
        }

    }

    @Test
    public void testPatternBalanceSheet() {

        Pattern pattern = IfrsParsingConstants
                .ifrsComponentsRegexes()
                .get(IfrsComponentType.BALANCE_SHEET)[0];

        String[] permutations = new String[]{
                "konzern-bilanz",
                "konzernbilanz",
                "bilanz",
        };
        for (String perm : permutations) {
            LOGGER.info("perm: {}", perm);
            assert perm.matches(pattern.pattern());
        }
    }


    @Test
    public void testPatternEquityChanges() {
        Pattern pattern = IfrsParsingConstants
                .ifrsComponentsRegexes()
                .get(IfrsComponentType.EQUITY_CHANGES_STATEMENT)[0];

        String[] permutations = new String[]{
                "eigenkapitalveraenderungsrechnung",
                "konzern-eigenkapitalveraenderungsrechnung",
                "eigenkapital-veraenderungsrechnung",
                "konzern-eigenkapital-veraenderungsrechnung",
        };
        for (String perm : permutations) {
            LOGGER.info("perm: {}", perm);
            assert perm.matches(pattern.pattern());
        }
    }
}
