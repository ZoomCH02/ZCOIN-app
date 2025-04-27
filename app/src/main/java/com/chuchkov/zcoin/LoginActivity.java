package com.chuchkov.zcoin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.chuchkov.zcoin.api.ApiClient;
import com.chuchkov.zcoin.api.ZCoinApi;
import com.chuchkov.zcoin.api.models.UserResponse;
import com.chuchkov.zcoin.api.models.requests.LoginRequest;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText loginEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private ZCoinApi apiService;
    private SharedPreferences sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginEditText = findViewById(R.id.loginEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);

        apiService = ApiClient.getApiService();
        sharedPrefs = getSharedPreferences("GamePrefs", MODE_PRIVATE);

        loginButton.setOnClickListener(v -> loginUser());
    }

    private void loginUser() {
        String login = loginEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        apiService.login(new LoginRequest(login, password)).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    handleUserResponse(response.body());
                } else {
                    Log.e("API_ERROR", "Code: " + response.code() + " Message: " + response.message());
                    try {
                        Log.e("API_ERROR", "Error body: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(LoginActivity.this, "Ошибка входа", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Log.e("API_FAILURE", "Error: " + t.getMessage(), t);
                Toast.makeText(LoginActivity.this, "Ошибка сети", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleUserResponse(UserResponse response) {
        String userId = response.getUserId();
        sharedPrefs.edit().putString("user_id", userId).apply();
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }
}
