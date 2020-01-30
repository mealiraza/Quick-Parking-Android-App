package com.example.crashtest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.crashtest.Adopter.NotificationAdaptor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Notification extends AppCompatActivity {

    RecyclerView recyclerView;

    List<NotificationType> notificationTypes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        notificationTypes=new ArrayList<>();
        recyclerView=findViewById(R.id.notificationRecyclerView);

        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Registered_Information")
                .child(FirebaseAuth.getInstance().getCurrentUser().getEmail().replace('.','_'))
                .child("Notification");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    
                    notificationTypes.add(snapshot.getValue(NotificationType.class));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        NotificationAdaptor notificationAdaptor=new NotificationAdaptor(Notification.this,notificationTypes);

        recyclerView.setLayoutManager(new LinearLayoutManager(Notification.this));
        recyclerView.setAdapter(notificationAdaptor);
    }
}
