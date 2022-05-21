package com.project.fotayapp.activities;

import static com.project.fotayapp.adapters.PostProfileAdapter.EXTRA_ID_PHOTO;
import static com.project.fotayapp.adapters.PostProfileAdapter.EXTRA_PHOTO;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hendraanggrian.appcompat.widget.SocialTextView;
import com.project.fotayapp.R;
import com.project.fotayapp.adapters.PostProfileAdapter;
import com.project.fotayapp.fragments.profileFragment;
import com.project.fotayapp.models.PostPhoto;
import com.project.fotayapp.models.UserDataSQLite;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class PostActivity extends AppCompatActivity {

    //private static String DELETE_PHOTO_URL = "";
    //Variables
    private ImageView iv_profile_pic, iv_post_photo, iv_more;
    private TextView tv_username, tv_post_date;
    private SocialTextView tv_post_description;
    private Toolbar toolbar_back;
    ImageView iv_post;
    private RecyclerView recyclerView;
    private PostProfileAdapter adapter;
    private PostProfileAdapter.OnItemClickListener listener;
    private ArrayList<PostPhoto> photoList = new ArrayList<PostPhoto>();
    public UserDataSQLite db;
    public profileFragment profileFragment;
    public static int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_post);

        //Inicializar variables
        toolbar_back = findViewById(R.id.post_toolbar);
        iv_post_photo = findViewById(R.id.post_image);

        // Inicializar base de datos SQLite para mostrar nombre de usuario según sesión
        db = new UserDataSQLite(getApplicationContext());

        profileFragment = new profileFragment();

        // Inicializar lista
        photoList = profileFragment.photoList;
/*        int size = photoList.size();
        Toast.makeText(getApplicationContext(), "Tamaño de la lista: " + size, Toast.LENGTH_SHORT).show();*/

        getSessionUsername();

        setSupportActionBar(toolbar_back);
        Objects.requireNonNull(getSupportActionBar()).setTitle(Html.fromHtml("<font color='#ffffff'>" + getSessionUsername() + "</font>"));
        //Flecha de regreso en la barra de herramientas de la actividad
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setIcon(R.drawable.ic_back);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Obtener datos de la actividad anterior y mostrarlos en esta actividad
        getFromIntent();

        toolbar_back.setNavigationOnClickListener(v -> {
            finish();
        });

        iv_post_photo.setOnClickListener(v -> {
            //Abrir la imagen a pantalla completa
        });
    }

    //Options menu en la barra de herramientas
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    //Acciones de los items del menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete_foto:
                //Dialog para confirmar eliminación de foto
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("¿Estás seguro de eliminar esta foto?")
                        .setCancelable(false)
                        .setPositiveButton("Eliminar", (dialog, which) -> {

                            new Handler().postDelayed(() -> {
                                deletePicture();

                            }, 1000);


                        }).setNegativeButton("Cancelar", (dialog, which) -> {

                    Toast.makeText(getApplicationContext(), getSessionId(), Toast.LENGTH_SHORT).show();
                    //profileFragment.reloadFragment();

                    dialog.dismiss();
                }).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Eliminar foto de la base de datos
    private void deletePicture() {
        String DELETE_PHOTO_URL = "https://fotay.000webhostapp.com/deletePhoto.php?foto_id=" + profileFragment.getPhotoId(position);

        //[Volley API]
        StringRequest request = new StringRequest(Request.Method.GET, DELETE_PHOTO_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equalsIgnoreCase("Foto eliminada.")) {
                    Toast.makeText(getApplicationContext(), "Foto eliminada.\nActualiza la página", Toast.LENGTH_SHORT).show();

                    //profileFragment.reloadFragment();
                    finish();

                } else {
                    Toast.makeText(getApplicationContext(), "Error al eliminar foto.", Toast.LENGTH_SHORT).show();
                }
            }
        }, error -> {
            Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    //Método para actualizar las fotos en el fragment
    public void updatePhotos() {
        String GET_PHOTOS_URL = "https://fotay.000webhostapp.com/fetchDataProfile.php?usu_nombre=" + getSessionUsername();
        //[Volley API]
        JsonObjectRequest JSONRequest = new JsonObjectRequest(Request.Method.GET, GET_PHOTOS_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray jsonArray = response.getJSONArray("profile_posts");

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject profile_posts = jsonArray.getJSONObject(i);
                                int foto_id = profile_posts.getInt("foto_id");
                                String usu_nombre = profile_posts.getString("usu_nombre");
                                String foto_fecha = profile_posts.getString("foto_fecha");
                                String foto_coment = profile_posts.getString("foto_coment");
                                String foto_ruta = profile_posts.getString("foto_ruta");
                                String foto_perfil = profile_posts.getString("foto_perfil");

                                //Agregar el objeto a la lista de objetos
                                photoList.add(new PostPhoto(foto_id, usu_nombre, foto_fecha, foto_coment, foto_ruta, foto_perfil));
                            }
                            //Toast.makeText(getContext(), "Foto ID nº 1: " + getPhotoId(0), Toast.LENGTH_SHORT).show();
                            //RecyclerAdapter
                            adapter = new PostProfileAdapter(PostActivity.this, photoList, listener);
                            //
                            //Pasar la lista de objetos a la vista del RecyclerView
                            recyclerView.setAdapter(adapter);

                            int size = photoList.size();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getContext(), "Sin imágenes.", Toast.LENGTH_SHORT).show();
            }
        }
        );
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(JSONRequest);
    }

    public String getSessionUsername() {
        HashMap<String, String> user_sqlite = db.getUserInfo();
        String nomUsu = user_sqlite.get("usu_nombre");
        return nomUsu;
    }

    public String getSessionId() {
        HashMap<String, String> user_sqlite = db.getUserInfo();
        String idUsu = user_sqlite.get("usu_id");
        return idUsu;
    }

    public void getFromIntent() {
        Intent intent = getIntent();
        String photo = intent.getStringExtra(EXTRA_PHOTO);
        Picasso.get().load(photo).fit().centerInside().into(iv_post_photo);

        position = intent.getIntExtra(EXTRA_ID_PHOTO, 0);

        Toast.makeText(this, "foto_id nº: " + profileFragment.getPhotoId(position), Toast.LENGTH_SHORT).show();


    }
}