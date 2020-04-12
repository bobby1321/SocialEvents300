package com.example.myapplication.list;

import android.os.Parcel;
import android.os.Parcelable;

import com.squareup.moshi.FromJson;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RssFeedModel implements Serializable {

    private String title;
    private String link;
    private String description;
    private Date timestamp;
    private String organization;
    private String location;
    private double latitude, longitude;

    public RssFeedModel(String title, String link, String description, String timestamp) {
        this.title = title;
        this.link = link;
        this.description = description;
        try {
            this.timestamp = new SimpleDateFormat("EEE, MMM dd hh:mm:ss zzz yyyy").parse(timestamp);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int loc = description.indexOf("<b>Organization</b>");
        if (loc != -1){
            organization = description.substring(loc + 26);
        }
    }

    public RssFeedModel(String title, String description, String timestamp, String organization, String location, double latitude, double longitude, String link) {
        this.title = title;
        this.link = link;
        this.description = description;
        try {
            this.timestamp = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(timestamp);
        } catch (ParseException e) {
            e.printStackTrace();
        }
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

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        try {
            this.timestamp = new SimpleDateFormat("EEE, MMM dd hh:mm:ss zzz yyyy").parse(timestamp);
        } catch (ParseException e) {
            e.printStackTrace();
        }
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public static class RssJson{
        String title, link, description, timestamp, organization, location, latitude, longitude;
    }

    public static class RssJsonAdapter{
        @FromJson RssFeedModel RSSFromJson(RssJson rssJson){
            double latitude, longitude;
            latitude = Double.parseDouble(rssJson.latitude);
            longitude = Double.parseDouble(rssJson.longitude);
            return new RssFeedModel(
                    rssJson.title,
                    rssJson.description,
                    rssJson.timestamp,
                    rssJson.organization,
                    rssJson.location,
                    latitude,
                    longitude,
                    rssJson.link
                    );
        }
    }
}