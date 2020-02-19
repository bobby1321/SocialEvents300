package com.example.myapplication.arview;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.myapplication.R;

public class ARViewFragment extends Fragment {

    private ARViewModel ARViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ARViewModel =
                ViewModelProviders.of(this).get(ARViewModel.class);
        View root = inflater.inflate(R.layout.fragment_arview, container, false);
        final TextView textView = root.findViewById(R.id.text_arview);
        ARViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}