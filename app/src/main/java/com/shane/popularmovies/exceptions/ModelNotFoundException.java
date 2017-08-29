package com.shane.popularmovies.exceptions;

/**
 * Created by Shane on 8/28/2017.
 */

public class ModelNotFoundException extends Exception {
    public ModelNotFoundException() {
        super();
    }

    public ModelNotFoundException(String message) {
        super(message);
    }

    public ModelNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ModelNotFoundException(Throwable cause) {
        super(cause);
    }
}
