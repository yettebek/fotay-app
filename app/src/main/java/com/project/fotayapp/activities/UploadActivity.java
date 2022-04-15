package com.project.fotayapp.activities;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;
import com.project.fotayapp.R;
import com.project.fotayapp.UserDataSQLite;

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

    //URL del servidor
    private static final String webhostURL = "https://fotay.000webhostapp.com/files/php/uploadData.php";

    private UserDataSQLite db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        //Denegación captura de pantalla en la App
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        // Inicializar base de datos SQLite para almacenar datos de usuario
        db = new UserDataSQLite(getApplicationContext());

        /*findViewById(R.id.upload_layout).setOnApplyWindowInsetsListener((v, insets) -> {
            /*int navigationBarHeight = WindowInsetsCompat.toWindowInsetsCompat(insets).getInsets(WindowInsetsCompat.Type.navigationBars()).bottom;
            int statusBarHeight = WindowInsetsCompat.toWindowInsetsCompat(insets).getInsets(WindowInsetsCompat.Type.statusBars()).top;*/

            /*ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            params.bottomMargin = insets.getSystemWindowInsetBottom();
            //params.topMargin = insets.getSystemWindowInsetTop();
            return insets.consumeSystemWindowInsets();
        });*/

        //Inicializar variables en la View
        upload_back = findViewById(R.id.upload_back);
        upload_img = findViewById(R.id.upload_img);
        btn_upload_img = findViewById(R.id.btn_upload_img);
        upload_description = findViewById(R.id.upload_description);

        //Obtener descripción de la foto
        photo_description = upload_description.getText().toString().trim();

        // Declara RequestQueue para gestionar las peticiones al servidor
        requestQueue = Volley.newRequestQueue(this);

        //Desactivar botón de subida de imagen al servidor
        publishBtnFalse();


        //Para acceder a la galería de fotos o la cámara
        ImagePicker.with(this)
                .crop()//Para recortar la imagen
                .compress(1024) //comprimir la imagen a un tamaño de 2MB
                .maxResultSize(3030, 3030)    //La resolución máxima permitida será 3030 x 3030 pixeles
                .start();

        //Salir de UploadActivity
        upload_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(UploadActivity.this, MenuActivity.class));
                finish();
            }
        });

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

    //Activa el botón 'Publicar Imagen'
    private void publishBtnTrue() {
        Toast.makeText(this, "Imagen selecionada.", Toast.LENGTH_LONG).show();
        btn_upload_img.setBackgroundColor(getResources().getColor(R.color.btn_upload_img));
        btn_upload_img.setEnabled(true);
        btn_upload_img.setText(R.string.publicar_imagen);
    }

    //Desactiva el botón 'Publicar Imagen'
    private void publishBtnFalse() {
        btn_upload_img.setBackgroundColor(getResources().getColor(R.color.btn_no_upload_img));
        btn_upload_img.setText(R.string.sin_imagen);
        btn_upload_img.setEnabled(false);
    }

    //Recoger la imagen proveniente de la cámara o de la galería.
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Si no elige ninguna de las opciones se cerrará la actividad
        if (resultCode == UploadActivity.RESULT_OK) {
            //Uri de la foto
            fotayUri = data.getData();
            //Añadir imagen al ImageView
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

            /*Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(fotayUri);
            this.sendBroadcast(mediaScanIntent);*/

        } else {
            Toast.makeText(this, "No se obtuvo la imagen.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    //Recibe la información de la imagen en bytes y la devuelve en una String
    public String getImagePath(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();

        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }
    /*public String getImagePath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA}; //EXTERNAL_CONTENT_URI
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) {
            return null;
        }
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s = cursor.getString(columnIndex);
        cursor.close();
        return s;
    }*/

    //Subir imagen al servidor remoto
    public void uploadImgToServer() {
        final ProgressDialog progressDialog = new ProgressDialog(this);

        // Hashmap para obtener los datos del usuario desde sqlite
        HashMap<String, String> user_sqlite = db.getUserDetails();
        String nomUsu = user_sqlite.get("usu_nombre");

        //[Volley API]
        StringRequest stringRequest = new StringRequest(Request.Method.POST, webhostURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(UploadActivity.this, response, Toast.LENGTH_LONG).show();

                progressDialog.setMessage("Subiendo imagen...");
                progressDialog.show();
                new Handler().postDelayed(new Runnable() {
                                              @Override
                                              public void run() {
                                                  progressDialog.dismiss();
                                                  //Cerrar actividad
                                                  finish();
                                              }
                                          }
                        , 6000);

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
                param.put("usu_nombre", nomUsu);
                param.put("foto_coment", photo_description);
                param.put("foto_ruta", getImagePath(bitmap));
                return param;
            }
        };

        requestQueue.add(stringRequest);

        /*Runnable runnable = new Runnable() {
            @Override
            public void run() {
                finish();
            }
        };
        Handler handler = new Handler();
        handler.postDelayed(runnable, 6999);*/
    }


}