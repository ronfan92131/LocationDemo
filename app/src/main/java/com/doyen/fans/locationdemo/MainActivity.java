package com.doyen.fans.locationdemo;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
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

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "GPS_ MainActivity";
    String tim = "10";
    Button btnStartLocationService;
    Button mBtnRmAllLocatons;
    Button mBtnShowAllLocations;

    GPS_Service gps_service;

    FirebaseDatabaseHelper firebaseDatabaseHelper;
    List<String> myLocationKeys;
    TextView mTextLat;
    TextView mTextLong;

 //   GPS_Service gps_service;
    DeviceUuidFactory deviceUuidFactory;
    String myDeviceName;
    GPS_Service gps;
    DeviceUuidFactory mDeviceUuidFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextLong = (TextView) findViewById(R.id.location_long);
        mTextLat = (TextView) findViewById(R.id.location_lat);
        btnStartLocationService= (Button) findViewById(R.id.btnStartLocationService);
        mBtnRmAllLocatons = (Button)findViewById(R.id.btn_rm_all_locations);
        mBtnShowAllLocations = (Button)findViewById(R.id.btn_show_all_locations);
        deviceUuidFactory = new DeviceUuidFactory(this);
        myLocationKeys = new ArrayList<>();

   //   Manifest.permission check
        Spinner mSpinTime= (Spinner) findViewById(R.id.spinner_time);
        mBtnShowThisLocation= (Button) findViewById(R.id.btnShowThisLocation);
        mBtnAddThisLocaton = (Button)findViewById(R.id.btn_add_this_location);
        mBtnAddOtherLocatons = (Button)findViewById(R.id.btn_add_other_locations);
        mBtnShowOtherLocations = (Button)findViewById(R.id.btn_show_other_locations);
        mDeviceUuidFactory = new DeviceUuidFactory(this);

//      permission check
        if(!runtime_permission())
            enable_button();
        runtime_permission();

        mBtnRmAllLocatons.setOnClickListener(new View.OnClickListener(){
        mSpinTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onClick(View v) {
              //rm all locations
                Toast.makeText(MainActivity.this, "Remove all my locations", Toast.LENGTH_SHORT).show();
                getAllMyLocations();
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                deleteMyAllLocations();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };
                tim= adapterView.getItemAtPosition(i).toString();
                Log.d(TAG, "tim: " + tim);
                if(tim.equals("Select time")){
                    Toast.makeText(MainActivity.this, "Please Select time!", Toast.LENGTH_SHORT).show();
                    tim="10";  //default
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

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();

                //    startActivity(new Intent(MainActivity.this, NewLocationActivity.class));
            }
        });

        mBtnShowAllLocations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LocationListActivity.class));
            public void onNothingSelected(AdapterView<?> adapterView) {
                tim= String.valueOf("10");
            }
        });

    }

    public void startService(View v){
        Log.d(TAG, "startService: ");
        gps_service = new GPS_Service(MainActivity.this,tim);
      //  Intent serviceIntent = new Intent(this, GPS_Service.class);
        //serviceIntent.putExtra("CONTEXT", getApplicationContext().toString());
            @Override
            public void onClick(View view) {
                gps = new GPS_Service(MainActivity.this,tim);
                startService(new Intent(MainActivity.this,GPS_Service.class));

                if(gps.canGetLocation()){
                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();
                    //  storeInDatabase(latitude,longitude);

                    mTextLat.setText(String.format("%.6f", latitude));
                    mTextLong.setText(String.format("%.6f", longitude));
                    Toast.makeText(MainActivity.this, latitude+" ::: "+ longitude, Toast.LENGTH_SHORT).show();
                }else{
                    gps.showSettingsAlert();
                }
            }
        });

            @Override
            public void DataIsUpdated() {

            }

            @Override
            public void DataIsDeleted() {

            }
        });
    }


    public void deleteMyAllLocations(){
        Log.d(TAG, "deleteMyAllLocations");

        while(myLocationKeys.size() > 0){

            String key = myLocationKeys.get(0);

            new FirebaseDatabaseHelper().deleteLocation(key, new FirebaseDatabaseHelper.DataStatus(){

                @Override
                public void DataIsLoaded(List<FirebaseLocation> firebaseLocations, List<String> keys) {

                }

                @Override
                public void DataIsInserted() {

                }

                @Override
                public void DataIsUpdated() {

                }

                @Override
                public void DataIsDeleted() {
                    Log.d(TAG, "DataIsDeleted");
                }
            });
            myLocationKeys.remove(0);
        }


    }

    private void enable_button() {

        btnStartLocationService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gps_service = new GPS_Service(MainActivity.this,tim);
                startService(new Intent(MainActivity.this,GPS_Service.class));

                if(gps_service.canGetLocation()){
                    double latitude = gps_service.getLatitude();
                    double longitude = gps_service.getLongitude();
                if(gps.canGetLocation()){
                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();
                  //  storeInDatabase(latitude,longitude);

                    mTextLat.setText(String.format("%.6f", latitude));
                    mTextLong.setText(String.format("%.6f", longitude));
                    Toast.makeText(MainActivity.this, latitude+" ::: "+ longitude, Toast.LENGTH_SHORT).show();
                }else{
                    gps_service.showSettingsAlert();
                }
            }
        });
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