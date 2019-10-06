package com.minhky.go4.restaurant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.minhky.go4.R;
import com.minhky.go4.model.Material;

//import static com.minhky.go4.utils.Constant.refApps;
//import static com.minhky.go4.utils.Constant.refChef;
import java.util.ArrayList;
import java.util.List;

import static com.minhky.go4.utils.Constant.refMaterial;

public class ResMaterial extends AppCompatActivity {

    private String mtrNameSelect;

    private String userEmail, tableName, orderPushKey, foodName;

    private ArrayAdapter<String> adpMaterial;

    private FirebaseRecyclerAdapter<Material, ResMaterial.MaterialViewHolder> adapterFirebaseListMaterial;
    private FirebaseRecyclerAdapter<Material, ResMaterial.MaterialViewHolder> adapterFirebaseListMaterialDialog;

    private static final int MTR_NORMAL = 0;
    private static final int MTR_LIMIT = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_res_material);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar_limit_mtr);
        setSupportActionBar(toolbar);

    }

    @Override
    protected void onResume() {
        super.onResume();

        RecyclerView mtrList = findViewById(R.id.recycler_res_material);
        mtrList.setHasFixedSize(true);
        //StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mtrList.setLayoutManager(linearLayoutManager);


        adapterFirebaseListMaterial = new FirebaseRecyclerAdapter<Material, MaterialViewHolder>(
                Material.class,
                R.layout.item_material,
                MaterialViewHolder.class,
                refMaterial
        ) {
            @Override
            public void onBindViewHolder(@NonNull MaterialViewHolder holder, int position, @NonNull List<Object> payloads) {
               Material mtr = getItem(position);
                float amount = Float.parseFloat(mtr.getMtrAmount());
                float limit = Float.parseFloat(mtr.getMtrLimit());

                if(amount < limit) {
                    holder.itemView.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
                    holder.name.setTextColor(getResources().getColor(android.R.color.white));
                    holder.unit.setTextColor(getResources().getColor(android.R.color.white));
                    holder.amount.setTextColor(getResources().getColor(android.R.color.white));
                }

                super.onBindViewHolder(holder, position, payloads);
            }

            @Override
            public MaterialViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_material, parent, false);
                return new MaterialViewHolder(v);
            }

            @SuppressLint("ResourceAsColor")
            @Override
            protected void populateViewHolder(final MaterialViewHolder viewHolder, final Material model, int position) {
                viewHolder.name.setText(model.getMtrName());
                viewHolder.unit.setText(model.getMtrUnit());
                viewHolder.amount.setText(model.getMtrAmount());





            }
        };
        mtrList.setAdapter(adapterFirebaseListMaterial);
        adapterFirebaseListMaterial.notifyDataSetChanged();
    }

    private void InOutMaterialDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ResMaterial.this);
        LayoutInflater inflater = LayoutInflater.from(ResMaterial.this);
        View dialogView = inflater.inflate(R.layout.dialog_in_out_mtr, null);
        builder.setView(dialogView);
        final Dialog dialog = builder.create();
        dialog.show();

        final Spinner spMtrIO;
        final EditText edtAmountIO;
        Button btnBackIO, btnInIO, btnOutIO;

        spMtrIO = dialogView.findViewById(R.id.sp_in_out_name);
        edtAmountIO = dialogView.findViewById(R.id.edt_in_out_amount);
        btnBackIO = dialogView.findViewById(R.id.btn_in_out_back);
        btnInIO = dialogView.findViewById(R.id.btn_in_mtr);
        btnOutIO = dialogView.findViewById(R.id.btn_out_mtr);

        final List<String> listMaterial = new ArrayList<String>();
        listMaterial.add("Chọn nguyên liệu");
        refMaterial.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> snapMaterial = dataSnapshot.getChildren();
                for (DataSnapshot itemMaterial:snapMaterial){

                    String mtrName = itemMaterial.getKey();
                    listMaterial.add(mtrName);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        adpMaterial = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_list_item_1, listMaterial);
        adpMaterial.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMtrIO.setAdapter(adpMaterial);

        spMtrIO.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mtrNameSelect = parent.getItemAtPosition(position).toString();
                //Toast.makeText(getApplicationContext(), position+"", Toast.LENGTH_LONG).show();
                //Toast.makeText(getApplicationContext(), mtrNameSelect, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnOutIO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // String mtrAmount = edtAmount.getText().toString();
                //refMaterial.child(mtrNameSelect).child("mtrLimit").setValue(mtrAmount);
                Toast.makeText(getApplicationContext(), "Xuất thành công", Toast.LENGTH_LONG).show();
                //spMaterial.setAdapter(adpMaterial);
                //spMaterial.setSelection(0);
                //edtAmount.setText("");

            }
        });

        btnBackIO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    private void addLimitMtrDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(ResMaterial.this);
        LayoutInflater inflater = LayoutInflater.from(ResMaterial.this);
        View dialogView = inflater.inflate(R.layout.dialog_limit_mtr, null);
        builder.setView(dialogView);
        final Dialog dialog = builder.create();
        dialog.show();

        final Spinner spMaterial;
        final EditText edtAmount;
        Button btnExit, btnOk;

        spMaterial = dialogView.findViewById(R.id.sp_in_out_name);
        edtAmount = dialogView.findViewById(R.id.edt_in_out_amount);
        btnExit = dialogView.findViewById(R.id.btn_in_out_back);
        btnOk = dialogView.findViewById(R.id.btn_in_mtr);

        final List<String> listMaterial = new ArrayList<String>();
        listMaterial.add("Chọn nguyên liệu");
        refMaterial.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> snapMaterial = dataSnapshot.getChildren();
                for (DataSnapshot itemMaterial:snapMaterial){

                    String mtrName = itemMaterial.getKey();
                    listMaterial.add(mtrName);
                    }
                }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        adpMaterial = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_list_item_1, listMaterial);
        adpMaterial.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMaterial.setAdapter(adpMaterial);

        spMaterial.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mtrNameSelect = parent.getItemAtPosition(position).toString();
                //Toast.makeText(getApplicationContext(), position+"", Toast.LENGTH_LONG).show();
                //Toast.makeText(getApplicationContext(), mtrNameSelect, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mtrAmount = edtAmount.getText().toString();
                refMaterial.child(mtrNameSelect).child("mtrLimit").setValue(mtrAmount);
                Toast.makeText(getApplicationContext(), "Đặt hạn mức thành công", Toast.LENGTH_LONG).show();
                //spMaterial.setAdapter(adpMaterial);
                spMaterial.setSelection(0);
                edtAmount.setText("");

            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


    }

    private class MaterialViewHolder extends RecyclerView.ViewHolder {
        TextView name, unit, amount;

        public MaterialViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.tv_name);
            unit = itemView.findViewById(R.id.tv_unit);
            amount = itemView.findViewById(R.id.tv_amount);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getLayoutPosition();
            Toast.makeText(getApplicationContext(), position+"", Toast.LENGTH_LONG).show();

                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_material, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_in_out_mtr) {
            InOutMaterialDialog();
        }
        if (id == R.id.action_limit_mtr) {
            addLimitMtrDialog();
        }

        return super.onOptionsItemSelected(item);

    }

}