package com.example.crashtest;



import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class UserSignUp extends AppCompatActivity {

    private EditText firstName;
    private EditText lastName;
    private EditText email;
    private EditText mobile;
    private EditText password;
    private EditText address;
    private Button createAccount;
    User u;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_sign_up);
        getSupportActionBar().hide();
        firstName=findViewById(R.id.signup_fn);
        lastName=findViewById(R.id.signup_ln);
        email=findViewById(R.id.signup_email);
        mobile=findViewById(R.id.signup_mobile);
        password=findViewById(R.id.signup_password);
        address=findViewById(R.id.signup_Address);


        db=FirebaseFirestore.getInstance();



        createAccount=findViewById(R.id.signup_createAccount);
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                u=new User();

                u.setFirstName(firstName.getText().toString());
                u.setLastName(lastName.getText().toString());
                u.setEmail(email.getText().toString());
                u.setMobile(mobile.getText().toString());
                u.setPassword(password.getText().toString());
                u.setAddress(address.getText().toString());
                UserDataValidation(u);



            }
        });

        findViewById(R.id.signup_already_have_account).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
    private  void UserDataValidation(final User user){



        if(user.getFirstName().length()<2){

            firstName.setError("name too short");
        }else if(user.getLastName().length()<2){

            lastName.setError("name too short");

        }else if(!Patterns.EMAIL_ADDRESS.matcher(user.getEmail()).matches()){

            email.setError("Incorrect Email");
        }else if(user.getMobile().length()>13){

            mobile.setError("Incorrect Number");
        }else if(user.getPassword().length()<6){
            password.setError("password too short");
        }else if(user.getAddress().length()<6){
            address.setError("Inccorect Address");
        }else{


            Intent intent=new Intent(UserSignUp.this,PhoneVarification.class);
            intent.putExtra("number",user.getMobile());
            startActivityForResult(intent ,2);

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode==2&&resultCode==RESULT_OK){
            if(data.getExtras().getInt("varification")==1){

                CreateUser(u);
            }else{

                Toast.makeText(this, "Varification Faild", Toast.LENGTH_SHORT).show();
            }

        }

    }

    private void CreateUser(final User cU){

        final AlertDialog progressDialog=loadingDialoge("Creating User");
        progressDialog.show();

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(cU.getEmail(),cU.getPassword())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            FirebaseDatabase.getInstance().getReference(Util.PERSONAL_INFO_COLL)
                                    .child(cU.getEmail().replace(".","_")).setValue(cU).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(UserSignUp.this, "Sccessfull data saved", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                        FirebaseAuth.getInstance().signOut();
                                        finish();
                                    }
                                }
                            });


                        }else{

                            progressDialog.dismiss();
                        }
                    }
                });

    }


    private AlertDialog loadingDialoge(String loadingDis){

        View view= LayoutInflater.from(UserSignUp.this).inflate(R.layout.loading_layout,null);
        TextView dis=view.findViewById(R.id.loadingText);
        dis.setText(loadingDis);
        return new AlertDialog.Builder(UserSignUp.this).setView(view).create();
    }
}
