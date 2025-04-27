package com.chuchkov.zcoin.api.models.requests;

public class RegisterRequest {
    private String login;
    private String password;

    public RegisterRequest(String login, String password) {
        this.login = login;
        this.password = password;
    }
}
