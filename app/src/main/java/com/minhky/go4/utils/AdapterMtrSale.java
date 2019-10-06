package com.minhky.go4.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.minhky.go4.R;
import com.minhky.go4.model.Food;
import com.minhky.go4.model.Material;

import java.util.List;

public class AdapterMtrSale extends RecyclerView.Adapter<AdapterMtrSale.MtrSaleViewHolder> {

    Context context;

    private List<Material> item;
    private Material mtrSale;
    private Activity activity;

    private String mtrName;



    public AdapterMtrSale() {
        super();

    }

    public AdapterMtrSale(Context context, List<Material> item, Activity activity) {
        this.context = context;
        this.item = item;
        this.activity = activity;
    }

    @Override
    public MtrSaleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_material, parent, false);
        return new MtrSaleViewHolder(v);

    }

    @Override
    public void onBindViewHolder(MtrSaleViewHolder holder, int position) {
        mtrSale = item.get(position);

        mtrName = mtrSale.getMtrName();


        holder.mtrNameTV.setText(mtrSale.getMtrName());
        holder.mtrAmountTV.setText(Utils.convertNumber(mtrSale.getMtrAmount()));
    }

    @Override
    public int getItemCount() {
        return item.size();
    }

    public class MtrSaleViewHolder extends RecyclerView.ViewHolder {
        TextView mtrNameTV, mtrAmountTV;
        public MtrSaleViewHolder(View itemView) {
            super(itemView);
            mtrNameTV = itemView.findViewById(R.id.tv_name);
            mtrAmountTV = itemView.findViewById(R.id.tv_amount);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builderPrice = new AlertDialog.Builder(activity);
                    builderPrice.setTitle(mtrName);
                    builderPrice.setMessage("Tổng doanh thu ngày hôm nay là ");
                    builderPrice.show();
                }
            });
        }
    }

}
