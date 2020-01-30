package com.example.crashtest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.example.crashtest.Adopter.GetOrderAdaptor;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GetOrderBack extends AppCompatActivity {

    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_order_back);
        recyclerView=findViewById(R.id.orderRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(GetOrderBack.this));



        driverAdminDBinit().child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){

                    final List<VehicalInformation> vehicalInformations=new ArrayList<>();
                    for(DataSnapshot child:dataSnapshot.getChildren()){


                        child.getRef().child("Vehicle Information").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                vehicalInformations.add(dataSnapshot.getValue(VehicalInformation.class));
                                recyclerView.setAdapter(new GetOrderAdaptor(GetOrderBack.this,vehicalInformations));

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





    }

    private DatabaseReference driverAdminDBinit(){

        DatabaseReference db;
        FirebaseOptions firebaseOptions=new FirebaseOptions.Builder()
                .setDatabaseUrl("https://fir-testingapplication-8d620.firebaseio.com/")
                .setApplicationId("1:735269236090:android:77c176bce7cf72e7")
                .setApiKey("AIzaSyBxaOvWH2rFtsRpajemaX3fxCIB5OopcVI")
                .build();

        try {
            FirebaseApp.initializeApp(this,firebaseOptions,"parkingAdminDB");
            db = FirebaseDatabase.getInstance(FirebaseApp.getInstance("parkingAdminDB")).getReference("Submit Orders");

        }catch (Exception e){
            db =FirebaseDatabase.getInstance(FirebaseApp.getInstance("parkingAdminDB")).getReference("Submit Orders");

        }


        return  db;

    }
}
