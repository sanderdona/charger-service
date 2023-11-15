package nl.dimensiontech.domotics.chargerservice.controller.controlleradvice;

import nl.dimensiontech.domotics.chargerservice.exception.InvalidIdentifierException;
import nl.dimensiontech.domotics.chargerservice.exception.RecordNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({InvalidIdentifierException.class, RecordNotFoundException.class})
    protected ResponseEntity<Object> handleBadRequest(RuntimeException exception, WebRequest request) {
        if (exception.getMessage() == null || exception.getMessage().isEmpty()) {
            return handleExceptionInternal(exception, "Not found", new HttpHeaders(), HttpStatus.NOT_FOUND, request);
        }
        return handleExceptionInternal(exception, exception.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }
}
