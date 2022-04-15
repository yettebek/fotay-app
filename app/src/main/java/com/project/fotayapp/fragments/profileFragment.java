package com.project.fotayapp.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.project.fotayapp.R;
import com.project.fotayapp.UserDataSQLite;
import com.project.fotayapp.activities.OptionsActivity;
import com.project.fotayapp.activities.UploadActivity;

import java.util.HashMap;

public class profileFragment extends Fragment {

    //Declarar variables
    private ImageView iv_profile_pic, iv_options_profile;
    private FloatingActionButton fab_imagen;
    private TextView tv_photo_count, tv_p_username;

    private UserDataSQLite db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        /*//change the status bar color to transparent
        Window window = requireActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);*/

        // Cargar los componentes de la vista del Fragment
        iv_profile_pic = view.findViewById(R.id.iv_p_userimage);
        iv_options_profile = view.findViewById(R.id.iv_p_options);
        fab_imagen = view.findViewById(R.id.fab_imagen);
        tv_photo_count = view.findViewById(R.id.tv_photo_counter);
        tv_p_username = view.findViewById(R.id.tv_p_username);


        // Inicializar base de datos sqlite
        db = new UserDataSQLite(getContext());

        // Hashmap para obtener los datos del usuario desde sqlite
        HashMap<String, String> user_sqlite = db.getUserDetails();
        String profile_username = user_sqlite.get("usu_nombre");

        // Setear el nombre de usuario en el TextView
        tv_p_username.setText(profile_username);

        // Abriendo la actividad de ajustes
        iv_options_profile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), OptionsActivity.class);
            startActivity(intent);
        });

        //Abir UploadActivity para elegir la imagen a subir a la app:
        fab_imagen.setOnClickListener(v -> {
            Intent uploadIntent = new Intent(getActivity(), UploadActivity.class);
            startActivity(uploadIntent);
        });


        //setOnClickListener para iv_profile_pic
        iv_profile_pic.setOnClickListener(v -> {

        //Para acceder a la galería de fotos o la cámara
            ImagePicker.with(this)
                    .crop(8f,8f)//Para recortar la imagen
                    .compress(1024) //comprimir la imagen a un tamaño máximo de 1MB
                    .maxResultSize(600, 600)    //La resolución máxima permitida será 600 x 600 pixeles
                    .start();
        });

        return view;
    }

    // Método para recibir la imagen desde la galería o la cámara
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Comprobar si el resultado es correcto
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ImagePicker.REQUEST_CODE) {
                // Obtener la imagen de la galería
                assert data != null;
                Uri imageUri = data.getData();

                // Cargar la imagen en el ImageView
                iv_profile_pic.setImageURI(imageUri);

                // Actualizar el contador de fotos
                //tv_photo_count.setText(String.valueOf(db.getPhotoCount()));

            } else {
                Toast.makeText(getContext(), "No se obtuvo la imagen.", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        }
    }
}

