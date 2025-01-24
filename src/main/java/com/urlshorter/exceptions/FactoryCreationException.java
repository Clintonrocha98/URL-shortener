package com.urlshorter.exceptions;

public class FactoryCreationException extends RuntimeException {
  public FactoryCreationException(String message, Throwable cause) {
    super(message, cause);
  }
}