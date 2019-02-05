package com.doyen.fans.locationdemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Queue;

import static com.doyen.fans.locationdemo.NotificationApp.CHANNEL_ID;

public class GPS_Service extends Service implements LocationListener {
    public static final String TAG = "GPS_ Service";
    private Context mContext;
    // flag for GPS status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;
    ConnectivityManager cm;
    NetworkInfo activeNetwork;
    boolean isNetworkConnected = false;
    boolean canGetLocation = false;

    Location location;//FirebaseLocation
    double latitude;//Latitude
    double longitude;//Longitude

    DeviceUuidFactory deviceUuidFactory;
    static int time;
    private static final long MIN_TIME_BW_UPDATES = 60000;  //60seconds
    private static final long MIN_DISTANCE_UPDATES = 100;  //meters

    private Queue<FirebaseLocation> firebaseLocationQueue;
    protected LocationManager mlocationManager;

    Geocoder geocoder;

    public GPS_Service() {
    }

    public GPS_Service(Context context, String tim) {
        Log.d(TAG, "GPS_Service");
        this.mContext = context;
        this.time = Integer.parseInt(tim);

        deviceUuidFactory = new DeviceUuidFactory(mContext);
        geocoder = new Geocoder(this.mContext, Locale.getDefault());

        getLocation();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");

        cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetwork = cm.getActiveNetworkInfo();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.d(TAG, "onStart");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0
        );
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Foreground Serviee")
                .setContentIntent(pendingIntent)
                .build();

            startForeground(1, notification);  //for long running service, even app is gone

        return START_NOT_STICKY;
    }

    @SuppressLint("MissingPermission")
    public Location getLocation() {

        try {
            mlocationManager = (LocationManager)mContext.getSystemService(LOCATION_SERVICE);
            isGPSEnabled = mlocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = mlocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                this.canGetLocation = true;
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    mlocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_UPDATES, this);
                    Log.d(TAG, "isNetworkEnabled");
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
                        Log.d(TAG, "isGPSEnabled && location == null");
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
    public void onLocationChanged(Location location)  {
        Log.d(TAG, "onLocationChanged: " + location.toString());
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

    public void saveLocationtoFireBase(Location location)  {
        Log.d(TAG, "saveLocationtoFireBase");

        //first, enque location to a local q, this is necessacary in case no data connection
        enQueueFirebaseLocation(location);

        // second, if data connection, dequeue
        deQueueFirebaseLocation();
    }

    public void  enQueueFirebaseLocation (Location location)  {
        if (firebaseLocationQueue == null) {
            firebaseLocationQueue = new LinkedList<>();
        }

        FirebaseLocation firebaseLocation = new FirebaseLocation();
        firebaseLocation.setName(deviceUuidFactory.getDeviceUuid().toString());
        firebaseLocation.setLatitude(location.getLatitude());
        firebaseLocation.setLongitude(location.getLongitude());

        firebaseLocation.setZipcode(getZipcode(location));
        firebaseLocation.setCity(getCity(location));

        firebaseLocation.setTimeStamp(System.currentTimeMillis() / 1000);

        firebaseLocationQueue.add(firebaseLocation);
        Log.d(TAG, "enQueueFirebaseLocation: " + firebaseLocationQueue.size());
    }

    public String getCity(Location location) {
        //List<Address> addresses;
      //  geocoder = new Geocoder(this.mContext, Locale.getDefault());
        List<Address> addresses = null;
        String city = "n/a";
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses.size()>0) {
                city = addresses.get(0).getLocality();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "getCity: " + city);
        return city;
    }

    public int getZipcode(Location location)  {
       // List<Address> addresses;
       // geocoder = new Geocoder(this.mContext, Locale.getDefault());
        String zipcode = null;
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses.size()>0){
               zipcode = addresses.get(0).getPostalCode();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        int zip = 0;
        if (zipcode!=null){
            zip = Integer.parseInt(zipcode);
        }
        Log.d(TAG, "getZipcode: " + zip);
        return zip;
    }

    public void deQueueFirebaseLocation(){
        Log.d(TAG, "deQueueFirebaseLocation: " + firebaseLocationQueue.size());

        while(firebaseLocationQueue.size() > 0) {
            new FirebaseDatabaseHelper().addLocation(firebaseLocationQueue.remove(), new FirebaseDatabaseHelper.DataStatus() {

                @Override
                public void DataIsLoaded(List<FirebaseLocation> firebaseLocations, List<String> keys) {
                    Log.d(TAG, "deQueueFirebaseLocation DataIsLoaded");
                }

                @Override
                public void DataIsInserted() {
                    Log.d(TAG, "deQueueFirebaseLocation DataIsInserted");
                    // Toast.makeText(MainActivity.this, "The location record added successfully", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void DataIsUpdated() {
                    Log.d(TAG, "deQueueFirebaseLocation DataIsUpdated");
                }

                @Override
                public void DataIsDeleted() {
                    Log.d(TAG, "deQueueFirebaseLocation DataIsDeleted");
                }

            });
        }
    }

}
