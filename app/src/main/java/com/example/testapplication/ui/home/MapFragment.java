package com.example.testapplication.ui.home;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.testapplication.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends SupportMapFragment implements OnMapReadyCallback {

    private GoogleMap googleMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getMapAsync(this);
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
}
