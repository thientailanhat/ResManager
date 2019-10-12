package com.minhky.go4.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.minhky.go4.R;
import com.minhky.go4.model.Food;
import com.minhky.go4.model.Material;
import com.minhky.go4.model.Table;
import com.minhky.go4.restaurant.ResCashier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.minhky.go4.utils.Constant.refFoodElement;
import static com.minhky.go4.utils.Constant.refOrder;

public class AdapterMtrSale extends RecyclerView.Adapter<AdapterMtrSale.MtrSaleViewHolder> {

    Context context;

    private List<Material> item;
    private Material mtrSale;
    private Activity activity;

    private DatabaseReference refBillByTime;

    private HashMap<String, Integer> mapMtrDay = new HashMap<>();
    private HashMap<String, Float> mapSaleDay = new HashMap<>();

    private List<Food> foodSaleList = new ArrayList<>();
    private List<Material> mtrSaleList = new ArrayList<>();




    private String choosenMonth;
    private String mtrName;
    private String foodNameSale;
    private String foodAmountSale;




    public AdapterMtrSale() {
        super();

    }

    public AdapterMtrSale(Context context, List<Material> item) {
        this.context = context;
        this.item = item;
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

            refBillByTime = refOrder.child("SaleMan/BillTotalByTime");

            mtrNameTV = itemView.findViewById(R.id.tv_name);
            mtrAmountTV = itemView.findViewById(R.id.tv_amount);

            //adapterMtrSale = new AdapterMtrSale(getApplicationContext(), mtrSaleList);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builderPrice = new AlertDialog.Builder(activity);
                    builderPrice.setTitle(mtrName);
                    builderPrice.setMessage("Tổng doanh thu ngày hôm nay là ");

                    v.startAnimation(Constant.buttonClick);

                    int pos = getLayoutPosition();

                    Material mtrSale = item.get(pos);

                    final String mtrName = mtrSale.getMtrName();

                    builderPrice.setPositiveButton("Chi tiết", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            AlertDialog.Builder builderBill = new AlertDialog.Builder(context);
                            LayoutInflater inflater = LayoutInflater.from(context);
                            View dialogView = inflater.inflate(R.layout.dialog_chart_time_product,null);
                            builderBill.setView(dialogView);
                            final Dialog dialogGraph = builderBill.create();
                            dialogGraph.show();

                            final Spinner spinMonth = dialogView.findViewById(R.id.spin_chart_product_month);
                            final BarChart barTime = dialogView.findViewById(R.id.bar_dialog_chart_product);
                            final TextView tvCustomerTotal = dialogView.findViewById(R.id.tv_time_product_total);
                            final List<BarEntry> dateEntries = new ArrayList<>();


                        }
                    });




                    builderPrice.show();

                }
            });
        }
    }

}
