package org.example.exceptions;

public class DoctorNotFoundExceptions extends RuntimeException{
    public DoctorNotFoundExceptions(String message){
        super(message);
    }
}
