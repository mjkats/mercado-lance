package br.com.katsilis.mercadolance.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class ErrorHandler {

    //TODO: Melhorar logica do error handler
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntimeException(RuntimeException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<Object> handleDatabaseException(DatabaseException e) {
        log.error("Exception {} happened on {} operation. Message: {}", e.getClass().getSimpleName(), e.getAction(), e.getMessage());
        return ResponseEntity.internalServerError().body("An internal error happened");
    }
}
