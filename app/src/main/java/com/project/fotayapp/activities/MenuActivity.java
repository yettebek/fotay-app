package com.project.fotayapp.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.project.fotayapp.R;
import com.project.fotayapp.fragments.favouriteFragment;
import com.project.fotayapp.fragments.homeFragment;
import com.project.fotayapp.fragments.notificationFragment;
import com.project.fotayapp.fragments.profileFragment;

//MenuActivity gestiona la BottomNavigationView y sus fragments
public class MenuActivity extends AppCompatActivity {

    //Declarar variables
    private BottomNavigationView bottomNavigationView;
    private Fragment currentFragment;
    public MenuActivity menuActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_menu);
        //Trasparencia barra de estado
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        //Inicializar variables en la View
        bottomNavigationView = findViewById(R.id.bottom_nav_view);

        //Cambiar el fragment al pulsar en uno de los iconos del bottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    currentFragment = new homeFragment();
                } else if (id == R.id.nav_notif) {
                    currentFragment = new notificationFragment();
                } else if (id == R.id.nav_fav) {
                    currentFragment = new favouriteFragment();
                } else if (id == R.id.nav_prof) {
                    currentFragment = new profileFragment();
                }
                if (currentFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, currentFragment).commit();
                }
                return true;
            }
        });
        //Iniciar por defecto el homeFragment()
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new homeFragment()).commit();
    }
}