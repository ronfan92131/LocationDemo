package com.doyen.fans.locationdemo;

public class Location {
    private String name;
    private double longitude;
    private double latitude;
    private int zipcode;

    public Location() {
    }

    public Location(String name, double longitude, double latitude, int zipcode) {
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
        this.zipcode = zipcode;
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
