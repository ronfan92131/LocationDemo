package com.doyen.fans.locationdemo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "GPS_ MainActivity";
    String timer[]={"Select time","5 sec","10 sec","15 sec","20 sec","30 sec"};
    String tim;
    Button mBtnShowThisLocation;
    Button mBtnAddThisLocaton;
    Button mBtnAddOtherLocatons;
    Button mBtnShowOtherLocations;

    TextView mTextLat;
    TextView mTextLong;

    GPS_Service gps;

    //Firebase Work
    DatabaseReference mDatabaseLocationDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextLong = (TextView) findViewById(R.id.location_long);
        mTextLat = (TextView) findViewById(R.id.location_lat);
        Spinner mSpinTime= (Spinner) findViewById(R.id.spinner_time);
        mBtnShowThisLocation= (Button) findViewById(R.id.btnShowThisLocation);
        mBtnAddThisLocaton = (Button)findViewById(R.id.btn_add_this_location);
        mBtnAddOtherLocatons = (Button)findViewById(R.id.btn_add_other_locations);
        mBtnShowOtherLocations = (Button)findViewById(R.id.btn_show_other_locations);
        mDatabaseLocationDetails = FirebaseDatabase.getInstance().getReference().child("Location_Details").push();

//      permission check
        if(!runtime_permission())
            enable_button();
        runtime_permission();


        mSpinTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                tim= adapterView.getItemAtPosition(i).toString();
                Log.d(TAG, "tim: " + tim);
                if(tim.equals("Select time")){
                    Toast.makeText(MainActivity.this, "Please Select time!", Toast.LENGTH_SHORT).show();
                }
                if(tim=="5 sec"){
                    tim= String.valueOf(tim.charAt(0));
                    Toast.makeText(MainActivity.this, tim+"", Toast.LENGTH_SHORT).show();
                }
                if(tim=="10 sec"){
                    tim= tim.substring(0,2);
                    Toast.makeText(MainActivity.this, tim+"", Toast.LENGTH_SHORT).show();
                }if(tim=="15 sec"){
                    tim= tim.substring(0,2);
                    Toast.makeText(MainActivity.this, tim+"", Toast.LENGTH_SHORT).show();
                }if(tim=="20 sec"){
                    tim= tim.substring(0,2);
                    Toast.makeText(MainActivity.this, tim+"", Toast.LENGTH_SHORT).show();
                }if(tim=="30 sec"){
                    tim= tim.substring(0,2);
                    Toast.makeText(MainActivity.this, tim+"", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                tim= String.valueOf(0);
            }
        });

        ArrayAdapter arrayAdapterCity = new ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,timer);
        arrayAdapterCity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinTime.setAdapter(arrayAdapterCity);


        mBtnAddThisLocaton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                //save this location to server
                Location location = new Location();
                location.setName("San Diego");  //dummy
                location.setLatitude(Double.parseDouble(mTextLat.getText().toString()));
                location.setLongitude(Double.parseDouble(mTextLong.getText().toString()));
                location.setZipcode(92130); //dummy

                new FirebaseDatabaseHelper().addLocation(location, new FirebaseDatabaseHelper.DataStatus() {

                    @Override
                    public void DataIsLoaded(List<Location> locations, List<String> keys) {

                    }

                    @Override
                    public void DataIsInserted() {
                        Toast.makeText(MainActivity.this, "The location record added successfully", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void DataIsUpdated() {

                    }

                    @Override
                    public void DataIsDeleted() {

                    }
                });
            }
        });

        mBtnAddOtherLocatons.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NewLocationActivity.class));
            }
        });

        mBtnShowOtherLocations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LocationListActivity.class));
            }
        });

    }

    private void enable_button() {

        mBtnShowThisLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gps = new GPS_Service(MainActivity.this,tim);
                startService(new Intent(MainActivity.this,GPS_Service.class));

                if(gps.canGetLocation()){
                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();
                    storeInDatabase(latitude,longitude);

                    mTextLat.setText(latitude+"");
                    mTextLong.setText(longitude+"");
                    Toast.makeText(MainActivity.this, latitude+" ::: "+ longitude, Toast.LENGTH_SHORT).show();
                }else{
                    gps.showSettingsAlert();
                }
            }
        });



    }

    private void storeInDatabase(double latitude, double longitude) {
        Log.d(TAG, "storeInDatabase lat: " + latitude  + " long: " + longitude);

        mDatabaseLocationDetails.child("latitude").setValue(latitude);
        mDatabaseLocationDetails.child("longitude").setValue(longitude);
    }

    private boolean runtime_permission() {
        if(Build.VERSION.SDK_INT>=23 && ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED&& ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},123);
            return true;
        }
        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==123){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED && grantResults[1]==PackageManager.PERMISSION_GRANTED){
                enable_button();
            }else{
                runtime_permission();
            }
        }
    }
}
