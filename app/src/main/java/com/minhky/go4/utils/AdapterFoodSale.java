package com.minhky.go4.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.minhky.go4.R;
import com.minhky.go4.model.Food;
import com.minhky.go4.restaurant.ResCashier;

import java.util.List;

public class AdapterFoodSale extends RecyclerView.Adapter<AdapterFoodSale.FoodSaleViewHolder> {

    Context context;

    private List<Food> items;
    private Food foodSale;
    private Activity activity;

    private String foodName;

    public AdapterFoodSale() {
        super();

    }

    public AdapterFoodSale(Context context, List<Food> items, Activity activity) {
        this.context = context;
        this.items = items;
        this.activity = activity;
    }

    @Override
    public FoodSaleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food_sale, parent, false);
        return new FoodSaleViewHolder(v);

    }

    @Override
    public void onBindViewHolder(FoodSaleViewHolder holder, int position) {
        foodSale = items.get(position);

        foodName = foodSale.getFoodName();


        holder.foodNameKey.setText(foodSale.getFoodName());
        holder.foodAmountValue.setText(Utils.convertNumber(foodSale.getFoodQuantity()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class FoodSaleViewHolder extends RecyclerView.ViewHolder {
        TextView foodNameKey, foodAmountValue;
        public FoodSaleViewHolder(View itemView) {
            super(itemView);
            foodNameKey = itemView.findViewById(R.id.tv_name);
            foodAmountValue = itemView.findViewById(R.id.tv_amount);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builderPrice = new AlertDialog.Builder(activity);
                    builderPrice.setTitle(foodName);
                    builderPrice.setMessage("Tổng doanh thu ngày hôm nay là ");
                    builderPrice.show();
                }
            });


        }
    }

}
