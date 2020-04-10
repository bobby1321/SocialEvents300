package com.example.myapplication.list;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class RssFeedModel implements Serializable {

    private String title;
    private String link;
    private String description;
    private String timestamp;
    private String organization;
    private String location;
    private double latitude, longitude;

    public RssFeedModel(String title, String link, String description, String timestamp) {
        this.title = title;
        this.link = link;
        this.description = description;
        this.timestamp = timestamp;
        int loc = description.indexOf("<b>Organization</b>");
        if (loc != -1){
            organization = description.substring(loc + 26);
        }
    }

    public RssFeedModel(String title, String description, String timestamp, String organization, String location, double latitude, double longitude, String link) {
        this.title = title;
        this.link = link;
        this.description = description;
        this.timestamp = timestamp;
        this.organization = organization;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
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