package com.minhky.go4.model;

public class Cash {
    String cashBegin;
    String cashIn;
    String cashOut;
    String cashTotal;
    String email;
    String timeStamp;

    public Cash() {
    }

    public Cash(String cashOut, String email, String timeStamp) {
        this.cashOut = cashOut;
        this.email = email;
        this.timeStamp = timeStamp;
    }

    public Cash(String cashBegin, String cashIn, String cashTotal, String email, String timeStamp) {
        this.cashBegin = cashBegin;
        this.cashIn = cashIn;
        this.cashTotal = cashTotal;
        this.email = email;
        this.timeStamp = timeStamp;
    }

    public String getCashBegin() {
        return cashBegin;
    }

    public String getCashIn() {
        return cashIn;
    }

    public String getCashTotal() {
        return cashTotal;
    }

    public String getCashOut() {
        return cashOut;
    }

    public String getEmail() {
        return email;
    }

    public String getTimeStamp() {
        return timeStamp;
    }
}
