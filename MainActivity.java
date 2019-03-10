package com.doyen.fans.locationdemo;

import android.Manifest;
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
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "GPS_ MainActivity";
    String tim = "10";
    Button btnStartLocationService;
    Button mBtnAddOtherLocatons;
    Button mBtnShowAllLocations;

    GPS_Service gps_service;

    TextView mTextLat;
    TextView mTextLong;

 //   GPS_Service gps_service;
    DeviceUuidFactory mDeviceUuidFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextLong = (TextView) findViewById(R.id.location_long);
        mTextLat = (TextView) findViewById(R.id.location_lat);
        btnStartLocationService= (Button) findViewById(R.id.btnStartLocationService);
        mBtnAddOtherLocatons = (Button)findViewById(R.id.btn_add_other_locations);
        mBtnShowAllLocations = (Button)findViewById(R.id.btn_show_all_locations);
        mDeviceUuidFactory = new DeviceUuidFactory(this);
/*
//      permission check
        if(!runtime_permission())
            enable_button();
        runtime_permission();
*/
        mBtnAddOtherLocatons.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NewLocationActivity.class));
            }
        });

        mBtnShowAllLocations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LocationListActivity.class));
            }
        });

    }

    public void startService(View v){
        Log.d(TAG, "startService: ");
        gps_service = new GPS_Service(MainActivity.this,tim);
        startService(new Intent(MainActivity.this,GPS_Service.class));
        //Intent serviceIntent = new Intent(this, GPS_Service.class);
        //startService(serviceIntent);
    }

    public void stopService(View v){
        Log.d(TAG, "stopService");
        Intent serviceIntent = new Intent(this, GPS_Service.class);
        stopService(serviceIntent);
    }

/*
    private void enable_button() {

        btnStartLocationService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gps_service = new GPS_Service(MainActivity.this,tim);
                startService(new Intent(MainActivity.this,GPS_Service.class));

                if(gps_service.canGetLocation()){
                    double latitude = gps_service.getLatitude();
                    double longitude = gps_service.getLongitude();
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
    */
}
