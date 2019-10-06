package com.minhky.go4.restaurant;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.minhky.go4.R;
import com.minhky.go4.login.LoginActivity;
import com.minhky.go4.model.Food;
import com.minhky.go4.model.Table;
import com.minhky.go4.utils.Constant;

//import static com.minhky.go4.utils.Constant.refApps;
import static com.minhky.go4.utils.Constant.refOrder;

public class ResChef extends AppCompatActivity {

    private DatabaseReference refFood, refTable;

    private String userEmail, tableName;
    private FirebaseRecyclerAdapter<Table, ResChef.TableViewHolder> adapterFirebase;

    private static final int TABLE_NORMAL = 1001;
    private static final int TABLE_ACTIVE = 1002;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_res_chef);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar_res_chef);
        setSupportActionBar(toolbar);

        //refTable = refApps.child("ResMan/Table");
        //refFood = refApps.child("ResMan/Food");

    }

    @Override
    protected void onResume() {
        super.onResume();

        RecyclerView tableList = findViewById(R.id.recycler_res_chef);
        tableList.setHasFixedSize(true);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        tableList.setLayoutManager(staggeredGridLayoutManager);

        adapterFirebase = new FirebaseRecyclerAdapter<Table, ResChef.TableViewHolder>(
                Table.class,
                R.layout.item_table,
                ResChef.TableViewHolder.class,
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
            public ResChef.TableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                if(viewType==TABLE_ACTIVE){
                    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_table_active,parent,false);
                    return new ResChef.TableViewHolder(v);

                }else{
                    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_table,parent,false);
                    return new ResChef.TableViewHolder(v);
                }
            }

            @SuppressLint("ResourceAsColor")
            @Override
            protected void populateViewHolder(final ResChef.TableViewHolder viewHolder, final Table model, int position) {
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
                                //viewBillDialog();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_res_chef, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_material) {
            startActivity(new Intent(this, ResMaterial.class));
        }
        return super.onOptionsItemSelected(item);

    }



}