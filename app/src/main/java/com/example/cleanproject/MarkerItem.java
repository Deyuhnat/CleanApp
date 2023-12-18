package com.example.cleanproject;

import java.util.ArrayList;
import java.util.List;

public class MarkerItem {
    private String title;
    private String description;
    private double latitude;
    private double longitude;
    private List<String> joinuserID;
    public MarkerItem(){joinuserID = new ArrayList<>();}

    // Constructor, getters, and setters
    public MarkerItem(String title, String description, double latitude, double longitude) {
        this.title = title;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public List<String> getJoinuserID() {
        return joinuserID;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}