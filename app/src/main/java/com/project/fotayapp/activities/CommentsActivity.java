package com.project.fotayapp.activities;

import static com.project.fotayapp.activities.PostActivity.POST_ACTIVITY;
import static com.project.fotayapp.adapters.HomeAdapter.HOME_ADAPTER;
import static com.project.fotayapp.adapters.PostProfileAdapter.EXTRA_ID_PHOTO;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.project.fotayapp.R;
import com.project.fotayapp.fragments.profileFragment;

public class CommentsActivity extends AppCompatActivity {

    //Variables
    public static int position;
    public profileFragment profileFragment;
    public PostActivity postActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coments);

        profileFragment = new profileFragment();
        postActivity = new PostActivity();

        //Si el intent viene de la actividad de post (PostActivity) o del adapter (HomeAdapter)
        Intent intentPost = getIntent();
        Intent intentHome = getIntent();

        String classPost = intentPost.getStringExtra(POST_ACTIVITY);
        String classAdapter = intentHome.getStringExtra(HOME_ADAPTER);

        //Toast.makeText(this, classAdapter, Toast.LENGTH_SHORT).show();

        if (classPost.equals("PostActivity")) {
            String name1 = intentPost.getStringExtra(POST_ACTIVITY);
            position = intentPost.getIntExtra(EXTRA_ID_PHOTO, 0);
            Toast.makeText(this, "PostActivity  ", Toast.LENGTH_SHORT).show();
        }
        else {
            String name2 = intentHome.getStringExtra(HOME_ADAPTER);
            position = intentHome.getIntExtra(EXTRA_ID_PHOTO, 0);
            Toast.makeText(this, "HomeAdapter", Toast.LENGTH_SHORT).show();
        }
        //Método que muestra los comentarios de una foto 
        showPhotoComments();
    }

    private void showPhotoComments() {
        //Voley api
        //SELECT txt_comment FROM comentarios WHERE foto_id = '$foto_id'
        //Si no hay comentarios, mostrar mensaje
        //Toast.makeText(this, "SIN COMENTARIOS...", Toast.LENGTH_SHORT).show();
        //Toast.makeText(this, "foto_id nº: " + postActivity.id_photo, Toast.LENGTH_SHORT).show();

        //Si hay comentarios, mostrar en lista

    }
}