package de.koware.gacc.parser.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;


@RestController
@RequestMapping("/api/v1")
public class IfrsParsingController {

    private static final Logger LOGGER = LoggerFactory.getLogger(IfrsParsingController.class);

    private final ParsingSvc parsingSvc;

    @Autowired
    public IfrsParsingController(ParsingSvc parsingSvc) {
        this.parsingSvc = parsingSvc;
    }

    @GetMapping("/hello")
    public String greeting() {
        return "hello";
    }

    @GetMapping("")
    public ResponseEntity<Resource> getXlsx() {
        try {
            Path filepath = Paths.get("parsed.xlsx");
            UrlResource urlResource = new UrlResource(filepath.toUri());

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + "parsed.xlsx\"");

            return new ResponseEntity<Resource>(urlResource, headers, HttpStatus.OK);

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping("/parse-ifrs")
    public Mono<ResponseEntity<Resource>> convertToXlsx(@RequestPart("file") FilePart file) {


        LOGGER.info("file received");

        return this.parsingSvc.parse(Mono.just(file))
                .map(workbook -> {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();

                    try {
                        if (workbook != null) {
                            LOGGER.info("writing workbook to baos");
                            workbook.write(baos);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        LOGGER.warn("returning empty response");
                        return new ResponseEntity<Resource>(new ByteArrayResource(new byte[0]), HttpStatus.INTERNAL_SERVER_ERROR);
                    }

                    HttpHeaders headers = new HttpHeaders();
                    headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.filename() + "-parsed.xlsx\"");

                    XlsxMultipartFile xlsxMultipartFile = new XlsxMultipartFile(file.filename() + "-parsed.xlsx", baos.toByteArray());

                    LOGGER.info("returning resource");


                    return new ResponseEntity<Resource>(xlsxMultipartFile.getResource(), headers, HttpStatus.OK);

                });
    }

    @PostMapping("crop")
    public Mono<ResponseEntity<Resource>> cropRelevantPages(@RequestPart("file") FilePart file) {
        return this.parsingSvc.extractIfrsPages(Mono.just(file))
                .map(pdf -> {
                    HttpHeaders headers = new HttpHeaders();
                    headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.filename() + "-parsed.xlsx\"");
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    try {
                        pdf.save(baos);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    PdfMultipartFile croppedPdf = new PdfMultipartFile(file.filename() + "-cropped.pdf", baos.toByteArray());
                    return new ResponseEntity<>(croppedPdf.getResource(), headers, HttpStatus.OK);
                });
    }


}
