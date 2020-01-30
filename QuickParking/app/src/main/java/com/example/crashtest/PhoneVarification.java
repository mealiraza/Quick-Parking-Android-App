package com.example.crashtest;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneVarification extends AppCompatActivity {

    private TextView userNumber;
    private EditText codeInput;
    private Button varifyBtn;
    private String vID;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_varification);
        userNumber=findViewById(R.id.varificationNumber);
        codeInput=findViewById(R.id.codeInput);
        varifyBtn=findViewById(R.id.numberVarificationBtn);
        progressDialog=new ProgressDialog(this);


        String phoneNumber=getIntent().getExtras().getString("number");
        userNumber.setText(phoneNumber);

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                        codeInput.setText(phoneAuthCredential.getSmsCode());
                        PhoneVarification(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {

                        Toast.makeText(PhoneVarification.this, "Varification Fail", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        Toast.makeText(PhoneVarification.this, "Code Sent", Toast.LENGTH_SHORT).show();
                        vID=s;
                    }

                    @Override
                    public void onCodeAutoRetrievalTimeOut(String s) {
                        super.onCodeAutoRetrievalTimeOut(s);
                    }
                });        // OnVerificationStateChangedCallbacks
        varifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneAuthCredential credential=PhoneAuthProvider.getCredential(vID,codeInput.getText().toString());
                PhoneVarification(credential);
            }
        });



    }
    private void PhoneVarification(PhoneAuthCredential phoneAuthCredential){


        progressDialog.show();

        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    Toast.makeText(PhoneVarification.this, "Varification Complete", Toast.LENGTH_SHORT).show();
                    FirebaseAuth.getInstance().signOut();
                    progressDialog.dismiss();
                    Intent intent=new Intent();
                    intent.putExtra("varification",1);
                    setResult(RESULT_OK,intent);
                    finish();
                    //FirebaseAuth.getInstance().signOut();

                }else{
                    Toast.makeText(PhoneVarification.this, "Wrong Pin", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
