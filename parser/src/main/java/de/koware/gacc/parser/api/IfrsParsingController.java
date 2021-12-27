package de.koware.gacc.parser.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


@RestController
@RequestMapping("/api/v1")
public class IfrsParsingController {

    private static final Logger LOGGER = LoggerFactory.getLogger(IfrsParsingController.class);

    private final ParsingSvc parsingSvc;

    @Autowired
    public IfrsParsingController(ParsingSvc parsingSvc) {
        this.parsingSvc = parsingSvc;
    }

    @PostMapping("/parse-ifrs")
    public Mono<ResponseEntity<Resource>> convertToXlsx(@RequestParam ("files") MultipartFile file) {


        LOGGER.info("file received");

        return this.parsingSvc.parse(Mono.just(file))
                .map(workbook -> {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();

                    try {
                        if (workbook != null) {
                            workbook.write(baos);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        LOGGER.warn("returning empty response");
                        return new ResponseEntity<Resource>(new ByteArrayResource(new byte[0]), HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                    ByteArrayResource byteArrayResource = new ByteArrayResource(baos.toByteArray());

                    return new ResponseEntity<Resource>(byteArrayResource, HttpStatus.OK);
                });
    }


}
