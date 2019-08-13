package com.wiki.search.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * @author Tinku Abraham
 */

@ControllerAdvice(annotations = RestController.class)
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    private final Logger log = LogManager.getLogger(this.getClass());

    // 400 BAD Request - for e.g when parse exception occurs like wildcard query in front
    @ExceptionHandler(value = { IndexSearchParseException.class })
    public ResponseEntity<Object> handleQueryParseException(final IndexSearchParseException ex, final WebRequest request) {
        log.info("Parse- wild card not allowed as first term or wrong search term", ex);
        return new ResponseEntity<>(ex.getRestError(), HttpStatus.BAD_REQUEST);
    }

    // 500 IOException- for e.g index files not found or directory
    @ExceptionHandler(value = { IndexIOException.class })
    public ResponseEntity<RestError> handleIOException(final IndexIOException ex, final WebRequest request) {
        log.info("Index- directory cannot be searched or found", ex);
        return new ResponseEntity<>(ex.getRestError(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
