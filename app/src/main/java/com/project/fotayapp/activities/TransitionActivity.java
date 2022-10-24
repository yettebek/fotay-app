package com.project.fotayapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.project.fotayapp.R;


public class TransitionActivity extends AppCompatActivity {

    //Declarar variables
    ProgressBar loadingProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transition);

        loadingProgressBar = findViewById(R.id.progressBar);
        loadingProgressBar.setVisibility(View.VISIBLE);

        //Añade tiempo de espera antes de pasar a la siguiente pantalla
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                //Muestra un tipo de pantalla basándose en los SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("LoginPreferences", Context.MODE_PRIVATE);
                boolean sesionPreference = sharedPreferences.getBoolean("login", false);
                boolean updatePreference = sharedPreferences.getBoolean("update", false);
                Intent preferenceIntent;

                //Si el usuario tiene una sesión abierta
                if (sesionPreference || updatePreference) {
                    preferenceIntent = new Intent(getApplicationContext(), MenuActivity.class);
                }  else {
                    preferenceIntent = new Intent(getApplicationContext(), StartActivity.class); //Pantalla de inicio
                }
                startActivity(preferenceIntent);
                finish();
            }
        }, 2000);
    }
}