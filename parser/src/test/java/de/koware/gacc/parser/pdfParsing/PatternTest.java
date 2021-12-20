package de.koware.gacc.parser.pdfParsing;

import de.koware.gacc.parser.ifrsParsing.IfrsComponentType;
import de.koware.gacc.parser.ifrsParsing.IfrsParsingConstants;
import de.koware.gacc.parser.ifrsParsing.PdfIrfsCroppingSvc;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.regex.Pattern;

@SpringBootTest
public class PatternTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(PatternTest.class);

    @Autowired
    private PdfIrfsCroppingSvc parserSvc;


    @Test
    public void testKonzernabschlussPatterns() {
        String text = "konzernabschluss konzern-bilanz in mio. € angabe 31.12.2020 31.12.2019 aktiva kurzfristige vermögenswerte 37.293 24.689 zahlungsmittel und zahlungsmitteläquivalente 1 12.939 5.393 forderungen aus lieferungen und leistungen 2 13.523 10.846 vertragsvermögenswerte 3 1.966 1.876 ertragsteuerforderungen 31 349 481 sonstige finanzielle vermögenswerte 11 3.224 3.254 vorräte 4 2.695 1.568 übrige vermögenswerte 12 1.484 1.175 zur veräußerung gehaltene langfristige vermögenswerte und veräußerungsgruppen 5 1.113 97 langfristige vermögenswerte 227.624 145.983 immaterielle vermögenswerte 6 118.066 68.202 sachanlagen 7 60.975 49.548 nutzungsrechte 8 30.302 17.998 aktivierte vertragskosten 9 2.192 2.075 beteiligungen an at equity bilanzierten unternehmen 10 543 489 sonstige finanzielle vermögenswerte 11 6.416 3.996 aktive latente steuern 31 7.972 2.704 übrige vermögenswerte 12 1.159 970 bilanzsumme 264.917 170.672 in mio. € angabe 31.12.2020 31.12.2019 passiva kurzfristige schulden 37.135 32.913 finanzielle verbindlichkeiten 13 12.652 11.463 leasing-verbindlichkeiten 13 5.108 3.987 verbindlichkeiten aus lieferungen und leistungen und sonstige verbindlichkeiten 14 9.760 9.431 ertragsteuerverbindlichkeiten 31 690 463 sonstige rückstellungen 16 3.638 3.082 übrige schulden 17 3.213 2.850 vertragsverbindlichkeiten 18 1.625 1.608 schulden in direktem zusammenhang mit zur veräußerung gehaltenen langfristigen vermögenswerten und veräußerungsgruppen 5 449 29 langfristige schulden 155.232 91.528 finanzielle verbindlichkeiten 13 94.456 54.886 leasing-verbindlichkeiten 13 27.607 15.848 pensionsrückstellungen und ähnliche verpflichtungen 15 7.684 5.831 sonstige rückstellungen 16 5.395 3.581 passive latente steuern 31 17.260 8.954 übrige schulden 17 2.418 1.972 vertragsverbindlichkeiten 18 411 456 schulden 192.367 124.441 eigenkapital 19 72.550 46.231 gezeichnetes kapital 12.189 12.189 eigene anteile (46) (47) 12.143 12.142 kapitalrücklage 62.640 55.029 gewinnrücklagen einschließlich ergebnisvortrag (38.905) (38.709) kumuliertes sonstiges konzernergebnis (4.115) (622) konzernüberschuss/(-fehlbetrag) 4.158 3.867 anteile der eigentümer des mutterunternehmens 35.922 31.707 anteile anderer gesellschafter 36.628 14.524 bilanzsumme 264.917 170.672 seit dem 1. april 2020 wird sprint als vollkonsolidiertes tochterunternehmen in den konzernabschluss der deutschen telekom einbezogen. die transaktion hat auswirkungen auf die vergleichbarkeit der werte der aktuellen periode mit den vorjahresvergleichswerten. weitere informationen zur transaktion finden sie im abschnitt „veränderung des konsolidierungskreises und sonstige transaktionen“. = p q konzernabschluss 150 deutsche telekom. das geschäftsjahr 2020. ";

        Pattern pattern = IfrsParsingConstants.chapterSlideIndicators()[0];

        int results = pattern.matcher(text).results().toArray().length;
        assert 2 <= results;

    }

    @Test
    public void testChapterSlidePatterns1() {
        String text = "konzern abschluss 186 gewinn-und-verlust-rechnung   des  konzerns und der  segmente 187 gesamtergebnisrechnung des  konzerns 188 bilanz des konzerns und der segmente zum  31.  dezember 2020 190 kapitalflussrech nung des  konzerns  und der  segmente 192 entwicklung des  konzerneigenkapitals 194 konzernanhang 194 grundsätze 208 erläuterungen zur gewinn-und- verlust-rechnung 217 erläuterungen zur gesamt ergebnis rechnung 219 erläuterungen zur bilanz 242 sonstige angaben 264 segment informationen 269 aufstellung des anteilsbesitzes zum 31. dezember 2020 185bmw group bericht 2020 konzernabschluss     ";


        HashMap<IfrsComponentType, Integer> comps = parserSvc.detectComponentsInPageTextString(text);

        assert comps.get(IfrsComponentType.BALANCE_SHEET) != 0;
        assert comps.get(IfrsComponentType.PROFIT_AND_LOSS_STATEMENT) != 0;
        assert comps.get(IfrsComponentType.EQUITY_CHANGES_STATEMENT) != 0;
        assert comps.get(IfrsComponentType.CASHFLOW_STATEMENT) != 0;
        assert comps.get(IfrsComponentType.OTHER_COMPREHENSIVE_INCOME) != 0;


    }

    @Test
    public void testChapterSlidePatterns() {
        String text = "konzernabschluss\n" +
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

        HashMap<IfrsComponentType, Integer> comps = parserSvc.detectComponentsInPageTextString(text);

        assert comps.get(IfrsComponentType.BALANCE_SHEET) != 0;
        assert comps.get(IfrsComponentType.PROFIT_AND_LOSS_STATEMENT) != 0;
        assert comps.get(IfrsComponentType.EQUITY_CHANGES_STATEMENT) != 0;
        assert comps.get(IfrsComponentType.CASHFLOW_STATEMENT) != 0;
        assert comps.get(IfrsComponentType.OTHER_COMPREHENSIVE_INCOME) != 0;
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
