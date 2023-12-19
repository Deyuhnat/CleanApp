package com.example.cleanproject;

import java.util.ArrayList;
import java.util.List;

public class MarkerItem {
    private String title;
    private String description;
    private double latitude;
    private double longitude;
    private List<String> joinuserID;

    private String documentId;

    public MarkerItem() {
        joinuserID = new ArrayList<>();
    }

    public List<String> getJoinuserID() {
        return joinuserID;
    }

    public void setJoinuserID(List<String> joinuserID) {
        this.joinuserID = joinuserID;
    }


    // Constructor, getters, and setters
    public MarkerItem(String title, String description, double latitude, double longitude, List<String> joinuserID, String documentId) {
        this.title = title;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.joinuserID = joinuserID;
        this.documentId = documentId;
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
    public String getDocumentId() {
        return documentId;
    }
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}