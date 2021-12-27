package de.koware.gacc.parser.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Controller()
public class StaticContentController {

    @Bean
    public RouterFunction<ServerResponse> indexRouter(@Value("classpath:public/index.html") final Resource indexHtml) {
        return route(GET("/"), request -> ok().contentType(MediaType.TEXT_HTML).bodyValue(indexHtml));
    }

    @Bean
    public RouterFunction<ServerResponse> mainJsRouter(@Value("classpath:public/main.js") final Resource mainjs) {
        return route(GET("/main.js"), request -> ok().contentType(MediaType.APPLICATION_OCTET_STREAM).bodyValue(mainjs));
    }

    @Bean
    public RouterFunction<ServerResponse> mainCssRouter(@Value("classpath:public/main.css") final Resource maincss) {
        return route(GET("/main1.css"), request -> ok().contentType(MediaType.APPLICATION_OCTET_STREAM).bodyValue(maincss));
    }
}
