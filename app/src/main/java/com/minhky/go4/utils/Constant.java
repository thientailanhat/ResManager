package com.minhky.go4.utils;



import android.view.animation.AlphaAnimation;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by toila on 09/07/2016.
 */
public class Constant {

    public static DatabaseReference refDatabase = FirebaseDatabase.getInstance().getReference();
    public static DatabaseReference refSystem = FirebaseDatabase.getInstance().getReference().child("1-System");

    public static DatabaseReference refLogin = FirebaseDatabase.getInstance().getReference().child("Login");
    //public static DatabaseReference refApps = FirebaseDatabase.getInstance().getReference().child("Apps");
    public static DatabaseReference refOrder = FirebaseDatabase.getInstance().getReference().child("Order");
    public static DatabaseReference refTable = FirebaseDatabase.getInstance().getReference().child("Table");
    public static DatabaseReference refFood = FirebaseDatabase.getInstance().getReference().child("Food");
    public static DatabaseReference refFoodElement = FirebaseDatabase.getInstance().getReference().child("FoodElement");
    public static DatabaseReference refMaterial = FirebaseDatabase.getInstance().getReference().child("Material");
    public static DatabaseReference refFoodSale = FirebaseDatabase.getInstance().getReference().child("FoodSale");





    public static DatabaseReference refAgentBiz = FirebaseDatabase.getInstance().getReference().child("AgentBiz");
    public static DatabaseReference refAgentService = FirebaseDatabase.getInstance().getReference().child("AgentService");
    public static DatabaseReference refEngagement = FirebaseDatabase.getInstance().getReference().child("Engagement");
    public static DatabaseReference refUserProfile = FirebaseDatabase.getInstance().getReference().child("UserProfile");

    public static StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    public static AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);

    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;
    public static final String PACKAGE_NAME =
            "vn.goingmeal.goingmeal";
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME +
            ".RESULT_DATA_KEY";
    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME +
            ".LOCATION_DATA_EXTRA";

    public static FirebaseFirestore refFirestore = FirebaseFirestore.getInstance();
}



