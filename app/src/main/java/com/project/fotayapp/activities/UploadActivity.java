package com.project.fotayapp.activities;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;
import com.project.fotayapp.R;
import com.project.fotayapp.fragments.profileFragment;
import com.project.fotayapp.models.UserDataSQLite;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UploadActivity extends AppCompatActivity {

    private ImageView upload_back;
    private ImageView upload_img;

    private Button btn_upload_img;

    SocialAutoCompleteTextView upload_description;

    String photo_description;

    private Uri fotayUri;
    private Bitmap bitmap;

    RequestQueue requestQueue;
    ProgressBar progressBar;

    //URL del servidor
    private static final String webhostURL = "https://fotay.000webhostapp.com/uploadData.php";

    private UserDataSQLite db;
    public profileFragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        //Denegación captura de pantalla en la App
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        // Inicializar base de datos SQLite para almacenar datos de usuario
        db = new UserDataSQLite(this);

        //Toast.makeText(getApplicationContext(), "Usuario: " + getSessionUsername() + "\nId: " + getSessionId(), Toast.LENGTH_SHORT).show();

        profileFragment = new profileFragment();

        //Inicializar variables en la View
        upload_back = findViewById(R.id.upload_back);
        upload_img = findViewById(R.id.upload_img);
        btn_upload_img = findViewById(R.id.btn_upload_img);
        upload_description = findViewById(R.id.upload_description);
        progressBar = findViewById(R.id.progress_circular);

        // Declara RequestQueue para gestionar las peticiones al servidor
        requestQueue = Volley.newRequestQueue(this);

        //Desactivar botón de subida de imagen al servidor si no hay imagen seleccionada
        publishBtnFalse();

        //Salir de UploadActivity
        upload_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Método que recoge la imagen proveniente del fragment a través del ImagePicker y mostrarla en el ImageView
        getImageFromFragment();

        //botón Subir foto al servidor
        btn_upload_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Obtener descripción de la foto
                photo_description = upload_description.getText().toString().trim();

                uploadImgToServer();
            }
        });

    }

    //Recoger la imagen proveniente del ImagePicker y mostrarla en el ImageView
    private void getImageFromFragment() {
        //Recoger la imagen del intent creado en el fragmento
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            fotayUri = Uri.parse(bundle.getString("fotayUri"));
            upload_img.setImageURI(fotayUri);

            //Guardar imagen a la App de galería del teléfono
            InputStream imageStream;
            OutputStream out;
            try {
                imageStream = getContentResolver().openInputStream(fotayUri);
                bitmap = BitmapFactory.decodeStream(imageStream);

                ContentResolver resolver = getContentResolver();
                ContentValues values = new ContentValues();

                long time = System.currentTimeMillis();

                values.put(MediaStore.MediaColumns.DISPLAY_NAME, "fotay_" + time + ".jpg");
                values.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg");
                values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM + File.separator + "Fotay");

                fotayUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                out = resolver.openOutputStream(Objects.requireNonNull(fotayUri));

                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

                Objects.requireNonNull(out);

            } catch (FileNotFoundException e) {
                Toast.makeText(this, "NO imagen en galería.", Toast.LENGTH_LONG).show();
            }
            //Botón de subida de imagen al servidor activado
            publishBtnTrue();
        } else {
            Toast.makeText(this, "NO imagen en galería.", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    //Activa el botón 'Publicar Imagen'
    private void publishBtnTrue() {
        progressBar.setVisibility(View.GONE);
        btn_upload_img.setBackgroundColor(getResources().getColor(R.color.btn_upload_img));
        btn_upload_img.setEnabled(true);
        btn_upload_img.setText(R.string.publicar_imagen);
    }

    //Desactiva el botón 'Publicar Imagen'
    private void publishBtnFalse() {
        //ProgressBar de espera de carga de imagen
        progressBar.setVisibility(View.VISIBLE);
        btn_upload_img.setBackgroundColor(getResources().getColor(R.color.btn_no_upload_img));
        btn_upload_img.setText(R.string.sin_imagen);
        btn_upload_img.setEnabled(false);
    }

    //Recibe la información de la imagen en bytes y la devuelve en una String para subirlo al servidor
    public String getImagePath(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();

        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    //Subir imagen al servidor remoto
    public void uploadImgToServer() {
        final ProgressDialog progressDialog = new ProgressDialog(this);

        //[Volley API]
        StringRequest stringRequest = new StringRequest(Request.Method.POST, webhostURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                btn_upload_img.setEnabled(false);
                progressDialog.setMessage("Subiendo imagen...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();

                        //Actualiza las fotos del usuario
                        //profileFragment.webhosturl();
                        profileFragment.getUserPosts();

                        //Cerrar actividad
                        finish();
                    }
                }, 6000);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        }
        ) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<String, String>();
                param.put("usu_id", getSessionId());
                param.put("usu_nombre", getSessionUsername());
                param.put("foto_coment", photo_description);
                param.put("foto_ruta", getImagePath(bitmap));
                return param;
            }
        };
        //Timeout de la petición
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //Añadir petición a la cola
        requestQueue.add(stringRequest);
    }

    public String getSessionUsername() {
        HashMap<String, String> user_sqlite = db.getUserInfo();
        String nomUsu = Objects.requireNonNull(user_sqlite.get("usu_nombre"));
        return nomUsu;
    }

    public String getSessionId() {
        HashMap<String, String> user_sqlite = db.getUserInfo();
        String idUsu = user_sqlite.get("usu_id");
        return idUsu;
    }
}