package com.doyen.fans.locationdemo;

public class FirebaseLocation {
    private String name;
    private double longitude;
    private double latitude;
    private String city;
    private int zipcode;
    private long timeStamp;

    public FirebaseLocation() {
    }

    public FirebaseLocation(String name, double longitude, double latitude, String city, int zipcode, long timeStamp) {
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
        this.city = city;
        this.zipcode = zipcode;
        this.timeStamp = timeStamp;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public int getZipcode() {
        return zipcode;
    }

    public void setZipcode(int zipcode) {
        this.zipcode = zipcode;
    }
}
