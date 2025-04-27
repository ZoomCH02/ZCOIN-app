package com.chuchkov.zcoin.api.models;

import java.util.List;

public class UserResponse {
    private String userId;
    private String login;
    private int coins;
    private int profitPerClick;
    private List<UpgradeResponse> upgrades;

    // Геттеры
    public String getUserId() {
        return userId;
    }

    public String getLogin() {
        return login;
    }

    public int getCoins() {
        return coins;
    }

    public int getProfitPerClick() {
        return profitPerClick;
    }

    public List<UpgradeResponse> getUpgrades() {
        return upgrades;
    }

    // Сеттеры
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public void setProfitPerClick(int profitPerClick) {
        this.profitPerClick = profitPerClick;
    }

    public void setUpgrades(List<UpgradeResponse> upgrades) {
        this.upgrades = upgrades;
    }
}
