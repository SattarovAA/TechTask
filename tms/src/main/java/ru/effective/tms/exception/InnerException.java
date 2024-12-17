package ru.effective.tms.exception;

public class InnerException extends RuntimeException{
    public InnerException(String message) {
        super(message);
    }
}
