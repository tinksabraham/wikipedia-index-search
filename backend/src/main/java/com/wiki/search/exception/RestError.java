package com.wiki.search.exception;

import java.util.Date;

/**
 * @author Tinku Abraham
 */

public class RestError {

    private String message;
    private String error;
    private int status;
    private Long timestamp;

    public RestError(final String error, final String message, final int status) {
        this.message = message;
        this.error = error;
        this.status = status;
        this.timestamp = new Date().getTime();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(final String error) {
        this.error = error;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(final Long timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(final int status) {
        this.status = status;
    }
}
