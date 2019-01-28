package com.doyen.fans.locationdemo;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

public class NewLocationActivity extends AppCompatActivity {

    private EditText mName;
    private EditText mLongtitude;
    private EditText mLatitude;
    private EditText mZipcode;
    private Button btnAdd;
    private Button btnBack;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_location);

        mName = (EditText)findViewById(R.id.txtName);
        mLongtitude = (EditText)findViewById(R.id.txtLongitude);
        mLatitude = (EditText)findViewById(R.id.txtLatitude);
        mZipcode = (EditText)findViewById(R.id.txtZipcode);
        btnAdd = findViewById(R.id.btnAdd);
        btnBack = findViewById(R.id.btnBack);

        btnAdd.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Location location = new Location();
                location.setName(mName.getText().toString());
                location.setLongitude(Double.parseDouble(mLongtitude.getText().toString()));
                location.setLatitude(Double.parseDouble(mLatitude.getText().toString()));
                location.setZipcode(Integer.parseInt(mZipcode.getText().toString()));

                new FirebaseDatabaseHelper().addLocation(location, new FirebaseDatabaseHelper.DataStatus(){

                    @Override
                    public void DataIsLoaded(List<Location> locations, List<String> keys) {

                    }

                    @Override
                    public void DataIsInserted() {
                        Toast.makeText(NewLocationActivity.this, "The location record added successfully", Toast.LENGTH_SHORT).show();
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

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                return;
            }
        });
    }
}
