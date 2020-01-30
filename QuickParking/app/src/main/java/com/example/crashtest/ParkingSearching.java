package com.example.crashtest;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;


public class ParkingSearching extends AppCompatActivity implements  OnMapReadyCallback {

    private AppBarConfiguration mAppBarConfiguration;
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private String[] permissions;
    private boolean locationPermissionGranted;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final String TAG = "ParkingSearching";
    private Marker myLocationPicker;
    private FloatingActionButton mapType1,mapType2,mapType3,mapType4;
    private CardView myLocationBtn;
    private ImageButton parkingOrderBtn;


    private FirebaseAuth mAuth;
    private FirebaseUser fUser;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_searching);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        permissions=new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE};

        Places.initialize(this, getString(R.string.google_maps_key));
        mAuth=FirebaseAuth.getInstance();





        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        View header = navigationView.getHeaderView(0);





        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        getLocationPermissions();

        if (locationPermissionGranted) {
            intiMap();
        } else {

            Toast.makeText(this, "All permission not granted", Toast.LENGTH_SHORT).show();
        }




        navigationItemSelectListnerImplementation(navigationView);
        updatePersonalnProfile(header);
        mapObjectInit();
        autoCompleteFragmentIniti();

        currentLocationUpdated();

    }


    private  void updatePersonalnProfile(View headerView){


        final TextView userNameView=headerView.findViewById(R.id.userNameView);
        final TextView userEmailView=headerView.findViewById(R.id.userEmailView);
        final ImageView userProfileView=headerView.findViewById(R.id.userProfileView);

        String currentUserEmail=FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".","_");

        Toast.makeText(this, currentUserEmail, Toast.LENGTH_SHORT).show();

        final DatabaseReference dbRefe= FirebaseDatabase.getInstance().getReference(Util.PERSONAL_INFO_COLL).child(currentUserEmail);

        dbRefe.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User userData=dataSnapshot.getValue(User.class);
                userNameView.setText(userData.getFirstName()+" "+userData.getLastName());
                userEmailView.setText(userData.getEmail());
                if(userData.getProfilePic()!=null){

                    Glide.with(ParkingSearching.this).load(userData.getProfilePic()).override(512,512).circleCrop().into(userProfileView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }
    private void mapObjectInit(){

        mapType1= findViewById(R.id.mapType);
        mapType2= findViewById(R.id.mapType2);
        mapType3= findViewById(R.id.mapType3);
        mapType4= findViewById(R.id.mapType4);
        myLocationBtn=findViewById(R.id.myLocationBtn);
        parkingOrderBtn=findViewById(R.id.pickUpBtn);

        myLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                getDeviceLocation();
            }
        });

        parkingOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(ParkingSearching.this, "Pressed", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder alert=new AlertDialog.Builder(ParkingSearching.this);
                alert.setPositiveButton("YES", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent=new Intent(ParkingSearching.this,DriverOrder.class);
                        intent.putExtra("location",myLocationPicker.getPosition());
                        startActivity(intent);
                    }
                });
                alert.setNegativeButton("NO",null);
                alert.setTitle("Confirmation");
                alert.setMessage("Do you want to order a driver for your car");
                alert.setIcon(R.drawable.ic_warning_black_24dp);
                AlertDialog dialog= alert.create();
                dialog.show();

            }
        });


    }

    private void navigationItemSelectListnerImplementation(NavigationView nv){


        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()){

                    case R.id.nav_orderDriver:
                        Toast.makeText(ParkingSearching.this, "Gallery", Toast.LENGTH_SHORT).show();
                        getDriverOrderBox().show();
                        break;
                    case R.id.nav_notification:
                        Toast.makeText(ParkingSearching.this, "Gallery", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ParkingSearching.this,Notification.class));
                        break;
                    case R.id.nav_getBack:
                        startActivity(new Intent(ParkingSearching.this,GetOrderBack.class));
                        break;
                    case R.id.nav_setting:
                        Toast.makeText(ParkingSearching.this, "satting", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ParkingSearching.this,PersonalProfileSetting.class));
                        break;
                    case R.id.nav_logout:
                        Toast.makeText(ParkingSearching.this, "Gallery", Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();
                        break;

                    case R.id.nav_support:
                        Toast.makeText(ParkingSearching.this, "Gallery", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ParkingSearching.this,SuppourtCenter.class));
                        break;
                    case R.id.nav_feedback:
                        Toast.makeText(ParkingSearching.this, "Gallery", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ParkingSearching.this,Feedback.class));
                        break;


                }
                return false;
            }
        });


    }

    private void autoCompleteFragmentIniti(){


        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

// Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.LAT_LNG,Place.Field.ADDRESS));

// Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setCountry("PK");
        autocompleteFragment.setTypeFilter(TypeFilter.ADDRESS);
        autocompleteFragment.setHint("Where we come ?");

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.d(TAG, "Place: " + place.getName() + ", " + place.getId()+" ,"+place.getLatLng());
                moveCamera(place.getLatLng(),17,place.getName());
                pickUpLocationCardView(place.getLatLng());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.parking_searching, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.action_profile:
                Toast.makeText(this, "Profile Clicked", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ParkingSearching.this,PersonalProfileSetting.class));
                break;
            case R.id.action_notification:
                Toast.makeText(this, "Setting Clicked", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ParkingSearching.this,Notification.class));
                break;

            case R.id.action_refresh:
                refereshThisActivity();
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onSupportNavigateUp() {
        final NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    @Override
    protected void onStart() {
        super.onStart();

        authStateListener= new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                fUser=firebaseAuth.getCurrentUser();


                if(fUser==null){

                    finish();
                    startActivity(new Intent(ParkingSearching.this,UserLogin.class));

                }
            }
        };

        mAuth.addAuthStateListener(authStateListener);


    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(authStateListener);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length>0){


            if(requestCode==1){


                for(int i=0;i<grantResults.length;i++){

                    if(grantResults[i]!=PackageManager.PERMISSION_GRANTED){

                        locationPermissionGranted=false;
                        return;

                    }
                }
                locationPermissionGranted=true;
                intiMap();
            }

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(ParkingSearching.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ParkingSearching.this,Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        MarkerOptions myDeviceLoc=new MarkerOptions().position(new LatLng(0,0))
                .draggable(true)
                .title("Pick Location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));


        myLocationPicker= mMap.addMarker(myDeviceLoc);

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {

                pickUpLocationCardView(marker.getPosition());
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                myLocationPicker.setPosition(latLng);
                pickUpLocationCardView(latLng);

            }
        });

        mapType1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
        });
        mapType2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            }
        });

        mapType3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            }
        });

        mapType4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            }
        });





    }




    private void pickUpLocationCardView(LatLng points){

        Geocoder geocoder=new Geocoder(ParkingSearching.this);
        try {
            List<Address> addressList= geocoder.getFromLocation(points.latitude,points.longitude,1);
            Address currentAddress=addressList.get(0);
            //  Toast.makeText(ParkingSearching.this, addressList.get(0).getSubAdminArea(), Toast.LENGTH_SHORT).show();

            TextView pickUplocationCity;
            TextView pickUpRoad;
            TextView pickUpLocalArea;
            pickUplocationCity=findViewById(R.id.pickUpLOcationTitle);
            pickUpRoad=findViewById(R.id.pickUpAddress);
            pickUpLocalArea=findViewById(R.id.pickUpAddressLocality);
            pickUplocationCity.setText("City : | :"+currentAddress.getSubAdminArea());
            pickUpRoad.setText("Road : | :"+currentAddress.getFeatureName());
            pickUpLocalArea.setText("Local Area : | :"+currentAddress.getSubLocality());


        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    private void moveCamera(LatLng latLng, float zoom, String locationName){

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));

        myLocationPicker.setPosition(latLng);


    }


    private void getDeviceLocation(){


        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(getApplicationContext());


        try {

            if(locationPermissionGranted){

                Task location=fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {

                        if(task.isSuccessful()){

                            Log.d(TAG, "onComplete: "+"Location is find");
                            Location currentLocation=(Location) task.getResult();



                            //  myLocationPicker.setPosition(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()));
                            moveCamera(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()),17f,"Your Location");
                            pickUpLocationCardView(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()));
                        }else{

                            Toast.makeText(ParkingSearching.this, "can't get location", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }

        }catch (SecurityException e){
            Log.d(TAG, "getDeviceLocation: "+e.getMessage());
        }
    }

    private void requestPermissions(){

        ActivityCompat.requestPermissions(ParkingSearching.this,permissions,1);
    }

    private void getLocationPermissions(){

        if(ContextCompat.checkSelfPermission(ParkingSearching.this,Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){

            if(ContextCompat.checkSelfPermission(ParkingSearching.this,Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED){

                if(ContextCompat.checkSelfPermission(ParkingSearching.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){

                    locationPermissionGranted=true;
                }else{
                    requestPermissions();
                }

            }else{
                requestPermissions();
            }
        }else{

            requestPermissions();
        }




    }
    private void intiMap() {

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        if (locationPermissionGranted) {

            getDeviceLocation();

        }


    }

    private void refereshThisActivity(){

        finish();
        startActivity(getIntent());

    }





    private void currentLocationUpdated(){

        LocationManager locationManager= (LocationManager) getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

               // Toast.makeText(ParkingSearching.this, "Location Change", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });


    }


    public AlertDialog getDriverOrderBox(){

        View orderView= LayoutInflater.from(ParkingSearching.this).inflate(R.layout.order_driver,null);

        final TextView driverType=orderView.findViewById(R.id.orderDriver_driverType);
        final TextView driverGender=orderView.findViewById(R.id.orderDriver_driverGender);
        final TextView driverDate=orderView.findViewById(R.id.orderDriver_driverDate);
        final TextView orderAddress=orderView.findViewById(R.id.orderDriver_driverAddress);
        Button orderBtn=orderView.findViewById(R.id.orderDriver_submit);
        final AlertDialog dialog=new AlertDialog.Builder(ParkingSearching.this).setView(orderView).create();
        orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(driverType.getText().toString().isEmpty()){
                    driverType.setError("Wrong Driver Type");
                }else if(driverGender.getText().toString().isEmpty()){

                    driverGender.setError("Wrong Gender");
                }else if(driverDate.getText().toString().isEmpty()){
                    driverDate.setError("Wrong Date");

                }else if(orderAddress.getText().toString().isEmpty()){

                    orderAddress.setError("Wrong Address");
                }else{
                    OrderDriver orderDriver=new OrderDriver();
                    orderDriver.setDriverType(driverType.getText().toString());
                    orderDriver.setDriverGender(driverGender.getText().toString());
                    orderDriver.setDate(driverDate.getText().toString());
                    orderDriver.setAddress(orderAddress.getText().toString());
                    FirebaseDatabase.getInstance().getReference("Registered_Information").child(FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".","_"))
                            .child("Services").child(String.valueOf(Calendar.getInstance().getTimeInMillis())).setValue(orderDriver);
                    new AlertDialog.Builder(ParkingSearching.this).setTitle("Service").setMessage("Your Service Accepted sccessfully we contact you soon").setNegativeButton("ok",null)
                            .create().show();

                }
            }
        });

        return dialog;

    }


}



