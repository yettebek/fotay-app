package com.project.fotayapp.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.project.fotayapp.R;
import com.project.fotayapp.fragments.profileFragment;
import com.project.fotayapp.models.UserDataSQLite;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class OptionsActivity extends AppCompatActivity {
    //declaracion de variables
    private TextView logout_account, delete_account;
    private Toolbar toolbar_back;
    private UserDataSQLite db;
    private String URL_DELETE_ACCOUNT = "https://fotay.000webhostapp.com/deleteAccount.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        //inicializacion de variables
        toolbar_back = findViewById(R.id.settings_toolbar);

        logout_account = findViewById(R.id.settings_logout_account);
        delete_account = findViewById(R.id.settings_delete_account);

        // Inicializar base de datos SQLite para eliminar datos de usuario
        db = new UserDataSQLite(getApplicationContext());

        getSessionUsername();

        setSupportActionBar(toolbar_back);
        //Objects.requireNonNull(getSupportActionBar()).setTitle("Ajustes de " + getSessionUsername());
        Objects.requireNonNull(getSupportActionBar()).setTitle(Html.fromHtml("<font color='#ffffff'>Ajustes de " + getSessionUsername() + "</font>"));
        //Flecha de regreso en la barra de herramientas de la actividad
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar_back.setNavigationOnClickListener(v -> {
            finish();
        });

        logout_account.setOnClickListener(v -> {
            // Dirigiendo a la pantalla de inicio de la aplicacion
            goToStartActivity();
        });

        delete_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Creando un dialogo para confirmar la eliminacion de la cuenta
                new AlertDialog.Builder(OptionsActivity.this)
                        .setIcon(R.drawable.ic_error)
                        .setTitle("Eliminar cuenta")
                        .setMessage("¿Está seguro de que desea eliminar su cuenta?")
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Eliminando la cuenta del usuario de la base de datos
                                deleteAccount();

                                // Dirigiendo a la pantalla de inicio de la aplicacion
                                goToStartActivity();
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Se cierra el dialogo
                        dialog.dismiss();
                    }
                }).setCancelable(false)
                        .show();
            }
        });
    }

    public void deleteAccount() {
        //
        HashMap<String, String> user_sqlite = db.getUserInfo();
        Integer idUsu = Integer.parseInt(Objects.requireNonNull(user_sqlite.get("usu_id")));
        String nomUsu = user_sqlite.get("usu_nombre");
        //StringRequest para eliminar los datos del usuario
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_DELETE_ACCOUNT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equalsIgnoreCase("Usuario eliminado")) {
                    // Mensaje de confirmacion de eliminacion de cuenta
                    Toast.makeText(getApplicationContext(), "Cuenta eliminada", Toast.LENGTH_LONG).show();
                } else {
                    // Mensaje de error de eliminacion de cuenta
                    Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show(); //getLocalizedMessage()
            }
        }   // Enviando el usuario
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("usu_nombre", nomUsu);
                return params;
            }
        };
        // Agregando la solicitud a la cola de solicitudes
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        // Agregando la solicitud a la cola de solicitudes
        requestQueue.add(stringRequest);
    }

    public String getSessionUsername() {
        HashMap<String, String> user_sqlite = db.getUserInfo();
        Integer idUsu = Integer.parseInt(Objects.requireNonNull(user_sqlite.get("usu_id")));
        String nomUsu = user_sqlite.get("usu_nombre");
        return nomUsu;
    }

    private void goToStartActivity() {
        // Eliminar los datos de la sesión actual del usuario
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("LoginPreferences", Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();

        //Dirigiendo a la pantalla de inicio de la aplicacion
        startActivity(new Intent(OptionsActivity.this, StartActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
        //Eliminando datos de usuario de la base de datos
        db.deleteUsers();

        profileFragment.clearPostIds();
        //Cerrando la actividad actual
        finish();
    }
}

