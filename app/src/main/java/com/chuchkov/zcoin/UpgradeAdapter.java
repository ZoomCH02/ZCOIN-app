package com.chuchkov.zcoin;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UpgradeAdapter extends RecyclerView.Adapter<UpgradeAdapter.ViewHolder> {
    private List<UpgradeItem> items;
    private Context context;
    private OnBuyClickListener listener;

    public interface OnBuyClickListener {
        void onBuyClick(int position);
    }

    public UpgradeAdapter(List<UpgradeItem> items, OnBuyClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_upgrade, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UpgradeItem item = items.get(position);

        holder.title.setText(item.getTitle());
        holder.cost.setText(String.format("Стоимость: %d", item.getCost()));
        holder.profit.setText(String.format("Прибыль: +%d/клик", item.getProfit()));
        holder.image.setImageResource(item.getImageRes());

        if(item.isPurchased()) {
            holder.buyButton.setEnabled(false);
            holder.buyButton.setText("Куплено");
            holder.buyButton.setBackgroundColor(Color.GRAY); // Добавляем изменение цвета
        } else {
            holder.buyButton.setEnabled(true);
            holder.buyButton.setText("Купить");
            holder.buyButton.setBackgroundColor(ContextCompat.getColor(context, R.color.green)); // Возвращаем исходный цвет
        }

        holder.buyButton.setOnClickListener(v -> {
            if(listener != null && !item.isPurchased()) { // Добавляем проверку
                listener.onBuyClick(holder.getAdapterPosition()); // Используем актуальную позицию
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title;
        TextView cost;
        TextView profit;
        Button buyButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.item_image);
            title = itemView.findViewById(R.id.item_title);
            cost = itemView.findViewById(R.id.item_cost);
            profit = itemView.findViewById(R.id.item_profit);
            buyButton = itemView.findViewById(R.id.buy_button);
        }
    }
}