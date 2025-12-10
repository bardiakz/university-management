//package io.github.bardiakz.api_gateway;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
//
//import org.springframework.core.annotation.Order;
//import org.springframework.core.io.buffer.DataBufferFactory;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ResponseStatusException;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
//import java.nio.charset.StandardCharsets;
//import java.time.LocalDateTime;
//
//@Component
//@Order(-1)
//public class GlobalExceptionHandler implements ErrorWebExceptionHandler {
//
//    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
//
//    @Override
//    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
//        log.error("Error occurred: ", ex);
//
//        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
//        String message = "An unexpected error occurred";
//
//        if (ex instanceof ResponseStatusException) {
//            ResponseStatusException rse = (ResponseStatusException) ex;
//            status = HttpStatus.valueOf(rse.getStatusCode().value());
//            message = rse.getReason() != null ? rse.getReason() : message;
//        } else if (ex instanceof IllegalArgumentException) {
//            status = HttpStatus.BAD_REQUEST;
//            message = ex.getMessage();
//        }
//
//        exchange.getResponse().setStatusCode(status);
//        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
//
//        String errorResponse = String.format(
//                "{\"error\": \"%s\", \"message\": \"%s\", \"status\": %d, \"timestamp\": \"%s\"}",
//                status.getReasonPhrase(),
//                message,
//                status.value(),
//                LocalDateTime.now()
//        );
//
//        DataBufferFactory bufferFactory = exchange.getResponse().bufferFactory();
//        return exchange.getResponse()
//                .writeWith(Mono.just(bufferFactory.wrap(errorResponse.getBytes(StandardCharsets.UTF_8))));
//    }
//}