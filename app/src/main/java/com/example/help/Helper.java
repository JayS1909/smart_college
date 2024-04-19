package com.example.help;

public class Helper {

    String name,email,zprn,number,number2,pass;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getZPRN() {
        return zprn;
    }

    public void setZPRN(String ZPRN) {
        this.zprn = zprn;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getNumber2() {
        return number2;
    }

    public void setNumber2(String number2) {
        this.number2 = number2;
    }

    public String getPassword() {
        return pass;
    }

    public void setPassword(String password) {
        this.pass = pass;
    }

    public Helper(String name, String email, String zprn, String number, String number2, String password) {
        this.name = name;
        this.email = email;
        this.zprn = zprn;
        this.number = number;
        this.number2 = number2;
        this.pass = pass;
    }

    public Helper() {
    }
}
