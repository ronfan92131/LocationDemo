package com.doyen.fans.locationdemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

public class GPS_Service extends Service implements LocationListener {
    public static final String TAG = "GPS_ Service";
    private Context mContext;
    // flag for GPS status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;

    Location location;//FirebaseLocation
    double latitude;//Latitude
    double longitude;//Longitude

    DeviceUuidFactory deviceUuidFactory;
    // The minimum time between updates in milliseconds
    static int time;
    private static final long MIN_TIME_BW_UPDATES = 1000 * time;  //seconds
    private static final long MIN_DISTANCE_UPDATES = 100;  //meters

    // Declaring a FirebaseLocation Manager
    protected LocationManager mlocationManager;

    public GPS_Service() {
    }

    public GPS_Service(Context mContext, String time) {
        this.mContext = mContext;
        this.time = Integer.parseInt(time);
        getLocation();
        deviceUuidFactory = new DeviceUuidFactory(mContext);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        getLocation();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        getLocation();
        Log.d(TAG, "Service Started");
    }

    @SuppressLint("MissingPermission")
    public Location getLocation() {

        try {
            mlocationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
            isGPSEnabled = mlocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = mlocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                this.canGetLocation = true;
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    //noinspection MissingPermission
                /*    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        Toast.makeText(this, "GPS permission denied", Toast.LENGTH_SHORT).show();
                        Log.d(TAG,"GPS permission denied" );
                        return null;
                    }
                    */
                    mlocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_UPDATES, this);
                    Log.d(TAG, "Network");
                    if (mlocationManager != null) {
                        //noinspection MissingPermission
                        location = mlocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }

                if (isGPSEnabled) {
                    if (location == null) {
                        //noinspection MissingPermission
                        mlocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (mlocationManager != null) {
                            //noinspection MissingPermission
                            location = mlocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        Log.d(TAG, "getLocation: " + latitude + " ::: " + longitude );
        return location;
    }
    public void stopUsingGPS(){
        if(mlocationManager != null){
            mlocationManager.removeUpdates(GPS_Service.this);
        }
    }
    public double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
        }


        return latitude;
    }
    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
        }

        return longitude;
    }
    public boolean canGetLocation() {
        return this.canGetLocation;
    }
    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        alertDialog.setTitle("GPS is settings");

        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, location.toString());
        //saveto firebase
        saveLocationtoFireBase(location);
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

    private void saveLocationtoFireBase(Location location) {
        FirebaseLocation firebaseLocation = new FirebaseLocation();

        //save this location to server
        firebaseLocation.setName(deviceUuidFactory.getDeviceUuid().toString());
        firebaseLocation.setLatitude(location.getLatitude());
        firebaseLocation.setLongitude(location.getLongitude());
        firebaseLocation.setZipcode(92130); //dummy
        firebaseLocation.setTimeStamp(System.currentTimeMillis()/1000);

        new FirebaseDatabaseHelper().addLocation(firebaseLocation, new FirebaseDatabaseHelper.DataStatus() {

            @Override
            public void DataIsLoaded(List<FirebaseLocation> firebaseLocations, List<String> keys) {

            }

            @Override
            public void DataIsInserted() {
                // Toast.makeText(MainActivity.this, "The location record added successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void DataIsUpdated() {

            }

            @Override
            public void DataIsDeleted() {

            }

        });
    }

}
