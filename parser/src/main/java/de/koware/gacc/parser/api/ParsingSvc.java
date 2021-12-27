package de.koware.gacc.parser.api;

import de.koware.gacc.parser.ifrsParsing.PdfIrfsCroppingSvc;
import de.koware.gacc.parser.ifrsParsing.PdfTableParsingSvc;
import de.koware.gacc.parser.ifrsParsing.TableToXslxSvc;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;

@Service
public class ParsingSvc {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParsingSvc.class);

    private final PdfIrfsCroppingSvc croppingSvc;
    private final PdfTableParsingSvc tableParsingSvc;
    private final TableToXslxSvc tableToXslxSvc;


    @Autowired
    public ParsingSvc(PdfIrfsCroppingSvc croppingSvc, PdfTableParsingSvc tableParsingSvc, TableToXslxSvc tableToXslxSvc) {
        this.croppingSvc = croppingSvc;
        this.tableParsingSvc = tableParsingSvc;
        this.tableToXslxSvc = tableToXslxSvc;
    }


    public Mono<XSSFWorkbook> parse(Mono<FilePart> file) {

        LOGGER.info("parsing filePart");
        return file
                .flatMap(filePart -> DataBufferUtils.join(filePart.content()))
                .map(this::load)
                //.map(this::load)
                .map(croppingSvc::extractIfrsRelevantPages)
                .map(tableParsingSvc::parseTablesFromPdf)
                .map(tableToXslxSvc::convertStringTableToXslx);

    }


    private PDDocument load(MultipartFile mpFile) {
        try {
            return PDDocument.load(mpFile.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            return new PDDocument();
        }
    }
    private PDDocument load(DataBuffer dataBuffer) {
        try {
            return PDDocument.load(dataBuffer.asInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            return new PDDocument();
        }
    }
}


