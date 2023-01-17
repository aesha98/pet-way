package com.example.myapplication.Model;

public class Vet {
    private String name;
    private String address;
    private String phoneNumber;
    private String placeID;
    private double latitude;
    private double longitude;

    public Vet() {
    }

    public Vet(String name, String address, String placeID) {

        this.name = name;
        this.address = address;
        this.placeID = placeID;
    }

//    public Vet(String name, String address, String phoneNumber, double latitude, double longitude) {
//        this.name = name;
//        this.address = address;
//        this.phoneNumber = phoneNumber;
//        this.latitude = latitude;
//        this.longitude = longitude;
//    }

    //getters and setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPlaceID() {
        return placeID;
    }

    public void setPlaceID(String placeID) {
        this.placeID = placeID;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
