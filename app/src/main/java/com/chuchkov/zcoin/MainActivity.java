package com.chuchkov.zcoin;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chuchkov.zcoin.api.ApiClient;
import com.chuchkov.zcoin.api.ZCoinApi;
import com.chuchkov.zcoin.api.models.UpgradeResponse;
import com.chuchkov.zcoin.api.models.UserResponse;
import com.chuchkov.zcoin.api.models.requests.BuyUpgradeRequest;
import com.chuchkov.zcoin.api.models.requests.UpdateUserRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements UpgradeAdapter.OnBuyClickListener {
    private int coins = 0;
    private int profitPerClick = 1;
    private TextView coinsText;
    private TextView profitText;
    private ImageButton coinButton;
    private RelativeLayout rootLayout;
    private RecyclerView upgradesRecycler;
    private LinearLayout topPanel;
    private List<UpgradeItem> upgrades = new ArrayList<>();
    private float lastTouchX;
    private float lastTouchY;
    private TextView communityText;

    private static final String PREFS_NAME = "GamePrefs";
    private String userId;
    private ZCoinApi apiService;
    private SharedPreferences sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPrefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        userId = sharedPrefs.getString("user_id", null);

        if (userId == null) {
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
            return;
        }

        // Инициализация всех View элементов ДО их использования
        initViews();
        setupUpgrades();
        setupNavigation();

        apiService = ApiClient.getApiService();
        loadUserData();

        setupCoinButton();
    }

    private void initViews() {
        // Основные элементы
        rootLayout = findViewById(R.id.rootLayout);
        topPanel = findViewById(R.id.topPanel);
        coinsText = findViewById(R.id.coinsText);
        profitText = findViewById(R.id.profitPerClickText);
        coinButton = findViewById(R.id.coinButton);
        upgradesRecycler = findViewById(R.id.upgrades_recycler);
        communityText = findViewById(R.id.communityText);

        // Проверка инициализации (для отладки)
        if (communityText == null) {
            Log.e("MainActivity", "communityText is null!");
        }
        if (upgradesRecycler == null) {
            Log.e("MainActivity", "upgradesRecycler is null!");
        }
    }

    private void setupUpgrades() {
        upgrades.clear();
        upgradesRecycler.setLayoutManager(new LinearLayoutManager(this));
        upgradesRecycler.setAdapter(new UpgradeAdapter(upgrades, this));
    }

    private void setupNavigation() {
        BottomNavigationView navView = findViewById(R.id.bottomNavigationView);

        navView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_upgrade) {
                showUpgrades();
                return true;
            } else if (itemId == R.id.navigation_home) {
                showMain();
                return true;
            } else if (itemId == R.id.navigation_community) {
                showCommunity();
                return true;
            }

            return false;
        });

        // Установите начальный выбранный элемент (опционально)
        navView.setSelectedItemId(R.id.navigation_home);
    }

    // Обновленные методы видимости с проверками
    private void showCommunity() {
        setViewVisibility(topPanel, View.GONE);
        setViewVisibility(coinButton, View.GONE);
        setViewVisibility(upgradesRecycler, View.GONE);
        setViewVisibility(communityText, View.VISIBLE);
    }

    private void showMain() {
        setViewVisibility(topPanel, View.VISIBLE);
        setViewVisibility(coinButton, View.VISIBLE);
        setViewVisibility(upgradesRecycler, View.GONE);
        setViewVisibility(communityText, View.GONE);
    }

    private void showUpgrades() {
        setViewVisibility(topPanel, View.GONE);
        setViewVisibility(coinButton, View.GONE);
        setViewVisibility(upgradesRecycler, View.VISIBLE);
        setViewVisibility(communityText, View.GONE);
    }

    // Вспомогательный метод для безопасного изменения видимости
    private void setViewVisibility(View view, int visibility) {
        if (view != null) {
            view.setVisibility(visibility);
        } else {
            Log.e("MainActivity", "Attempt to set visibility on null view");
        }
    }

    private void updateUI() {
        coinsText.setText(String.format("Монеты: %d", coins));
        profitText.setText(String.format("Прибыль/клик: %d", profitPerClick));
        upgradesRecycler.getAdapter().notifyDataSetChanged();
    }

    private void animateCoin() {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(coinButton, "scaleX", 1f, 0.9f, 1.1f, 1f),
                ObjectAnimator.ofFloat(coinButton, "scaleY", 1f, 0.9f, 1.1f, 1f)
        );
        animatorSet.setDuration(400);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.start();
    }

    private void loadUserData() {
        apiService.getUser(userId).enqueue(new Callback<UserResponse>() {
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
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Log.e("API_FAILURE", "Error: " + t.getMessage(), t);
                t.printStackTrace();
            }
        });
    }

    private void handleUserResponse(UserResponse response) {
        coins = response.getCoins();
        profitPerClick = response.getProfitPerClick();

        // Проверяем, есть ли список улучшений в ответе
        if (response.getUpgrades() != null) {
            upgrades.clear();
            for (UpgradeResponse upgrade : response.getUpgrades()) {
                int imageResId = getResources().getIdentifier(upgrade.getImageRes(), "drawable", getPackageName());
                UpgradeItem item = new UpgradeItem(
                        upgrade.getTitle(),
                        imageResId,
                        upgrade.getCost(),
                        upgrade.getProfit()
                );
                item.setPurchased(upgrade.isPurchased());
                upgrades.add(item);
            }
        }

        updateUI();
    }

    // Измените метод onBuyClick
    @Override
    public void onBuyClick(int position) {
        UpgradeItem item = upgrades.get(position);
        if (coins >= item.getCost() && !item.isPurchased()) {
            apiService.buyUpgrade(userId, new BuyUpgradeRequest(position + 1))
                    .enqueue(new Callback<UserResponse>() {
                        @Override
                        public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                // 1. Обновляем монеты и доход
                                coins = response.body().getCoins();
                                profitPerClick = response.body().getProfitPerClick();

                                // 2. Помечаем предмет как купленный
                                item.setPurchased(true);

                                // 3. Обновляем только эту карточку
                                upgradesRecycler.getAdapter().notifyItemChanged(position);

                                // 4. Обновляем UI (монеты и доход)
                                updateUI();
                            } else {
                                Toast.makeText(MainActivity.this, "Ошибка покупки", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<UserResponse> call, Throwable t) {
                            Toast.makeText(MainActivity.this, "Ошибка сети", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Недостаточно монет!", Toast.LENGTH_SHORT).show();
        }
    }

    // Измените обработчик клика по монете
    @SuppressLint("ClickableViewAccessibility")
    private void setupCoinButton() {
        coinButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    lastTouchX = event.getX();
                    lastTouchY = event.getY();
                }
                return false;
            }
        });

        coinButton.setOnClickListener(v -> {
            coins += profitPerClick;
            updateUI();
            animateCoin();
            showProfitText();

            apiService.updateUser(userId, new UpdateUserRequest(coins, profitPerClick))
                    .enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (!response.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "Ошибка сохранения", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(MainActivity.this, "Ошибка сети", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    private void showProfitText() {
        TextView profitTextView = new TextView(this);
        profitTextView.setText("+" + profitPerClick);
        profitTextView.setTextSize(28);
        profitTextView.setTextColor(ContextCompat.getColor(this, android.R.color.holo_orange_light));
        profitTextView.setTypeface(null, Typeface.BOLD);

        int[] location = new int[2];
        coinButton.getLocationOnScreen(location);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        params.leftMargin = (int) (location[0] + lastTouchX - 50);
        params.topMargin = (int) (location[1] + lastTouchY - 50);

        rootLayout.addView(profitTextView, params);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(profitTextView, "translationY", 0f, -300f),
                ObjectAnimator.ofFloat(profitTextView, "translationX", (float) (Math.random() * 100 - 50)),
                ObjectAnimator.ofFloat(profitTextView, "alpha", 1f, 0f)
        );
        animatorSet.setDuration(1200).start();
        animatorSet.addListener(new android.animation.Animator.AnimatorListener() {
            @Override public void onAnimationStart(android.animation.Animator animation) {}
            @Override public void onAnimationEnd(android.animation.Animator animation) {
                rootLayout.removeView(profitTextView);
            }
            @Override public void onAnimationCancel(android.animation.Animator animation) {}
            @Override public void onAnimationRepeat(android.animation.Animator animation) {}
        });
    }
}
