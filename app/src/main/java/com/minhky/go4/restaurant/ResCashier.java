package com.minhky.go4.restaurant;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.minhky.go4.R;
import com.minhky.go4.model.Bill;
import com.minhky.go4.model.Food;
import com.minhky.go4.model.Material;
import com.minhky.go4.model.Table;
import com.minhky.go4.utils.AdapterFoodSale;
import com.minhky.go4.utils.AdapterMtrSale;
import com.minhky.go4.utils.Constant;
import com.minhky.go4.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

//import static com.minhky.go4.utils.Constant.refApps;
import static com.minhky.go4.utils.Constant.refFood;
import static com.minhky.go4.utils.Constant.refFoodElement;
import static com.minhky.go4.utils.Constant.refMaterial;
import static com.minhky.go4.utils.Constant.refOrder;
import static com.minhky.go4.utils.Constant.refTable;
import static com.minhky.go4.utils.Utils.covertStringToURL;

public class ResCashier extends AppCompatActivity {
    //private DatabaseReference refFood, refTable;
    private boolean portrait, largeScreen, picChoosen;
    private String userEmail, tableName;
    private FirebaseRecyclerAdapter<Table, TableViewHolder> adapterFirebase;
    private FirebaseRecyclerAdapter<Food, FoodBillViewHolder> adapterFirebaseBill;
    private FirebaseRecyclerAdapter<Bill, BillViewHolder> adapterFirebaseListBill;
    private FirebaseRecyclerAdapter<Food, ProductBillListViewHolder> adapterProductBillList;
    private FirebaseRecyclerAdapter<Food, FoodPromotionOrderHolder> adapterFirebasePromotionBill;
    private FirebaseRecyclerAdapter<Food, FoodReviewOrderHolder> adapterFirebasePromotion;



    //private FirebaseRecyclerAdapter<Food, FoodBillViewHolder> adapterFirebaseBill;
    //private FirebaseRecyclerAdapter<Food, FoodPromotionOrderHolder> adapterFirebasePromotionBill;
    //private FirebaseRecyclerAdapter<Food, FoodReviewOrderHolder> adapterFirebasePromotion;
    //private FirebaseRecyclerAdapter<Bill, BillViewHolder> adapterFirebaseListBill;
    //private FirebaseRecyclerAdapter<Food, ProductBillListViewHolder> adapterProductBillList;

    private static final int TABLE_NORMAL = 1001;
    private static final int TABLE_ACTIVE = 1002;
    private Bundle b = new Bundle();
    private String thisYear, thisMonth, thisDate;
    private String shopCode;
    private String promotionType,promotionValue,promotionRate;
    private Dialog dialogPromotion;

    //private String foodName;
    //private String foodAmount;


    private DatabaseReference refUserUid, refBillByTime, refPOSProductSale, refBillTotalByTime, refEmployee, refCash;


    private float billPayment;

    private float cashBackFloat;
    private RecyclerView billList;
    private RecyclerView foodSaleRecycler;

    private LinearLayoutManager linearLayoutManager;

    private ProgressDialog mProgressDialog;

    private String emailLogin;
    boolean cashier, free;
    private String choosenYear, choosenMonth, choosenTop, agent;
    private String customerCash;
    private TextView tvPromotionCash;
    private RecyclerView promotionList;
    private TextView tvMonKM;
    private Switch swPromotion;

    private String foodNameSale;
    private String foodAmountSale;

    private  String foodNameKey;
    private float foodAmountValue;

    private HashMap<String, Float> mapSaleDay = new HashMap<>();

    private HashMap<String, Integer> mapMtrDay = new HashMap<>();


    private Activity activity;


    private List<Food> foodSaleList = new ArrayList<>();
    private List<Food> foodSaleListTD = new ArrayList<>();

    private AdapterFoodSale adapterFoodSale;


    private List<Material> mtrSaleList = new ArrayList<>();
    private AdapterMtrSale adapterMtrSale;

    private ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_res_cashier);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar_res_cashier);
        setSupportActionBar(toolbar);

        Intent it = this.getIntent();

        agent = it.getStringExtra("Agent");

        //Toast.makeText(getApplicationContext(), agent, Toast.LENGTH_LONG).show();
        //Toast.makeText(getApplicationContext(), emailLogin, Toast.LENGTH_LONG).show();

        userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", ",");

        //refTable = refApps.child("ResMan/Table");
        //refFood = refApps.child("ResMan/Food");
        refCash = refOrder.child("Cash");
        refBillByTime = refOrder.child("SaleMan/BillByTime");
        refBillTotalByTime = refOrder.child("SaleMan/BillTotalByTime");

        refPOSProductSale = refOrder.child("SaleMan/ProductSale");

        thisYear = (Calendar.getInstance().get(Calendar.YEAR)) + "";
        thisMonth = (Calendar.getInstance().get(Calendar.MONTH) + 1) + "";
        thisDate = (Calendar.getInstance().get(Calendar.DATE)) + "";

        //Toast.makeText(getApplicationContext(), thisDate, Toast.LENGTH_LONG).show();
        //Toast.makeText(getApplicationContext(), thisYear, Toast.LENGTH_LONG).show();
        //Toast.makeText(getApplicationContext(), thisMonth, Toast.LENGTH_LONG).show();

    }

    @Override
    protected void onResume() {
        super.onResume();

        RecyclerView tableList = findViewById(R.id.recycler_res_cashier);
        tableList.setHasFixedSize(true);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        tableList.setLayoutManager(staggeredGridLayoutManager);

        adapterFirebase = new FirebaseRecyclerAdapter<Table, ResCashier.TableViewHolder>(
                Table.class,
                R.layout.item_table,
                ResCashier.TableViewHolder.class,
                refTable
        ) {
            @Override
            public int getItemViewType(int position) {
                Table table = getItem(position);
                if(!table.isActive()){
                    return TABLE_NORMAL;
                }else{
                    return TABLE_ACTIVE;
                }
            }


            @Override
            public ResCashier.TableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                if(viewType==TABLE_ACTIVE){
                    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_table_active,parent,false);
                    return new ResCashier.TableViewHolder(v);

                }else{
                    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_table,parent,false);
                    return new ResCashier.TableViewHolder(v);
                }
            }

            @SuppressLint("ResourceAsColor")
            @Override
            protected void populateViewHolder(final ResCashier.TableViewHolder viewHolder, final Table model, int position) {
                viewHolder.name.setText(model.getTableName());
                //viewHolder.name.setTextColor(model.isActive()? R.color.colorPrimaryDark:R.color.com_facebook_likeview_text_color);
            }
        };
        tableList.setAdapter(adapterFirebase);
        adapterFirebase.notifyDataSetChanged();
    }

    private class TableViewHolder extends RecyclerView.ViewHolder {
        TextView name;

        public TableViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_item_table_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    view.startAnimation(Constant.buttonClick);
                    int position = getLayoutPosition();
                    Table table = adapterFirebase.getItem(position);
                    tableName = table.getTableName();
                    refOrder.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild("Order "+tableName)){
                                viewBillDialog();
                            }else{
                                Toast.makeText(getApplicationContext(),"Bàn không có khách!",Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            });

        }
    }

    private void viewBillDialog() {

        android.app.AlertDialog.Builder builderBill = new AlertDialog.Builder(ResCashier.this);
        LayoutInflater inflater = LayoutInflater.from(ResCashier.this);
        final View dialogView = inflater.inflate(R.layout.dialog_view_bill, null);
        builderBill.setView(dialogView);
        //builderBill.setCancelable(false);
        final Dialog dialog = builderBill.create();

        dialog.show();

        //final boolean promotionProduct = b.getBoolean(tableName+"PromotionProduct");

        tvPromotionCash = dialogView.findViewById(R.id.tv_view_bill_promotion_cash);
        if(promotionValue!=null)
            tvPromotionCash.setText(Utils.convertNumber(promotionValue));
        final TextView tvTienKM = dialogView.findViewById(R.id.tv_view_bill_tienKM);
        tvMonKM = dialogView.findViewById(R.id.tv_view_bill_monKM);

        promotionList = dialogView.findViewById(R.id.recycler_view_bill_promotion_food);
        promotionList.setHasFixedSize(false);
        //StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager((largeScreen)?4:2,LinearLayoutManager.VERTICAL);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        promotionList.setLayoutManager(linearLayoutManager2);

        adapterFirebasePromotionBill = new FirebaseRecyclerAdapter<Food, FoodPromotionOrderHolder>(
                Food.class,
                R.layout.item_promotion,
                FoodPromotionOrderHolder.class,
                refOrder.child("Order "+tableName).child("Promotion")
        )
        {
            @Override
            public FoodPromotionOrderHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_promotion,parent,false);
                return new FoodPromotionOrderHolder(v);
            }

            @Override
            protected void populateViewHolder(final FoodPromotionOrderHolder viewHolder, final Food model, int position) {
                viewHolder.name.setText(model.getFoodName());
                viewHolder.quantity.setText(model.getFoodQuantity());
            }
        };
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.DOWN | ItemTouchHelper.UP) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                final int position = viewHolder.getAdapterPosition();
                final String itemKey = adapterFirebasePromotionBill.getRef(position).getKey();
                refUserUid.child("Order "+tableName).child("Promotion").child(itemKey).setValue(null);
                adapterFirebaseBill.notifyItemRemoved(position);
                adapterFirebaseBill.notifyDataSetChanged();


            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(promotionList);
        promotionList.setAdapter(adapterFirebasePromotionBill);
        adapterFirebasePromotionBill.notifyDataSetChanged();

        boolean setCheckPromotion = b.getBoolean(tableName+"SetCheckPromotion");
        swPromotion = dialogView.findViewById(R.id.sw_view_bill_promotion);
        swPromotion.setChecked(setCheckPromotion);

        swPromotion.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    dialog.dismiss();
                    promotionDialog();

                }
            }
        });

        final TextView tvBillTotal = dialogView.findViewById(R.id.tv_bill_view_payment);
        final TextView tvBillCode = dialogView.findViewById(R.id.tv_bill_view_code);
        final TextView tvCashBack = dialogView.findViewById(R.id.tv_view_bill_cashBack);
        final TextView tvCustomerCash = dialogView.findViewById(R.id.tv_view_bill_customerCash);

        final EditText edtCustomerCash = dialogView.findViewById(R.id.edt_view_bill_customerCash);

        final Button btnCancel = dialogView.findViewById(R.id.btn_bill_view_cancel);
        final Button btnPrint = dialogView.findViewById(R.id.btn_bill_view_print);
        final Button btnOk = dialogView.findViewById(R.id.btn_bill_view_ok);

        edtCustomerCash.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                    //View view = getCurrentFocus();
                    View view = dialogView.getRootView();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager)getApplicationContext().getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                    }

                    customerCash = edtCustomerCash.getText().toString();

                    if(TextUtils.isEmpty(customerCash)){
                        Toast.makeText(getApplicationContext(),"Vui lòng nhập tiền khách đưa!",Toast.LENGTH_LONG).show();

                    }else if(Float.parseFloat(customerCash)<billPayment){
                        Toast.makeText(getApplicationContext(),"Tiền khách đưa chưa đủ!",Toast.LENGTH_LONG).show();
                    }else{
                        //cashBackFloat = (promotionValue == null|| promotionProduct)? Float.parseFloat(customerCash) - billPayment
                        //        :Float.parseFloat(customerCash) - billPayment+Float.parseFloat(promotionValue);
                        tvCashBack.setText(Utils.convertNumber(cashBackFloat + ""));
                        edtCustomerCash.setVisibility(View.INVISIBLE);
                        tvCustomerCash.setVisibility(View.VISIBLE);

                        tvCustomerCash.setText(Utils.convertNumber(edtCustomerCash.getText().toString()));

                    }
                    //b.putString("CustomerCash",edtCustomerCash.getText().toString());




                    return true;
                }
                return false;
            }
        });

//Get productBill info
        refOrder.child("Order "+tableName).child("FoodList").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> productSnap = dataSnapshot.getChildren();
                int i = 0;
                for (DataSnapshot p : productSnap) {
                    Food product = p.getValue(Food.class);
                    String productName = product.getFoodName();
                    String productNameNoViet = Utils.capSentences(covertStringToURL(productName).replace("-"," "));
                    String productQuantity = product.getFoodQuantity();
                    String productPrice = product.getFoodPrice();
                    String productTotal = product.getFoodPayment();
                    b.putString("ProductNameNotViet" + i, productNameNoViet);
                    b.putString("ProductQuantity" + i, productQuantity);
                    b.putString("ProductPrice" + i, productPrice);
                    i++;
                    b.putInt("ForIntValue", i);

                }
                Toast.makeText(getApplicationContext(),i+"",Toast.LENGTH_LONG).show();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        refBillByTime.child(thisYear +"-"+ thisMonth+"-" + thisDate).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String billCountCode = (dataSnapshot.getChildrenCount() + 1) + "";
                String billCode = shopCode + thisYear + thisMonth + thisDate + billCountCode;
                tvBillCode.setText(billCode);
                b.putString("BillCode", billCode);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //get Bill Total
        refOrder.child("Order "+tableName).child("FoodList").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> productSnap = dataSnapshot.getChildren();
                billPayment = 0;
                for(DataSnapshot itemProduct:productSnap){
                    Food food = itemProduct.getValue(Food.class);
                    billPayment = billPayment + Float.parseFloat(food.getFoodPayment());
                    tvBillTotal.setText(Utils.convertNumber(billPayment+""));
                    b.putString("BillTotal",billPayment+"");

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //get BillCOde
        refBillByTime.child(thisYear+"-" + thisMonth+"-"+ thisDate).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String billCountCode = (dataSnapshot.getChildrenCount() + 1) + "";
                String billCode = thisYear + thisMonth + thisDate + billCountCode;
                tvBillCode.setText(billCode);
                b.putString("BillCode",billCode);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //get Bill List
        RecyclerView billListItem = dialogView.findViewById(R.id.recycler_bill_view_itemList);
        billListItem.setHasFixedSize(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ResCashier.this);
        billListItem.setLayoutManager(linearLayoutManager);

        adapterFirebaseBill = new FirebaseRecyclerAdapter<Food, FoodBillViewHolder>(
                Food.class,
                R.layout.item_drug_cart,
                FoodBillViewHolder.class,
                refOrder.child("Order "+tableName).child("FoodList")
        )
        {
            @Override
            public FoodBillViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_drug_cart,parent,false);

                return new FoodBillViewHolder(v);
            }

            @Override
            protected void populateViewHolder(final FoodBillViewHolder viewHolder, final Food model, int position) {
                viewHolder.name.setText(model.getFoodName());
                viewHolder.quantity.setText(model.getFoodQuantity());
                viewHolder.total.setText(Utils.convertNumber(model.getFoodPayment()));
                viewHolder.price.setText(Utils.convertNumber(model.getFoodPrice()));

            }
        };
        billListItem.setAdapter(adapterFirebaseBill);
        adapterFirebaseBill.notifyDataSetChanged();
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(Constant.buttonClick);

                final String billPushKey = refOrder.child("Bill").push().getKey();

                final String timeStamp = (Calendar.getInstance().getTime().getTime())+"";
                final String billCode = b.getString("BillCode");
                customerCash = edtCustomerCash.getText().toString();

                if (TextUtils.isEmpty(customerCash)) {
                    Toast.makeText(getApplicationContext(), "Vui lòng nhập tiền khách đưa", Toast.LENGTH_LONG).show();
                } else{
                    showProgressDialog();
                    final Bill bill = new Bill(billCode,billPayment+"",timeStamp,cashBackFloat+"");
                    refCash.child("BillUnapproved").child(userEmail).push().setValue(bill);

                    refOrder.child("Order "+tableName).child("FoodList").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshotList) {
                            Iterable<DataSnapshot> productSnap = dataSnapshotList.getChildren();
                            //float productTotalFloat = 0;

                            for (final DataSnapshot p : productSnap) {
                                final Food product = p.getValue(Food.class);
                                final String productName = product.getFoodName();
                                final String productTotal = product.getFoodPayment();

                                refPOSProductSale.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.hasChild(productName)){
                                            refPOSProductSale.child(productName).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    if(dataSnapshot.hasChild(thisYear +"-"+ thisMonth)){
                                                        refPOSProductSale.child(productName).child(thisYear +"-" + thisMonth).addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                float currentSale = Float.parseFloat(dataSnapshot.getValue().toString());
                                                                float updateSale = currentSale + Float.parseFloat(productTotal);
                                                                refPOSProductSale.child(productName).child(thisYear +"-" + thisMonth).setValue(updateSale+"");

                                                            }

                                                            @Override
                                                            public void onCancelled(DatabaseError databaseError) {

                                                            }
                                                        });

                                                    }else{
                                                        refPOSProductSale.child(productName).child(thisYear +"-" + thisMonth).setValue(productTotal);

                                                    }

                                                    if(dataSnapshot.hasChild(thisYear +"-" + thisMonth+"-" + thisDate)){
                                                        refPOSProductSale.child(productName).child(thisYear +"-" + thisMonth+"-" + thisDate).addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                float currentSale = Float.parseFloat(dataSnapshot.getValue().toString());
                                                                float updateSale = currentSale + Float.parseFloat(productTotal);
                                                                refPOSProductSale.child(productName).child(thisYear +"-" + thisMonth+"-" + thisDate).setValue(updateSale+"");

                                                            }

                                                            @Override
                                                            public void onCancelled(DatabaseError databaseError) {

                                                            }
                                                        });

                                                    }else{
                                                        refPOSProductSale.child(productName).child(thisYear +"-" + thisMonth+"-" + thisDate).setValue(productTotal);

                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });

                                        }else{
                                            refPOSProductSale.child(productName).child(thisYear +"-" + thisMonth).setValue(productTotal);
                                            refPOSProductSale.child(productName).child(thisYear +"-" + thisMonth+"-" + thisDate).setValue(productTotal);

                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                                refOrder.child("Bill").child(billPushKey).child("Food").push().setValue(product);

                            }

                            refBillTotalByTime.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if(dataSnapshot.hasChild(thisYear +"-" + thisMonth+"-" + thisDate)){
                                        refBillTotalByTime.child(thisYear +"-" + thisMonth+"-" + thisDate).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                float currentSale = Float.parseFloat(dataSnapshot.getValue().toString());
                                                float updateSale = currentSale + billPayment;
                                                refBillTotalByTime.child(thisYear +"-" + thisMonth+"-" + thisDate).setValue(updateSale+"");

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });

                                    }else{
                                        refBillTotalByTime.child(thisYear +"-" + thisMonth+"-" + thisDate).setValue(billPayment);
                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            refBillByTime.child(thisYear +"-" + thisMonth).child(billPushKey).setValue(bill);
                            refBillByTime.child(thisYear +"-" + thisMonth+"-" + thisDate).child(billPushKey).setValue(bill);

                            refTable.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Iterable<DataSnapshot> tableSnap = dataSnapshot.getChildren();
                                    for(DataSnapshot itemTable:tableSnap){
                                        Table table = dataSnapshot.getValue(Table.class);
                                        String itemTableName = table.getTableName();
                                        DatabaseReference refItem = itemTable.getRef();
                                        if(tableName.equals(itemTableName)){
                                            refItem.child("active").setValue(false);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            refOrder.child("Bill").child(billPushKey).child("Info").setValue(bill).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    onResume();
                                    hideProgressDialog();
                                    dialog.dismiss();
                                    refOrder.child("Order "+tableName).setValue(null);
                                }
                            });

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    checkTableActive();
                }
            }
        });

    }
    private class FoodBillViewHolder extends RecyclerView.ViewHolder {
        TextView name,price,quantity,total;
        public FoodBillViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_item_cart_name);
            quantity = itemView.findViewById(R.id.tv_item_cart_quantity);
            total = itemView.findViewById(R.id.tv_item_cart_total);
            price = itemView.findViewById(R.id.tv_item_cart_price);

        }
    }

    private void billList(String date) {
        billList.setHasFixedSize(false);
        linearLayoutManager = new LinearLayoutManager(ResCashier.this);
        billList.setLayoutManager(linearLayoutManager);

        adapterFirebaseListBill = new FirebaseRecyclerAdapter<Bill, BillViewHolder>(
                Bill.class,
                R.layout.item_bill_list,
                BillViewHolder.class,
                refBillByTime.child(date)
        ) {
            @Override
            public BillViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bill_list,parent,false);
                return new BillViewHolder(v);
            }

            @Override
            protected void populateViewHolder(final BillViewHolder viewHolder, final Bill model, int position) {
                viewHolder.time.setText(Utils.getHourFromTimeStamp(Long.parseLong(model.getTimeStamp())));
                viewHolder.value.setText(Utils.convertNumber(model.getPayment()));

            }
        };

        billList.setAdapter(adapterFirebaseListBill);
        adapterFirebaseListBill.notifyDataSetChanged();
    }

    private void foodSaleRecycler (String date) {
        foodSaleRecycler.setHasFixedSize(false);
        linearLayoutManager = new LinearLayoutManager(ResCashier.this);
        foodSaleRecycler.setLayoutManager(linearLayoutManager);

        adapterFoodSale = new AdapterFoodSale(getApplicationContext(), foodSaleList, ResCashier.this);

        foodSaleRecycler.setAdapter(adapterFoodSale);
        adapterFoodSale.notifyDataSetChanged();
    }

    private class BillViewHolder extends RecyclerView.ViewHolder {
        TextView time,value;

        public BillViewHolder(View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.tv_item_bill_list_time);
            value = itemView.findViewById(R.id.tv_item_bill_list_value);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.startAnimation(Constant.buttonClick);
                    int position = getLayoutPosition();
                    final String orderKey = adapterFirebaseListBill.getRef(position).getKey();

                    AlertDialog.Builder builderPrice = new AlertDialog.Builder(ResCashier.this);
                    LayoutInflater inflater = LayoutInflater.from(ResCashier.this);
                    View dialogView = inflater.inflate(R.layout.dialog_product_bill,null);
                    builderPrice.setView(dialogView);
                    final Dialog dialog = builderPrice.create();
                    dialog.show();

                    RecyclerView productList = dialogView.findViewById(R.id.recycler_product_bill);

                    productList.setHasFixedSize(false);
                    linearLayoutManager = new LinearLayoutManager(ResCashier.this);
                    productList.setLayoutManager(linearLayoutManager);

                    adapterProductBillList = new FirebaseRecyclerAdapter<Food, ProductBillListViewHolder>(
                            Food.class,
                            R.layout.item_top_product,
                            ProductBillListViewHolder.class,
                            refOrder.child("Bill").child(orderKey).child("Food")
                    ) {
                        @Override
                        public ProductBillListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_top_product,parent,false);
                            return new ProductBillListViewHolder(v);
                        }

                        @Override
                        protected void populateViewHolder(final ProductBillListViewHolder viewHolder, final Food model, int position) {
                            viewHolder.name.setText(model.getFoodName());
                            viewHolder.sale.setText(Utils.convertNumber(model.getFoodPayment()));

                        }
                    };

                    productList.setAdapter(adapterProductBillList);
                    adapterProductBillList.notifyDataSetChanged();

                }
            });
        }
    }
    private class ProductBillListViewHolder extends RecyclerView.ViewHolder {
        TextView name,sale;
        private ProductBillListViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_item_product_top_name);
            sale = itemView.findViewById(R.id.tv_item_product_top_sale);
            itemView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction()== MotionEvent.ACTION_DOWN){

                        int position = getLayoutPosition();
                        DatabaseReference productRef = adapterProductBillList.getRef(position);
                        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Food p = dataSnapshot.getValue(Food.class);
                                final String productName = p.getFoodName();

                                refPOSProductSale.child(productName).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.hasChild(thisYear+"-"+thisMonth+"-"+thisDate)){
                                            refPOSProductSale.child(productName).child(thisYear+"-"+thisMonth+"-"+thisDate).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    String productSale = dataSnapshot.getValue().toString();
                                                   AlertDialog.Builder builderPrice = new AlertDialog.Builder(ResCashier.this);
                                                    builderPrice.setTitle(productName);
                                                    builderPrice.setMessage("Tổng doanh thu ngày hôm nay là "+Utils.convertNumber(productSale));

                                                    builderPrice.setPositiveButton("Chi tiết", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.dismiss();
                                                            AlertDialog.Builder builderBill = new AlertDialog.Builder(ResCashier.this);
                                                            LayoutInflater inflater = LayoutInflater.from(ResCashier.this);
                                                            View dialogView = inflater.inflate(R.layout.dialog_chart_time_product,null);
                                                            builderBill.setView(dialogView);
                                                            final Dialog dialogGraph = builderBill.create();
                                                            dialogGraph.show();

                                                            final Spinner spinYear = dialogView.findViewById(R.id.spin_chart_product_year);
                                                            final Spinner spinMonth = dialogView.findViewById(R.id.spin_chart_product_month);
                                                            final BarChart barTime = dialogView.findViewById(R.id.bar_dialog_chart_product);
                                                            final TextView tvCustomerTotal = dialogView.findViewById(R.id.tv_time_product_total);
                                                            final List<BarEntry> monthEntries = new ArrayList<>();
                                                            final List<BarEntry> dateEntries = new ArrayList<>();

                                                            spinYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                                @Override
                                                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                                    if(position!=0){
                                                                        choosenYear = (String)parent.getItemAtPosition(position);

                                                                        refPOSProductSale.child(productName).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                final float[] yearSale = {0};
                                                                                for (int i = 1;i<=12;i++){
                                                                                    if(dataSnapshot.hasChild(choosenYear+"-"+i)){
                                                                                        refPOSProductSale.child(productName).child(choosenYear+"-"+i).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                            @Override
                                                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                                float monthSale = Float.parseFloat(dataSnapshot.getValue().toString());
                                                                                                yearSale[0] = yearSale[0] +monthSale;
                                                                                                tvCustomerTotal.setText(Utils.convertNumber(yearSale[0]+""));

                                                                                            }

                                                                                            @Override
                                                                                            public void onCancelled(DatabaseError databaseError) {

                                                                                            }
                                                                                        });
                                                                                    }

                                                                                }


                                                                            }

                                                                            @Override
                                                                            public void onCancelled(DatabaseError databaseError) {

                                                                            }
                                                                        });

                                                                        refPOSProductSale.child(productName).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                Iterable<DataSnapshot> iterable = dataSnapshot.getChildren();
                                                                                for(DataSnapshot it:iterable){
                                                                                    String key = it.getKey();
                                                                                    float value = Float.parseFloat(it.getValue().toString());
                                                                                    for(int i = 1;i<=12;i++){
                                                                                        if(key.equals(choosenYear+"-"+i)){
                                                                                            //barValue.add(value);
                                                                                            monthEntries.add(new BarEntry(i,value));
                                                                                        }else{
                                                                                            // barValue.add(0f);
                                                                                            monthEntries.add(new BarEntry(i,0f));

                                                                                        }
                                                                                    }

                                                                                }
                                                                                //CustomMarkerView marker = new CustomMarkerView(getApplicationContext(),R.layout.custom_marker_view_layout);

                                                                                BarDataSet set = new BarDataSet(monthEntries,"Doanh thu năm của "+choosenYear+" theo tháng");
                                                                                BarData data = new BarData(set);
                                                                                Description description = new Description();
                                                                                description.setText("");
                                                                                barTime.setDescription(description);
                                                                                barTime.getAxisRight().setEnabled(false);
                                                                                barTime.setTouchEnabled(true);
                                                                                //barTime.setMarker(mv);
                                                                                barTime.setData(data);
                                                                                barTime.animateXY(1000,2000);
                                                                                barTime.setFitBars(true); // make the x-axis fit exactly all bars
                                                                                barTime.invalidate(); // refresh


                                                                            }

                                                                            @Override
                                                                            public void onCancelled(DatabaseError databaseError) {

                                                                            }
                                                                        });
                                                                    }


                                                                }

                                                                @Override
                                                                public void onNothingSelected(AdapterView<?> parent) {

                                                                }
                                                            });
                                                            spinMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                                @Override
                                                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                                    if(position!= 0){
                                                                        choosenMonth = (String)parent.getItemAtPosition(position);

                                                                        refPOSProductSale.child(productName).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                float monthSale = 0;
                                                                                for (int i = 1;i<=31;i++){
                                                                                    if(dataSnapshot.hasChild(choosenYear+"-"+choosenMonth+"-"+i)){
                                                                                        String daySale = dataSnapshot.child(choosenYear+"-"+choosenMonth+"-"+i).getValue().toString();
                                                                                        monthSale = monthSale + Float.parseFloat(daySale);
                                                                                        tvCustomerTotal.setText(Utils.convertNumber(monthSale+""));

                                                                                    }
                                                                                }

                                                                            }

                                                                            @Override
                                                                            public void onCancelled(DatabaseError databaseError) {

                                                                            }
                                                                        });
                                                                        refPOSProductSale.child(productName).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                Iterable<DataSnapshot> iterable = dataSnapshot.getChildren();
                                                                                for(DataSnapshot it:iterable){
                                                                                    String key = it.getKey();
                                                                                    float value = Float.parseFloat(it.getValue().toString());
                                                                                    for(int i = 1;i<=31;i++){
                                                                                        if(key.equals(choosenYear+"-"+choosenMonth+"-"+i)){
                                                                                            //barValue.add(value);
                                                                                            dateEntries.add(new BarEntry(i,value));
                                                                                        }else{
                                                                                            // barValue.add(0f);
                                                                                            dateEntries.add(new BarEntry(i,0f));

                                                                                        }
                                                                                    }

                                                                                }


                                                                                //CustomMarkerView mv = new CustomMarkerView (getApplicationContext(), R.layout.custom_marker_view_layout);

                                                                                BarDataSet set = new BarDataSet(dateEntries,"Doanh thu sản phẩm tháng "+choosenMonth+" theo ngày");
                                                                                BarData data = new BarData(set);
                                                                                Description description = new Description();
                                                                                description.setText("");
                                                                                barTime.setDescription(description);
                                                                                barTime.getAxisRight().setEnabled(false);
                                                                                barTime.setTouchEnabled(true);
                                                                                //barTime.setMarker(mv);
                                                                                barTime.setData(data);
                                                                                barTime.animateXY(1000,2000);
                                                                                barTime.setFitBars(true); // make the x-axis fit exactly all bars
                                                                                barTime.invalidate(); // refresh

                                                                            }

                                                                            @Override
                                                                            public void onCancelled(DatabaseError databaseError) {

                                                                            }
                                                                        });
                                                                    }

                                                                }

                                                                @Override
                                                                public void onNothingSelected(AdapterView<?> parent) {

                                                                }
                                                            });

                                                        }
                                                    });

                                                    builderPrice.show();

                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                        }else{
                                            AlertDialog.Builder builderPrice = new AlertDialog.Builder(ResCashier.this);
                                            builderPrice.setTitle(productName);
                                            builderPrice.setMessage("Không phát sinh doanh thu trong ngày.");

                                            builderPrice.setPositiveButton("Chi tiết", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                    AlertDialog.Builder builderBill = new AlertDialog.Builder(ResCashier.this);
                                                    LayoutInflater inflater = LayoutInflater.from(ResCashier.this);
                                                    View dialogView = inflater.inflate(R.layout.dialog_chart_time_product,null);
                                                    builderBill.setView(dialogView);
                                                    final Dialog dialogGraph = builderBill.create();
                                                    dialogGraph.show();

                                                    final Spinner spinYear = dialogView.findViewById(R.id.spin_chart_product_year);
                                                    final Spinner spinMonth = dialogView.findViewById(R.id.spin_chart_product_month);
                                                    final BarChart barTime = dialogView.findViewById(R.id.bar_dialog_chart_product);
                                                    final TextView tvCustomerTotal = dialogView.findViewById(R.id.tv_time_product_total);
                                                    final List<BarEntry> monthEntries = new ArrayList<>();
                                                    final List<BarEntry> dateEntries = new ArrayList<>();

                                                    spinYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                        @Override
                                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                            if(position!=0){
                                                                choosenYear = (String)parent.getItemAtPosition(position);

                                                                refPOSProductSale.child(productName).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                                        final float[] yearSale = {0};
                                                                        for (int i = 1;i<=12;i++){
                                                                            if(dataSnapshot.hasChild(choosenYear+"-"+i)){
                                                                                refPOSProductSale.child(productName).child(choosenYear+"-"+i).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                    @Override
                                                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                        float monthSale = Float.parseFloat(dataSnapshot.getValue().toString());
                                                                                        yearSale[0] = yearSale[0] +monthSale;
                                                                                        tvCustomerTotal.setText(Utils.convertNumber(yearSale[0]+""));

                                                                                    }

                                                                                    @Override
                                                                                    public void onCancelled(DatabaseError databaseError) {

                                                                                    }
                                                                                });
                                                                            }

                                                                        }


                                                                    }

                                                                    @Override
                                                                    public void onCancelled(DatabaseError databaseError) {

                                                                    }
                                                                });

                                                                refPOSProductSale.child(productName).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                                        Iterable<DataSnapshot> iterable = dataSnapshot.getChildren();
                                                                        for(DataSnapshot it:iterable){
                                                                            String key = it.getKey();
                                                                            float value = Float.parseFloat(it.getValue().toString());
                                                                            for(int i = 1;i<=12;i++){
                                                                                if(key.equals(choosenYear+"-"+i)){
                                                                                    //barValue.add(value);
                                                                                    monthEntries.add(new BarEntry(i,value));
                                                                                }else{
                                                                                    // barValue.add(0f);
                                                                                    monthEntries.add(new BarEntry(i,0f));

                                                                                }
                                                                            }

                                                                        }
                                                                        //CustomMarkerView marker = new CustomMarkerView(getApplicationContext(),R.layout.custom_marker_view_layout);

                                                                        BarDataSet set = new BarDataSet(monthEntries,"Doanh thu năm của "+choosenYear+" theo tháng");
                                                                        BarData data = new BarData(set);
                                                                        Description description = new Description();
                                                                        description.setText("");
                                                                        barTime.setDescription(description);
                                                                        barTime.getAxisRight().setEnabled(false);
                                                                        barTime.setTouchEnabled(true);
                                                                        //barTime.setMarker(mv);
                                                                        barTime.setData(data);
                                                                        barTime.animateXY(1000,2000);
                                                                        barTime.setFitBars(true); // make the x-axis fit exactly all bars
                                                                        barTime.invalidate(); // refresh


                                                                    }

                                                                    @Override
                                                                    public void onCancelled(DatabaseError databaseError) {

                                                                    }
                                                                });
                                                            }


                                                        }

                                                        @Override
                                                        public void onNothingSelected(AdapterView<?> parent) {

                                                        }
                                                    });
                                                    spinMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                        @Override
                                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                            if(position!= 0){
                                                                choosenMonth = (String)parent.getItemAtPosition(position);

                                                                refPOSProductSale.child(productName).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                                        float monthSale = 0;
                                                                        for (int i = 1;i<=31;i++){
                                                                            if(dataSnapshot.hasChild(choosenYear+"-"+choosenMonth+"-"+i)){
                                                                                String daySale = dataSnapshot.child(choosenYear+"-"+choosenMonth+"-"+i).getValue().toString();
                                                                                monthSale = monthSale + Float.parseFloat(daySale);
                                                                                tvCustomerTotal.setText(Utils.convertNumber(monthSale+""));

                                                                            }
                                                                        }

                                                                    }

                                                                    @Override
                                                                    public void onCancelled(DatabaseError databaseError) {

                                                                    }
                                                                });
                                                                refPOSProductSale.child(productName).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                                        Iterable<DataSnapshot> iterable = dataSnapshot.getChildren();
                                                                        for(DataSnapshot it:iterable){
                                                                            String key = it.getKey();
                                                                            float value = Float.parseFloat(it.getValue().toString());
                                                                            for(int i = 1;i<=31;i++){
                                                                                if(key.equals(choosenYear+"-"+choosenMonth+"-"+i)){
                                                                                    //barValue.add(value);
                                                                                    dateEntries.add(new BarEntry(i,value));
                                                                                }else{
                                                                                    // barValue.add(0f);
                                                                                    dateEntries.add(new BarEntry(i,0f));

                                                                                }
                                                                            }

                                                                        }


                                                                        //CustomMarkerView mv = new CustomMarkerView (getApplicationContext(), R.layout.custom_marker_view_layout);

                                                                        BarDataSet set = new BarDataSet(dateEntries,"Doanh thu sản phẩm tháng "+choosenMonth+" theo ngày");
                                                                        BarData data = new BarData(set);
                                                                        Description description = new Description();
                                                                        description.setText("");
                                                                        barTime.setDescription(description);
                                                                        barTime.getAxisRight().setEnabled(false);
                                                                        barTime.setTouchEnabled(true);
                                                                        //barTime.setMarker(mv);
                                                                        barTime.setData(data);
                                                                        barTime.animateXY(1000,2000);
                                                                        barTime.setFitBars(true); // make the x-axis fit exactly all bars
                                                                        barTime.invalidate(); // refresh

                                                                    }

                                                                    @Override
                                                                    public void onCancelled(DatabaseError databaseError) {

                                                                    }
                                                                });
                                                            }

                                                        }

                                                        @Override
                                                        public void onNothingSelected(AdapterView<?> parent) {

                                                        }
                                                    });
                                                }
                                            });

                                            builderPrice.show();

                                        }

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        return true;
                    }
                    return false;
                }
            });

/*            itemView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction()== MotionEvent.ACTION_DOWN){

                        int position = getLayoutPosition();
                        DatabaseReference productRef = adapterProductBillList.getRef(position);
                        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Food p = dataSnapshot.getValue(Food.class);
                                final String productName = p.getFoodName();

                                refPOSProductSale.child(productName).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.hasChild(thisYear+"-"+thisMonth+"-"+thisDate)){
                                            refPOSProductSale.child(productName).child(thisYear+"-"+thisMonth+"-"+thisDate).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    String productSale = dataSnapshot.getValue().toString();
                                                    AlertDialog.Builder builderPrice = new AlertDialog.Builder(ResCashier.this);
                                                    builderPrice.setTitle(productName);
                                                    builderPrice.setMessage("Tổng doanh thu ngày hôm nay là "+Utils.convertNumber(productSale));

                                                    builderPrice.setPositiveButton("Chi tiết", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.dismiss();
                                                           AlertDialog.Builder builderBill = new AlertDialog.Builder(ResCashier.this);
                                                            LayoutInflater inflater = LayoutInflater.from(ResCashier.this);
                                                            View dialogView = inflater.inflate(R.layout.dialog_chart_time_product,null);
                                                            builderBill.setView(dialogView);
                                                            final Dialog dialogGraph = builderBill.create();
                                                            dialogGraph.show();

                                                            final Spinner spinYear = dialogView.findViewById(R.id.spin_chart_product_year);
                                                            final Spinner spinMonth = dialogView.findViewById(R.id.spin_chart_product_month);
                                                            final BarChart barTime = dialogView.findViewById(R.id.bar_dialog_chart_product);
                                                            final TextView tvCustomerTotal = dialogView.findViewById(R.id.tv_time_product_total);
                                                            final List<BarEntry> monthEntries = new ArrayList<>();
                                                            final List<BarEntry> dateEntries = new ArrayList<>();

                                                            spinYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                                @Override
                                                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                                    if(position!=0){
                                                                        choosenYear = (String)parent.getItemAtPosition(position);

                                                                        refPOSProductSale.child(productName).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                final float[] yearSale = {0};
                                                                                for (int i = 1;i<=12;i++){
                                                                                    if(dataSnapshot.hasChild(choosenYear+"-"+i)){
                                                                                        refPOSProductSale.child(productName).child(choosenYear+"-"+i).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                            @Override
                                                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                                float monthSale = Float.parseFloat(dataSnapshot.getValue().toString());
                                                                                                yearSale[0] = yearSale[0] +monthSale;
                                                                                                tvCustomerTotal.setText(Utils.convertNumber(yearSale[0]+""));

                                                                                            }

                                                                                            @Override
                                                                                            public void onCancelled(DatabaseError databaseError) {

                                                                                            }
                                                                                        });
                                                                                    }

                                                                                }


                                                                            }

                                                                            @Override
                                                                            public void onCancelled(DatabaseError databaseError) {

                                                                            }
                                                                        });

                                                                        refPOSProductSale.child(productName).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                Iterable<DataSnapshot> iterable = dataSnapshot.getChildren();
                                                                                for(DataSnapshot it:iterable){
                                                                                    String key = it.getKey();
                                                                                    float value = Float.parseFloat(it.getValue().toString());
                                                                                    for(int i = 1;i<=12;i++){
                                                                                        if(key.equals(choosenYear+"-"+i)){
                                                                                            //barValue.add(value);
                                                                                            monthEntries.add(new BarEntry(i,value));
                                                                                        }else{
                                                                                            // barValue.add(0f);
                                                                                            monthEntries.add(new BarEntry(i,0f));

                                                                                        }
                                                                                    }

                                                                                }
                                                                                //CustomMarkerView marker = new CustomMarkerView(getApplicationContext(),R.layout.custom_marker_view_layout);

                                                                                BarDataSet set = new BarDataSet(monthEntries,"Doanh thu năm của "+choosenYear+" theo tháng");
                                                                                BarData data = new BarData(set);
                                                                                Description description = new Description();
                                                                                description.setText("");
                                                                                barTime.setDescription(description);
                                                                                barTime.getAxisRight().setEnabled(false);
                                                                                barTime.setTouchEnabled(true);
                                                                                //barTime.setMarker(mv);
                                                                                barTime.setData(data);
                                                                                barTime.animateXY(1000,2000);
                                                                                barTime.setFitBars(true); // make the x-axis fit exactly all bars
                                                                                barTime.invalidate(); // refresh


                                                                            }

                                                                            @Override
                                                                            public void onCancelled(DatabaseError databaseError) {

                                                                            }
                                                                        });
                                                                    }


                                                                }

                                                                @Override
                                                                public void onNothingSelected(AdapterView<?> parent) {

                                                                }
                                                            });
                                                            spinMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                                @Override
                                                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                                    if(position!= 0){
                                                                        choosenMonth = (String)parent.getItemAtPosition(position);

                                                                        refPOSProductSale.child(productName).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                float monthSale = 0;
                                                                                for (int i = 1;i<=31;i++){
                                                                                    if(dataSnapshot.hasChild(choosenYear+"-"+choosenMonth+"-"+i)){
                                                                                        String daySale = dataSnapshot.child(choosenYear+"-"+choosenMonth+"-"+i).getValue().toString();
                                                                                        monthSale = monthSale + Float.parseFloat(daySale);
                                                                                        tvCustomerTotal.setText(Utils.convertNumber(monthSale+""));

                                                                                    }
                                                                                }

                                                                            }

                                                                            @Override
                                                                            public void onCancelled(DatabaseError databaseError) {

                                                                            }
                                                                        });
                                                                        refPOSProductSale.child(productName).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                Iterable<DataSnapshot> iterable = dataSnapshot.getChildren();
                                                                                for(DataSnapshot it:iterable){
                                                                                    String key = it.getKey();
                                                                                    float value = Float.parseFloat(it.getValue().toString());
                                                                                    for(int i = 1;i<=31;i++){
                                                                                        if(key.equals(choosenYear+"-"+choosenMonth+"-"+i)){
                                                                                            //barValue.add(value);
                                                                                            dateEntries.add(new BarEntry(i,value));
                                                                                        }else{
                                                                                            // barValue.add(0f);
                                                                                            dateEntries.add(new BarEntry(i,0f));

                                                                                        }
                                                                                    }

                                                                                }


                                                                                //CustomMarkerView mv = new CustomMarkerView (getApplicationContext(), R.layout.custom_marker_view_layout);

                                                                                BarDataSet set = new BarDataSet(dateEntries,"Doanh thu sản phẩm tháng "+choosenMonth+" theo ngày");
                                                                                BarData data = new BarData(set);
                                                                                Description description = new Description();
                                                                                description.setText("");
                                                                                barTime.setDescription(description);
                                                                                barTime.getAxisRight().setEnabled(false);
                                                                                barTime.setTouchEnabled(true);
                                                                                //barTime.setMarker(mv);
                                                                                barTime.setData(data);
                                                                                barTime.animateXY(1000,2000);
                                                                                barTime.setFitBars(true); // make the x-axis fit exactly all bars
                                                                                barTime.invalidate(); // refresh

                                                                            }

                                                                            @Override
                                                                            public void onCancelled(DatabaseError databaseError) {

                                                                            }
                                                                        });
                                                                    }

                                                                }

                                                                @Override
                                                                public void onNothingSelected(AdapterView<?> parent) {

                                                                }
                                                            });

                                                        }
                                                    });

                                                    builderPrice.show();

                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                        }else{
                                            android.support.v7.app.AlertDialog.Builder builderPrice = new android.support.v7.app.AlertDialog.Builder(ResCashier.this);
                                            builderPrice.setTitle(productName);
                                            builderPrice.setMessage("Không phát sinh doanh thu trong ngày.");

                                            builderPrice.setPositiveButton("Chi tiết", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                    android.support.v7.app.AlertDialog.Builder builderBill = new android.support.v7.app.AlertDialog.Builder(ResCashier.this);
                                                    LayoutInflater inflater = LayoutInflater.from(ResCashier.this);
                                                    View dialogView = inflater.inflate(R.layout.dialog_chart_time_product,null);
                                                    builderBill.setView(dialogView);
                                                    final Dialog dialogGraph = builderBill.create();
                                                    dialogGraph.show();

                                                    final Spinner spinYear = dialogView.findViewById(R.id.spin_chart_product_year);
                                                    final Spinner spinMonth = dialogView.findViewById(R.id.spin_chart_product_month);
                                                    final BarChart barTime = dialogView.findViewById(R.id.bar_dialog_chart_product);
                                                    final TextView tvCustomerTotal = dialogView.findViewById(R.id.tv_time_product_total);
                                                    final List<BarEntry> monthEntries = new ArrayList<>();
                                                    final List<BarEntry> dateEntries = new ArrayList<>();

                                                    spinYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                        @Override
                                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                            if(position!=0){
                                                                choosenYear = (String)parent.getItemAtPosition(position);

                                                                refPOSProductSale.child(productName).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                                        final float[] yearSale = {0};
                                                                        for (int i = 1;i<=12;i++){
                                                                            if(dataSnapshot.hasChild(choosenYear+"-"+i)){
                                                                                refPOSProductSale.child(productName).child(choosenYear+"-"+i).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                    @Override
                                                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                        float monthSale = Float.parseFloat(dataSnapshot.getValue().toString());
                                                                                        yearSale[0] = yearSale[0] +monthSale;
                                                                                        tvCustomerTotal.setText(Utils.convertNumber(yearSale[0]+""));

                                                                                    }

                                                                                    @Override
                                                                                    public void onCancelled(DatabaseError databaseError) {

                                                                                    }
                                                                                });
                                                                            }

                                                                        }


                                                                    }

                                                                    @Override
                                                                    public void onCancelled(DatabaseError databaseError) {

                                                                    }
                                                                });

                                                                refPOSProductSale.child(productName).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                                        Iterable<DataSnapshot> iterable = dataSnapshot.getChildren();
                                                                        for(DataSnapshot it:iterable){
                                                                            String key = it.getKey();
                                                                            float value = Float.parseFloat(it.getValue().toString());
                                                                            for(int i = 1;i<=12;i++){
                                                                                if(key.equals(choosenYear+"-"+i)){
                                                                                    //barValue.add(value);
                                                                                    monthEntries.add(new BarEntry(i,value));
                                                                                }else{
                                                                                    // barValue.add(0f);
                                                                                    monthEntries.add(new BarEntry(i,0f));

                                                                                }
                                                                            }

                                                                        }
                                                                        //CustomMarkerView marker = new CustomMarkerView(getApplicationContext(),R.layout.custom_marker_view_layout);

                                                                        BarDataSet set = new BarDataSet(monthEntries,"Doanh thu năm của "+choosenYear+" theo tháng");
                                                                        BarData data = new BarData(set);
                                                                        Description description = new Description();
                                                                        description.setText("");
                                                                        barTime.setDescription(description);
                                                                        barTime.getAxisRight().setEnabled(false);
                                                                        barTime.setTouchEnabled(true);
                                                                        //barTime.setMarker(mv);
                                                                        barTime.setData(data);
                                                                        barTime.animateXY(1000,2000);
                                                                        barTime.setFitBars(true); // make the x-axis fit exactly all bars
                                                                        barTime.invalidate(); // refresh


                                                                    }

                                                                    @Override
                                                                    public void onCancelled(DatabaseError databaseError) {

                                                                    }
                                                                });
                                                            }


                                                        }

                                                        @Override
                                                        public void onNothingSelected(AdapterView<?> parent) {

                                                        }
                                                    });
                                                    spinMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                        @Override
                                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                            if(position!= 0){
                                                                choosenMonth = (String)parent.getItemAtPosition(position);

                                                                refPOSProductSale.child(productName).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                                        float monthSale = 0;
                                                                        for (int i = 1;i<=31;i++){
                                                                            if(dataSnapshot.hasChild(choosenYear+"-"+choosenMonth+"-"+i)){
                                                                                String daySale = dataSnapshot.child(choosenYear+"-"+choosenMonth+"-"+i).getValue().toString();
                                                                                monthSale = monthSale + Float.parseFloat(daySale);
                                                                                tvCustomerTotal.setText(Utils.convertNumber(monthSale+""));

                                                                            }
                                                                        }

                                                                    }

                                                                    @Override
                                                                    public void onCancelled(DatabaseError databaseError) {

                                                                    }
                                                                });
                                                                refPOSProductSale.child(productName).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                                        Iterable<DataSnapshot> iterable = dataSnapshot.getChildren();
                                                                        for(DataSnapshot it:iterable){
                                                                            String key = it.getKey();
                                                                            float value = Float.parseFloat(it.getValue().toString());
                                                                            for(int i = 1;i<=31;i++){
                                                                                if(key.equals(choosenYear+"-"+choosenMonth+"-"+i)){
                                                                                    //barValue.add(value);
                                                                                    dateEntries.add(new BarEntry(i,value));
                                                                                }else{
                                                                                    // barValue.add(0f);
                                                                                    dateEntries.add(new BarEntry(i,0f));

                                                                                }
                                                                            }

                                                                        }


                                                                        //CustomMarkerView mv = new CustomMarkerView (getApplicationContext(), R.layout.custom_marker_view_layout);

                                                                        BarDataSet set = new BarDataSet(dateEntries,"Doanh thu sản phẩm tháng "+choosenMonth+" theo ngày");
                                                                        BarData data = new BarData(set);
                                                                        Description description = new Description();
                                                                        description.setText("");
                                                                        barTime.setDescription(description);
                                                                        barTime.getAxisRight().setEnabled(false);
                                                                        barTime.setTouchEnabled(true);
                                                                        //barTime.setMarker(mv);
                                                                        barTime.setData(data);
                                                                        barTime.animateXY(1000,2000);
                                                                        barTime.setFitBars(true); // make the x-axis fit exactly all bars
                                                                        barTime.invalidate(); // refresh

                                                                    }

                                                                    @Override
                                                                    public void onCancelled(DatabaseError databaseError) {

                                                                    }
                                                                });
                                                            }

                                                        }

                                                        @Override
                                                        public void onNothingSelected(AdapterView<?> parent) {

                                                        }
                                                    });
                                                }
                                            });

                                            builderPrice.show();

                                        }

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        return true;
                    }
                    return false;
                }
            });

 */

        }

    }
    private class FoodPromotionOrderHolder extends RecyclerView.ViewHolder {
        TextView name,quantity,slash,finished;

        public FoodPromotionOrderHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_item_food_day_name);
            quantity = itemView.findViewById(R.id.tv_item_food_day_quantity);


        }

    }
    private class FoodReviewOrderHolder extends RecyclerView.ViewHolder {
        TextView name,quantity,slash,finished;

        public FoodReviewOrderHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_item_food_day_name);
            quantity = itemView.findViewById(R.id.tv_item_food_day_quantity);
            slash = itemView.findViewById(R.id.tv_item_food_review_slash);
            finished = itemView.findViewById(R.id.tv_item_food_review_done);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.startAnimation(Constant.buttonClick);

                    int position = getLayoutPosition();
                    Food product = adapterFirebasePromotion.getItem(position);
                    final String promotionName = product.getFoodName();
                    AlertDialog.Builder builder = new AlertDialog.Builder(ResCashier.this);
                    LayoutInflater inflater = getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.dialog_pos_promotion_quantity, null);
                    builder.setView(dialogView);
                    final Dialog dialog = builder.create();
                    dialog.show();

                    TextView tvPromotionName = dialogView.findViewById(R.id.tv_pos_promotionQuantity);
                    final EditText edtPromtionQuantity = dialogView.findViewById(R.id.edt_pos_promotion_quantity);
                    Button btnOk = dialogView.findViewById(R.id.btn_pos_promotion_quantity);

                    tvPromotionName.setText(promotionName);

                    btnOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            v.startAnimation(Constant.buttonClick);
                            final String promotionQuantity = edtPromtionQuantity.getText().toString();
                            if(TextUtils.isEmpty(promotionQuantity)){
                                Toast.makeText(getApplicationContext(),"Vui lòng nhập số lượng khuyến mãi",Toast.LENGTH_LONG).show();
                            }else{
                                //set promotion product of table = true
                                Food promotionProduct = new Food(promotionName,promotionQuantity);
                                refUserUid.child("Order "+tableName).child("Promotion").push().setValue(promotionProduct);
                                /*
                                                                refFood.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Iterable<DataSnapshot> foodSnap = dataSnapshot.getChildren();
                                        for(DataSnapshot itemFood:foodSnap){
                                            Food food = itemFood.getValue(Food.class);
                                            String foodName = food.getFoodName();
                                            String foodPrice = food.getFoodPrice();
                                            if(promotionName.equals(foodName)){
                                                productPromotionValueFloat = productPromotionValueFloat + Float.parseFloat(foodPrice)*Float.parseFloat(promotionQuantity);
                                                promotionValue = productPromotionValueFloat+"";
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                                 */

                            }

                            dialog.dismiss();
                        }
                    });

                }
            });

        }

    }

    private void hashMapTest(){

        refBillByTime.child(thisYear+"-"+thisMonth+"-"+thisDate).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Iterable<DataSnapshot> snapSaleDay = dataSnapshot.getChildren();
                for (DataSnapshot itemMaterial:snapSaleDay){

                   String node = itemMaterial.getKey();

                   refBillByTime.child("Bill").child(node).child("Food").addListenerForSingleValueEvent(new ValueEventListener() {
                       @Override
                       public void onDataChange(DataSnapshot dataSnapshot) {

                           Iterable<DataSnapshot> snapFood = dataSnapshot.getChildren();
                           for (DataSnapshot itemFood : snapFood) {

                               Food food = itemFood.getValue(Food.class);

                               //foodName = food.getFoodName();
                               //foodAmount = food.getFoodQuantityFinished();

                           }
                       }

                       @Override
                       public void onCancelled(DatabaseError databaseError) {

                       }

                       });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        String test = "123";

        float test1 = Float.parseFloat(test);

        //Toast.makeText(getApplicationContext(), test1+"", Toast.LENGTH_LONG).show();



        //float foodAmountF = Float.parseFloat(foodAmount);

        //HashMap<String, Float> mapSaleDay = new HashMap<String, Float>();

        //mapSaleDay.put(foodName,foodAmountF);

        //Toast.makeText(getApplicationContext(), foodName, Toast.LENGTH_LONG).show();
        //Toast.makeText(getApplicationContext(), foodAmount, Toast.LENGTH_LONG).show();



/*
        //kiem tra trung
        if (mapSaleDay.containsKey(foodName)){
            Float slhientai = mapSaleDay.get(foodAmountF);
            Float slnew = slhientai + slnew;
            mapSaleDay.put(foodName, foodAmountF);
        }

        List<Food> listSaleDay = new ArrayList<>();


*/
        //interate mapSaleDay;
        //Food addFood = new F(foodName,foodAmountF);
        //listSaleDay.add(addFood);


    }

    private void daySaleDialog() {
        AlertDialog.Builder builderPrice = new AlertDialog.Builder(ResCashier.this);
        LayoutInflater inflater = LayoutInflater.from(ResCashier.this);
        View dialogView = inflater.inflate(R.layout.dialog_day_sale,null);
        builderPrice.setView(dialogView);
        final Dialog dialog = builderPrice.create();
        dialog.show();

        Button btnMoreAction = dialogView.findViewById(R.id.btn_day_vatu);
        final Switch swToday = dialogView.findViewById(R.id.switch_day_sale);
        billList = dialogView.findViewById(R.id.recycler_day_sale);
        final Spinner spinDay = dialogView.findViewById(R.id.spin_day_sale);
        final TextView tvSaleTotal = dialogView.findViewById(R.id.tv_day_sale_total);

        spinDay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position!=0){
                    final String choosenDate = (String)parent.getItemAtPosition(position);
                    refBillByTime.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild(thisYear+"-"+thisMonth+"-"+choosenDate)){
                                billList(thisYear+"-"+thisMonth+"-"+choosenDate);
                                refBillByTime.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.hasChild(thisYear+"-"+thisMonth+"-"+choosenDate)){
                                            refBillByTime.child(thisYear+"-"+thisMonth+"-"+choosenDate).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    Iterable<DataSnapshot> billSnap = dataSnapshot.getChildren();
                                                    float saleTotal = 0;
                                                    for (DataSnapshot bill:billSnap){
                                                        Bill currentBill = bill.getValue(Bill.class);
                                                        saleTotal = saleTotal + Float.parseFloat(currentBill.getPayment());
                                                    }
                                                    tvSaleTotal.setText(Utils.convertNumber(saleTotal+""));

                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });

                                        }else{
                                            tvSaleTotal.setText("0");
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });


                            }else{
                                Toast.makeText(getApplicationContext(),"Không có dữ liệu bán hàng vào ngày này",Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        swToday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    swToday.setText("Ngày khác");
                    spinDay.setVisibility(View.VISIBLE);
                }else{
                    swToday.setText("Hôm nay");
                    spinDay.setVisibility(View.GONE);
                    billList(thisYear+"-"+thisMonth+"-"+thisDate);

                    refBillByTime.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild(thisYear+"-"+thisMonth+"-"+thisDate)){
                                refBillByTime.child(thisYear+"-"+thisMonth+"-"+thisDate).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Iterable<DataSnapshot> billSnap = dataSnapshot.getChildren();
                                        float saleTotal = 0;
                                        for (DataSnapshot bill:billSnap){
                                            Bill currentBill = bill.getValue(Bill.class);
                                            saleTotal = saleTotal + Float.parseFloat(currentBill.getPayment());
                                        }
                                        tvSaleTotal.setText(Utils.convertNumber(saleTotal+""));

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }else{
                                tvSaleTotal.setText("0");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            }
        });
        refBillByTime.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(thisYear+"-"+thisMonth+"-"+thisDate)){
                    refBillByTime.child(thisYear+"-"+thisMonth+"-"+thisDate).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Iterable<DataSnapshot> billSnap = dataSnapshot.getChildren();
                            float saleTotal = 0;
                            for (DataSnapshot bill:billSnap){
                                Bill currentBill = bill.getValue(Bill.class);
                                saleTotal = saleTotal + Float.parseFloat(currentBill.getPayment());
                            }
                            tvSaleTotal.setText(Utils.convertNumber(saleTotal+""));

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }else{
                    tvSaleTotal.setText("0");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        billList(thisYear+"-"+thisMonth+"-"+thisDate);

        btnMoreAction.setVisibility(View.GONE);

    }

    private void dayFoodSaleDialog() {



        AlertDialog.Builder builderPrice = new AlertDialog.Builder(ResCashier.this);
        LayoutInflater inflater = LayoutInflater.from(ResCashier.this);
        View dialogView = inflater.inflate(R.layout.dialog_food_report, null);
        builderPrice.setView(dialogView);
        final Dialog dialog = builderPrice.create();

        dialog.show();


        final Button btnDayMtr = dialogView.findViewById(R.id.btn_day_vatu);

        final Switch swToday = dialogView.findViewById(R.id.switch_day_sale);

        final Spinner spinDay = dialogView.findViewById(R.id.spin_day_sale);

        foodSaleRecycler = dialogView.findViewById(R.id.recycler_day_sale);

        mapSaleDay.clear();
        refBillByTime.child(thisYear + "-" + thisMonth + "-" + thisDate).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final int countB = (int) dataSnapshot.getChildrenCount();

                Iterable<DataSnapshot> snapBill = dataSnapshot.getChildren();

                int i = 0;

                for (final DataSnapshot itemBill : snapBill) {

                    String keyBill = itemBill.getKey();

                    i = i + 1;

                    if (i == countB) {

                        mapMtrDay.clear();

                        refOrder.child("Bill").child(keyBill).child("Food").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                final int countF = (int) dataSnapshot.getChildrenCount();

                                int ii = 0;

                                Iterable<DataSnapshot> snapFood = dataSnapshot.getChildren();
                                for (DataSnapshot itemFood : snapFood) {

                                    Food food = itemFood.getValue(Food.class);

                                    foodNameSale = food.getFoodName();
                                    foodAmountSale = food.getFoodQuantityFinished();

                                    ii = ii + 1;
                                    if (ii == countF) {

                                        if (mapSaleDay.containsKey(foodNameSale)) {


                                            float OLDfoodAmountF = mapSaleDay.get(foodNameSale);
                                            float NEWfoodAmountF = Float.parseFloat(foodAmountSale);

                                            float FINALfoodAmountF = OLDfoodAmountF + NEWfoodAmountF;

                                            mapSaleDay.put(foodNameSale, FINALfoodAmountF);

                                            Toast.makeText(getApplicationContext(), mapSaleDay.size() + "", Toast.LENGTH_LONG).show();


                                            Iterator<Map.Entry<String, Float>> iterator = mapSaleDay.entrySet().iterator();

                                            foodSaleList.clear();

                                            for (Map.Entry<String, Float> entry : mapSaleDay.entrySet()) {

                                                final String foodNameKey = entry.getKey();

                                                final float foodAmountValue = entry.getValue();

                                                Food foodSale = new Food(foodNameKey, foodAmountValue + "");

                                                foodSaleList.add(foodSale);
                                                foodSaleListTD.add(foodSale);

                                                //adapterFoodSale = new AdapterFoodSale(getApplicationContext(), foodSaleList);

                                                foodSaleRecycler.setAdapter(adapterFoodSale);

                                                final int sizeMapF = mapSaleDay.size();

                                                refFoodElement.child(foodNameKey).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        Iterable<DataSnapshot> snapElement = dataSnapshot.getChildren();
                                                        for (DataSnapshot itemElement : snapElement) {
                                                            final String mtrName = itemElement.getKey();
                                                            final String mtrAmount = itemElement.getValue().toString();

                                                            int iCmtr = 0;
                                                            iCmtr++;

                                                            if (iCmtr == sizeMapF) {

                                                                if (mapMtrDay.containsKey(mtrName)) {

                                                                    int soluongmonOLD = mapMtrDay.get(mtrName);

                                                                    int soluongmonNEW = (int) foodAmountValue;
                                                                    int soluongvt = Integer.parseInt(mtrAmount);

                                                                    int vtFINAL = soluongmonOLD + soluongmonNEW * soluongvt;

                                                                    mapMtrDay.put(mtrName, vtFINAL);

                                                                    Iterator<Map.Entry<String, Integer>> iterator = mapMtrDay.entrySet().iterator();

                                                                    mtrSaleList.clear();

                                                                    for (Map.Entry<String, Integer> entry : mapMtrDay.entrySet()) {

                                                                        String mtrNameKey = entry.getKey();

                                                                        float mtrAmountValue = entry.getValue();

                                                                        Material mtrSale = new Material(mtrNameKey, mtrAmountValue + "");

                                                                        mtrSaleList.add(mtrSale);
                                                                        adapterMtrSale = new AdapterMtrSale(getApplicationContext(), mtrSaleList, ResCashier.this);

                                                                    }
                                                                } else {

                                                                    int soluongmonNEW = (int) foodAmountValue;
                                                                    int soluongvt = Integer.parseInt(mtrAmount);

                                                                    int vtFINAL = soluongmonNEW * soluongvt;

                                                                    mapMtrDay.put(mtrName, vtFINAL);

                                                                    Iterator<Map.Entry<String, Integer>> iterator = mapMtrDay.entrySet().iterator();

                                                                    mtrSaleList.clear();

                                                                    for (Map.Entry<String, Integer> entry : mapMtrDay.entrySet()) {

                                                                        String mtrNameKey = entry.getKey();

                                                                        float mtrAmountValue = entry.getValue();

                                                                        Material mtrSale = new Material(mtrNameKey, mtrAmountValue + "");

                                                                        mtrSaleList.add(mtrSale);
                                                                        adapterMtrSale = new AdapterMtrSale(getApplicationContext(), mtrSaleList, ResCashier.this);

                                                                    }
                                                                }
                                                            } else {
                                                                if (mapMtrDay.containsKey(mtrName)) {

                                                                    int soluongmonOLD = mapMtrDay.get(mtrName);

                                                                    int soluongmonNEW = (int) foodAmountValue;
                                                                    int soluongvt = Integer.parseInt(mtrAmount);

                                                                    int vtFINAL = soluongmonOLD + soluongmonNEW * soluongvt;

                                                                    mapMtrDay.put(mtrName, vtFINAL);

                                                                    Iterator<Map.Entry<String, Integer>> iterator = mapMtrDay.entrySet().iterator();

                                                                    mtrSaleList.clear();

                                                                    for (Map.Entry<String, Integer> entry : mapMtrDay.entrySet()) {

                                                                        String mtrNameKey = entry.getKey();

                                                                        float mtrAmountValue = entry.getValue();

                                                                        Material mtrSale = new Material(mtrNameKey, mtrAmountValue + "");

                                                                        mtrSaleList.add(mtrSale);
                                                                        adapterMtrSale = new AdapterMtrSale(getApplicationContext(), mtrSaleList, ResCashier.this);

                                                                    }
                                                                } else {

                                                                    int soluongmonNEW = (int) foodAmountValue;
                                                                    int soluongvt = Integer.parseInt(mtrAmount);

                                                                    int vtFINAL = soluongmonNEW * soluongvt;

                                                                    mapMtrDay.put(mtrName, vtFINAL);

                                                                }

                                                            }

                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });

                                            }
                                        } else {
                                            float NEWfoodAmountF = Float.parseFloat(foodAmountSale);

                                            mapSaleDay.put(foodNameSale, NEWfoodAmountF);
                                            //Toast.makeText(getApplicationContext(), mapSaleDay.size()+"", Toast.LENGTH_LONG).show();

                                            final int sizeMapF = mapSaleDay.size();

                                            Iterator<Map.Entry<String, Float>> iterator = mapSaleDay.entrySet().iterator();

                                            foodSaleList.clear();

                                            for (Map.Entry<String, Float> entry : mapSaleDay.entrySet()) {

                                                String foodNameKey = entry.getKey();

                                                final float foodAmountValue = entry.getValue();

                                                Food foodSale = new Food(foodNameKey, foodAmountValue + "");

                                                foodSaleList.add(foodSale);
                                                foodSaleListTD.add(foodSale);

                                                //adapterFoodSale = new AdapterFoodSale(getApplicationContext(), foodSaleList);
                                                foodSaleRecycler.setAdapter(adapterFoodSale);

                                                refFoodElement.child(foodNameKey).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        Iterable<DataSnapshot> snapElement = dataSnapshot.getChildren();
                                                        for (DataSnapshot itemElement : snapElement) {
                                                            final String mtrName = itemElement.getKey();
                                                            final String mtrAmount = itemElement.getValue().toString();

                                                            int iCmtr = 0;
                                                            iCmtr++;

                                                            if (iCmtr == sizeMapF) {

                                                                if (mapMtrDay.containsKey(mtrName)) {

                                                                    int soluongmonOLD = mapMtrDay.get(mtrName);

                                                                    int soluongmonNEW = (int) foodAmountValue;
                                                                    int soluongvt = Integer.parseInt(mtrAmount);

                                                                    int vtFINAL = soluongmonOLD + soluongmonNEW * soluongvt;

                                                                    mapMtrDay.put(mtrName, vtFINAL);

                                                                    Iterator<Map.Entry<String, Integer>> iterator = mapMtrDay.entrySet().iterator();

                                                                    mtrSaleList.clear();

                                                                    for (Map.Entry<String, Integer> entry : mapMtrDay.entrySet()) {

                                                                        String mtrNameKey = entry.getKey();

                                                                        float mtrAmountValue = entry.getValue();

                                                                        Material mtrSale = new Material(mtrNameKey, mtrAmountValue + "");

                                                                        mtrSaleList.add(mtrSale);
                                                                        adapterMtrSale = new AdapterMtrSale(getApplicationContext(), mtrSaleList, ResCashier.this);

                                                                    }
                                                                } else {

                                                                    int soluongmonNEW = (int) foodAmountValue;
                                                                    int soluongvt = Integer.parseInt(mtrAmount);

                                                                    int vtFINAL = soluongmonNEW * soluongvt;

                                                                    mapMtrDay.put(mtrName, vtFINAL);

                                                                    Iterator<Map.Entry<String, Integer>> iterator = mapMtrDay.entrySet().iterator();

                                                                    mtrSaleList.clear();

                                                                    for (Map.Entry<String, Integer> entry : mapMtrDay.entrySet()) {

                                                                        String mtrNameKey = entry.getKey();

                                                                        float mtrAmountValue = entry.getValue();

                                                                        Material mtrSale = new Material(mtrNameKey, mtrAmountValue + "");

                                                                        mtrSaleList.add(mtrSale);
                                                                        adapterMtrSale = new AdapterMtrSale(getApplicationContext(), mtrSaleList, ResCashier.this);

                                                                    }
                                                                }
                                                            } else {
                                                                if (mapMtrDay.containsKey(mtrName)) {

                                                                    int soluongmonOLD = mapMtrDay.get(mtrName);

                                                                    int soluongmonNEW = (int) foodAmountValue;
                                                                    int soluongvt = Integer.parseInt(mtrAmount);

                                                                    int vtFINAL = soluongmonOLD + soluongmonNEW * soluongvt;

                                                                    mapMtrDay.put(mtrName, vtFINAL);

                                                                    Iterator<Map.Entry<String, Integer>> iterator = mapMtrDay.entrySet().iterator();

                                                                    mtrSaleList.clear();

                                                                    for (Map.Entry<String, Integer> entry : mapMtrDay.entrySet()) {

                                                                        String mtrNameKey = entry.getKey();

                                                                        float mtrAmountValue = entry.getValue();

                                                                        Material mtrSale = new Material(mtrNameKey, mtrAmountValue + "");

                                                                        mtrSaleList.add(mtrSale);
                                                                        adapterMtrSale = new AdapterMtrSale(getApplicationContext(), mtrSaleList, ResCashier.this);

                                                                    }
                                                                } else {

                                                                    int soluongmonNEW = (int) foodAmountValue;
                                                                    int soluongvt = Integer.parseInt(mtrAmount);

                                                                    int vtFINAL = soluongmonNEW * soluongvt;

                                                                    mapMtrDay.put(mtrName, vtFINAL);

                                                                }

                                                            }

                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });

                                            }
                                        }

                                    } else {

                                        if (mapSaleDay.containsKey(foodNameSale)) {

                                            float OLDfoodAmountF = mapSaleDay.get(foodNameSale);
                                            float NEWfoodAmountF = Float.parseFloat(foodAmountSale);

                                            float FINALfoodAmountF = OLDfoodAmountF + NEWfoodAmountF;

                                            mapSaleDay.put(foodNameSale, FINALfoodAmountF);

                                        } else {

                                            float NEWfoodAmountF = Float.parseFloat(foodAmountSale);

                                            mapSaleDay.put(foodNameSale, NEWfoodAmountF);

                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    } else {

                        refOrder.child("Bill").child(keyBill).child("Food").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                final int countF = (int) dataSnapshot.getChildrenCount();

                                int ii = 0;

                                Iterable<DataSnapshot> snapFood = dataSnapshot.getChildren();
                                for (DataSnapshot itemFood : snapFood) {

                                    Food food = itemFood.getValue(Food.class);

                                    foodNameSale = food.getFoodName();
                                    foodAmountSale = food.getFoodQuantityFinished();


                                    ii = ii + 1;
                                    if (ii == countF) {

                                        if (mapSaleDay.containsKey(foodNameSale)) {

                                            float OLDfoodAmountF = mapSaleDay.get(foodNameSale);
                                            float NEWfoodAmountF = Float.parseFloat(foodAmountSale);

                                            float FINALfoodAmountF = OLDfoodAmountF + NEWfoodAmountF;

                                            mapSaleDay.put(foodNameSale, FINALfoodAmountF);

                                        } else {

                                            float NEWfoodAmountF = Float.parseFloat(foodAmountSale);

                                            mapSaleDay.put(foodNameSale, NEWfoodAmountF);
                                        }

                                    } else {

                                        if (mapSaleDay.containsKey(foodNameSale)) {

                                            float OLDfoodAmountF = mapSaleDay.get(foodNameSale);
                                            float NEWfoodAmountF = Float.parseFloat(foodAmountSale);

                                            float FINALfoodAmountF = OLDfoodAmountF + NEWfoodAmountF;

                                            mapSaleDay.put(foodNameSale, FINALfoodAmountF);

                                        } else {

                                            float NEWfoodAmountF = Float.parseFloat(foodAmountSale);

                                            mapSaleDay.put(foodNameSale, NEWfoodAmountF);

                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        foodSaleRecycler(thisYear+"-"+thisMonth+"-"+thisDate);

        spinDay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position!=0){
                    String choosenDate = (String)parent.getItemAtPosition(position);

                    mapSaleDay.clear();

                    refBillByTime.child(thisYear + "-" + thisMonth + "-" + choosenDate).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            final int countB = (int) dataSnapshot.getChildrenCount();

                            Iterable<DataSnapshot> snapBill = dataSnapshot.getChildren();

                            int i = 0;

                            for (final DataSnapshot itemBill : snapBill) {

                                String keyBill = itemBill.getKey();

                                i = i + 1;

                                if (i == countB) {

                                    mapMtrDay.clear();

                                    refOrder.child("Bill").child(keyBill).child("Food").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            final int countF = (int) dataSnapshot.getChildrenCount();

                                            int ii = 0;

                                            Iterable<DataSnapshot> snapFood = dataSnapshot.getChildren();
                                            for (DataSnapshot itemFood : snapFood) {

                                                Food food = itemFood.getValue(Food.class);

                                                foodNameSale = food.getFoodName();
                                                foodAmountSale = food.getFoodQuantityFinished();

                                                ii = ii + 1;
                                                if (ii == countF) {

                                                    if (mapSaleDay.containsKey(foodNameSale)) {


                                                        float OLDfoodAmountF = mapSaleDay.get(foodNameSale);
                                                        float NEWfoodAmountF = Float.parseFloat(foodAmountSale);

                                                        float FINALfoodAmountF = OLDfoodAmountF + NEWfoodAmountF;

                                                        mapSaleDay.put(foodNameSale, FINALfoodAmountF);

                                                        Toast.makeText(getApplicationContext(), mapSaleDay.size() + "", Toast.LENGTH_LONG).show();


                                                        Iterator<Map.Entry<String, Float>> iterator = mapSaleDay.entrySet().iterator();

                                                        foodSaleList.clear();

                                                        for (Map.Entry<String, Float> entry : mapSaleDay.entrySet()) {

                                                            final String foodNameKey = entry.getKey();

                                                            final float foodAmountValue = entry.getValue();

                                                            Food foodSale = new Food(foodNameKey, foodAmountValue + "");

                                                            foodSaleList.add(foodSale);
                                                            //adapterFoodSale = new AdapterFoodSale(getApplicationContext(), foodSaleList);

                                                            foodSaleRecycler.setAdapter(adapterFoodSale);

                                                            final int sizeMapF = mapSaleDay.size();

                                                            refFoodElement.child(foodNameKey).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                    Iterable<DataSnapshot> snapElement = dataSnapshot.getChildren();
                                                                    for (DataSnapshot itemElement : snapElement) {
                                                                        final String mtrName = itemElement.getKey();
                                                                        final String mtrAmount = itemElement.getValue().toString();

                                                                        int iCmtr = 0;
                                                                        iCmtr++;

                                                                        if (iCmtr == sizeMapF) {

                                                                            if (mapMtrDay.containsKey(mtrName)) {

                                                                                int soluongmonOLD = mapMtrDay.get(mtrName);

                                                                                int soluongmonNEW = (int) foodAmountValue;
                                                                                int soluongvt = Integer.parseInt(mtrAmount);

                                                                                int vtFINAL = soluongmonOLD + soluongmonNEW * soluongvt;

                                                                                mapMtrDay.put(mtrName, vtFINAL);

                                                                                Iterator<Map.Entry<String, Integer>> iterator = mapMtrDay.entrySet().iterator();

                                                                                mtrSaleList.clear();

                                                                                for (Map.Entry<String, Integer> entry : mapMtrDay.entrySet()) {

                                                                                    String mtrNameKey = entry.getKey();

                                                                                    float mtrAmountValue = entry.getValue();

                                                                                    Material mtrSale = new Material(mtrNameKey, mtrAmountValue + "");

                                                                                    mtrSaleList.add(mtrSale);
                                                                                    adapterMtrSale = new AdapterMtrSale(getApplicationContext(), mtrSaleList, ResCashier.this);

                                                                                }
                                                                            } else {

                                                                                int soluongmonNEW = (int) foodAmountValue;
                                                                                int soluongvt = Integer.parseInt(mtrAmount);

                                                                                int vtFINAL = soluongmonNEW * soluongvt;

                                                                                mapMtrDay.put(mtrName, vtFINAL);

                                                                                Iterator<Map.Entry<String, Integer>> iterator = mapMtrDay.entrySet().iterator();

                                                                                mtrSaleList.clear();

                                                                                for (Map.Entry<String, Integer> entry : mapMtrDay.entrySet()) {

                                                                                    String mtrNameKey = entry.getKey();

                                                                                    float mtrAmountValue = entry.getValue();

                                                                                    Material mtrSale = new Material(mtrNameKey, mtrAmountValue + "");

                                                                                    mtrSaleList.add(mtrSale);
                                                                                    adapterMtrSale = new AdapterMtrSale(getApplicationContext(), mtrSaleList, ResCashier.this);

                                                                                }
                                                                            }
                                                                        } else {
                                                                            if (mapMtrDay.containsKey(mtrName)) {

                                                                                int soluongmonOLD = mapMtrDay.get(mtrName);

                                                                                int soluongmonNEW = (int) foodAmountValue;
                                                                                int soluongvt = Integer.parseInt(mtrAmount);

                                                                                int vtFINAL = soluongmonOLD + soluongmonNEW * soluongvt;

                                                                                mapMtrDay.put(mtrName, vtFINAL);

                                                                                Iterator<Map.Entry<String, Integer>> iterator = mapMtrDay.entrySet().iterator();

                                                                                mtrSaleList.clear();

                                                                                for (Map.Entry<String, Integer> entry : mapMtrDay.entrySet()) {

                                                                                    String mtrNameKey = entry.getKey();

                                                                                    float mtrAmountValue = entry.getValue();

                                                                                    Material mtrSale = new Material(mtrNameKey, mtrAmountValue + "");

                                                                                    mtrSaleList.add(mtrSale);
                                                                                    adapterMtrSale = new AdapterMtrSale(getApplicationContext(), mtrSaleList, ResCashier.this);

                                                                                }
                                                                            } else {

                                                                                int soluongmonNEW = (int) foodAmountValue;
                                                                                int soluongvt = Integer.parseInt(mtrAmount);

                                                                                int vtFINAL = soluongmonNEW * soluongvt;

                                                                                mapMtrDay.put(mtrName, vtFINAL);

                                                                            }

                                                                        }

                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(DatabaseError databaseError) {

                                                                }
                                                            });

                                                        }
                                                    } else {
                                                        float NEWfoodAmountF = Float.parseFloat(foodAmountSale);

                                                        mapSaleDay.put(foodNameSale, NEWfoodAmountF);
                                                        //Toast.makeText(getApplicationContext(), mapSaleDay.size()+"", Toast.LENGTH_LONG).show();

                                                        final int sizeMapF = mapSaleDay.size();

                                                        Iterator<Map.Entry<String, Float>> iterator = mapSaleDay.entrySet().iterator();

                                                        foodSaleList.clear();

                                                        for (Map.Entry<String, Float> entry : mapSaleDay.entrySet()) {

                                                            String foodNameKey = entry.getKey();

                                                            final float foodAmountValue = entry.getValue();

                                                            Food foodSale = new Food(foodNameKey, foodAmountValue + "");

                                                            foodSaleList.add(foodSale);
                                                            //adapterFoodSale = new AdapterFoodSale(getApplicationContext(), foodSaleList);
                                                            foodSaleRecycler.setAdapter(adapterFoodSale);

                                                            refFoodElement.child(foodNameKey).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                    Iterable<DataSnapshot> snapElement = dataSnapshot.getChildren();
                                                                    for (DataSnapshot itemElement : snapElement) {
                                                                        final String mtrName = itemElement.getKey();
                                                                        final String mtrAmount = itemElement.getValue().toString();

                                                                        int iCmtr = 0;
                                                                        iCmtr++;

                                                                        if (iCmtr == sizeMapF) {

                                                                            if (mapMtrDay.containsKey(mtrName)) {

                                                                                int soluongmonOLD = mapMtrDay.get(mtrName);

                                                                                int soluongmonNEW = (int) foodAmountValue;
                                                                                int soluongvt = Integer.parseInt(mtrAmount);

                                                                                int vtFINAL = soluongmonOLD + soluongmonNEW * soluongvt;

                                                                                mapMtrDay.put(mtrName, vtFINAL);

                                                                                Iterator<Map.Entry<String, Integer>> iterator = mapMtrDay.entrySet().iterator();

                                                                                mtrSaleList.clear();

                                                                                for (Map.Entry<String, Integer> entry : mapMtrDay.entrySet()) {

                                                                                    String mtrNameKey = entry.getKey();

                                                                                    float mtrAmountValue = entry.getValue();

                                                                                    Material mtrSale = new Material(mtrNameKey, mtrAmountValue + "");

                                                                                    mtrSaleList.add(mtrSale);
                                                                                    adapterMtrSale = new AdapterMtrSale(getApplicationContext(), mtrSaleList, ResCashier.this);

                                                                                }
                                                                            } else {

                                                                                int soluongmonNEW = (int) foodAmountValue;
                                                                                int soluongvt = Integer.parseInt(mtrAmount);

                                                                                int vtFINAL = soluongmonNEW * soluongvt;

                                                                                mapMtrDay.put(mtrName, vtFINAL);

                                                                                Iterator<Map.Entry<String, Integer>> iterator = mapMtrDay.entrySet().iterator();

                                                                                mtrSaleList.clear();

                                                                                for (Map.Entry<String, Integer> entry : mapMtrDay.entrySet()) {

                                                                                    String mtrNameKey = entry.getKey();

                                                                                    float mtrAmountValue = entry.getValue();

                                                                                    Material mtrSale = new Material(mtrNameKey, mtrAmountValue + "");

                                                                                    mtrSaleList.add(mtrSale);
                                                                                    adapterMtrSale = new AdapterMtrSale(getApplicationContext(), mtrSaleList, ResCashier.this);

                                                                                }
                                                                            }
                                                                        } else {
                                                                            if (mapMtrDay.containsKey(mtrName)) {

                                                                                int soluongmonOLD = mapMtrDay.get(mtrName);

                                                                                int soluongmonNEW = (int) foodAmountValue;
                                                                                int soluongvt = Integer.parseInt(mtrAmount);

                                                                                int vtFINAL = soluongmonOLD + soluongmonNEW * soluongvt;

                                                                                mapMtrDay.put(mtrName, vtFINAL);

                                                                                Iterator<Map.Entry<String, Integer>> iterator = mapMtrDay.entrySet().iterator();

                                                                                mtrSaleList.clear();

                                                                                for (Map.Entry<String, Integer> entry : mapMtrDay.entrySet()) {

                                                                                    String mtrNameKey = entry.getKey();

                                                                                    float mtrAmountValue = entry.getValue();

                                                                                    Material mtrSale = new Material(mtrNameKey, mtrAmountValue + "");

                                                                                    mtrSaleList.add(mtrSale);
                                                                                    adapterMtrSale = new AdapterMtrSale(getApplicationContext(), mtrSaleList, ResCashier.this);

                                                                                }
                                                                            } else {

                                                                                int soluongmonNEW = (int) foodAmountValue;
                                                                                int soluongvt = Integer.parseInt(mtrAmount);

                                                                                int vtFINAL = soluongmonNEW * soluongvt;

                                                                                mapMtrDay.put(mtrName, vtFINAL);

                                                                            }

                                                                        }

                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(DatabaseError databaseError) {

                                                                }
                                                            });

                                                        }
                                                    }

                                                } else {

                                                    if (mapSaleDay.containsKey(foodNameSale)) {

                                                        float OLDfoodAmountF = mapSaleDay.get(foodNameSale);
                                                        float NEWfoodAmountF = Float.parseFloat(foodAmountSale);

                                                        float FINALfoodAmountF = OLDfoodAmountF + NEWfoodAmountF;

                                                        mapSaleDay.put(foodNameSale, FINALfoodAmountF);

                                                    } else {

                                                        float NEWfoodAmountF = Float.parseFloat(foodAmountSale);

                                                        mapSaleDay.put(foodNameSale, NEWfoodAmountF);

                                                    }
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                } else {

                                    refOrder.child("Bill").child(keyBill).child("Food").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            final int countF = (int) dataSnapshot.getChildrenCount();

                                            int ii = 0;

                                            Iterable<DataSnapshot> snapFood = dataSnapshot.getChildren();
                                            for (DataSnapshot itemFood : snapFood) {

                                                Food food = itemFood.getValue(Food.class);

                                                foodNameSale = food.getFoodName();
                                                foodAmountSale = food.getFoodQuantityFinished();


                                                ii = ii + 1;
                                                if (ii == countF) {

                                                    if (mapSaleDay.containsKey(foodNameSale)) {

                                                        float OLDfoodAmountF = mapSaleDay.get(foodNameSale);
                                                        float NEWfoodAmountF = Float.parseFloat(foodAmountSale);

                                                        float FINALfoodAmountF = OLDfoodAmountF + NEWfoodAmountF;

                                                        mapSaleDay.put(foodNameSale, FINALfoodAmountF);

                                                    } else {

                                                        float NEWfoodAmountF = Float.parseFloat(foodAmountSale);

                                                        mapSaleDay.put(foodNameSale, NEWfoodAmountF);
                                                    }

                                                } else {

                                                    if (mapSaleDay.containsKey(foodNameSale)) {

                                                        float OLDfoodAmountF = mapSaleDay.get(foodNameSale);
                                                        float NEWfoodAmountF = Float.parseFloat(foodAmountSale);

                                                        float FINALfoodAmountF = OLDfoodAmountF + NEWfoodAmountF;

                                                        mapSaleDay.put(foodNameSale, FINALfoodAmountF);

                                                    } else {

                                                        float NEWfoodAmountF = Float.parseFloat(foodAmountSale);

                                                        mapSaleDay.put(foodNameSale, NEWfoodAmountF);

                                                    }
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    //foodSaleRecycler.setVisibility(View.VISIBLE);
                    foodSaleRecycler(thisYear+"-"+thisMonth+"-"+choosenDate);


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        swToday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    swToday.setText("Ngày khác");
                    spinDay.setVisibility(View.VISIBLE);
                    //foodSaleRecycler.setVisibility(View.GONE);


                } else {
                    swToday.setText("Hôm nay");
                    spinDay.setVisibility(View.GONE);

                    mapSaleDay.clear();

                    refBillByTime.child(thisYear + "-" + thisMonth + "-" + thisDate).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            final int countB = (int) dataSnapshot.getChildrenCount();

                            Iterable<DataSnapshot> snapBill = dataSnapshot.getChildren();

                            int i = 0;

                            for (final DataSnapshot itemBill : snapBill) {

                                String keyBill = itemBill.getKey();

                                i = i + 1;

                                if (i == countB) {

                                    mapMtrDay.clear();

                                    refOrder.child("Bill").child(keyBill).child("Food").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            final int countF = (int) dataSnapshot.getChildrenCount();

                                            int ii = 0;

                                            Iterable<DataSnapshot> snapFood = dataSnapshot.getChildren();
                                            for (DataSnapshot itemFood : snapFood) {

                                                Food food = itemFood.getValue(Food.class);

                                                foodNameSale = food.getFoodName();
                                                foodAmountSale = food.getFoodQuantityFinished();

                                                ii = ii + 1;
                                                if (ii == countF) {

                                                    if (mapSaleDay.containsKey(foodNameSale)) {


                                                        float OLDfoodAmountF = mapSaleDay.get(foodNameSale);
                                                        float NEWfoodAmountF = Float.parseFloat(foodAmountSale);

                                                        float FINALfoodAmountF = OLDfoodAmountF + NEWfoodAmountF;

                                                        mapSaleDay.put(foodNameSale, FINALfoodAmountF);

                                                        Toast.makeText(getApplicationContext(), mapSaleDay.size() + "", Toast.LENGTH_LONG).show();


                                                        Iterator<Map.Entry<String, Float>> iterator = mapSaleDay.entrySet().iterator();

                                                        foodSaleList.clear();

                                                        for (Map.Entry<String, Float> entry : mapSaleDay.entrySet()) {

                                                            final String foodNameKey = entry.getKey();

                                                            final float foodAmountValue = entry.getValue();

                                                            Food foodSale = new Food(foodNameKey, foodAmountValue + "");

                                                            foodSaleList.add(foodSale);
                                                            foodSaleListTD.add(foodSale);

                                                            //adapterFoodSale = new AdapterFoodSale(getApplicationContext(), foodSaleList);

                                                            foodSaleRecycler.setAdapter(adapterFoodSale);

                                                            final int sizeMapF = mapSaleDay.size();

                                                            refFoodElement.child(foodNameKey).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                    Iterable<DataSnapshot> snapElement = dataSnapshot.getChildren();
                                                                    for (DataSnapshot itemElement : snapElement) {
                                                                        final String mtrName = itemElement.getKey();
                                                                        final String mtrAmount = itemElement.getValue().toString();

                                                                        int iCmtr = 0;
                                                                        iCmtr++;

                                                                        if (iCmtr == sizeMapF) {

                                                                            if (mapMtrDay.containsKey(mtrName)) {

                                                                                int soluongmonOLD = mapMtrDay.get(mtrName);

                                                                                int soluongmonNEW = (int) foodAmountValue;
                                                                                int soluongvt = Integer.parseInt(mtrAmount);

                                                                                int vtFINAL = soluongmonOLD + soluongmonNEW * soluongvt;

                                                                                mapMtrDay.put(mtrName, vtFINAL);

                                                                                Iterator<Map.Entry<String, Integer>> iterator = mapMtrDay.entrySet().iterator();

                                                                                mtrSaleList.clear();

                                                                                for (Map.Entry<String, Integer> entry : mapMtrDay.entrySet()) {

                                                                                    String mtrNameKey = entry.getKey();

                                                                                    float mtrAmountValue = entry.getValue();

                                                                                    Material mtrSale = new Material(mtrNameKey, mtrAmountValue + "");

                                                                                    mtrSaleList.add(mtrSale);
                                                                                    adapterMtrSale = new AdapterMtrSale(getApplicationContext(), mtrSaleList, ResCashier.this);

                                                                                }
                                                                            } else {

                                                                                int soluongmonNEW = (int) foodAmountValue;
                                                                                int soluongvt = Integer.parseInt(mtrAmount);

                                                                                int vtFINAL = soluongmonNEW * soluongvt;

                                                                                mapMtrDay.put(mtrName, vtFINAL);

                                                                                Iterator<Map.Entry<String, Integer>> iterator = mapMtrDay.entrySet().iterator();

                                                                                mtrSaleList.clear();

                                                                                for (Map.Entry<String, Integer> entry : mapMtrDay.entrySet()) {

                                                                                    String mtrNameKey = entry.getKey();

                                                                                    float mtrAmountValue = entry.getValue();

                                                                                    Material mtrSale = new Material(mtrNameKey, mtrAmountValue + "");

                                                                                    mtrSaleList.add(mtrSale);
                                                                                    adapterMtrSale = new AdapterMtrSale(getApplicationContext(), mtrSaleList, ResCashier.this);

                                                                                }
                                                                            }
                                                                        } else {
                                                                            if (mapMtrDay.containsKey(mtrName)) {

                                                                                int soluongmonOLD = mapMtrDay.get(mtrName);

                                                                                int soluongmonNEW = (int) foodAmountValue;
                                                                                int soluongvt = Integer.parseInt(mtrAmount);

                                                                                int vtFINAL = soluongmonOLD + soluongmonNEW * soluongvt;

                                                                                mapMtrDay.put(mtrName, vtFINAL);

                                                                                Iterator<Map.Entry<String, Integer>> iterator = mapMtrDay.entrySet().iterator();

                                                                                mtrSaleList.clear();

                                                                                for (Map.Entry<String, Integer> entry : mapMtrDay.entrySet()) {

                                                                                    String mtrNameKey = entry.getKey();

                                                                                    float mtrAmountValue = entry.getValue();

                                                                                    Material mtrSale = new Material(mtrNameKey, mtrAmountValue + "");

                                                                                    mtrSaleList.add(mtrSale);
                                                                                    adapterMtrSale = new AdapterMtrSale(getApplicationContext(), mtrSaleList, ResCashier.this);

                                                                                }
                                                                            } else {

                                                                                int soluongmonNEW = (int) foodAmountValue;
                                                                                int soluongvt = Integer.parseInt(mtrAmount);

                                                                                int vtFINAL = soluongmonNEW * soluongvt;

                                                                                mapMtrDay.put(mtrName, vtFINAL);

                                                                            }

                                                                        }

                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(DatabaseError databaseError) {

                                                                }
                                                            });

                                                        }
                                                    } else {
                                                        float NEWfoodAmountF = Float.parseFloat(foodAmountSale);

                                                        mapSaleDay.put(foodNameSale, NEWfoodAmountF);
                                                        //Toast.makeText(getApplicationContext(), mapSaleDay.size()+"", Toast.LENGTH_LONG).show();

                                                        final int sizeMapF = mapSaleDay.size();

                                                        Iterator<Map.Entry<String, Float>> iterator = mapSaleDay.entrySet().iterator();

                                                        foodSaleList.clear();

                                                        for (Map.Entry<String, Float> entry : mapSaleDay.entrySet()) {

                                                            String foodNameKey = entry.getKey();

                                                            final float foodAmountValue = entry.getValue();

                                                            Food foodSale = new Food(foodNameKey, foodAmountValue + "");

                                                            foodSaleList.add(foodSale);
                                                            foodSaleListTD.add(foodSale);

                                                            //adapterFoodSale = new AdapterFoodSale(getApplicationContext(), foodSaleList);
                                                            foodSaleRecycler.setAdapter(adapterFoodSale);

                                                            refFoodElement.child(foodNameKey).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                    Iterable<DataSnapshot> snapElement = dataSnapshot.getChildren();
                                                                    for (DataSnapshot itemElement : snapElement) {
                                                                        final String mtrName = itemElement.getKey();
                                                                        final String mtrAmount = itemElement.getValue().toString();

                                                                        int iCmtr = 0;
                                                                        iCmtr++;

                                                                        if (iCmtr == sizeMapF) {

                                                                            if (mapMtrDay.containsKey(mtrName)) {

                                                                                int soluongmonOLD = mapMtrDay.get(mtrName);

                                                                                int soluongmonNEW = (int) foodAmountValue;
                                                                                int soluongvt = Integer.parseInt(mtrAmount);

                                                                                int vtFINAL = soluongmonOLD + soluongmonNEW * soluongvt;

                                                                                mapMtrDay.put(mtrName, vtFINAL);

                                                                                Iterator<Map.Entry<String, Integer>> iterator = mapMtrDay.entrySet().iterator();

                                                                                mtrSaleList.clear();

                                                                                for (Map.Entry<String, Integer> entry : mapMtrDay.entrySet()) {

                                                                                    String mtrNameKey = entry.getKey();

                                                                                    float mtrAmountValue = entry.getValue();

                                                                                    Material mtrSale = new Material(mtrNameKey, mtrAmountValue + "");

                                                                                    mtrSaleList.add(mtrSale);
                                                                                    adapterMtrSale = new AdapterMtrSale(getApplicationContext(), mtrSaleList, ResCashier.this);

                                                                                }
                                                                            } else {

                                                                                int soluongmonNEW = (int) foodAmountValue;
                                                                                int soluongvt = Integer.parseInt(mtrAmount);

                                                                                int vtFINAL = soluongmonNEW * soluongvt;

                                                                                mapMtrDay.put(mtrName, vtFINAL);

                                                                                Iterator<Map.Entry<String, Integer>> iterator = mapMtrDay.entrySet().iterator();

                                                                                mtrSaleList.clear();

                                                                                for (Map.Entry<String, Integer> entry : mapMtrDay.entrySet()) {

                                                                                    String mtrNameKey = entry.getKey();

                                                                                    float mtrAmountValue = entry.getValue();

                                                                                    Material mtrSale = new Material(mtrNameKey, mtrAmountValue + "");

                                                                                    mtrSaleList.add(mtrSale);
                                                                                    adapterMtrSale = new AdapterMtrSale(getApplicationContext(), mtrSaleList, ResCashier.this);

                                                                                }
                                                                            }
                                                                        } else {
                                                                            if (mapMtrDay.containsKey(mtrName)) {

                                                                                int soluongmonOLD = mapMtrDay.get(mtrName);

                                                                                int soluongmonNEW = (int) foodAmountValue;
                                                                                int soluongvt = Integer.parseInt(mtrAmount);

                                                                                int vtFINAL = soluongmonOLD + soluongmonNEW * soluongvt;

                                                                                mapMtrDay.put(mtrName, vtFINAL);

                                                                                Iterator<Map.Entry<String, Integer>> iterator = mapMtrDay.entrySet().iterator();

                                                                                mtrSaleList.clear();

                                                                                for (Map.Entry<String, Integer> entry : mapMtrDay.entrySet()) {

                                                                                    String mtrNameKey = entry.getKey();

                                                                                    float mtrAmountValue = entry.getValue();

                                                                                    Material mtrSale = new Material(mtrNameKey, mtrAmountValue + "");

                                                                                    mtrSaleList.add(mtrSale);
                                                                                    adapterMtrSale = new AdapterMtrSale(getApplicationContext(), mtrSaleList, ResCashier.this);

                                                                                }
                                                                            } else {

                                                                                int soluongmonNEW = (int) foodAmountValue;
                                                                                int soluongvt = Integer.parseInt(mtrAmount);

                                                                                int vtFINAL = soluongmonNEW * soluongvt;

                                                                                mapMtrDay.put(mtrName, vtFINAL);

                                                                            }

                                                                        }

                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(DatabaseError databaseError) {

                                                                }
                                                            });

                                                        }
                                                    }

                                                } else {

                                                    if (mapSaleDay.containsKey(foodNameSale)) {

                                                        float OLDfoodAmountF = mapSaleDay.get(foodNameSale);
                                                        float NEWfoodAmountF = Float.parseFloat(foodAmountSale);

                                                        float FINALfoodAmountF = OLDfoodAmountF + NEWfoodAmountF;

                                                        mapSaleDay.put(foodNameSale, FINALfoodAmountF);

                                                    } else {

                                                        float NEWfoodAmountF = Float.parseFloat(foodAmountSale);

                                                        mapSaleDay.put(foodNameSale, NEWfoodAmountF);

                                                    }
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                } else {

                                    refOrder.child("Bill").child(keyBill).child("Food").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            final int countF = (int) dataSnapshot.getChildrenCount();

                                            int ii = 0;

                                            Iterable<DataSnapshot> snapFood = dataSnapshot.getChildren();
                                            for (DataSnapshot itemFood : snapFood) {

                                                Food food = itemFood.getValue(Food.class);

                                                foodNameSale = food.getFoodName();
                                                foodAmountSale = food.getFoodQuantityFinished();


                                                ii = ii + 1;
                                                if (ii == countF) {

                                                    if (mapSaleDay.containsKey(foodNameSale)) {

                                                        float OLDfoodAmountF = mapSaleDay.get(foodNameSale);
                                                        float NEWfoodAmountF = Float.parseFloat(foodAmountSale);

                                                        float FINALfoodAmountF = OLDfoodAmountF + NEWfoodAmountF;

                                                        mapSaleDay.put(foodNameSale, FINALfoodAmountF);

                                                    } else {

                                                        float NEWfoodAmountF = Float.parseFloat(foodAmountSale);

                                                        mapSaleDay.put(foodNameSale, NEWfoodAmountF);
                                                    }

                                                } else {

                                                    if (mapSaleDay.containsKey(foodNameSale)) {

                                                        float OLDfoodAmountF = mapSaleDay.get(foodNameSale);
                                                        float NEWfoodAmountF = Float.parseFloat(foodAmountSale);

                                                        float FINALfoodAmountF = OLDfoodAmountF + NEWfoodAmountF;

                                                        mapSaleDay.put(foodNameSale, FINALfoodAmountF);

                                                    } else {

                                                        float NEWfoodAmountF = Float.parseFloat(foodAmountSale);

                                                        mapSaleDay.put(foodNameSale, NEWfoodAmountF);

                                                    }
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    //foodSaleRecycler.setVisibility(View.VISIBLE);
                    foodSaleRecycler(thisYear+"-"+thisMonth+"-"+thisDate);

                }
            }
        });

        btnDayMtr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builderPrice = new AlertDialog.Builder(ResCashier.this);
                LayoutInflater inflater = LayoutInflater.from(ResCashier.this);
                View dialogView = inflater.inflate(R.layout.dialog_material_day, null);
                builderPrice.setView(dialogView);
                final Dialog dialog = builderPrice.create();
                dialog.show();

                final RecyclerView mtrSaleRecycler = dialogView.findViewById(R.id.recycler_day_mtr);

                LinearLayoutManager linearLayoutManagerMTR = new LinearLayoutManager(ResCashier.this);


                mtrSaleRecycler.setHasFixedSize(true);
                mtrSaleRecycler.setLayoutManager(linearLayoutManagerMTR);

                mtrSaleRecycler.setAdapter(adapterMtrSale);

            }
        });
    }

    private void checkTableActive() {
        refTable.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> tableSnap = dataSnapshot.getChildren();

                int i = 1;
                for (final DataSnapshot itemTable : tableSnap) {

                    final String tableKey = itemTable.getKey();

                    final int finalI = i;
                    refOrder.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild("Order Bàn " + finalI)) {
                                refTable.child(tableKey).child("active").setValue(true);
                            } else {
                                refTable.child(tableKey).child("active").setValue(false);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    i++;

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void promotionDialog() {
        AlertDialog.Builder builderBill = new AlertDialog.Builder(ResCashier.this);
        LayoutInflater inflater = LayoutInflater.from(ResCashier.this);
        final View dialogView = inflater.inflate(R.layout.dialog_promotion,null);
        builderBill.setView(dialogView);
        //builderBill.setCancelable(false);
        dialogPromotion = builderBill.create();
        dialogPromotion.show();

        final Spinner spinPromotionRate = dialogView.findViewById(R.id.spin_pos_promotion_rate);
        final Spinner spinPromotionType = dialogView.findViewById(R.id.spin_pos_promotion_type);
        final TextView tvPromotionValue = dialogView.findViewById(R.id.tv_pos_promotion_value);

        spinPromotionRate.setEnabled(false);

        Button btnOk = dialogView.findViewById(R.id.btn_pos_add_promotion);

        final RecyclerView productList = dialogView.findViewById(R.id.recycler_pos_promotion);
        productList.setHasFixedSize(true);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager((largeScreen)?4:2,LinearLayoutManager.VERTICAL);

        //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        productList.setLayoutManager(staggeredGridLayoutManager);
        productList.setVisibility(View.VISIBLE);

        adapterFirebasePromotion = new FirebaseRecyclerAdapter<Food, FoodReviewOrderHolder>(
                Food.class,
                R.layout.item_food_review_done,
                FoodReviewOrderHolder.class,
                refFood
        ) {
            @Override
            public FoodReviewOrderHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food_review_done, parent, false);
                return new FoodReviewOrderHolder(v);
            }


            @Override
            protected void populateViewHolder(FoodReviewOrderHolder viewHolder, Food model, int position) {
                viewHolder.name.setText(model.getFoodName());

            }
        };

        productList.setAdapter(adapterFirebasePromotion);
        adapterFirebasePromotion.notifyDataSetChanged();

        spinPromotionType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position!=0){
                    promotionType = (String) parent.getItemAtPosition(position);
                    b.putString("PromotionType",promotionType);

                    if(promotionType.equals("Món ăn")){
                        //promotionValue = "0";
                        tvPromotionValue.setText("0");
                        spinPromotionRate.setEnabled(false);
                        b.putBoolean(tableName+"PromotionProduct",true);

                    }else if(promotionType.equals("Mã KM")){
                        //dialogDiscountByCode();
                    }
                    else{
                        b.putBoolean(tableName+"PromotionCash",true);

                        refOrder.child("Order "+tableName).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.hasChild("Promotion")){
                                    refOrder.child("Order "+tableName).child("Promotion").setValue(null);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        spinPromotionRate.setEnabled(true);
                        spinPromotionRate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if(position!=0){
                                    promotionRate = (String) parent.getItemAtPosition(position);
                                    float promotionValueFloat = billPayment*Float.parseFloat(promotionRate)/100;
                                    promotionValue = promotionValueFloat+"";
                                    tvPromotionValue.setText(Utils.convertNumber(promotionValue+""));

                                }

                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(Constant.buttonClick);
                b.putBoolean(tableName+"SetCheckPromotion",true);

                if(promotionType==null){
                    Toast.makeText(getApplicationContext(),"Vui lòng chọn hình thức khuyến mãi",Toast.LENGTH_LONG).show();
                }else if(promotionType.equals("Tiền") && promotionRate==null) {
                    Toast.makeText(getApplicationContext(),"Vui lòng chọn mức chiết khấu",Toast.LENGTH_LONG).show();

                }else {
                    dialogPromotion.dismiss();
                }

            }
        });
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();

    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_res_cashier, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if( id == R.id.action_hash){
            hashMapTest();
        }
        if( id == R.id.action_report){
            daySaleDialog();
        }
        if( id == R.id.action_food_report){
            dayFoodSaleDialog();

        }


        return super.onOptionsItemSelected(item);
    }
}