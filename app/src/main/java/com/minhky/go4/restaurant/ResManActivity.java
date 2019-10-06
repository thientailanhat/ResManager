package com.minhky.go4.restaurant;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.minhky.go4.R;
import com.minhky.go4.admin.AdminActivity;
import com.minhky.go4.login.LoginActivity;
import com.minhky.go4.model.Account;
import com.minhky.go4.model.Food;
import com.minhky.go4.model.Material;
import com.minhky.go4.model.Table;
import com.minhky.go4.utils.Constant;
import com.minhky.go4.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

//import static com.minhky.go4.utils.Constant.refApps;
//import static com.minhky.go4.utils.Constant.refChef;
import static com.minhky.go4.utils.Constant.refFood;
import static com.minhky.go4.utils.Constant.refFoodElement;
import static com.minhky.go4.utils.Constant.refMaterial;
import static com.minhky.go4.utils.Constant.refOrder;
import static com.minhky.go4.utils.Constant.refTable;

public class ResManActivity extends AppCompatActivity {
    private FirebaseRecyclerAdapter<Table, TableViewHolder> adapterFirebase;
    private FirebaseRecyclerAdapter<Food, FoodViewHolder> adapterFirebaseOrder;
    private FirebaseRecyclerAdapter<Food, FoodReviewOrderHolder> adapterFirebaseReview;


    //private DatabaseReference refTable, refFood, refMaterial;
    private boolean portrait, largeScreen, picChoosen;


    private static final int TABLE_NORMAL = 0;
    private static final int TABLE_ACTIVE = 1;

    private String userEmail, tableName, orderPushKey;

    private Bundle b = new Bundle();

    private LinearLayoutManager linearLayoutManager;

    private String emailLogin, agent;
    private String spSelect1, spSelect2, spSelect3;
    private String foodAmount;
    String foodQuantityFinished;



    private ConstraintLayout constraintLayout;

    private ImageView ivFoodPic;
    private ImageView ivProductInvest;



    public static final int GALLERY_REQUEST = 999;
    public static final int MY_REQUEST_READ = 1000;
    public static final int MY_REQUEST_INVEST = 1001;
    private static final int FOOD_DONE = 2;
    private static final int FOOD_UNDONE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_res_man);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar_res_man);
        setSupportActionBar(toolbar);


        Intent it = this.getIntent();

        agent = it.getStringExtra("Agent");
        emailLogin = it.getStringExtra("EmailLogin");


        Toast.makeText(getApplicationContext(), agent, Toast.LENGTH_LONG).show();
        Toast.makeText(getApplicationContext(), emailLogin, Toast.LENGTH_LONG).show();

        userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", ",");

        //refTable = refApps.child("ResMan/Table");
        //refFood = refApps.child("ResMan/Food");
        //refMaterial = refChef.child("Material");


    }

    @Override
    protected void onResume() {
        super.onResume();

        RecyclerView tableList = findViewById(R.id.recycler_res_man);
        tableList.setHasFixedSize(true);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        tableList.setLayoutManager(staggeredGridLayoutManager);

        adapterFirebase = new FirebaseRecyclerAdapter<Table, TableViewHolder>(
                Table.class,
                R.layout.item_table,
                TableViewHolder.class,
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
            public TableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                if(viewType==TABLE_ACTIVE){
                    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_table_active,parent,false);
                    return new TableViewHolder(v);

                }else{
                    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_table,parent,false);
                    return new TableViewHolder(v);
                }
            }

            @SuppressLint("ResourceAsColor")
            @Override
            protected void populateViewHolder(final TableViewHolder viewHolder, final Table model, int position) {
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
                public void onClick(View v) {
                    v.startAnimation(Constant.buttonClick);
                    int position = getLayoutPosition();
                    Table table = adapterFirebase.getItem(position);
                    tableName = table.getTableName();
                    //orderPushKey = refOrder.push().getKey();
                    addTableOrderDialog();

                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ResManActivity.this);
                    builder.setMessage("Bớt 1 bàn?");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Query query = refTable.limitToLast(1);
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Iterable<DataSnapshot> itemTable = dataSnapshot.getChildren();
                                    for (DataSnapshot item : itemTable) {
                                        String key = item.getKey();
                                        refTable.child(key).setValue(null);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                        }
                    }).show();
                    return true;
                }
            });
        }
    }

    private void addTableOrderDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ResManActivity.this);
        LayoutInflater inflater = LayoutInflater.from(ResManActivity.this);
        View dialogView = inflater.inflate(R.layout.dialog_order_food, null);
        builder.setView(dialogView);
        final Dialog dialog = builder.create();
        dialog.show();

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                onResume();
            }
        });

        TextView tvTableName = dialogView.findViewById(R.id.tv_order_tableName);
        Button btnCancel = dialogView.findViewById(R.id.btn_order_food_cancel);
        tvTableName.setText(tableName);

        final RecyclerView foodList = dialogView.findViewById(R.id.recycler_order_food);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        foodList.setHasFixedSize(false);
        foodList.setLayoutManager(linearLayoutManager);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
        adapterFirebaseOrder = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(
                Food.class,
                R.layout.item_food,
                FoodViewHolder.class,
                refFood
        ) {

            @Override
            public FoodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food, parent, false);
                return new FoodViewHolder(v);
            }

            @Override
            protected void populateViewHolder(final FoodViewHolder viewHolder, final Food model, int position) {
                final String foodName = model.getFoodName();
                viewHolder.name.setText(foodName);
                viewHolder.price.setText(Utils.convertNumber(model.getFoodPrice()));
                Glide.with(getApplicationContext()).load(model.getFoodUrl()).error(R.drawable.cooking).into(viewHolder.pic);

            }
        };

        foodList.setAdapter(adapterFirebaseOrder);
        adapterFirebaseOrder.notifyDataSetChanged();

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.DOWN | ItemTouchHelper.UP) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                final int position = viewHolder.getAdapterPosition();
                final String itemKey = adapterFirebaseOrder.getRef(position).getKey();
                //refOrder.child("ResMan/Food").child(itemKey).setValue(null);
                adapterFirebaseOrder.notifyItemRemoved(position);
                adapterFirebaseOrder.notifyDataSetChanged();


            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(foodList);

        final RecyclerView reviewList = dialogView.findViewById(R.id.recycler_order_review);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager((largeScreen) ? 4 : 2, LinearLayoutManager.VERTICAL);
        reviewList.setHasFixedSize(false);
        reviewList.setLayoutManager(staggeredGridLayoutManager);

        adapterFirebaseReview = new FirebaseRecyclerAdapter<Food, FoodReviewOrderHolder>(
                Food.class,
                R.layout.item_food_review,
                FoodReviewOrderHolder.class,
                refOrder.child("Order " + tableName).child("FoodList")
        ) {
            @Override
            public int getItemViewType(int position) {
                Food food = getItem(position);
                if (food.isFoodUndone()) {
                    return FOOD_UNDONE;
                } else {
                    return FOOD_DONE;
                }
            }


            @Override
            public FoodReviewOrderHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                Toast.makeText(getApplicationContext(), viewType + "", Toast.LENGTH_LONG).show();

                if (viewType == FOOD_UNDONE) {
                    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food_review, parent, false);
                    return new FoodReviewOrderHolder(v);
                } else {
                    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food_review_done, parent, false);
                    //minusMaterial();
                    return new FoodReviewOrderHolder(v);
                }


            }

            @Override
            protected void populateViewHolder(final FoodReviewOrderHolder viewHolder, final Food model, int position) {
                final String foodName = model.getFoodName();
                viewHolder.name.setText(foodName);
                viewHolder.quantity.setText(model.getFoodQuantity());
                viewHolder.finished.setText(model.getFoodQuantityFinished());
            }
        };
        reviewList.setAdapter(adapterFirebaseReview);
        adapterFirebaseReview.notifyDataSetChanged();

    }

    private void minusMaterial() {
        Toast.makeText(getApplicationContext(), "minus", Toast.LENGTH_SHORT).show();

        //final Food model;

        //final String foodName = refFood.getFoodName();



        //refMaterial.child("Bún").child(na)

    }

    private class FoodViewHolder extends RecyclerView.ViewHolder {

        TextView name, price, add, minus, quantity;
        ImageView pic, note;

        public FoodViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_item_prepare_foodName);
            price = itemView.findViewById(R.id.tv_item_food_price);
            quantity = itemView.findViewById(R.id.tv_item_food_quantity);
            add = itemView.findViewById(R.id.tv_item_food_plus);
            minus = itemView.findViewById(R.id.tv_item_food_minus);
            pic = itemView.findViewById(R.id.iv_item_food_pic);
            note = itemView.findViewById(R.id.iv_item_food_note);

            quantity.setVisibility(View.INVISIBLE);

            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    v.startAnimation(Constant.buttonClick);
                    final int position = getLayoutPosition();
                    Food food = adapterFirebaseOrder.getItem(position);
                    final String foodName = food.getFoodName();
                    final String foodPrice = food.getFoodPrice();
                    add.setVisibility(View.INVISIBLE);

                    refOrder.child("Order " + tableName).child("FoodList").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(foodName)) {
                                refOrder.child("Order " + tableName).child("FoodList").child(foodName).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Food f = dataSnapshot.getValue(Food.class);
                                        String foodQuantity = f.getFoodQuantity();
                                        String foodPrice = f.getFoodPrice();
                                        int updatefoodQuantity = Integer.parseInt(foodQuantity) + 1;

                                        quantity.setVisibility(View.VISIBLE);
                                        quantity.setText(updatefoodQuantity + "");
                                        float updateFoodTotal = Float.parseFloat(foodPrice) * updatefoodQuantity;
                                        Food updateFood = new Food(tableName, foodName, foodPrice, updatefoodQuantity + "", updateFoodTotal + "", true, "0", "Không");

                                        refOrder.child("Preparation").push().setValue(updateFood);
                                        refOrder.child("Order " + tableName).child("FoodList").child(foodName).setValue(updateFood).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                        add.setVisibility(View.VISIBLE);
                                            }
                                        });

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                                checkTableActive();

                            } else {
                                quantity.setVisibility(View.VISIBLE);
                                quantity.setText(1 + "");
                                Food fList = new Food(tableName, foodName, foodPrice, 1 + "", foodPrice, true, "0", "Không");
                                refOrder.child("Preparation").push().setValue(fList);
                                refOrder.child("Order " + tableName).child("FoodList").child(foodName).setValue(fList).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                add.setVisibility(View.VISIBLE);
                                    }
                                });

                                checkTableActive();

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            });

            minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    v.startAnimation(Constant.buttonClick);
                    final int position = getLayoutPosition();
                    Food food = adapterFirebaseOrder.getItem(position);
                    final String foodName = food.getFoodName();
                    final String foodPrice = food.getFoodPrice();

                    minus.setVisibility(View.INVISIBLE);

                    refOrder.child("Order " + tableName).child("FoodList").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(foodName)) {
                                refOrder.child("Order " + tableName).child("FoodList").child(foodName).child("foodQuantity").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        String foodQuantity = dataSnapshot.getValue().toString();
                                        int updateFoodQuantity = Integer.parseInt(foodQuantity) - 1;
                                        float updateFoodTotal = Float.parseFloat(foodPrice) * updateFoodQuantity;

                                        if (updateFoodQuantity == 0) {
                                            refOrder.child("Order " + tableName).child("FoodList").child(foodName).setValue(null);
                                            quantity.setVisibility(View.VISIBLE);
                                            quantity.setText("0");
                                            minus.setVisibility(View.VISIBLE);

                                            checkTableActive();


                                        } else {
                                            quantity.setVisibility(View.VISIBLE);
                                            quantity.setText(updateFoodQuantity + "");

                                            refOrder.child("Order " + tableName).child("FoodList").child(foodName).child("foodQuantity").setValue(updateFoodQuantity + "");
                                            refOrder.child("Order " + tableName).child("FoodList").child(foodName).child("foodPayment").setValue(updateFoodTotal + "");

                                            minus.setVisibility(View.VISIBLE);

                                            checkTableActive();


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

                    refOrder.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild("Preparation")) {
                                refOrder.child("Preparation").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Iterable<DataSnapshot> foodSnap = dataSnapshot.getChildren();
                                        for (DataSnapshot itemFood : foodSnap) {
                                            DatabaseReference itemFoodRef = itemFood.getRef();
                                            Food food = itemFood.getValue(Food.class);
                                            String itemTableName = food.getTableName();
                                            String itemfFoodName = food.getFoodName();

                                            if (itemTableName.equals(tableName) && itemfFoodName.equals(foodName)) {
                                                itemFoodRef.setValue(null);
                                                break;
                                            }
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

                }
            });
        }
    }

    private class FoodReviewOrderHolder extends RecyclerView.ViewHolder {
        TextView name, quantity, slash, finished;

        public FoodReviewOrderHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_item_food_day_name);
            quantity = itemView.findViewById(R.id.tv_item_food_day_quantity);
            slash = itemView.findViewById(R.id.tv_item_food_review_slash);
            finished = itemView.findViewById(R.id.tv_item_food_review_done);

            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    int position = getLayoutPosition();
                    Food foodReview = adapterFirebaseReview.getItem(position);
                    final String foodName = foodReview.getFoodName();
                    final String foodQuantity = foodReview.getFoodQuantity();
                    final String foodQuantityFinished = foodReview.getFoodQuantityFinished();


                    //final String

                    AlertDialog.Builder builder = new AlertDialog.Builder(ResManActivity.this);
                    builder.setMessage("Đã mang ra cho khách?");

                    builder.setPositiveButton("Xong", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            Toast.makeText(getApplicationContext(), foodQuantity, Toast.LENGTH_LONG).show();



                            refOrder.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild("Preparation")) {
                                        refOrder.child("Preparation").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                Iterable<DataSnapshot> foodSnap = dataSnapshot.getChildren();
                                                for (DataSnapshot itemFood : foodSnap) {
                                                    DatabaseReference itemFoodRef = itemFood.getRef();
                                                    Food food = itemFood.getValue(Food.class);
                                                    String itemTableName = food.getTableName();
                                                    String itemfFoodName = food.getFoodName();

                                                    if (itemTableName.equals(tableName) && itemfFoodName.equals(foodName)) {
                                                        itemFoodRef.setValue(null);
                                                    }
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
                            refOrder.child("Order " + tableName).child("FoodList").child(foodName).child("foodUndone").setValue(false);
                            refOrder.child("Order " + tableName).child("FoodList").child(foodName).child("foodQuantityFinished").setValue(foodQuantity);

                            refFoodElement.child(foodName).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Iterable<DataSnapshot> snapElement = dataSnapshot.getChildren();
                                    for(DataSnapshot itemElement:snapElement){
                                        final String mtrName = itemElement.getKey();
                                        final String mtrAmount = itemElement.getValue().toString();

                                        refMaterial.child(mtrName).child("mtrAmount").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                String tontruoc = dataSnapshot.getValue().toString();
                                                int tontruocInt = Integer.parseInt(tontruoc);
                                                int soluongmon = Integer.parseInt(foodQuantity);
                                                int soluongvt = Integer.parseInt(mtrAmount);

                                                int toncuoi = tontruocInt - soluongmon * soluongvt;

                                                refMaterial.child(mtrName).child("mtrAmount").setValue(toncuoi+"");

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
                        }

                    }).setNegativeButton("Chưa", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AlertDialog.Builder builderBill = new AlertDialog.Builder(ResManActivity.this);
                            LayoutInflater inflater = LayoutInflater.from(ResManActivity.this);
                            View dialogView = inflater.inflate(R.layout.dialog_ready_food, null);
                            builderBill.setView(dialogView);
                            final Dialog dialogReadyFood = builderBill.create();
                            dialogReadyFood.show();

                            final EditText edtReadyQuantity = dialogView.findViewById(R.id.edt_ready_food_number);
                            Button btnOk = dialogView.findViewById(R.id.btn_ready_food_ok);

                            btnOk.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    v.startAnimation(Constant.buttonClick);
                                    String readyQuantity = edtReadyQuantity.getText().toString();
                                    if (TextUtils.isEmpty(readyQuantity)) {
                                        Toast.makeText(getApplicationContext(), "Vui lòng nhập số lượng", Toast.LENGTH_LONG).show();
                                    } else {
                                        refOrder.child("Order " + tableName).child("FoodList").child(foodName).child("foodQuantityFinished").setValue(readyQuantity);
                                        dialogReadyFood.dismiss();
                                    }
                                }
                            });
                        }
                    }).show();
                }
            });

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void dialogAddFood() {

        AlertDialog.Builder builder = new AlertDialog.Builder(ResManActivity.this);
        LayoutInflater inflater = LayoutInflater.from(ResManActivity.this);
        View dialogView = inflater.inflate(R.layout.dialog_add_food, null);
        builder.setView(dialogView);
        final Dialog dialog = builder.create();
        dialog.show();

        ivFoodPic = dialogView.findViewById(R.id.iv_add_food_pic);
        final EditText edtFoodName = dialogView.findViewById(R.id.edt_add_food_name);
        final EditText edtFoodPrice = dialogView.findViewById(R.id.edt_add_food_price);
        final Button btnOk = dialogView.findViewById(R.id.btn_add_food_ok);


        final Spinner spEle1 = dialogView.findViewById(R.id.sp_ele1);
        final Spinner spEle2 = dialogView.findViewById(R.id.sp_ele2);
        final Spinner spEle3 = dialogView.findViewById(R.id.sp_ele3);


        final EditText edtFoodEle1 = dialogView.findViewById(R.id.edt_ele1);
        final EditText edtFoodEle2 = dialogView.findViewById(R.id.edt_ele2);
        final EditText edtFoodEle3 = dialogView.findViewById(R.id.edt_ele3);



        spEle1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    spSelect1 = (String) parent.getItemAtPosition(position);
/*                    refFoodMaterial.child(spDuocChon).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String sl = dataSnapshot.getValue().toString();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
*/
                }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spEle2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                spSelect2 = (String) parent.getItemAtPosition(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spEle3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                spSelect3 = (String) parent.getItemAtPosition(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        picChoosen = false;

        ivFoodPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                picChoosen = true;
                if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                                MY_REQUEST_READ);
                    }

                } else {
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
                }
            }
        });


        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(Constant.buttonClick);
                final String foodName = edtFoodName.getText().toString();
                final String foodPrice = edtFoodPrice.getText().toString();

                final String foodEle1 = edtFoodEle1.getText().toString();
                final String foodEle2 = edtFoodEle2.getText().toString();
                final String foodEle3 = edtFoodEle3.getText().toString();

                refFoodElement.child(foodName).child(spSelect1).child("foodAmount").setValue(foodEle1);
                refFoodElement.child(foodName).child(spSelect2).child("foodAmount").setValue(foodEle2);
                refFoodElement.child(foodName).child(spSelect3).child("foodAmount").setValue(foodEle3);

                if (!picChoosen) {
                    Toast.makeText(getApplicationContext(), "Vui lòng chọn hình ảnh món ăn", Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(foodName) || TextUtils.isEmpty(foodPrice)) {
                    Toast.makeText(getApplicationContext(), "Vui lòng nhập đầy đủ thông tin của món", Toast.LENGTH_LONG).show();

                } else {
                    btnOk.setEnabled(false);
                    picChoosen = false;
                    StorageReference storageRefFood = Constant.storageRef.child("Food Name").child(foodName + ".jpg");

                    ivFoodPic.setDrawingCacheEnabled(true);
                    ivFoodPic.buildDrawingCache();
                    Bitmap bitmap = ivFoodPic.getDrawingCache();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();

                    UploadTask uploadTask = storageRefFood.putBytes(data);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                            String imageUrl = "https:" + taskSnapshot.getDownloadUrl().getEncodedSchemeSpecificPart();
                            Food resFood = new Food(foodName, foodPrice, imageUrl);
                            refFood.push().setValue(resFood);
                            dialog.dismiss();

                        }
                    });

                }

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {

            case GALLERY_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedImage = data.getData();
                    assert selectedImage != null;
                    String imagePath = selectedImage.getPath();
                    File img = new File(imagePath);
                    long length = img.length() / 1024;
                    if (length > 100) {
                        Toast.makeText(getApplicationContext(), "Vui lòng chọn hình có kích thước nhỏ hơn 100kb", Toast.LENGTH_LONG).show();
                    } else {
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                            ivFoodPic.setImageBitmap(bitmap);

                        } catch (IOException ignored) {
                        }
                    }

                }

                break;

            case MY_REQUEST_INVEST:
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedImage = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                        ivProductInvest.setImageBitmap(bitmap);

                    } catch (IOException ignored) {
                    }
                }

                break;

        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_res_man, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_cashier) {
            startActivity(new Intent(this, ResCashier.class));
        }
        if (id == R.id.action_material) {
            startActivity(new Intent(this, ResMaterial.class));
        }

        if (id == R.id.action_add_table) {
            refTable.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    long itemCount = dataSnapshot.getChildrenCount();

                    //String keyPush = refTable.push().getKey();
                    refTable.push().child("tableName").setValue("Bàn " + (itemCount + 1) + "");
                    //Toast.makeText(getApplicationContext(), keyPush, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }

        if (id == R.id.action_add_food) {
            dialogAddFood();
        }
        if (id == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
        }
         return super.onOptionsItemSelected(item);

    }
}

