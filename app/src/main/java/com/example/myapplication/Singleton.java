package com.example.myapplication;

import com.example.myapplication.list.RssFeedModel;

import java.util.ArrayList;

public class Singleton {

    private static volatile Singleton mInstance;

    private Singleton() {
    }

    public static Singleton getInstance() {
        if (mInstance == null) {
            synchronized (Singleton.class) {
                if (mInstance == null) {
                    mInstance = new Singleton();
                }
            }
        }
        return mInstance;
    }

    private ArrayList<RssFeedModel> feedModels;

    public void setState(ArrayList<RssFeedModel> feedModels){
        this.feedModels = feedModels;
    }

    public ArrayList<RssFeedModel> getState(){
        return feedModels;
    }
}
