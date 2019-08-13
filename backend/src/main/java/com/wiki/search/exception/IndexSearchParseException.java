package com.wiki.search.exception;

/**
 * @author Tinku Abraham
 */

public class IndexSearchParseException extends RuntimeException {
    private final RestError restError;

    public IndexSearchParseException(RestError restError) {
        super(String.format("%d %s / ErrorCode: %s", restError.getStatus(), restError.getError(), restError.getMessage()));
        this.restError = restError;
    }

    public RestError getRestError() {
        return restError;
    }
}

