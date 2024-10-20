package com.example.whack_a_mole;

public class LocationData {
    private double latitude;
    private double longitude;
    private String address;

    public LocationData(double latitude, double longitude, String address) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getAddress() {
        return address;
    }
}

