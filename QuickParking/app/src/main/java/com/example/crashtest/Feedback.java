package com.example.crashtest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;




import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class Feedback extends AppCompatActivity {

    private EditText problemName;
    private EditText problemDiscription;
    private Button submitBtn;

    private static final String TAG = "SupportCenter";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        if(getSupportActionBar().isShowing()){

            getSupportActionBar().hide();
        }

        problemName=findViewById(R.id.support_center_problemName);
        problemDiscription=findViewById(R.id.support_center_problemDiscription);
        submitBtn=findViewById(R.id.support_center_submit);




        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String problem=problemName.getText().toString();
                final String problemDis=problemDiscription.getText().toString();

                if(problem.isEmpty()){


                }else if(problemDis.isEmpty()){

                }else{

                    AlertDialog submitAlert= new AlertDialog.Builder(Feedback.this)
                            .setTitle("Report Submit")
                            .setMessage("Do you want to submit this report")
                            .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {


                                    final ProgressDialog progressDialog=new ProgressDialog(Feedback.this);
                                    progressDialog.setMessage("Wait for server reponse");
                                    progressDialog.show();
                                    DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Registered_Information").child(FirebaseAuth.getInstance().getCurrentUser().getEmail().replace('.','_'))
                                            .child("Feedback");
                                    databaseReference
                                            .child(String.valueOf(Calendar.getInstance().getTimeInMillis()))
                                            .child(problem).setValue(problemDis, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(DatabaseError error, DatabaseReference ref) {

                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    progressDialog.dismiss();
                                                    AlertDialog successAlert=new AlertDialog.Builder(Feedback.this)
                                                            .setCancelable(false)
                                                            .setMessage("Your Report Submit Sccessfuly Thank you for your feedback")
                                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    finish();
                                                                }
                                                            }).create();
                                                    successAlert.show();
                                                    Log.d(TAG, "onComplete: Report Saved");
                                                }
                                            });

                                        }
                                    });
                                }
                            })
                            .setNegativeButton("Cancel",null)
                            .setCancelable(false)
                            .create();
                    submitAlert.show();
                }
            }
        });

    }
}
