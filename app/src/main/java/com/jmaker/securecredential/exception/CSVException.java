package com.jmaker.securecredential.exception;

/**
 * Created by wchan on 10/31/2016.
 * Represent a CSV file related exception.
 */

public class CSVException extends SecureCredentialException {

    public CSVException(String message, Exception e) {
        super(message, e);
    }
}
