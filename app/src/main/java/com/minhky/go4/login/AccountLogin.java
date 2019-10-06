package com.minhky.go4.login;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.minhky.go4.R;
import com.minhky.go4.admin.AdminActivity;
import com.minhky.go4.model.Account;
import com.minhky.go4.restaurant.ResManActivity;

import java.util.HashMap;
import java.util.Map;

import im.delight.android.location.SimpleLocation;

import static com.minhky.go4.utils.Constant.refLogin;

public class AccountLogin extends AppCompatActivity {

    private boolean loginFacebook,loginGoogle,customer,loginAnonymous,free,freeRes;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    //private SimpleLocation location;
    public static final int MY_REQUEST_LOCATION = 5;
    private HashMap<String, Double> distanceMap = new HashMap<>();
    private Map sortTopProduct;

    Handler handler;

    private FusedLocationProviderClient mFusedLocationClient;
    private SimpleLocation.Point endPoint;
    Location customerLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chef_res);

        Intent it = this.getIntent();
        loginFacebook = it.getBooleanExtra("LoginFacebook", false);
        loginGoogle = it.getBooleanExtra("LoginGoogle", false);
        loginAnonymous = it.getBooleanExtra("LoginAnonymous", false);
        freeRes = it.getBooleanExtra("Free", false);

        sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        customer = sharedPreferences.getBoolean("Customer", false);
        free = sharedPreferences.getBoolean("Free", false);

    }

    @Override
    protected void onResume() {
        loginByAccount();

        super.onResume();
    }
    @SuppressLint("HandlerLeak")

    private void loginByAccount() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String userEmail = user.getEmail().replace(".", ",");

        refLogin.child("LoginPartner").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(userEmail)){
                    refLogin.child("LoginPartner").child(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Account account = dataSnapshot.getValue(Account.class);
                            final String emailLogin = account.getEmail();
                            final String role = account.getRole();
                            final String agent = account.getAgent();

                            switch (role) {
                                case "Admin": {
                                    Intent it = new Intent(getApplicationContext(), AdminActivity.class);
                                    it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    it.putExtra("Agent", agent);
                                    startActivity(it);
                                    break;
                                }

                                case "Boss": {
                                    Intent it = new Intent(getApplicationContext(), ResManActivity.class);
                                    it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    it.putExtra("Boss", true);
                                    it.putExtra("EmailLogin", emailLogin);
                                    it.putExtra("Agent", agent);

                                    startActivity(it);

                                    break;
                                }

                                default: {

                                    Intent it = new Intent(getApplicationContext(), ResManActivity.class);
                                    it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    it.putExtra("Waiter", true);
                                    it.putExtra("EmailLogin", emailLogin);
                                    it.putExtra("Agent", agent);

                                    startActivity(it);

                                    break;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }else{
                    Toast.makeText(getApplicationContext(),"Tài khoản chưa đăng ký!",Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        }else{
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }

    }
}
