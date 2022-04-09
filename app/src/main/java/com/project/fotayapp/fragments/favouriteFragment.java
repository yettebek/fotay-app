package com.project.fotayapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.project.fotayapp.R;

public class favouriteFragment extends Fragment {

    //Declarar variables
    //private FloatingActionButton fab_imagen;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourite, container, false);

        //fab_imagen = view.findViewById(R.id.fab_imagen);

        /*fab_imagen.setOnClickListener(v -> {
            //Abir UploadActivity para elegir la imagen a subir a la app:
            Intent uploadIntent = new Intent(getActivity(), UploadActivity.class);
            startActivity(uploadIntent);
            //Toast.makeText(getContext(), "FAB CLICKED!",Toast.LENGTH_LONG).show();
        });*/
        return view;
    }

}