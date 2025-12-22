package com.bank.banksystem.dto.response;

import java.time.LocalDate;

public class UserResponse {
    private Long id;
    private String user_email;
    private String user_name;
    private String user_surname;
    private LocalDate user_birthday;
    private Long user_salary;
    private String user_phone_number;

    //UserResponse returns back data if login is successful, notice it DOES NOT RETURN back PASSWORD

    public UserResponse(Long id, String user_email, String user_name, String user_surname, LocalDate user_birthday, Long user_salary, String user_phone_number) {
        this.id = id;
        this.user_email = user_email;
        this.user_name = user_name;
        this.user_surname = user_surname;
        this.user_birthday = user_birthday;
        this.user_salary = user_salary;
        this.user_phone_number = user_phone_number;
    }


    //getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_surname() {
        return user_surname;
    }

    public void setUser_surname(String user_surname) {
        this.user_surname = user_surname;
    }

    public LocalDate getUser_birthday() {
        return user_birthday;
    }

    public void setUser_birthday(LocalDate user_birthday) {
        this.user_birthday = user_birthday;
    }

    public Long getUser_salary() {
        return user_salary;
    }

    public void setUser_salary(Long user_salary) {
        this.user_salary = user_salary;
    }

    public String getUser_phone_number() {
        return user_phone_number;
    }

    public void setUser_phone_number(String user_phone_number) {
        this.user_phone_number = user_phone_number;
    }

}