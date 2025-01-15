package com.urlshorter.exceptions;


public class UrlException extends Exception {

    private final int statusCode;

    public UrlException(String msg, int statusCode) {
        super(msg);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return this.statusCode;
    }
}
