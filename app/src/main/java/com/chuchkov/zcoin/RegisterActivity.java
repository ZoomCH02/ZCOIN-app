package com.chuchkov.zcoin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.chuchkov.zcoin.api.ApiClient;
import com.chuchkov.zcoin.api.ZCoinApi;
import com.chuchkov.zcoin.api.models.UserResponse;
import com.chuchkov.zcoin.api.models.requests.RegisterRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private TextInputEditText loginEditText;
    private TextInputEditText passwordEditText;
    private MaterialButton registerButton;
    private TextView loginTextView;
    private ZCoinApi apiService;
    private SharedPreferences sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        loginEditText = findViewById(R.id.loginEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        registerButton = findViewById(R.id.registerButton);
        loginTextView = findViewById(R.id.loginTextView);

        apiService = ApiClient.getApiService();
        sharedPrefs = getSharedPreferences("GamePrefs", MODE_PRIVATE);

        registerButton.setOnClickListener(v -> registerUser());

        // Добавляем переход на экран входа
        loginTextView.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void registerUser() {
        String login = loginEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (login.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        registerButton.setEnabled(false);
        registerButton.setText("Регистрация...");

        apiService.register(new RegisterRequest(login, password)).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                registerButton.setEnabled(true);
                registerButton.setText("Зарегистрироваться");

                if (response.isSuccessful() && response.body() != null) {
                    handleUserResponse(response.body());
                } else {
                    showError(response);
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                registerButton.setEnabled(true);
                registerButton.setText("Зарегистрироваться");
                Toast.makeText(RegisterActivity.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("API_FAILURE", "Error: " + t.getMessage(), t);
            }
        });
    }

    private void showError(Response<UserResponse> response) {
        try {
            String error = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
            Toast.makeText(this, "Ошибка: " + error, Toast.LENGTH_SHORT).show();
            Log.e("API_ERROR", "Code: " + response.code() + " Error: " + error);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleUserResponse(UserResponse response) {
        String userId = response.getUserId();
        sharedPrefs.edit().putString("user_id", userId).apply();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}