package de.koware.gacc.parser.ifrsParsing;

import java.util.List;

public class IfrsPdfDocument {

    private final List<IfrsPdfPage> pages;

    public IfrsPdfDocument(List<IfrsPdfPage> pages) {
        this.pages = pages;
    }

    public List<IfrsPdfPage> getPages() {
        return pages;
    }
}
