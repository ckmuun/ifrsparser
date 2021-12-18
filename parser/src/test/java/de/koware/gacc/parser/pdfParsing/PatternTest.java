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
    public void testChapterSlidePatterns() {
        String chapterSlide = "konzernabschluss\n" +
                "142 konzernbilanz\n" +
                "144 konzern-gewinn- und  \n" +
                "verlustrechnung\n" +
                "145 konzern-gesamtergebnis-\n" +
                "rechnung\n" +
                "146 konzern-kapitalflussrech-\n" +
                "nung\n" +
                "148 konzern-eigenkapital-\n" +
                "veränderungsrechnung\n" +
                "150 anhang zum konzern-\n" +
                "abschluss\n" +
                "141\n";


    }

    @Test
    public void guvPatternTest() {
        Pattern guvPattern = Pattern.compile("(konzern)?[-]?gewinn-[ ]*und[- ]*verlust[-]?rechnung");

        assert guvPattern.matcher("KONZERN-GEWINN- UND VERLUSTRECHNUNG".toLowerCase()).find();

        String guv = "konzern\u00ADgewinn\u00AD und verlustrechnung";
        String guvRep = guv.replaceAll("\u00AD", "-");
        LOGGER.info("replaced: {}", guvRep);

        assert guvPattern.matcher(guvRep).find();
    }


    @Test
    public void bilanzPatternTest() {
        Pattern bilanzPattern = Pattern.compile("bilanz\\s+");

        assert "bilanz ".matches(bilanzPattern.pattern());
        assert !"bilanziert".matches(bilanzPattern.pattern());

        Pattern bilanzPattern1 = Pattern.compile("bilanz[^a-z]*");
        assert "bilanz".matches(bilanzPattern1.pattern());
        assert !"bilanziert".matches(bilanzPattern1.pattern());


        Pattern fullPattern = Pattern.compile("(konzern)?[-]?(bilanz[^a-z])");
        assert !"bilanziert".matches(fullPattern.pattern());

        assert !fullPattern.matcher("bilanziert").find();

    }

    @Test
    public void yearPatternTest() {
        Pattern pattern = Pattern.compile("20(\\d{2})");

        assert "2020".matches(pattern.pattern());
        assert !"20211".matches(pattern.pattern());

        Pattern complexPattern = Pattern.compile("(20(\\d{2}))|(1.(\\s)*Jan(uar)?(\\s)*31.(\\s)*Dez(ember)?)");

        assert "dskafjaslkfd".matches(".*");
        assert "\n\t\t".matches("\\s*");
        assert "dskafjaslkfd\n\n".matches("([.\\s])*");


        Pattern mediumPattern = Pattern.compile("1.(\\s)*Jan(uar)?");
        assert "1.    Januar".matches(mediumPattern.pattern());
        assert "1. Januar".matches(mediumPattern.pattern());
        assert "1.Januar".matches(mediumPattern.pattern());
        assert "1.     Jan".matches(mediumPattern.pattern());
        assert "1. Jan".matches(mediumPattern.pattern());
        assert "1.Jan".matches(mediumPattern.pattern());


        assert "2021".matches(complexPattern.pattern());
        assert "1. Jan 2020 \n bis 31. Dez 2021".matches(complexPattern.pattern());
    }


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
    public void testPatternCashflowStmt() {
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
