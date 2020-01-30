package com.example.crashtest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.model.LatLng;
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
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.List;

public class DriverOrder extends AppCompatActivity{

    private Spinner parkingSelection;
    private ProgressDialog progressDialog;
    private Button orderBtn;
    private static final String TAG = "DriverOrder";
    DatabaseReference driverDB;
    ValueEventListener valueEventListener;
    private CheckBox ltv;
    private CheckBox htv;
    private CheckBox psv;
    private CheckBox bike;
    ProgressDialog driverFindProgress;
    boolean orderPrssed=false;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_order);
        progressDialog=new ProgressDialog(this);
        driverFindProgress=new ProgressDialog(this);
        orderBtn=findViewById(R.id.driverOrderBtn);
        progressDialog.show();
        parkingSelection=findViewById(R.id.parkingOption);
        ltv=findViewById(R.id.ltvBox);
        htv=findViewById(R.id.htvBox);
        psv=findViewById(R.id.psvBox);
        bike=findViewById(R.id.bikeBox);

        gettingParkingData();
        driverDBinit();
        checkBoxController();









//        parkingSelection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//                Log.d(TAG, "onItemSelected: "+checkBoxController());
//                Log.d(TAG, "onItemSelected: "+position);
//                if(!checkBoxController().isEmpty()&&position!=0){
//
//                //    Toast.makeText(DriverOrder.this, parkingSelection.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
////                    readDriver(checkBoxController(), parkingSelection.getSelectedItem().toString(), new DriverGetListner() {
////                        @Override
////                        public void onDriverFound(List<DriverType> drivers) {
////                            Log.d(TAG, "onDriverFound: "+drivers.size());
////                        }
////                    });
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });











        orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(parkingSelection.getSelectedItemPosition()!=0)
                {
                    orderPrssed=true;

                    if(!checkBoxController().isEmpty()){

                        gettingDriver(checkBoxController(),parkingSelection.getSelectedItem().toString());



                    }else{

                        Toast.makeText(DriverOrder.this, "Please select a license type", Toast.LENGTH_SHORT).show();

                    }

                }else{


                    Toast.makeText(DriverOrder.this, "Please select a parking", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void gettingDriver(final String requireLicenseType, final String requireParkingName){


        driverFindProgress.show();


        valueEventListener= new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    for (DataSnapshot snapshot:dataSnapshot.getChildren()){

                        DriverType t=snapshot.getValue(DriverType.class);
                        Log.d(TAG, "onDataChange: "+t.getParkingName());


                        if(t.getParkingName().equalsIgnoreCase(requireParkingName)&&t.getLicenseType().equalsIgnoreCase(requireLicenseType)){

                            Log.d(TAG, "onDataChange: Driver is matched "+t.getFirstName()+" "+t.getLastName());

                            if(orderPrssed){

                                sendOrderNotification(t);
                            }
                            driverDB.child(t.getUid()).child("LiveOrder").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    LiveOrder status=dataSnapshot.getValue(LiveOrder.class);
                                    // Log.d(TAG, "onDataChange: "+status);

                                    if (status!=null&&status.getOrderStatus()!=null&&status.getOrderStatus().equalsIgnoreCase("accepted")) {
                                        Log.d(TAG, "onDataChange: "+status);
                                        Intent intent = new Intent(DriverOrder.this, DriverTracker.class);
                                        Log.d(TAG, "onDataChange: "+status.getOrderPhoneNumber());
                                        intent.putExtra("driverData",status);
                                        startActivity(intent);
                                        finish();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                            break;
                        }






                    }



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        driverDB.addValueEventListener(valueEventListener);










    }

    private void driverDBinit(){

        FirebaseOptions  firebaseOptions=new FirebaseOptions.Builder()
                .setDatabaseUrl("https://quick-parking-driver.firebaseio.com/")
                .setApplicationId("1:48083144115:android:58331a75d5dba028540fc6")
                .setApiKey("AIzaSyD5IOVdoNXadkocDvyRA_9meUxmtgE8fKU")
                .build();

        try {
            FirebaseApp.initializeApp(this,firebaseOptions,"driverOrderLink");
            driverDB =FirebaseDatabase.getInstance(FirebaseApp.getInstance("driverOrderLink")).getReference("Drivers Personal Information");
            Log.d(TAG, "sendOrderToDriver: ");
        }catch (Exception e){
            driverDB =FirebaseDatabase.getInstance(FirebaseApp.getInstance("driverOrderLink")).getReference("Drivers Personal Information");

        }






    }


    private void gettingParkingData(){

        final List<String> parkingList = new ArrayList<String>();
        parkingList.add("Please Select Parking");

        Toast.makeText(this, "now Run order", Toast.LENGTH_SHORT).show();

        FirebaseOptions  firebaseOptions=new FirebaseOptions.Builder()
                .setDatabaseUrl("https://fir-testingapplication-8d620.firebaseio.com/")
                .setApplicationId("1:735269236090:android:77c176bce7cf72e7")
                .setApiKey("AIzaSyBxaOvWH2rFtsRpajemaX3fxCIB5OopcVI")
                .build();
        DatabaseReference databaseReference;
        try {
            FirebaseApp.initializeApp(this,firebaseOptions,"fromUserSide");
            databaseReference =FirebaseDatabase.getInstance(FirebaseApp.getInstance("fromUserSide")).getReference("Drivers Personal Information");

        }catch (Exception e){
            databaseReference =FirebaseDatabase.getInstance(FirebaseApp.getInstance("fromUserSide")).getReference("Drivers Personal Information");

        }

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){

                    parkingList.add(snapshot.getValue(ParkingAdmin.class).getParkingName());
                }

                Toast.makeText(DriverOrder.this,"Now", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,parkingList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        parkingSelection.setAdapter(dataAdapter);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(valueEventListener!=null)
       driverDB.removeEventListener(valueEventListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        driverDB.removeEventListener(valueEventListener);
    }

    public void sendOrderNotification(DriverType driver){



        LiveOrder order=new LiveOrder();
        order.setOrderUserName(driver.getFirstName()+" "+driver.getLastName());
        order.setOrderPhoneNumber(driver.getPhoneNumber());
        order.setLat(getSelectedLocation().latitude);
        order.setLan(getSelectedLocation().longitude);
        order.setDriverUID(driver.getUid());
        order.setOrderStatus("live");
        order.setUserUid(FirebaseAuth.getInstance().getUid());



        driverDB.child(driver.getUid()).child("LiveOrder").setValue(order, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                orderPrssed=false;
            }
        });









    }

    private String checkBoxController(){




        ltv.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                htv.setChecked(false);
                psv.setChecked(false);
                bike.setChecked(false);
                ltv.setChecked(isChecked);
            }
        });
        psv.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                htv.setChecked(false);
                ltv.setChecked(false);
                bike.setChecked(false);
                psv.setChecked(isChecked);
            }
        });
        htv.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ltv.setChecked(false);
                psv.setChecked(false);
                bike.setChecked(false);
                htv.setChecked(isChecked);
            }
        });

        bike.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                htv.setChecked(false);
                psv.setChecked(false);
                ltv.setChecked(false);
                bike.setChecked(isChecked);
            }
        });

        String checked;

        if(ltv.isChecked()){
            checked="LTV";
        }else if(psv.isChecked()){

            checked="PSV";
        }else if(htv.isChecked()){
            checked="HTV";
        }else if(bike.isChecked()){
            checked="BIKE";
        }else{
            checked="";
        }


        return  checked;
    }

    private LatLng getSelectedLocation(){


        return getIntent().getExtras().getParcelable("location");

    }

    private void readDriver (final String licenseType, final String parkingName,final DriverGetListner driverGetListner){





       driverDB.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        List<DriverType> driverList=new ArrayList<>();


                        for (DataSnapshot snapshot: dataSnapshot.getChildren()){


                            DriverType temp=snapshot.getValue(DriverType.class);
                            Log.d(TAG, "onDataChange: "+temp.getParkingName()+" "+temp.getLicenseType());
                            if(temp.getParkingName().equalsIgnoreCase(parkingName)&&temp.getLicenseType().equalsIgnoreCase(licenseType)){
                                driverList.add(snapshot.getValue(DriverType.class));


                            }


                        }

                        driverGetListner.onDriverFound(driverList);


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });





    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////PUBLIC LISTNERS/////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////







    public interface DriverGetListner{

        void onDriverFound(List<DriverType> drivers);
    }


}
