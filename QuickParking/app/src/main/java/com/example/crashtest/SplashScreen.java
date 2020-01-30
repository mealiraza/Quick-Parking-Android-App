package com.example.crashtest;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.widget.Toast;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
       if(getSupportActionBar().isShowing()){
           getSupportActionBar().hide();
       }

          CheckGpsStatus();


    }

    public void CheckGpsStatus(){
       LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;
       boolean GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(GpsStatus == true) {
            Handler handler=new Handler();
            handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                startActivity(new Intent(SplashScreen.this,UserLogin.class));
                finish();
            }
        },5000);
        } else {

            getDialoge("GPS","Please On Your GPS and restart application").show();
        }
    }



    public AlertDialog getDialoge(String title,String dis){


        return  new AlertDialog.Builder(SplashScreen.this).setTitle(title).setMessage(dis)
                .setNegativeButton("Cancel",null)
                .setPositiveButton("On Location GPS", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent1 = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent1,1);
                        finish();
                    }
                }).create();
    }
}
