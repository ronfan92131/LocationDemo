package com.doyen.fans.locationdemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class LocationListActivity extends AppCompatActivity {
    public static final String TAG = "GPS_ LocationList";
    private RecyclerView mRecyclerView;

    DeviceUuidFactory deviceUuidFactory;
    String myDeviceName;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_location_list);
        mRecyclerView = findViewById(R.id.recyclerview_locations);
        deviceUuidFactory = new DeviceUuidFactory(this);
        myDeviceName = deviceUuidFactory.getDeviceUuid().toString();

        new FirebaseDatabaseHelper().readLocations(new FirebaseDatabaseHelper.DataStatus() {
            @Override
            public void DataIsLoaded(List<FirebaseLocation> firebaseLocations, List<String> keys) {
                //here display only device specific locations, in the future, add family and friends if allowed
                List<FirebaseLocation> myFirebaseLocations = new ArrayList<>();
                List<String> myKeys = new ArrayList<>();

                for (int i = 0; i < firebaseLocations.size(); i++){
                    if (firebaseLocations.get(i).getName().equals(myDeviceName)){
                        myFirebaseLocations.add(firebaseLocations.get(i));
                        myKeys.add(keys.get(i));
                    }
                }
                new RecyclerView_Config().setConfig(mRecyclerView, LocationListActivity.this, myFirebaseLocations, myKeys);
            }

            @Override
            public void DataIsInserted() {

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
