package com.doyen.fans.locationdemo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class RecyclerView_Config {
    private Context mContext;
    private LocationsAdapter mLocationsAdapter;
    public void setConfig(RecyclerView recyclerView, Context context, List<Location> locations, List<String> keys){
        mContext = context;
        mLocationsAdapter = new LocationsAdapter(locations, keys);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(mLocationsAdapter);
    }


    class LocationItemView extends RecyclerView.ViewHolder{

        private TextView mName;
        private TextView mLong;
        private TextView mLat;
        private TextView mZip;

        private String key;

        public LocationItemView(ViewGroup parent){
            super(LayoutInflater.from(mContext).inflate(R.layout.location_list_item, parent, false));

            mName = (TextView)itemView.findViewById(R.id.textView_name);
            mLong = (TextView)itemView.findViewById(R.id.textView_long);
            mLat = (TextView)itemView.findViewById(R.id.textView_lat);
            mZip = (TextView)itemView.findViewById(R.id.textView_zip);


        }

        public void bind(Location location, String key){
            mName.setText(location.getName());
            mLong.setText(location.getLongitude()+"");
            mLat.setText(location.getLatitude()+"");
            mZip.setText(location.getZipcode()+"");

            this.key = key;
        }


        public LocationItemView(View itemView) {
            super(itemView);
        }
    }
    class LocationsAdapter extends RecyclerView.Adapter<LocationItemView>{
        private List<Location> mLocationList;
        private List<String> mKeys;

        public LocationsAdapter(List<Location> mLocationList, List<String> mKeys) {
            this.mLocationList = mLocationList;
            this.mKeys = mKeys;
        }

        @NonNull
        @Override
        public LocationItemView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new LocationItemView(parent);
        }

        @Override
        public void onBindViewHolder(@NonNull LocationItemView holder, int position) {
            holder.bind(mLocationList.get(position), mKeys.get(position));
        }

        @Override
        public int getItemCount() {
            return mLocationList.size();
        }
    }
}
