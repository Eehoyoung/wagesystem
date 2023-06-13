package com.example.wagesystem.exeption;

public class LoginIdNotFoundException extends RuntimeException {

    public LoginIdNotFoundException(String s) {
        super(s);
    }
}
