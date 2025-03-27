package com.demo.tes.backend.exception.security;

import java.io.Serial;

public class ServiceException extends Exception {
    @Serial
    private static final long serialVersionUID = 8024895122045859786L;

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
