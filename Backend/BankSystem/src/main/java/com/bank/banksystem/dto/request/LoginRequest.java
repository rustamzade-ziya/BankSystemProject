package com.bank.banksystem.dto.request;

public class LoginRequest {
    private String user_email;
    private String user_password;
    public LoginRequest() {}

    //for testing API we'll send request like this:
//    {
//        "email": "test@gmail.com",
//            "password": "123"
//    }
    public LoginRequest(String email, String password) {
        this.user_email = email;
        this.user_password = password;
    }

    public String getEmail()
    { return user_email; }

    public void setEmail(String email)
    { this.user_email = email.trim(); }

    public String getPassword()
    { return user_password; }

    public void setPassword(String password)
    { this.user_password = password.trim(); }
}