package com.chuchkov.zcoin.api.models.requests;

public class UpdateUserRequest {
    private int coins;
    private int profitPerClick;

    public UpdateUserRequest(int coins, int profitPerClick) {
        this.coins = coins;
        this.profitPerClick = profitPerClick;
    }
}