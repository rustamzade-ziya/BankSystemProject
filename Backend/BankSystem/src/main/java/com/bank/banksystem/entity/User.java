package com.bank.banksystem.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "user_information")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_ID")
    private Long user_id;

    @Column(name = "user_email", unique = true, nullable = false)
    private String user_email;

    @Column(name = "user_password", nullable = false)
    private String user_password;

    @Column(name = "user_name", nullable = false)
    private String user_name;

    @Column(name = "user_surname", nullable = false)
    private String user_surname;

    @Column(name = "user_salary")
    private Long user_salary;

    @Column(name = "user_birthday")
    private LocalDate user_birthday;

    @Column(name = "user_id_card_no_series")
    private String user_id_card_no_series;

    @Column(name = "user_id_card_no")
    private String user_id_card_no;

    @Column(name = "user_fin")
    private String user_fin;

    @Column(name = "user_phone_number")
    private String user_phone_number;

    @Column(name = "user_codeword")
    private String user_codeword;

    @OneToMany
    @JoinColumn(name = "user_ID", referencedColumnName = "user_ID")
    private List<DebitCard> debitCards;

    @OneToMany
    @JoinColumn(name = "user_ID", referencedColumnName = "user_ID")
    private List<CreditCard> creditCards;

    @OneToMany
    @JoinColumn(name = "user_ID", referencedColumnName = "user_ID")
    private List<CreditCardStatement> CreditCardStatement;

    @OneToMany
    @JoinColumn(name = "user_ID", referencedColumnName = "user_ID")
    private List<Cashback> Cashback;

    public User(Long user_id, String user_email, String user_password, String user_name, String user_surname, Long user_salary, LocalDate user_birthday, String user_id_card_no_series, String user_id_card_no, String user_fin, String user_phone_number, String user_codeword) {
        this.user_id = user_id;
        this.user_email = user_email;
        this.user_password = user_password;
        this.user_name = user_name;
        this.user_surname = user_surname;
        this.user_salary = user_salary;
        this.user_birthday = user_birthday;
        this.user_id_card_no_series = user_id_card_no_series;
        this.user_id_card_no = user_id_card_no;
        this.user_fin = user_fin;
        this.user_phone_number = user_phone_number;
        this.user_codeword = user_codeword;
    }

    public User() {}

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

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

    public Long getUser_salary() {
        return user_salary;
    }

    public void setUser_salary(Long user_salary) {
        this.user_salary = user_salary;
    }

    public LocalDate getUser_birthday() {
        return user_birthday;
    }

    public void setUser_birthday(LocalDate user_birthday) {
        this.user_birthday = user_birthday;
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

    public List<DebitCard> getDebitCards() {
        return debitCards;
    }

    public void setDebitCards(List<DebitCard> debitCards) {
        this.debitCards = debitCards;
    }

    public List<CreditCard> getCreditCards() {
        return creditCards;
    }

    public void setCreditCards(List<CreditCard> creditCards) {
        this.creditCards = creditCards;
    }

    public List<CreditCardStatement> getCreditCardStatement() {
        return CreditCardStatement;
    }

    public void setCreditCardStatement(List<CreditCardStatement> creditCardStatement) {
        CreditCardStatement = creditCardStatement;
    }

    public List<Cashback> getCashback() {
        return Cashback;
    }

    public void setCashback(List<Cashback> cashback) {
        Cashback = cashback;
    }

    public String getUser_codeword() {
        return user_codeword;
    }

    public void setUser_codeword(String user_codeword) {
        this.user_codeword = user_codeword;
    }
}

