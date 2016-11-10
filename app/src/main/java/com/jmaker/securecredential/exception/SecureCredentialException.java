package com.jmaker.securecredential.exception;

/**
 * Created by wchan on 10/31/2016.
 * The root exception class.
 */

public class SecureCredentialException extends Exception {

    public SecureCredentialException(String message, Exception e) {
        super(message, e);
    }
}
