package de.koware.gacc.parser.api;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DataBufferWrapper;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

@Service
public class ParsingSvc {

    public Mono<PDDocument> createPdfFromByte(Mono<FilePart> filePartFlux) {

        return filePartFlux
                .flatMap(filePart -> DataBufferUtils.join(filePart.content()))
                .map(dataBuffer -> load(dataBuffer.asInputStream()));

    }

    private PDDocument load(InputStream is) {
        try {
            return PDDocument.load(is);
        } catch (IOException e) {
            e.printStackTrace();
            return new PDDocument();
        }
    }
}


