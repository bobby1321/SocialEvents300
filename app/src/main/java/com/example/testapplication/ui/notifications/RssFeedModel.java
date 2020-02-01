package com.example.testapplication.ui.notifications;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class RssFeedModel implements Parcelable {

    public String title;
    public String link;
    public String description;
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public RssFeedModel createFromParcel(Parcel in) {
            return new RssFeedModel(in);
        }

        public RssFeedModel[] newArray(int size) {
            return new RssFeedModel[size];
        }
    };

    public RssFeedModel(String title, String link, String description) {
        this.title = title;
        this.link = link;
        this.description = description;
    }

    public RssFeedModel(Parcel p){
        this(p.readString(), p.readString(), p.readString());
    }

    @Override
    public int describeContents(){
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(link);
        dest.writeString(description);
    }
}