package de.koware.gacc.parser.api;

import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class PdfMultipartFile implements MultipartFile {

    private final String name;
    private final byte[] bytes;

    public PdfMultipartFile(String name, byte[] bytes) {
        this.name = name;
        this.bytes = bytes;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getOriginalFilename() {
        return name;
    }

    @Override
    public String getContentType() {
        return "application/pdf";
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public long getSize() {
        return this.bytes.length;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return bytes;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(this.bytes);
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {

    }
}
