package org.camera.cameratool.exception;

public class ServiceException extends RuntimeException {
    private final String message;

    public ServiceException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
