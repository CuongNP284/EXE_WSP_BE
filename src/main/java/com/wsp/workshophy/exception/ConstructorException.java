package com.wsp.workshophy.exception;


public class ConstructorException extends RuntimeException {

    public ConstructorException() {
        super("Suppress default constructor for noninstantiability");
    }

}
