package com.project.fotayapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.project.fotayapp.R;
import com.project.fotayapp.activities.OptionsActivity;
import com.project.fotayapp.activities.UploadActivity;

public class profileFragment extends Fragment {

    //Declarar variables
    private ImageView iv_background_profile, iv_options_profile;
    private FloatingActionButton fab_imagen;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        iv_background_profile = view.findViewById(R.id.iv_p_background);
        iv_options_profile = view.findViewById(R.id.iv_p_options);
        fab_imagen = view.findViewById(R.id.fab_imagen);

        //get username data from fragment to sharedPreferences
        //String preferenceUser = getContext().getSharedPreferences("PROFILE", Context.MODE_PRIVATE).getString("profileUsername", "");

        iv_options_profile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), OptionsActivity.class);
            startActivity(intent);
        });

        fab_imagen.setOnClickListener(v -> {
        //Abir UploadActivity para elegir la imagen a subir a la app:
        Intent uploadIntent = new Intent(getActivity(), UploadActivity.class);
        startActivity(uploadIntent);
        });

        return view;
    }

    /*Activar menu de opciones en este Fragment*/
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        //Activar Options Menu
        //setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    //Instanciar el Options Menu que albergará el item 'Cerrar sesión'
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //inflater.inflate(R.menu.options_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /*Al pulsar sobre el item 'Cerrar sesión', limpiará los datos de la sharedPreference anterior
    y nos llevará de vuelta a la pantalla de registro*/
    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.log_out) {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("LoginPreferences", Context.MODE_PRIVATE);
            sharedPreferences.edit().clear().apply();

            Intent sharedPreferencesIntent = new Intent(getActivity(), StartActivity.class);
            startActivity(sharedPreferencesIntent);
            getActivity().finish();

            Toast.makeText(getContext(), "SESIÓN CERRADA", Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }*/
}