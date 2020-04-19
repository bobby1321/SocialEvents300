package com.example.myapplication.map;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.Singleton;
import com.example.myapplication.list.RssFeedModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap googleMap;
    private FloatingActionButton fab;
    private ArrayList<RssFeedModel> rssFeedModels;
    private boolean mapType = true;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_map, container, false);
        mapView = root.findViewById(R.id.mapview);
        fab = root.findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                toggleMapMode(root);
            };
        });
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        try{
            rssFeedModels = (ArrayList<RssFeedModel>)getArguments().getSerializable("key");
        } catch (Exception e) {
        }
        return root;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        addMarkers();
        googleMap.moveCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(29.189999, -81.048228) , 16.0f) );
    }

    public void toggleMapMode(View view){
        if (mapType){
            googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        } else if (!mapType){
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
        mapType=!mapType;
    }

    public void addMarker(double lat, double lng, String name){
        LatLng tempPoint = new LatLng(lat, lng);
        googleMap.addMarker(new MarkerOptions().position(tempPoint)
                .title(name));
    }

    public void addMarkers(){
        try{
            if(!(Singleton.getInstance().getState().equals(null))) {
                rssFeedModels = Singleton.getInstance().getState();
            }
        } catch (Exception e) {
            Log.d("Map", "" + e.getMessage());
            rssFeedModels = new ArrayList<RssFeedModel>();
        }
        for (RssFeedModel r : rssFeedModels){
            if(r.getLongitude()!= 0.0 && r.getLatitude() != 0.0){
                addMarker(r.getLatitude(), r.getLongitude(), r.getTitle());
            }
            Log.d("Map", r.getTitle() + " " + r.getLatitude() + " " + r.getLongitude());
        }
    }

    @Override
    public void onResume() {
        if (mapView != null) {
            mapView.onResume();}
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null){
        mapView.onPause();}
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mapView != null){
        mapView.onDestroy();}
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null){
        mapView.onLowMemory();}
    }



}