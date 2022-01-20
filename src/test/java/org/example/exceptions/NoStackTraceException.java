package org.example.exceptions;

public class NoStackTraceException extends Exception {
    public NoStackTraceException(String errorMessage) { super(errorMessage); }
    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
