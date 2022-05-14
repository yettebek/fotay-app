package com.project.fotayapp.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hendraanggrian.appcompat.widget.SocialTextView;
import com.project.fotayapp.R;
import com.project.fotayapp.adapters.PostProfileAdapter;
import com.project.fotayapp.models.PostPhoto;
import com.project.fotayapp.models.UserDataSQLite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class PostActivity extends AppCompatActivity {

    //Variables
    private ImageView iv_profile_pic, iv_post_photo, iv_more;
    private TextView tv_username, tv_post_date;
    private SocialTextView tv_post_description;
    private Toolbar toolbar_back;
    private RecyclerView recyclerView;
    private PostProfileAdapter adapter;
    private PostProfileAdapter.OnItemClickListener listener;
    private ArrayList<PostPhoto> photoList = new ArrayList<PostPhoto>();
    public UserDataSQLite db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_post);

        //Recoger datos del fragment
        PostPhoto post = (PostPhoto) getIntent().getSerializableExtra("post");


        //Inicializar variables
        toolbar_back = findViewById(R.id.post_toolbar);

        // Inicializar base de datos SQLite para mostrar nombre de usuario según sesión
        db = new UserDataSQLite(getApplicationContext());

        // Inicializar lista
        photoList = new ArrayList<>();

        //Inicializar adaptador
        adapter = new PostProfileAdapter(getApplicationContext(), photoList, listener);

        //Inicializar recyclerView
        recyclerView = findViewById(R.id.rv_profile_posts);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(adapter);

        getSessionUsername();

        setSupportActionBar(toolbar_back);
        Objects.requireNonNull(getSupportActionBar()).setTitle(getSessionUsername());
        //Flecha de regreso en la barra de herramientas de la actividad
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar_back.setNavigationOnClickListener(v -> {
            finish();
        });
    }

    public String getSessionUsername() {
        HashMap<String, String> user_sqlite = db.getUserInfo();
        String nomUsu = user_sqlite.get("usu_nombre");
        return nomUsu;
    }
}