package com.project.fotayapp.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.project.fotayapp.R;
import com.project.fotayapp.adapters.ViewPagerAdapter;
import com.project.fotayapp.fragments.chatFragment;
import com.project.fotayapp.fragments.homeFragment;
import com.project.fotayapp.fragments.profileFragment;

//MenuActivity gestiona la BottomNavigationView y sus fragments
public class MenuActivity extends AppCompatActivity {

    //Declarar variables
    private BottomNavigationView bottomNavigationView;

    private Fragment homeFragment;
    private Fragment chatFragment;
    private Fragment profileFragment;

    private ViewPager2 viewPager2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        //Trasparencia barra de estado
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        //Inicializar variables en la View
        bottomNavigationView = findViewById(R.id.bottom_nav_view);
        viewPager2 = findViewById(R.id.viewpager2);

        //Cambiar el fragment al pulsar en uno de los iconos del bottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        viewPager2.setCurrentItem(0, true);
                        break;
                    case R.id.nav_chat:
                        viewPager2.setCurrentItem(1, true);
                        break;
                    case R.id.nav_prof:
                        viewPager2.setCurrentItem(2, true);
                        break;

                }
                return false;
            }
        });
        //Iniciar por defecto el homeFragment()
        //getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new homeFragment()).commit();
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                switch (position) {
                    case 0:
                        bottomNavigationView.getMenu().findItem(R.id.nav_home).setChecked(true);
                        break;
                    case 1:
                        bottomNavigationView.getMenu().findItem(R.id.nav_chat).setChecked(true);
                        break;
                    case 2:
                        bottomNavigationView.getMenu().findItem(R.id.nav_prof).setChecked(true);
                        break;

                }
            }
        });
        setupViewPager(viewPager2);
    }
    //Llenar el ViewPager2 con los fragments
    private void setupViewPager(ViewPager2 viewPager2) {
        //Crear el adapter
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), getLifecycle());

        homeFragment = new homeFragment();
        chatFragment = new chatFragment();
        profileFragment = new profileFragment();

        //Añadir los fragments al adapter
        adapter.addFragment(homeFragment);
        adapter.addFragment(chatFragment);
        adapter.addFragment(profileFragment);

          //Desactivar el swipe
        viewPager2.setUserInputEnabled(false);


        //Asignar el adapter al viewPager2
        viewPager2.setAdapter(adapter);
    }
}