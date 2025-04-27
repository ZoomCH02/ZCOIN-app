package com.chuchkov.zcoin.api;

import com.chuchkov.zcoin.api.models.UserResponse;
import com.chuchkov.zcoin.api.models.requests.BuyUpgradeRequest;
import com.chuchkov.zcoin.api.models.requests.LoginRequest;
import com.chuchkov.zcoin.api.models.requests.RegisterRequest;
import com.chuchkov.zcoin.api.models.requests.UpdateUserRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ZCoinApi {
    @POST("api/register")
    Call<UserResponse> register(@Body RegisterRequest request);

    @POST("api/login")
    Call<UserResponse> login(@Body LoginRequest request);

    @GET("api/user/{userId}")
    Call<UserResponse> getUser(@Path("userId") String userId);

    @PUT("api/user/{userId}")
    Call<Void> updateUser(@Path("userId") String userId, @Body UpdateUserRequest request);

    @POST("api/user/{userId}/upgrade")
    Call<UserResponse> buyUpgrade(@Path("userId") String userId, @Body BuyUpgradeRequest request);
}