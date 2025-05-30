package br.com.katsilis.mercadolance.exception.handler;

import br.com.katsilis.mercadolance.exception.BidWaitPeriodException;
import br.com.katsilis.mercadolance.exception.DatabaseException;
import br.com.katsilis.mercadolance.exception.JsonParsingException;
import br.com.katsilis.mercadolance.exception.ThreadException;
import br.com.katsilis.mercadolance.exception.illegalargument.BidIllegalArgumentException;
import br.com.katsilis.mercadolance.exception.model.ErrorResponse;
import br.com.katsilis.mercadolance.exception.notfound.generic.GenericNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
@Slf4j
public class ErrorHandler {

    private static final String INTERNAL_ERROR_MESSAGE = "An internal error happened";
    private static final String GENERIC_USER_ERROR_MESSAGE = "Um erro ocorreu";

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("An untreated exception happened.", e);

        ErrorResponse errorResponse = new ErrorResponse(
            GENERIC_USER_ERROR_MESSAGE,
            INTERNAL_ERROR_MESSAGE
        );

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceNotFoundException(NoResourceFoundException e) {
        log.error("A Resource not found exception happened. Path: {}, Http Method: {}, ", e.getResourcePath(), e.getHttpMethod(), e);

        ErrorResponse errorResponse = new ErrorResponse(
            "O recurso não foi encontrado",
            INTERNAL_ERROR_MESSAGE
        );

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<Object> handleDatabaseException(DatabaseException e) {
        log.error("A DB error happened.", e);

        ErrorResponse errorResponse = new ErrorResponse(
            GENERIC_USER_ERROR_MESSAGE,
            "An internal database error happened"
        );

        return ResponseEntity.internalServerError().body(errorResponse);
    }

    @ExceptionHandler(GenericNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleGenericNotFoundException(GenericNotFoundException e) {
        log.error("Entity not found: {}", e.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
            GENERIC_USER_ERROR_MESSAGE,
            INTERNAL_ERROR_MESSAGE
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(BidIllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(BidIllegalArgumentException e) {
        log.error("An illegal bid exception happened: {}", e.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
            e.getUserMessage(),
            INTERNAL_ERROR_MESSAGE
        );

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("An illegal argument exception happened: {}", e.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
            GENERIC_USER_ERROR_MESSAGE,
            INTERNAL_ERROR_MESSAGE
        );

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(JsonParsingException.class)
    public ResponseEntity<ErrorResponse> handleJsonParsingException(JsonParsingException e) {
        log.error("A JSON parsing error happened.", e);

        ErrorResponse errorResponse = new ErrorResponse(
            GENERIC_USER_ERROR_MESSAGE,
            INTERNAL_ERROR_MESSAGE
        );

        return ResponseEntity.internalServerError().body(errorResponse);
    }

    @ExceptionHandler(ThreadException.class)
    public ResponseEntity<ErrorResponse> handleThreadException(ThreadException e) {
        log.error("A thread error happened.", e);

        ErrorResponse errorResponse = new ErrorResponse(
            "Um erro ocorreu durante o lance, tente novamente",
            INTERNAL_ERROR_MESSAGE
        );

        return ResponseEntity.internalServerError().body(errorResponse);
    }

    @ExceptionHandler(BidWaitPeriodException.class)
    public ResponseEntity<ErrorResponse> handleBidWaitPeriodException(BidWaitPeriodException e) {
        log.error("Bid waiting time has finished: {}", e.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
            "Um erro ocorreu durante o lance, tente novamente",
            e.getMessage()
        );

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse);
    }
}
