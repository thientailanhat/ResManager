package com.minhky.go4.model;

/**
 * Created by toila on 13/07/2016.
 */
public class Food {
    public String foodName;

    public String getFoodAmount() {
        return foodAmount;
    }

    public String foodAmount;



    public String foodNameSale;
    public String getFoodNameSale() {
        return foodNameSale;
    }



    public String foodAmountSale;
    public String getFoodAmountSale() {
        return foodAmountSale;
    }


    public String foodNameNotViet;
    public String foodPrice;
    public String foodElement;
    String foodQuantity;
    String foodSummary;
    public String foodUrl;
    String chefAddress;
    String chefUrl;
    String chefIntro;
    String foodCatName;
    public String foodPayment;
    String foodNote;
    String tableName;
    String foodQuantityFinished;
    boolean foodUndone;
    String discount;
    String groupNumLev1,groupNumLev2,groupNumLev3,groupNumLev4,groupNumLev5;
    String foodMoreInfo;
    String foodGroup;
    String foodAgent;



    public Food() {
    }

    public Food(String foodName, String foodQuantity) {
        this.foodName = foodName;
        this.foodQuantity = foodQuantity;
    }

    public Food(String foodName, String foodPrice, String foodUrl) {
        this.foodName = foodName;
        this.foodPrice = foodPrice;
        this.foodUrl = foodUrl;
    }

    public Food(String foodName, String foodPrice, String foodSummary, String foodUrl, String chefIntro) {
        this.foodName = foodName;
        this.foodPrice = foodPrice;
        this.foodSummary = foodSummary;
        this.foodUrl = foodUrl;
        this.chefIntro = chefIntro;
    }

    public Food(String foodAgent, String foodName, String foodGroup, String foodPrice, String foodUrl, String chefAddress, String chefIntro, String foodSummary, String foodMoreInfo) {
        this.foodName = foodName;
        this.foodPrice = foodPrice;
        this.foodUrl = foodUrl;
        this.chefAddress = chefAddress;
        this.chefIntro = chefIntro;
        this.foodSummary = foodSummary;
        this.foodMoreInfo = foodMoreInfo;
        this.foodGroup = foodGroup;
        this.foodAgent = foodAgent;
    }

    public Food(String foodName, String foodPrice, String foodQuantity, String foodPayment, String foodNote, String chefIntro) {
        this.foodName = foodName;
        this.foodPrice = foodPrice;
        this.foodQuantity = foodQuantity;
        this.foodPayment = foodPayment;
        this.foodNote = foodNote;
        this.chefIntro = chefIntro;
    }

    public Food(String tableName, String foodName, String foodPrice, String foodQuantity, String foodPayment, boolean foodUndone, String foodQuantityFinished, String foodNote) {
        this.tableName = tableName;
        this.foodName = foodName;
        this.foodPrice = foodPrice;
        this.foodQuantity = foodQuantity;
        this.foodPayment = foodPayment;
        this.foodUndone = foodUndone;
        this.foodQuantityFinished = foodQuantityFinished;
        this.foodNote = foodNote;
    }


    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getFoodSummary() {
        return foodSummary;
    }

    public String getFoodUrl() {
        return foodUrl;
    }

    public String getChefUrl() {
        return chefUrl;
    }

    public String getFoodCatName() {
        return foodCatName;
    }

    public String getFoodPrice() {
        return foodPrice;
    }

    public String getFoodQuantity() {
        return foodQuantity;
    }

    public String getFoodPayment() {
        return foodPayment;
    }

    public String getFoodNote() {
        return foodNote;
    }

    public String getFoodQuantityFinished() {
        return foodQuantityFinished;
    }

    public boolean isFoodUndone() {
        return foodUndone;
    }

    public String getTableName() {
        return tableName;
    }

    public String getDiscount() {
        return discount;
    }

    public String getGroupNumLev1() {
        return groupNumLev1;
    }

    public String getGroupNumLev2() {
        return groupNumLev2;
    }

    public String getGroupNumLev3() {
        return groupNumLev3;
    }

    public String getGroupNumLev4() {
        return groupNumLev4;
    }

    public String getGroupNumLev5() {
        return groupNumLev5;
    }

    public String getChefIntro() {
        return chefIntro;
    }

    public String getFoodMoreInfo() {
        return foodMoreInfo;
    }

    public Food(String chefAddress) {
        this.chefAddress = chefAddress;
    }

    public String getChefAddress() {
        return chefAddress;
    }

    public String getFoodGroup() {
        return foodGroup;
    }

    public String getFoodAgent() {
        return foodAgent;
    }
}
