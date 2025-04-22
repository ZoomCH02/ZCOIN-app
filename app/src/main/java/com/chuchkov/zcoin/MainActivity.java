package com.chuchkov.zcoin;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

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

    private static final String PREFS_NAME = "GamePrefs";
    private static final String KEY_COINS = "coins";
    private static final String KEY_PROFIT = "profit";
    private static final String KEY_UPGRADE_PREFIX = "upgrade_";

    private TextView communityText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация всех элементов
        initViews();
        setupUpgrades();
        loadGameData(); // Загружаем сохранённые данные
        setupNavigation();
        restoreState(savedInstanceState);

        // Обработчики событий
        setupCoinButton();
    }

    private void initViews() {
        coinsText = findViewById(R.id.coinsText);
        profitText = findViewById(R.id.profitPerClickText);
        coinButton = findViewById(R.id.coinButton);
        rootLayout = findViewById(R.id.rootLayout);
        upgradesRecycler = findViewById(R.id.upgrades_recycler);
        topPanel = findViewById(R.id.topPanel);
    }

    private void setupUpgrades() {
        upgrades.clear();
        upgrades.add(new UpgradeItem("Улучшение экипировки", R.drawable.weapon, 50, 2));
        upgrades.add(new UpgradeItem("Оборона границ", R.drawable.pvo, 200, 5));
        upgrades.add(new UpgradeItem("Поддержка РБ", R.drawable.bel, 1000, 10));
        upgrades.add(new UpgradeItem("Поддержка КНДР", R.drawable.kndr, 5000, 20));
        upgrades.add(new UpgradeItem("Поддержка КНР", R.drawable.kit, 20000, 50));
        upgrades.add(new UpgradeItem("Создание БРИКС", R.drawable.briks, 100000, 100));
        upgrades.add(new UpgradeItem("Интервью Такера Карлосона", R.drawable.tak, 500000, 200));
        upgrades.add(new UpgradeItem("Политическое убежище для Канье Уэста", R.drawable.kanye, 2000000, 500));
        upgrades.add(new UpgradeItem("Ухудщение репутации презедента США", R.drawable.biden, 10000000, 1000));
        upgrades.add(new UpgradeItem("Трамп выигрывает выборы", R.drawable.biden_vs_trump, 50000000, 2000));
        upgrades.add(new UpgradeItem("ZCOIN смещает биткоин", R.drawable.btc, 200000000, 5000));
        upgrades.add(new UpgradeItem("Запуск физ. карт для оплаты криптой", R.drawable.zz, 700000000, 10000));
        upgrades.add(new UpgradeItem("ZCOIN становится главной валютой во вселенной", R.drawable.ufo, 1000000000, 20000));

        // После создания списка загружаем данные
        loadGameData();

        upgradesRecycler.setLayoutManager(new LinearLayoutManager(this));
        upgradesRecycler.setAdapter(new UpgradeAdapter(upgrades, this));
    }

    private void setupNavigation() {
        BottomNavigationView navView = findViewById(R.id.bottomNavigationView);
        communityText = findViewById(R.id.communityText);
        navView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_upgrade) {
                showUpgrades();
            } else if(item.getItemId() == R.id.navigation_home) {
                showMain();
            } else if (item.getItemId() == R.id.navigation_community) {
                showCommunity();
            }
            return true;
        });
    }

    private void restoreState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            coins = savedInstanceState.getInt("coins");
            profitPerClick = savedInstanceState.getInt("profit");
            for (int i = 0; i < upgrades.size(); i++) {
                upgrades.get(i).setPurchased(savedInstanceState.getBoolean("upgrade_" + i));
            }
        }
        updateUI();
    }

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
        });
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
        animatorSet.start(); // Вызываем start() отдельно
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

    private void showCommunity() {
        topPanel.setVisibility(View.GONE);
        coinButton.setVisibility(View.GONE);
        upgradesRecycler.setVisibility(View.GONE);
        communityText.setVisibility(View.VISIBLE);
    }

    private void showMain() {
        topPanel.setVisibility(View.VISIBLE);
        coinButton.setVisibility(View.VISIBLE);
        upgradesRecycler.setVisibility(View.GONE);
        communityText.setVisibility(View.GONE);
    }

    private void showUpgrades() {
        topPanel.setVisibility(View.GONE);
        coinButton.setVisibility(View.GONE);
        upgradesRecycler.setVisibility(View.VISIBLE);
        communityText.setVisibility(View.GONE);
    }

    @Override
    public void onBuyClick(int position) {
        UpgradeItem item = upgrades.get(position);
        if (coins >= item.getCost() && !item.isPurchased()) {
            coins -= item.getCost();
            profitPerClick += item.getProfit();
            item.setPurchased(true);
            updateUI();
            saveGameData(); // Сохраняем сразу после изменения
        } else {
            Toast.makeText(this, "Недостаточно монет!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("coins", coins);
        outState.putInt("profit", profitPerClick);
        for (int i = 0; i < upgrades.size(); i++) {
            outState.putBoolean("upgrade_" + i, upgrades.get(i).isPurchased());
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        coins = savedInstanceState.getInt("coins");
        profitPerClick = savedInstanceState.getInt("profit");
        for (int i = 0; i < upgrades.size(); i++) {
            upgrades.get(i).setPurchased(savedInstanceState.getBoolean("upgrade_" + i));
        }
        updateUI();
    }

    private void saveGameData() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putInt(KEY_COINS, coins);
        editor.putInt(KEY_PROFIT, profitPerClick);

        // Сохраняем статусы улучшений
        for (int i = 0; i < upgrades.size(); i++) {
            editor.putBoolean(KEY_UPGRADE_PREFIX + i, upgrades.get(i).isPurchased());
        }

        editor.apply();
    }

    private void loadGameData() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        coins = prefs.getInt(KEY_COINS, 0);
        profitPerClick = prefs.getInt(KEY_PROFIT, 1);

        // Загружаем статусы улучшений
        for (int i = 0; i < upgrades.size(); i++) {
            boolean purchased = prefs.getBoolean(KEY_UPGRADE_PREFIX + i, false);
            upgrades.get(i).setPurchased(purchased);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveGameData();
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveGameData();
    }
}