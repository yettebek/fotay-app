package com.project.fotayapp.activities;

import static com.project.fotayapp.activities.PostActivity.POST_ACTIVITY;
import static com.project.fotayapp.adapters.HomeAdapter.HOME_ADAPTER;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.project.fotayapp.R;
import com.project.fotayapp.fragments.homeFragment;
import com.project.fotayapp.fragments.profileFragment;

public class CommentsActivity extends AppCompatActivity {

    //Variables
    public static int position;
    public profileFragment profileFragment;
    public PostActivity postActivity;
    public homeFragment homeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coments);

        profileFragment = new profileFragment();
        homeFragment = new homeFragment();
        postActivity = new PostActivity();

        //Si el intent viene de la actividad de post (PostActivity) o del adapter (HomeAdapter)

        String classIntent = getIntent().getStringExtra("Class");
        //String classAdapter = intentHome.getStringExtra(HOME_ADAPTER);


        if (classIntent.equals("PostActivity")) {
            position = getIntent().getIntExtra(POST_ACTIVITY, 0);
            Toast.makeText(this, "PostActivity  ", Toast.LENGTH_SHORT).show();
            //Método que muestra los comentarios de una foto
            showPostComments();
        } else if (classIntent.equals("HomeAdapter")) {
            position = getIntent().getIntExtra(HOME_ADAPTER, 0);
            Toast.makeText(this, "HomeAdapter", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "foto_id nº: " + homeFragment.getPhotoHomeId(position), Toast.LENGTH_SHORT).show();
            //Método que muestra los comentarios de una foto
            showHomeComments();
        }

    }

    private void showPostComments() {
        //Voley api
        //SELECT txt_comment FROM comentarios WHERE foto_id = '$foto_id'
        //Si no hay comentarios, mostrar mensaje
        //Toast.makeText(this, "SIN COMENTARIOS...", Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "foto_id nº: " + postActivity.id_photo, Toast.LENGTH_SHORT).show();

        //Si hay comentarios, mostrar en lista

    }

    private void showHomeComments() {
        //Voley api
        //SELECT txt_comment FROM comentarios WHERE foto_id = '$foto_id'
        //Si no hay comentarios, mostrar mensaje
        //Toast.makeText(this, "SIN COMENTARIOS...", Toast.LENGTH_SHORT).show();


        //Si hay comentarios, mostrar en lista

    }
}