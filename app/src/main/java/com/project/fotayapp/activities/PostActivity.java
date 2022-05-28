package com.project.fotayapp.activities;

import static com.project.fotayapp.adapters.PostProfileAdapter.EXTRA_DATE;
import static com.project.fotayapp.adapters.PostProfileAdapter.EXTRA_DESCRIPTION;
import static com.project.fotayapp.adapters.PostProfileAdapter.EXTRA_ID_PHOTO;
import static com.project.fotayapp.adapters.PostProfileAdapter.EXTRA_PHOTO;
import static com.project.fotayapp.adapters.PostProfileAdapter.EXTRA_PROFILE_PHOTO;
import static com.project.fotayapp.adapters.PostProfileAdapter.EXTRA_USERNAME;

import android.content.Intent;
import android.os.Bundle;
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
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hendraanggrian.appcompat.widget.SocialTextView;
import com.project.fotayapp.R;
import com.project.fotayapp.adapters.PostProfileAdapter;
import com.project.fotayapp.fragments.profileFragment;
import com.project.fotayapp.models.PostPhoto;
import com.project.fotayapp.models.UserDataSQLite;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class PostActivity extends AppCompatActivity {

    //private static String DELETE_PHOTO_URL = "";
    //Variables
    private ImageView iv_profile_pic, iv_comments;
    private TextView tv_username, tv_post_date, tv_comments;
    private SocialTextView tv_post_description;
    private Toolbar toolbar_back;
    private RecyclerView recyclerView;
    private PostProfileAdapter adapter;
    private PostProfileAdapter.OnItemClickListener listener;
    private ArrayList<PostPhoto> photoList = new ArrayList<PostPhoto>();
    public UserDataSQLite db;
    public profileFragment profileFragment;
    public static int position;
    public ImageView iv_post_photo;
    private NestedScrollView nestedScrollView;
    public static int id_photo;
    public static final String POST_ACTIVITY = "PostActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_post);

        //Inicializar variables
        toolbar_back = findViewById(R.id.post_toolbar);
        iv_post_photo = findViewById(R.id.post_image);
        iv_profile_pic = findViewById(R.id.post_profile_image);
        tv_username = findViewById(R.id.post_username);
        tv_post_date = findViewById(R.id.post_date);
        iv_comments = findViewById(R.id.iv_comment);
        tv_comments = findViewById(R.id.tv_comments);
        tv_post_description = findViewById(R.id.description);


        // Inicializar base de datos SQLite para mostrar nombre de usuario según sesión
        db = new UserDataSQLite(getApplicationContext());

        profileFragment = new profileFragment();

        // Inicializar lista
        photoList = profileFragment.photoList;
        /*int size = photoList.size();
        Toast.makeText(getApplicationContext(), "Tamaño de la lista: " + size, Toast.LENGTH_SHORT).show();*/

        getSessionUsername();

        setSupportActionBar(toolbar_back);
        Objects.requireNonNull(getSupportActionBar()).setTitle(Html.fromHtml("<font color='#ffffff'>" + "Foto" + "</font>"));
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
            Intent intent = new Intent(getApplicationContext(), FullScreenImageActivity.class);
            intent.putExtra(EXTRA_PHOTO, photoList.get(position).getFoto_ruta());
            startActivity(intent);
        });

        tv_comments.setOnClickListener(v -> {
            //Abrir actividad de comentarios
            Intent intent = new Intent(getApplicationContext(), CommentsActivity.class);
            intent.putExtra("Class", "PostActivity");
            intent.putExtra(EXTRA_ID_PHOTO, photoList.get(position).getFoto_id());
            intent.putExtra(EXTRA_PHOTO, photoList.get(position).getFoto_ruta());
            intent.setClass(getApplicationContext(), CommentsActivity.class);
            startActivity(intent);
        });

        id_photo = profileFragment.getPhotoId(position);
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
                            //Método para eliminar foto seleccionada de la base de datos
                            deletePicture();
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
                    Toast.makeText(getApplicationContext(), "Foto eliminada, desliza \nhacia abajo para actualizar.", Toast.LENGTH_LONG).show();

                    //Eliminar foto de la lista
                    photoList.remove(position);
                    adapter.notifyDataSetChanged();

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

        String profile_photo = intent.getStringExtra(EXTRA_PROFILE_PHOTO);
        Picasso.get().load(profile_photo).fit().centerInside().into(iv_profile_pic);

        String username = intent.getStringExtra(EXTRA_USERNAME);

        String post_date = intent.getStringExtra(EXTRA_DATE);

        position = intent.getIntExtra(EXTRA_ID_PHOTO, 0);

        String photo = intent.getStringExtra(EXTRA_PHOTO);
        Picasso.get().load(photo).fit().centerInside().into(iv_post_photo);

        String post_comment = intent.getStringExtra(EXTRA_DESCRIPTION);

        tv_username.setText(username);
        tv_post_date.setText(post_date);
        tv_post_description.setText(post_comment);

        //Toast.makeText(this, "foto_id nº: " + profileFragment.getPhotoId(position), Toast.LENGTH_SHORT).show();
    }
}