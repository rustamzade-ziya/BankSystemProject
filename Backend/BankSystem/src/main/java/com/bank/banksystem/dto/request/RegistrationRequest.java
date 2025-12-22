package com.bank.banksystem.dto.request;

import java.time.LocalDate;

public class RegistrationRequest {
        private String user_email;
        private String user_password;
        private String user_name;
        private String user_surname;
        private LocalDate user_birthday;
        private Long user_salary;
        private String user_id_card_no_series;
        private String user_id_card_no;
        private String user_fin;
        private String user_phone_number;
        private String user_codeword;

        // constructors
        public RegistrationRequest() {}

    public RegistrationRequest(String user_email, String user_password, String user_name, String user_surname, LocalDate user_birthday, Long user_salary, String user_id_card_no_series, String user_id_card_no, String user_fin, String user_phone_number, String user_codeword) {
        this.user_email = user_email;
        this.user_password = user_password;
        this.user_name = user_name;
        this.user_surname = user_surname;
        this.user_birthday = user_birthday;
        this.user_salary = user_salary;
        this.user_id_card_no_series = user_id_card_no_series;
        this.user_id_card_no = user_id_card_no;
        this.user_fin = user_fin;
        this.user_phone_number = user_phone_number;
        this.user_codeword = user_codeword;
    }

    //getters and setters

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getUser_password() {
        return user_password;
    }

    public void setUser_password(String user_password) {
        this.user_password = user_password;
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

    public String getUser_id_card_no_series() {
        return user_id_card_no_series;
    }

    public void setUser_id_card_no_series(String user_id_card_no_series) {
        this.user_id_card_no_series = user_id_card_no_series;
    }

    public String getUser_id_card_no() {
        return user_id_card_no;
    }

    public void setUser_id_card_no(String user_id_card_no) {
        this.user_id_card_no = user_id_card_no;
    }

    public String getUser_fin() {
        return user_fin;
    }

    public void setUser_fin(String user_fin) {
        this.user_fin = user_fin;
    }

    public String getUser_phone_number() {
        return user_phone_number;
    }

    public void setUser_phone_number(String user_phone_number) {
        this.user_phone_number = user_phone_number;
    }

    public String getUser_codeword() {
        return user_codeword;
    }

    public void setUser_codeword(String user_codeword) {
        this.user_codeword = user_codeword;
    }
}

