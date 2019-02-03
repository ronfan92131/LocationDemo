package com.doyen.fans.locationdemo;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseDatabaseHelper {
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReferenceLocations;
    private List<FirebaseLocation> firebaseLocations = new ArrayList<>();

    public interface DataStatus{
        void DataIsLoaded(List<FirebaseLocation> firebaseLocations, List<String> keys);
        void DataIsInserted();
        void DataIsUpdated();
        void DataIsDeleted();
    }

    public FirebaseDatabaseHelper(){
        mDatabase = FirebaseDatabase.getInstance();
        mReferenceLocations = mDatabase.getReference("firebaseLocations");
    }

    public void readLocations(final DataStatus dataStatus){
        mReferenceLocations.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                firebaseLocations.clear();
                List<String> keys = new ArrayList<>();
                for (DataSnapshot keyNode: dataSnapshot.getChildren()){
                    keys.add(keyNode.getKey());
                    FirebaseLocation firebaseLocation = keyNode.getValue(FirebaseLocation.class);
                    firebaseLocations.add(firebaseLocation);
                }
                dataStatus.DataIsLoaded(firebaseLocations, keys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void addLocation(FirebaseLocation firebaseLocation, final DataStatus dataStatus){
        String key = mReferenceLocations.push().getKey();
        mReferenceLocations.child(key).setValue(firebaseLocation)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dataStatus.DataIsInserted();
                    }
                });
    }

    public void updateLocation(String key, FirebaseLocation firebaseLocation, final DataStatus dataStatus){
        mReferenceLocations.child(key).setValue(firebaseLocation)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    dataStatus.DataIsUpdated();
                }
            });
    }

    public void deleteLocation(String key, final DataStatus dataStatus){
        mReferenceLocations.child(key).setValue(null)
        .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                dataStatus.DataIsDeleted();
            }
        });
    }
}
