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

import com.android.volley.AuthFailureError;
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
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    //Declarar variables
    TextInputLayout til_nom_usu;
    TextInputEditText tiet_usu;

    TextInputLayout til_contr;
    TextInputEditText tiet_contr;

    TextInputLayout til_contr2;
    TextInputEditText tiet_contr2;

    Button btn_registrar;

    TextView tv_si_acc;

    // Instanciar RequestQueue.
    RequestQueue requestQueue;

    //Obtener los datos de los campos
    String idUsu, nomUsu;
    String pswd;
    String pswd2;
    ProgressDialog progressDialog;

    //URL del servidor
    private static final String webhostURL = "https://fotay.000webhostapp.com/insertData.php";

    //Validación de los campos
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^" + "(?=.*[0-9])" + "(?=.*[a-zA-Z])" + "(?=\\S+$)" + ".{8,}" + "$");

    //Instanciar la base de datos local sqlite
    private UserDataSQLite db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Trasparencia barra de estado
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        //Inicializar variables en la View
        til_nom_usu = findViewById(R.id.til_nom_usu);
        tiet_usu = findViewById(R.id.tiet_usu);
        til_contr = findViewById(R.id.til_contr);
        tiet_contr = findViewById(R.id.tiet_contr);
        til_contr2 = findViewById(R.id.til_contr2);
        tiet_contr2 = findViewById(R.id.tiet_contr2);
        btn_registrar = findViewById(R.id.btn_registrar);
        tv_si_acc = findViewById(R.id.tv_si_acc);

        // Inicializar base de datos SQLite para almacenar datos de usuario
        db = new UserDataSQLite(getApplicationContext());

        //Dar formato de estilo de letra al textview
        tv_si_acc.setText(Html.fromHtml("¿Ya tienes una cuenta? <b><u> Inicia sesión</u></b>"));

        //Obtener datos de usuario de la última sesion
        getLoginSharedPreferencesFromApp();

        //Limpiar mensaje de error al pulsar sobre cada TextInputEditText
        tiet_usu.setOnClickListener(v -> til_nom_usu.setError(null));
        tiet_contr.setOnClickListener(v -> til_contr.setError(null));
        tiet_contr2.setOnClickListener(v -> til_contr2.setError(null));

        // Declara RequestQueue para gestionar las peticiones al servidor
        requestQueue = Volley.newRequestQueue(this);

        //Quitar el mensaje de error al escribir sobre cada TextInputEditText
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

        tiet_contr2.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                til_contr2.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //Si el usuario ya está registrado lo lleva a la pantalla de incio-sesión
        tv_si_acc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });

        btn_registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Obtener los datos de los campos
                nomUsu = tiet_usu.getText().toString().trim();
                pswd = tiet_contr.getText().toString().trim();
                pswd2 = tiet_contr2.getText().toString().trim();

                //validación de los campos
                if (nomUsu.isEmpty()) {
                    til_nom_usu.setError("El nombre no puede estar vacío.");
                } else if (pswd.isEmpty() && pswd2.isEmpty()) {
                    til_contr.setError("La contraseña no puede estar vacía.");
                } else if (!PASSWORD_PATTERN.matcher(pswd).matches()) {
                    til_contr.setError("La contraseña debe ser alfanumérica y tener al menos 8 caracteres");
                } else if (!pswd.equals(pswd2)) {
                    til_contr2.setError("Las contraseñas no coinciden.");
                } else {
                    til_nom_usu.setError(null);
                    til_contr.setError(null);
                    til_contr2.setError(null);
                    registerValidation(nomUsu, pswd);
                }
            }
        });
    }

    //Enviar los datos al SERVIDOR REMOTO mediante método POST
    private void registerValidation(String nomUsu, String pswd) {
        //[Volley API]
        StringRequest stringRequest = new StringRequest(Request.Method.POST, webhostURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

              try {
                  //Convertir la respuesta en un objeto JSON
                  JSONObject jsonObject = new JSONObject(response);
                  //Obtener los datos del array dentro del objeto JSON que contiene los datos del usuario
                  JSONArray jsonArray = jsonObject.getJSONArray("register");


                  if (!jsonArray.isNull(0)) {
                      Toast.makeText(RegisterActivity.this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show();
                      //Diálogo de espera para el usuario.
                      progressDialog = new ProgressDialog(RegisterActivity.this);
                      progressDialog.show();
                      progressDialog.setContentView(R.layout.progress_dialog);
                      progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                      progressDialog.setCancelable(false);
                      progressDialog.setCanceledOnTouchOutside(false);
                      //Anadir los datos del usuario a la base de datos local sqlite
                      //db.addUserTableUsuarios(idUsu,nomUsu);
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

                              saveLoginSharedPreferences();

                              //La validación es correcta se inicia la siguiente pantalla terminando la activity anterior
                              Intent menuIntent = new Intent(RegisterActivity.this, MenuActivity.class);
                              menuIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                              startActivity(menuIntent);
                              finish();
                              progressDialog.dismiss();

                          }
                      }, 3000);

                  } else  {
                      Toast.makeText(RegisterActivity.this, "El usuario ya existe.", Toast.LENGTH_SHORT).show();
                  }
              }catch (Exception e){
                  Toast.makeText(RegisterActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
              }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) { //Parámetros que hay que introducir en la BBDD
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<String, String>();
                param.put("usu_nombre", nomUsu);
                param.put("usu_contrasena", pswd);
                return param;
            }
        };

        //Procesa todas las peticiones de nuestra app
        requestQueue.add(stringRequest);

        //En caso de que la app se quede en espera [TimeoutError fix]
        stringRequest.setRetryPolicy(new

                DefaultRetryPolicy(
                10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
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
