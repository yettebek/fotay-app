package com.project.fotayapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.project.fotayapp.R;
import com.project.fotayapp.models.UserDataSQLite;

import java.util.Objects;


public class OptionsActivity extends AppCompatActivity {
    //declaracion de variables
    private TextView logout_settings;
    private Toolbar toolbar_back;
    private UserDataSQLite db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        logout_settings = findViewById(R.id.log_out_settings);
        toolbar_back = findViewById(R.id.settings_toolbar);

        setSupportActionBar(toolbar_back);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Ajustes");
        //Flecha de regreso en la barra de herramientas de la actividad
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Inicializar base de datos SQLite para eliminar datos de usuario
        db = new UserDataSQLite(getApplicationContext());

        toolbar_back.setNavigationOnClickListener(v -> {
            finish();
        });

        logout_settings.setOnClickListener(v -> {
            // Eliminar los datos de la sesión actual del usuario
            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("LoginPreferences", Context.MODE_PRIVATE);
            sharedPreferences.edit().clear().apply();
            //Dirigiendo a la pantalla de inicio de la aplicacion
            startActivity(new Intent(OptionsActivity.this, StartActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            //Eliminando datos de usuario de la base de datos
            db.deleteUsers();
            //Cerrando la actividad actual
            finish();
        });
    }
}
