package de.koware.gacc.parser.api;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class InmemoryFileResource implements Resource {

    private final InputStream is;

    private final String filename;

    public InmemoryFileResource(InputStream is, String filename) {
        this.is = is;
        this.filename = filename;
    }

    @Override
    public boolean exists() {
        return true;
    }

    @Override
    public URL getURL() throws IOException {
        return new URL("");
    }

    @Override
    public URI getURI() throws IOException {
        return URI.create("");
    }

    @Override
    public File getFile() throws IOException {
        return new File("");
    }

    @Override
    public long contentLength() throws IOException {
        return 0;
    }

    @Override
    public long lastModified() throws IOException {
        return 0;
    }

    @Override
    public Resource createRelative(String relativePath) throws IOException {
        return new ByteArrayResource(new byte[0]);
    }

    @Override
    public String getFilename() {
        return this.filename;
    }

    @Override
    public String getDescription() {
        return "file description";
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return this.is;
    }
}
