package com.doyen.fans.locationdemo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class RecyclerView_Config {
    private Context mContext;
    DeviceUuidFactory deviceUuidFactory;

    private LocationsAdapter mLocationsAdapter;
    public void setConfig(RecyclerView recyclerView, Context context, List<FirebaseLocation> firebaseLocations, List<String> keys){
        mContext = context;
        mLocationsAdapter = new LocationsAdapter(firebaseLocations, keys);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(mLocationsAdapter);
    }


    class LocationItemView extends RecyclerView.ViewHolder{

        private TextView mZip;
        private TextView mLong;
        private TextView mLat;
        private TextView mTime;
        private String deviceName;


        private String key;

        public LocationItemView(ViewGroup parent){
            super(LayoutInflater.from(mContext).inflate(R.layout.location_list_item, parent, false));
            mZip = (TextView)itemView.findViewById(R.id.textView_zip);
            mLat = (TextView)itemView.findViewById(R.id.textView_lat);
            mLong = (TextView)itemView.findViewById(R.id.textView_long);
            mTime = (TextView)itemView.findViewById(R.id.textView_time);

            deviceUuidFactory = new DeviceUuidFactory(mContext);
            deviceName = deviceUuidFactory.getDeviceUuid().toString();
        }

        public void bind(FirebaseLocation firebaseLocation, String key){

            //if the database is shared among many users,
            // the restrict must be set here, to show this device locations,
            // if show other device location, must be under permission (family circle, etc.).
            String name = firebaseLocation.getName();
         //   if (name.length()>20){name = name.substring(0,20);}
           if (name.equals(deviceName)) {

               mZip.setText(firebaseLocation.getZipcode()+"");  //testing, just shoter display id string

               mLat.setText(String.format("%.6f", firebaseLocation.getLatitude()));
               mLong.setText(String.format("%.6f", firebaseLocation.getLongitude()));
               mTime.setText(getDateCurrentTimeZone(firebaseLocation.getTimeStamp()) + "");

               this.key = key;
           }
        }

        public LocationItemView(View itemView) {
            super(itemView);
        }

        public  String getDateCurrentTimeZone(long timestamp) {
            try{
                Calendar calendar = Calendar.getInstance();
                TimeZone tz = TimeZone.getTimeZone("PST");  //testing
                calendar.setTimeInMillis(timestamp * 1000);
                calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date currenTimeZone = (Date) calendar.getTime();
                return sdf.format(currenTimeZone);
            }catch (Exception e) {
            }
            return "";
        }
    }

    class LocationsAdapter extends RecyclerView.Adapter<LocationItemView>{
        private List<FirebaseLocation> mFirebaseLocationList;
        private List<String> mKeys;

        public LocationsAdapter(List<FirebaseLocation> mFirebaseLocationList, List<String> mKeys) {
            this.mFirebaseLocationList = mFirebaseLocationList;
            this.mKeys = mKeys;
        }

        @NonNull
        @Override
        public LocationItemView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new LocationItemView(parent);
        }

        @Override
        public void onBindViewHolder(@NonNull LocationItemView holder, int position) {
            holder.bind(mFirebaseLocationList.get(position), mKeys.get(position));
        }

        @Override
        public int getItemCount() {
            return mFirebaseLocationList.size();
        }
    }
}
