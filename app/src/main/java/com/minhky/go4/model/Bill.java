package com.minhky.go4.model;

public class Bill  {

    String billID;
    String billCode;
    String shopCode;
    String payment;
    String timeStamp;
    String cashBack;
    boolean sync;

    public Bill() {
    }

    public Bill(String billCode, String payment, String timeStamp,String cashBack) {
        this.billCode = billCode;
        this.payment = payment;
        this.timeStamp = timeStamp;
        this.cashBack = cashBack;

    }

    public String getBillCode() {
        return billCode;
    }

    public String getPayment() {
        return payment;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
    }

    public String getShopCode() {
        return shopCode;
    }
}

