package br.com.katsilis.mercadolance.exception.handler;

import br.com.katsilis.mercadolance.exception.BidWaitPeriodException;
import br.com.katsilis.mercadolance.exception.DatabaseException;
import br.com.katsilis.mercadolance.exception.JsonParsingException;
import br.com.katsilis.mercadolance.exception.ThreadException;
import br.com.katsilis.mercadolance.exception.notfound.generic.GenericNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class ErrorHandler {

    private static final String INTERNAL_ERROR_MESSAGE = "An internal error happened";

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception e) {
        log.error("An untreated exception happened.", e);
        return ResponseEntity.badRequest().body(INTERNAL_ERROR_MESSAGE);
    }

    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<Object> handleDatabaseException(DatabaseException e) {
        log.error("A DB error happened.", e);
        return ResponseEntity.internalServerError().body("An internal database error happened");
    }

    @ExceptionHandler(GenericNotFoundException.class)
    public ResponseEntity<Object> handleGenericNotFoundException(GenericNotFoundException e) {
        log.error("Entity not found: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("An illegal argument exception happened: {}", e.getMessage());
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(JsonParsingException.class)
    public ResponseEntity<Object> handleJsonParsingException(JsonParsingException e) {
        log.error("A JSON parsing error happened.", e);
        return ResponseEntity.internalServerError().body(INTERNAL_ERROR_MESSAGE);
    }

    @ExceptionHandler(ThreadException.class)
    public ResponseEntity<Object> handleThreadException(ThreadException e) {
        log.error("A thread error happened.", e);
        return ResponseEntity.internalServerError().body(INTERNAL_ERROR_MESSAGE);
    }

    @ExceptionHandler(BidWaitPeriodException.class)
    public ResponseEntity<Object> handleBidWaitPeriodException(BidWaitPeriodException e) {
        log.error("Bid waiting time has finished: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(e.getMessage());
    }
}
