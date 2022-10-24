package com.project.fotayapp.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.project.fotayapp.R;
import com.project.fotayapp.models.UserDataSQLite;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    //Declarar variables
    TextInputLayout til_nom_usu;
    TextInputEditText tiet_usu;
    TextInputLayout til_contr;
    TextInputEditText tiet_contr;
    Button btn_ini_ses;
    TextView tv_no_acc;
    String nomUsu, pswd;

    ProgressDialog progressDialog;

    // Instanciar RequestQueue.
    RequestQueue requestQueue;

    private static final String webhostURL = "https://fotay.000webhostapp.com/selectDataLogin.php";

    private UserDataSQLite db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Trasparencia barra de estado
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        //Inicializar variables en la View
        til_nom_usu = findViewById(R.id.til_nom_usu);
        tiet_usu = findViewById(R.id.tiet_usu);
        til_contr = findViewById(R.id.til_contr);
        tiet_contr = findViewById(R.id.tiet_contr);
        btn_ini_ses = findViewById(R.id.btn_ini_ses);
        tv_no_acc = findViewById(R.id.tv_no_acc);


        //Dar formato de estilo de letra al textview
        tv_no_acc.setText(Html.fromHtml("¿No tienes una cuenta? <b><u> Regístrate</u></b>"));

        // Inicializar base de datos SQLite para almacenar datos de usuario
        db = new UserDataSQLite(getApplicationContext());

        //Obtener datos de usuario de la última sesion
        getLoginSharedPreferencesFromApp();

        //Limpiar mensaje de error al pulsar sobre cada TextInputEditText
        tiet_usu.setOnClickListener(v -> til_nom_usu.setError(null));
        tiet_contr.setOnClickListener(v -> til_contr.setError(null));

        // Declara RequestQueue para gestionar las peticiones al servidor
        requestQueue = Volley.newRequestQueue(this);

        //Quitar el mensaje de error al escribir o pulsar sobre el campo seleccionado
        tiet_usu.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                til_nom_usu.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        tiet_contr.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                til_contr.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //Si el usuario no está registrado lo lleva a la pantalla de registro
        tv_no_acc.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
        });

        btn_ini_ses.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                nomUsu = tiet_usu.getText().toString().trim();
                pswd = tiet_contr.getText().toString().trim();

                if (nomUsu.isEmpty()) {
                    til_nom_usu.setError("El nombre no puede estar vacío.");
                } else if (pswd.isEmpty()) {
                    til_contr.setError("La contraseña no puede estar vacía.");
                } else {
                    til_nom_usu.setError(null);
                    til_contr.setError(null);
                    loginValidation(nomUsu, pswd);

                }
            }
        });
    }

    //Enviar los datos al servidor mediante método POST
    private void loginValidation(String nomUsu, String pswd) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, webhostURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    //Convertir la respuesta en un objeto JSON
                    JSONObject jsonObject = new JSONObject(response);
                    //Obtener los datos del array dentro del objeto JSON que contiene los datos del usuario
                    JSONArray jsonArray = jsonObject.getJSONArray("login");
                    //Obtener el string que indica el resultado de la consulta
                    String success = jsonObject.getString("success");

                    if (success.equals("1")) {
                        Toast.makeText(LoginActivity.this, "Bienvenid@ de nuevo \n " + nomUsu, Toast.LENGTH_SHORT).show();
                        //Diálogo de espera para el usuario.
                        progressDialog = new ProgressDialog(LoginActivity.this);
                        progressDialog.show();
                        progressDialog.setContentView(R.layout.progress_dialog);
                        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        progressDialog.setCancelable(false);
                        progressDialog.setCanceledOnTouchOutside(false);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);

                            String usu_id = object.getString("usu_id");
                            String usu_nombre = object.getString("usu_nombre");

                            //Guardar los datos del usuario en una sesión
                            db.addUserTableUsuarios(usu_id, usu_nombre);
                        }

                        //Especificar el tiempo de espera del diálogo
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                //Guarda los datos de sesión
                                saveLoginSharedPreferences();

                                //La validación es correcta, se inicia la siguiente pantalla termiando la activity anterior
                                Intent menuIntent = new Intent(LoginActivity.this, MenuActivity.class);
                                menuIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(menuIntent);
                                finish();
                                progressDialog.dismiss();
                            }
                        }, 4000);
                    } else {
                        Toast.makeText(LoginActivity.this, "Usuario y/o contraseña \nintroducidos no son correctos", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }) { //Parámetros que solicitamos para generar una respuesta del servidor
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> param = new HashMap<String, String>();
                param.put("usu_nombre", nomUsu);
                param.put("usu_contrasena", pswd);
                param.put("login", "true");
                return param;
            }

        };
        //TimeoutError arreglo
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        //Procesa todas las peticiones de nuestra app
        stringRequest.setShouldCache(false);
        requestQueue.add(stringRequest);

    }

    //Guardar/mantener datos sesión mediante SharedPreferences
    public void saveLoginSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("usu_nombre", nomUsu);
        editor.putString("usu_contrasena", pswd);
        editor.putBoolean("login", true); //guardar sesión en caso afirmativo de login
        editor.apply(); //guarda todos los cambios
    }

    //Recuperar valores de SharedPreferences
    public void getLoginSharedPreferencesFromApp() {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPreferences", Context.MODE_PRIVATE);
        tiet_usu.setText(sharedPreferences.getString("usu_nombre", ""));
        tiet_contr.setText(sharedPreferences.getString("usu_contrasena", ""));
    }
}