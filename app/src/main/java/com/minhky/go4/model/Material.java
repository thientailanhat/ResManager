package com.minhky.go4.model;

public class Material {

    String mtrName;
    String mtrUnit;
    String mtrAmount;
    String mtrLimit;
    boolean limit;


    public Material(String mtrName, String mtrAmount) {
        this.mtrName = mtrName;
        this.mtrAmount = mtrAmount;
    }


    public String getMtrName() {
        return mtrName;
    }

    public String getMtrUnit() {
        return mtrUnit;
    }

    public String getMtrAmount() {
        return mtrAmount;
    }

    public String getMtrLimit() {
        return mtrLimit;
    }

    public boolean isLimit() {
        return limit;
    }
}
