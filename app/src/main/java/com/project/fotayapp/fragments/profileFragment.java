package com.project.fotayapp.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.project.fotayapp.R;
import com.project.fotayapp.activities.OptionsActivity;
import com.project.fotayapp.activities.UploadActivity;
import com.project.fotayapp.adapters.PostPhotoAdapter;
import com.project.fotayapp.models.PostPhoto;
import com.project.fotayapp.models.UserDataSQLite;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class profileFragment extends Fragment {

    //Declarar variables
    private ImageView iv_profile_pic, iv_options_profile;
    private FloatingActionButton fab_imagen;
    private TextView tv_photo_count, tv_p_username;

    private UserDataSQLite db;

    private RecyclerView recyclerView;
    private PostPhotoAdapter adapter;
    private ArrayList<PostPhoto> photoList = new ArrayList<PostPhoto>();

    private static final int NUM_COLUMNS = 2;

    private Uri fotayUri;
    private Bitmap bitmap;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        /*//change the status bar color to transparent
        Window window = requireActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);*/

        // Cargar los componentes de la vista del Fragment
        iv_profile_pic = view.findViewById(R.id.iv_p_userimage);
        iv_options_profile = view.findViewById(R.id.iv_p_options);
        fab_imagen = view.findViewById(R.id.fab_imagen);
        tv_photo_count = view.findViewById(R.id.tv_photo_counter);
        tv_p_username = view.findViewById(R.id.tv_p_username);

        //Inicializar adaptador
        adapter = new PostPhotoAdapter(getContext(), photoList);
        //Inicializar recyclerView encargado de mostrar las fotos
        recyclerView = view.findViewById(R.id.rv_p_photos);

        // Inicializar base de datos sqlite
        db = new UserDataSQLite(getContext());

        // Hashmap para obtener el nombre de usuario desde sqlite y mostrarlo en la vista del fragment
        HashMap<String, String> user_sqlite = db.getUserName();
        String profileusername = user_sqlite.get("usu_nombre");

        //Método para obtener los posts del usuario desde la base de datos
        getUserPosts();
        //Método para obtener la imagen de perfil del usuario desde la base de datos
        loadProfileImg();

        //declarar un StaggeredGridLayoutManager con 2 columnas
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(NUM_COLUMNS, LinearLayoutManager.VERTICAL);

        /*GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);*/

        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        //Inicializar la lista de fotos
        photoList = new ArrayList<>();

        // Setear el nombre de usuario en el TextView
        tv_p_username.setText(profileusername);


        //counter fotos
        //int photo_count = db.getPhotoCount();
        //tv_photo_count.setText(String.valueOf(photo_count));

        // Abriendo la actividad de ajustes
        iv_options_profile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), OptionsActivity.class);
            startActivity(intent);

        });

        //Abir UploadActivity para elegir la imagen a subir a la app:
        fab_imagen.setOnClickListener(v -> {
            Intent uploadIntent = new Intent(getActivity(), UploadActivity.class);
            startActivity(uploadIntent);
            getParentFragmentManager().beginTransaction().detach(this).attach(this).commit();
        });


        //setOnClickListener para iv_profile_pic
        iv_profile_pic.setOnClickListener(v -> {

            //Para acceder a la galería de fotos o la cámara
            ImagePicker.with(this)
                    .crop(8f, 8f)//Para recortar la imagen
                    .compress(1024) //comprimir la imagen a un tamaño máximo de 1MB
                    .maxResultSize(600, 600)    //La resolución máxima permitida será 600 x 600 pixeles
                    .start();
        });
        return view;
    }

    // Método para recibir la imagen desde la galería o la cámara
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Comprobar si el resultado es correcto
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ImagePicker.REQUEST_CODE) {
                // Obtener la imagen de la galería
                assert data != null;
                fotayUri = data.getData();
                // Cargar la imagen en el ImageView
                iv_profile_pic.setImageURI(fotayUri);

                //Guardar imagen a la App de galería del teléfono
                InputStream imageStream;
                OutputStream out;
                try {
                    imageStream = requireActivity().getContentResolver().openInputStream(fotayUri);
                    bitmap = BitmapFactory.decodeStream(imageStream);
                    out = new FileOutputStream(new File(requireContext().getCacheDir(), "user_profile.jpg"));
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                    //out.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                //Enviar la imagen a la base de datos
                updateProfilePicture();

                // Actualizar el contador de fotos
                //tv_photo_count.setText(String.valueOf(db.getPhotoCount()));
            } else {
                Toast.makeText(getContext(), "No se obtuvo la imagen.", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        }
    }

    public String webhosturl() {
        HashMap<String, String> user_sqlite = db.getUserName();
        String nomUsu = user_sqlite.get("usu_nombre");
        return "https://fotay.000webhostapp.com/fetchDataProfile.php?usu_nombre=" + nomUsu;
    }


    //Método para obtener los posts del usuario desde la base de datos
    public void getUserPosts() {
        //[Volley API]
        JsonArrayRequest JSONRequest = new JsonArrayRequest(webhosturl(),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        tv_photo_count.setText(String.valueOf(response.length()));
                        try {
                            for (int i = 0; i < response.length(); i++) {

                                JSONObject jsonObject = (JSONObject) response.get(i);

                                /*String usu_nombre = jsonObject.getString("user");
                                String foto_fecha = jsonObject.getString("type");
                                String foto_coment = jsonObject.getString("tags");*/
                                String foto_ruta = jsonObject.getString("foto_ruta");

                                //Agregar el objeto a la lista de objetos
                                photoList.add(new PostPhoto(/*usu_nombre, foto_fecha, foto_coment,*/ foto_ruta));

                                //Agregar a sqlite
                                //db.addUserTableFotos(usu_nombre, foto_fecha, foto_coment, foto_ruta);

                                //RecyclerAdapter
                                adapter = new PostPhotoAdapter(getContext(), photoList);
                                recyclerView.setAdapter(adapter);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Sin imágenes.", Toast.LENGTH_SHORT).show();
                //Toast.makeText(getContext(), error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        }
        );/* {
            @NonNull
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<String, String>();
                param.put("usu_nombre", nomUsu);
                return param;
            }
        };*/
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(JSONRequest);
    }

    // Método para actualizar la imagen del usuario //NO SUBE LA IMAGEN A LA BASE DE DATOS
    private void updateProfilePicture() {
        // Hashmap para obtener los datos del usuario desde sqlite
        HashMap<String, String> user_sqlite = db.getUserName();
        String profileusername = user_sqlite.get("usu_nombre");

        String url = "https://fotay.000webhostapp.com/profileUpload.php";
        //StringRequest para actualizar la imagen del usuario en la base de datos
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getContext(), "Imagen subida correctamente", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("usu_nombre", profileusername);
                params.put("foto_perfil", getImagePath(bitmap));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(stringRequest);
    }

    public String getImagePath(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();

        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    public String webhosturl2() {
        HashMap<String, String> user_sqlite = db.getUserName();
        String nomUsu = user_sqlite.get("usu_nombre");
        return "https://fotay.000webhostapp.com/profileFetch.php?usu_nombre=" + nomUsu;
    }

    // Método para cargar la imagen de perfil del usuario
    private void loadProfileImg() {
        //Petición en formato json para recibir la imagen de perfil del usuario
        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(webhosturl2(), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                //Toast.makeText(getContext(), String.valueOf(response), Toast.LENGTH_LONG).show();
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObject = (JSONObject) response.get(i);
                        String foto_perfil = jsonObject.getString("foto_perfil");

                        //Si no hay imagen de perfil, se carga la imagen por defecto
                        if (foto_perfil.equals("null")) {
                            Toast.makeText(getContext(), "Sin imagen de perfil.", Toast.LENGTH_SHORT).show();
                        } else {
                            //Cargar la imagen de perfil del usuario en el ImageView
                            Picasso.get().load(foto_perfil).fit().centerInside().into(iv_profile_pic);
                            Toast.makeText(getContext(), "Imagen de usuario cargada.", Toast.LENGTH_SHORT).show();

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), String.valueOf(error), Toast.LENGTH_LONG).show();
                Toast.makeText(getContext(), String.valueOf(error), Toast.LENGTH_LONG).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(jsonArrayRequest);
    }
}



