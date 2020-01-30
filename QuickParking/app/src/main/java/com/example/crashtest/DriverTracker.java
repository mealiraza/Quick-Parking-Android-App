package com.example.crashtest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class DriverTracker extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {

    private GoogleMap mMap;
    private TextView driverName;
    private TextView driverPhone;
    private Button callBtn;
    private Button mesgBtn;
    private Button cancelOrderBtn;
    private Button submitVehical;
    private Button qrScanner;
    private VehicalInformation vehicalInformation;
    private boolean scanConfirm;
    LiveOrder d;
    MarkerOptions markerOptions;
    Marker driverLocationaMarker;
    private static final String TAG = "DriverTracker";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_tracker);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.driverMap);
        mapFragment.getMapAsync(this);

        driverName=findViewById(R.id.driverUserName);
        driverPhone=findViewById(R.id.driverCallNumber);
        callBtn=findViewById(R.id.userCall);
        callBtn.setOnClickListener(this);
        mesgBtn=findViewById(R.id.userMesg);
        mesgBtn.setOnClickListener(this);
        cancelOrderBtn=findViewById(R.id.orderCancel);
        cancelOrderBtn.setOnClickListener(this);
        submitVehical=findViewById(R.id.submitVehical);
        submitVehical.setOnClickListener(this);
        qrScanner=findViewById(R.id.qrScanner);
        qrScanner.setOnClickListener(this);
        markerOptions=new MarkerOptions().position(new LatLng(0,0))
                .title("Driver Location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

        d = getIntent().getExtras().getParcelable("driverData");
        Log.d(TAG, "onCreate: "+d.getOrderPhoneNumber());
        driverName.setText(d.getOrderUserName());
        driverPhone.setText(d.getOrderPhoneNumber());




    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        driverLocationaMarker= mMap.addMarker(markerOptions);

        updateDriverLocation(mMap);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){


            case R.id.orderCancel:
                driverDB(d.getDriverUID()).child("LiveOrder").removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        finish();
                    }
                });
                break;

            case R.id.submitVehical:

                getSubmitVehicalDialoge().show();

                break;

            case R.id.qrScanner:
                qrCodeScanner();
                break;
        }
    }



    public AlertDialog getSubmitVehicalDialoge(){


        View vView= LayoutInflater.from(DriverTracker.this).inflate(R.layout.vehical_information_submit,null);
        final AlertDialog vehicalDialoge= new AlertDialog.Builder(DriverTracker.this).setView(vView)
                .setNegativeButton("Back",null)
                .create();


        final EditText ownerName=vView.findViewById(R.id.vehical_ownerName);
        final EditText ownerEmail=vView.findViewById(R.id.vehical_ownerEmail);
        final EditText ownerMobile=vView.findViewById(R.id.vehical_ownerNumber);
        final EditText vehicalCompany=vView.findViewById(R.id.vehical_Company);
        final EditText vehicalNumber=vView.findViewById(R.id.vehical_Number);
        final EditText ownerAddress=vView.findViewById(R.id.vehical_address);
        final EditText idNumber=vView.findViewById(R.id.vehical_ownerID);
        Button vehicalSubmitBtn=vView.findViewById(R.id.vehical_submit);


        DatabaseReference autoFillLink=FirebaseDatabase.getInstance().getReference("Registered_Information")
                .child(FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".","_"));
                autoFillLink.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists()){
                            User  myPerinfo=dataSnapshot.getValue(User.class);

                            ownerName.setText(myPerinfo.getFirstName()+" "+myPerinfo.getLastName());
                            ownerEmail.setText(myPerinfo.getEmail());
                            ownerMobile.setText(myPerinfo.getMobile());
                            ownerAddress.setText(myPerinfo.getAddress());
                            idNumber.setText(myPerinfo.getIdNumber());
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


        vehicalSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                vehicalInformation=new VehicalInformation();
                vehicalInformation.setOwnerName(ownerName.getText().toString());
                vehicalInformation.setOwnerEmail(ownerEmail.getText().toString());
                vehicalInformation.setOwnerMobile(ownerMobile.getText().toString());
                vehicalInformation.setVehicalCompany(vehicalCompany.getText().toString());
                vehicalInformation.setVehicalNumber(vehicalNumber.getText().toString());
                vehicalInformation.setOwnerAddress(ownerAddress.getText().toString());
                vehicalInformation.setIdNumber(idNumber.getText().toString());
                if(ownerName.getText().toString().isEmpty()){

                    ownerName.setError("Incorrect Name");
                    ownerName.requestFocus();
                }else if(ownerEmail.getText().toString().isEmpty()){
                    ownerEmail.setError("Incorrect Name");
                    ownerEmail.requestFocus();
                }else if(ownerMobile.getText().toString().isEmpty()){
                    ownerMobile.setError("Incorrect Name");
                    ownerMobile.requestFocus();
                }else if(vehicalCompany.getText().toString().isEmpty()){
                    vehicalCompany.setError("Incorrect Name");
                    vehicalCompany.requestFocus();
                }else if(vehicalNumber.getText().toString().isEmpty()){
                    vehicalNumber.setError("Incorrect Name");
                    vehicalNumber.requestFocus();
                }else if(ownerAddress.getText().toString().isEmpty()){
                    ownerAddress.setError("Incorrect Name");
                    ownerAddress.requestFocus();
                }else if(idNumber.getText().toString().isEmpty()){
                    idNumber.setError("Incorrect Name");
                    idNumber.requestFocus();
                }else{


                    driverDB(d.getDriverUID()).child("LiveOrder").child("Vehicle Information").setValue(vehicalInformation).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(DriverTracker.this, "Information Submited", Toast.LENGTH_SHORT).show();
                            getSubmitVehicalDialoge().dismiss();
                            getAlertDialoge("Vehical Information","Your Vehical Information Submited Successfuly");
                            submitVehical.setVisibility(View.GONE);
                            qrScanner.setVisibility(View.VISIBLE);
                            vehicalDialoge.dismiss();
                        }
                    });
                }





            }
        });

        return  vehicalDialoge;


    }


    private DatabaseReference driverDB(String driverUID){

        DatabaseReference driverDB;
        FirebaseOptions firebaseOptions=new FirebaseOptions.Builder()
                .setDatabaseUrl("https://quick-parking-driver.firebaseio.com/")
                .setApplicationId("1:48083144115:android:58331a75d5dba028540fc6")
                .setApiKey("AIzaSyD5IOVdoNXadkocDvyRA_9meUxmtgE8fKU")
                .build();

        try {
            FirebaseApp.initializeApp(this,firebaseOptions,"driverOrderLink");
            driverDB =FirebaseDatabase.getInstance(FirebaseApp.getInstance("driverOrderLink")).getReference("Drivers Personal Information").child(driverUID);
            Log.d(TAG, "sendOrderToDriver: ");
        }catch (Exception e){
            driverDB =FirebaseDatabase.getInstance(FirebaseApp.getInstance("driverOrderLink")).getReference("Drivers Personal Information").child(driverUID);

        }






        return  driverDB;
    }


    private AlertDialog getAlertDialoge(String title,String dis){


        return  new AlertDialog.Builder(DriverTracker.this).setTitle(title).setMessage(dis).setNegativeButton("OK",null).create();
    }


    public void qrCodeScanner(){

        IntentIntegrator integrator = new IntentIntegrator(DriverTracker.this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt("Scan Driver QR");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(false);
        integrator.setOrientationLocked(false);
        integrator.initiateScan();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Log.e("Scan*******", "Cancelled scan");

            } else {
                Log.e("Scan", "Scanned");


                if(d.getDriverUID().equals(result.getContents())){

                    Toast.makeText(this, "Driver Validation Complete", Toast.LENGTH_SHORT).show();
                    scanConfirm=true;
                    getFinshAlertDialoge("Order Submited","Your Order Submited successfuly thank you for using quick parking").show();
                }
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private AlertDialog getFinshAlertDialoge(String title,String dis){


        return  new AlertDialog.Builder(DriverTracker.this).setTitle(title)
                .setView(R.layout.sccess_dialoge).setMessage(dis).setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              //  finish();

                driverDB(d.getDriverUID()).child("LiveOrder").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if(scanConfirm){
                            LiveOrder submitOrder=dataSnapshot.getValue(LiveOrder.class);
                            submitOrder.setOrderStatus("pending");
                            final long milS=Calendar.getInstance().getTimeInMillis();

                            driverDB(d.getDriverUID()).child("Pending Order").child(String.valueOf(milS))
                                    .setValue(submitOrder).addOnCompleteListener(new OnCompleteListener<Void>() {

                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    driverDB(d.getDriverUID()).child("LiveOrder").removeValue(new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference)

                                        {
                                            driverDB(d.getDriverUID()).child("Pending Order").child(String.valueOf(milS)).child("Vehicle Information")
                                                    .setValue(vehicalInformation, new DatabaseReference.CompletionListener() {
                                                        @Override
                                                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                                            finish();
                                                        }
                                                    });
                                        }
                                    });


                                }
                            });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }).create();
    }







    private void updateDriverLocation(final GoogleMap m){

        driverDB(d.getDriverUID()).child("LiveOrder").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    LiveOrder o=dataSnapshot.getValue(LiveOrder.class);

                    if(o.getDriverLat()!=null){
                        LatLng loc=new LatLng(Double.valueOf(o.getDriverLat()),Double.valueOf(o.getDriverLng()));
                        driverLocationaMarker.setPosition(loc);
                        m.moveCamera(CameraUpdateFactory.newLatLngZoom(loc,18));
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
