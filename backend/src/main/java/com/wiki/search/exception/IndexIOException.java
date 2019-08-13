package com.wiki.search.exception;


/**
 * @author Tinku Abraham
 */

public class IndexIOException extends RuntimeException {
    private final RestError restError;

    public IndexIOException(RestError restError) {
        super(String.format("%d %s / ErrorCode: %s", restError.getStatus(), restError.getError(), restError.getMessage()));
        this.restError = restError;
    }

    public RestError getRestError() {
        return restError;
    }
}
