package com.project.fotayapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.project.fotayapp.R;


public class homeFragment extends Fragment {

    //Declarar variables
    //private FloatingActionButton fab_pic;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        /*fab_pic = view.findViewById(R.id.fab_pic);

        fab_pic.setOnClickListener(v -> {

            //Abir UploadActivity para elegir la imagen a subir a la app:
            Intent uploadIntent = new Intent(getActivity(), UploadActivity.class);
            startActivity(uploadIntent);
        });*/
        return view;
    }
}