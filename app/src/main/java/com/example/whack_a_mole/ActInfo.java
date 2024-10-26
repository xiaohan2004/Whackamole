package com.example.whack_a_mole;

public class ActInfo {
    private long id = 0;
    private String userName = "";
    private long uid = 0;
    private String game = "";
    private long time = 0;
    private String message = "";
    private double latitude = 0.0;
    private double longitude = 0.0;
    private double altitude = 0.0;
    private String address = "";

    public ActInfo(String userName, double latitude, double longitude, long time, String address, String game, String message) {
        this.uid = 1;
        this.userName = userName;
        this.time = time;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.game = game;
        this.message = message;
    }

    public ActInfo(String userName, double latitude, double longitude, long time, String address) {
        this.uid = 2;
        this.userName = userName;
        this.time = time;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
    }

    public long getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public long getUid() {
        return uid;
    }

    public String getGame() {
        return game;
    }

    public long getTime() {
        return time;
    }

    public String getMessage() {
        return message;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public String getAddress() {
        return address;
    }
}
