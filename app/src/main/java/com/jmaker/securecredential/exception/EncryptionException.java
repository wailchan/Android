package com.jmaker.securecredential.exception;

/**
 * Created by wchan on 10/31/2016.
 * Represent a encryption related exception.
 */

public class EncryptionException extends SecureCredentialException {

    public EncryptionException(String message, Exception e) {
        super(message, e);
    }
}
