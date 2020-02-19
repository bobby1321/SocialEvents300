package com.example.myapplication.notifications;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class RssFeedModel implements Parcelable {

    private String title;
    private String link;
    private String description;
    private String timestamp;
    private String organization;

    public static final Creator CREATOR = new Creator() {
        public RssFeedModel createFromParcel(Parcel in) {
            return new RssFeedModel(in);
        }

        public RssFeedModel[] newArray(int size) {
            return new RssFeedModel[size];
        }
    };

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

    public RssFeedModel(Parcel p){
        this(p.readString(), p.readString(), p.readString(), p.readString());
        int loc = description.indexOf("<b>Organization</b>");
        if (loc != -1){
            organization = description.substring(loc + 26);
        }
    }

    @Override
    public int describeContents(){
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(link);
        dest.writeString(description);
        dest.writeString(timestamp);
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
}