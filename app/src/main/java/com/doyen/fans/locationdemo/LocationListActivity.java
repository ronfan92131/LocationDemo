package com.doyen.fans.locationdemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.List;

public class LocationListActivity extends AppCompatActivity {
    public static final String TAG = "LocationListActivity";
    private RecyclerView mRecyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_location_list);
        mRecyclerView = findViewById(R.id.recyclerview_locations);

        new FirebaseDatabaseHelper().readLocations(new FirebaseDatabaseHelper.DataStatus() {
            @Override
            public void DataIsLoaded(List<FirebaseLocation> firebaseLocations, List<String> keys) {
                new RecyclerView_Config().setConfig(mRecyclerView, LocationListActivity.this, firebaseLocations, keys);
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
