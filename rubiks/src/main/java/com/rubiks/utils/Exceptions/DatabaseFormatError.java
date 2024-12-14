package com.rubiks.utils.Exceptions;

public class DatabaseFormatError extends Exception{
    private static final long serialVersionUID = 1L;

	public DatabaseFormatError(String message) {
        super(message);
    }
}
