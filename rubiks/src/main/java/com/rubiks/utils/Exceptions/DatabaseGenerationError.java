package com.rubiks.utils.Exceptions;


public class DatabaseGenerationError extends Exception{
    private static final long serialVersionUID = 1L;

	public DatabaseGenerationError(String message) {
        super(message);
    }
}