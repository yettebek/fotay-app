package com.project.fotayapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.project.fotayapp.R;


public class OptionsActivity extends AppCompatActivity {
    //declaracion de variables
    TextView logout_settings;
    Toolbar toolbar_settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        logout_settings = findViewById(R.id.log_out_settings);
        toolbar_settings = findViewById(R.id.settings_toolbar);
        setSupportActionBar(toolbar_settings);
        getSupportActionBar().setTitle("Ajustes");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar_settings.setNavigationOnClickListener(v -> {
            finish();
        });

        logout_settings.setOnClickListener(v -> {
            //vuelta a la pantalla de inicio
            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("LoginPreferences", Context.MODE_PRIVATE);
            sharedPreferences.edit().clear().apply();

            startActivity(new Intent(OptionsActivity.this, StartActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
        });
    }
}