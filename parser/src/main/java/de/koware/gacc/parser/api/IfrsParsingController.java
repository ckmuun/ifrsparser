package de.koware.gacc.parser.api;

import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1")
public class IfrsParsingController {


    public MultipartFile convertToXlss(Flux<FilePart> filePartFlux) {

        // TOOD
        return null;
    }
}
