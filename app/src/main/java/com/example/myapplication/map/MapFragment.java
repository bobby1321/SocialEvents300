package com.example.myapplication.map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;


import com.example.myapplication.R;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MapFragment extends Fragment implements OnMapReadyCallback{

    private MapView mapView;
    private GoogleMap googleMap;
    private FloatingActionButton fab;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_map, container, false);
        mapView = root.findViewById(R.id.mapview);
        fab = root.findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                openAddMarkerDialogue(root);
            };
        });
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(this);
        return root;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.moveCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(29.189999, -81.048228) , 16.0f) );
    }

    public void openAddMarkerDialogue(View view){
        final EditText latText = new EditText(getActivity());
        latText.setRawInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL|InputType.TYPE_NUMBER_FLAG_SIGNED);
        latText.setHint("Latitude");
        final EditText lngText = new EditText(getActivity());
        lngText.setRawInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL|InputType.TYPE_NUMBER_FLAG_SIGNED);
        lngText.setHint("Longitude");
        final EditText nameText = new EditText(getActivity());
        nameText.setInputType(InputType.TYPE_CLASS_TEXT);
        nameText.setHint("Marker Name");


        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(latText);
        layout.addView(lngText);
        layout.addView(nameText);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Enter Marker");
        builder.setView(layout);
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                try{
                    double lat = Double.parseDouble(latText.getText().toString());
                    double lng = Double.parseDouble(lngText.getText().toString());
                    addMarker(lat, lng, nameText.getText().toString());
                } catch (Exception e){
                    Toast toast = Toast.makeText(getActivity(), "You have failed.", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    public void addMarker(double lat, double lng, String name){
        LatLng tempPoint = new LatLng(lat, lng);
        googleMap.addMarker(new MarkerOptions().position(tempPoint)
                .title("" + name + "(" + lat + ", " + lng + ")"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(tempPoint));
    }
    @Override
    public void onResume() {
        if (mapView != null){
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