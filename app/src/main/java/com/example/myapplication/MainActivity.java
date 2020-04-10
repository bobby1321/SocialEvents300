package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;

import com.example.myapplication.arview.ARViewFragment;
import com.example.myapplication.list.ListFragment;
import com.example.myapplication.list.RssFeedModel;
import com.example.myapplication.map.MapFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity{

    private ArrayList<RssFeedModel> rssFeedModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_map, R.id.navigation_arview, R.id.navigation_list)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    public ArrayList<RssFeedModel> getRssFeedModels() {
        Log.d("Map3", "Get");
        return rssFeedModels;
    }

    public void setRssFeedModels(ArrayList<RssFeedModel> rssFeedModels) {
        this.rssFeedModels = rssFeedModels;
        Log.d("Map3", "Set");
    }
}
