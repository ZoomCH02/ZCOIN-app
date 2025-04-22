package com.chuchkov.zcoin;

public class UpgradeItem {
    private String title;
    private int imageRes;
    private int cost;
    private int profit;
    private boolean purchased;

    public UpgradeItem(String title, int imageRes, int cost, int profit) {
        this.title = title;
        this.imageRes = imageRes;
        this.cost = cost;
        this.profit = profit;
        this.purchased = false;
    }

    // Геттеры
    public String getTitle() {
        return title;
    }

    public int getImageRes() {
        return imageRes;
    }

    public int getCost() {
        return cost;
    }

    public int getProfit() {
        return profit;
    }

    public boolean isPurchased() {
        return purchased;
    }

    // Сеттеры (только для изменяемых полей)
    public void setPurchased(boolean purchased) {
        this.purchased = purchased;
    }
}