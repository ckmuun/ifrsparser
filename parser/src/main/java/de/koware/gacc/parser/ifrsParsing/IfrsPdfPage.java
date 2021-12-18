package de.koware.gacc.parser.ifrsParsing;

import de.koware.gacc.parser.pdfParsing.PdfContent;
import org.apache.pdfbox.pdmodel.PDPage;

import java.util.Set;

public class IfrsPdfPage implements PdfContent {

    private final PDPage page;
    private final Set<IfrsComponentType> typesDetectedOnPage;


    public IfrsPdfPage(PDPage page, Set<IfrsComponentType> typesDetectedOnPage) {
        this.page = page;
        this.typesDetectedOnPage = typesDetectedOnPage;
    }

    public Set<IfrsComponentType> getTypesDetectedOnPage() {
        return typesDetectedOnPage;
    }

    public PDPage getPage() {
        return page;
    }
}
