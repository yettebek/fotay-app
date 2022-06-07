package com.project.fotayapp.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.project.fotayapp.R;
import com.project.fotayapp.activities.OptionsActivity;
import com.project.fotayapp.activities.UploadActivity;
import com.project.fotayapp.adapters.PostProfileAdapter;
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

public class profileFragment extends Fragment implements PostProfileAdapter.AdapterCallback{

    //Declarar variables
    private ImageView iv_profile_pic, iv_options_profile;
    private FloatingActionButton fab_imagen;
    public static TextView tv_photo_count;
    private TextView tv_p_username;

    public UserDataSQLite db;
    private RecyclerView recyclerView;
    private PostProfileAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    public static ArrayList<PostPhoto> photoList = new ArrayList<PostPhoto>();

    private static final int NUM_COLUMNS = 3;

    private Uri fotayUri;
    private Bitmap bitmap;
    String newUsername;
    public static ArrayList<Integer> post_ids = new ArrayList<>();
    public static final String EXTRA_PROFILE_PHOTO_P = "profile_photo";
    PostProfileAdapter.AdapterCallback callback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Cargar los componentes de la vista del Fragment
        iv_profile_pic = view.findViewById(R.id.iv_p_userimage);
        iv_options_profile = view.findViewById(R.id.iv_p_options);
        fab_imagen = view.findViewById(R.id.fab_imagen);
        tv_photo_count = view.findViewById(R.id.tv_photo_counter);
        tv_p_username = view.findViewById(R.id.tv_p_username);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        //Toast profileFragment
        Toast.makeText(getContext(), "Profile Fragment", Toast.LENGTH_SHORT).show();


        //Inicializar adaptador, llamar al método setOnClickListener para que se ejecute la interfaz OnItemClickListener de la clase PostProfileAdapter
        adapter = new PostProfileAdapter(getContext(), photoList, callback);

        //Inicializar recyclerView encargado de mostrar las fotos
        recyclerView = view.findViewById(R.id.profileRecyclerView);

        // Inicializar base de datos sqlite
        db = new UserDataSQLite(requireContext());

        //Metodo para obtener la imagen de perfil del usuario desde la base de datos
        loadProfileImg();

        //Método para obtener los posts del usuario desde la base de datos
        getUserPosts();

        //declarar un StaggeredGridLayoutManager con 3 columnas
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(NUM_COLUMNS, LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        //Inicializar la lista de fotos
        photoList = new ArrayList<>();

        // Setear el nombre de usuario en el TextView
        tv_p_username.setText(getSessionUsername());

        //Toast.makeText(getContext(), "ID usuario: " + getSessionId(), Toast.LENGTH_SHORT).show();

        //Actualizar la lista de fotos al actualizar la vista
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Actualizar la lista de posts
                /*photoList.clear();
                loadProfileImg();
                getUserPosts();
                clearPostIds();*/
                swipeRefreshLayout.setRefreshing(false);

            }
        });

        // Abriendo la actividad de ajustes
        iv_options_profile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), OptionsActivity.class);
            startActivity(intent);
        });

        //Editar el nombre de usuario
        tv_p_username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Creando un dialogo para confirmar edición del nombre de usuario
                AlertDialog.Builder nameDialog = new AlertDialog.Builder(requireContext());
                nameDialog.setCancelable(false);
                nameDialog.setIcon(R.drawable.ic_user_icon);
                nameDialog.setTitle("Editar nombre");

                // Creando un EditText para ingresar el nuevo nombre de usuario
                final EditText input = new EditText(getContext());
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setHint("Nuevo nombre de usuario");
                input.setImeOptions(EditorInfo.IME_ACTION_DONE);
                nameDialog.setView(input);

                nameDialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (input.getText().toString().isEmpty() || input.getText().toString().equals("") || input.getText().toString().length() < 4) {
                            Toast.makeText(getContext(), "Nombre NO válido.", Toast.LENGTH_SHORT).show();
                        } else {
                            // Obteniendo el nombre de usuario
                            newUsername = input.getText().toString();
                            tv_p_username.setText(newUsername);

                            saveLoginSharedPreferences();

                            db.updateUserTableUsuarios(newUsername, getSessionId());
                            Toast.makeText(getContext(), "Nuevo nombre de usuario: " + newUsername, Toast.LENGTH_SHORT).show();

                            //Actualizar nombre de usuario en la base de datos mysql [updateUsername.php]
                            updateUserName(newUsername);
                        }
                    }
                });
                nameDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                nameDialog.show();
            }
        });

        //Abir UploadActivity para elegir la imagen a subir a la app:
        fab_imagen.setOnClickListener(v -> {
            //Para acceder a la galería de fotos o la cámara
            ImagePicker.with(this)
                    .crop()//Para recortar la imagen
                    .compress(1024) //comprimir la imagen a un tamaño de 2MB
                    .maxResultSize(1080, 1920)    //La resolución máxima permitida
                    .start(2);

        });

        //setOnClickListener para iv_profile_pic
        iv_profile_pic.setOnClickListener(v -> {
            //Para acceder a la galería de fotos o la cámara
            ImagePicker.with(this)
                    .crop(8f, 8f)//Para recortar la imagen
                    .compress(1024) //comprimir la imagen a un tamaño máximo de 1MB
                    .maxResultSize(600, 600)    //La resolución máxima permitida será 600 x 600 pixeles
                    .start(1); //1 para la imagen de perfil, 2 para la imagen de post
        });

        //Toast.makeText(requireContext(), "ID: " + getUserId() ,Toast.LENGTH_SHORT).show();
        return view;
    }

    private void updateUserName(String newUsername) {
        String UPDATE_USERNAME_URL = "https://fotay.000webhostapp.com/updateUsername.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPDATE_USERNAME_URL, response -> {
            if (response.equals("Nombre actualizado")) {
                Toast.makeText(getContext(), "Nombre actualizado", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), response, Toast.LENGTH_LONG).show();
            }
        }, error -> {
            Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("usu_id", getSessionId());
                params.put("usu_nombre", newUsername);
                return params;
            }
        };
        //TimeoutError arreglo
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        //Procesa todas las peticiones de nuestra app
        stringRequest.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(stringRequest);
    }

    // Método para recibir la imagen desde la galería o la cámara
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Comprobar si el resultado es correcto
        if (requestCode == 1 && resultCode != getActivity().RESULT_CANCELED) {
            Toast.makeText(getContext(), "requestCode 1", Toast.LENGTH_SHORT).show();
            assert data != null;
            // Obtener la imagen de la galería
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
                out.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            //Enviar la imagen a la base de datos
            updateProfilePicture();

        }
        if (requestCode == 2 && resultCode != getActivity().RESULT_CANCELED) {
            Toast.makeText(getContext(), "Cargando imagen...", Toast.LENGTH_SHORT).show();

            //Uri de la foto
            fotayUri = data.getData();

            //detach fragment
            //getActivity().getSupportFragmentManager().beginTransaction().remove(this).attach(this).commit();
            //getActivity().getSupportFragmentManager().beginTransaction().detach(this).commit();

            //Enviar la imagen a la otra actividad por medio de un intent
            Intent uploadIntent = new Intent(getActivity(), UploadActivity.class);
            uploadIntent.putExtra("fotayUri", fotayUri.toString());
            startActivity(uploadIntent);

            //getParentFragmentManager().beginTransaction().detach(this).attach(this).commit();


        } else if (resultCode == getActivity().RESULT_CANCELED) {
            Toast.makeText(getContext(), "No se obtuvo la imagen.", Toast.LENGTH_SHORT).show();

        }
    }

    public String webhosturl() {
        HashMap<String, String> user_sqlite = db.getUserInfo();
        String nomUsu = user_sqlite.get("usu_nombre");
        return "https://fotay.000webhostapp.com/fetchDataProfile.php?usu_nombre=" + nomUsu;
    }

    //Método para obtener los posts del usuario desde la base de datos
    public void getUserPosts() {

        //[Volley API]
        JsonObjectRequest JSONRequest = new JsonObjectRequest(Request.Method.GET, webhosturl(), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("profile_posts");
                            //Toast.makeText(getContext(), jsonArray.length() + " fotos".toUpperCase(Locale.ROOT), Toast.LENGTH_SHORT).show();
                            tv_photo_count.setText(String.valueOf(jsonArray.length()));
                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject profile_posts = jsonArray.getJSONObject(i);
                                int foto_id = profile_posts.getInt("foto_id");
                                String usu_nombre = profile_posts.getString("usu_nombre");
                                String foto_fecha = profile_posts.getString("foto_fecha");
                                String foto_coment = profile_posts.getString("foto_coment");
                                String foto_ruta = profile_posts.getString("foto_ruta");
                                String foto_perfil = profile_posts.getString("foto_perfil");
                                int total_comentarios = profile_posts.getInt("total_comentarios");

//Agregar el objeto a la lista de objetos
                                photoList.add(new PostPhoto(foto_id, usu_nombre, foto_fecha, foto_coment, foto_ruta, foto_perfil, total_comentarios));
                                //Guardar foto_id en una lista
                                post_ids.add(foto_id);
                            }
                            //RecyclerAdapter
                            adapter = new PostProfileAdapter(getContext(), photoList,callback);
                            adapter.notifyDataSetChanged();
                            //
                            //adapter.notifyItemInserted(photoList.size() - 1);
                            //Pasar la lista de objetos a la vista del RecyclerView
                            recyclerView.setAdapter(adapter);

                            int size = photoList.size();
                            //tv_photo_count.setText(String.valueOf(size));


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Snackbar snackbar = Snackbar
                            .make(requireView(), "Error de conexión, inténtalo de nuevo", Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction("REINTENTAR", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (view != null) {
                                //Actualizar la lista de posts
                                photoList.clear();
                                loadProfileImg();
                                getUserPosts();
                                clearPostIds();
                            } else {
                                Snackbar snackbar1 = Snackbar.make(view, "Error de conexión, inténtalo de nuevo", Snackbar.LENGTH_SHORT);
                                snackbar1.show();
                            }

                        }
                    }).setTextColor(Color.RED);
                    snackbar.show();
                }
            }
        }
        );
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(JSONRequest);
    }

    // Método para actualizar la imagen del usuario //NO SUBE LA IMAGEN A LA BASE DE DATOS
    private void updateProfilePicture() {
        //Url conexion con el webhost
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
                params.put("usu_id", getSessionId());
                params.put("usu_nombre", getSessionUsername());
                params.put("foto_perfil", getImagePath(bitmap));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(stringRequest);
    }

    //Recibe la información de la imagen en bytes y la devuelve en una String para subirlo al servidor
    public String getImagePath(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    public String webhosturl2() {
        HashMap<String, String> user_sqlite = db.getUserInfo();
        String nomUsu = user_sqlite.get("usu_nombre");
        return "https://fotay.000webhostapp.com/profileFetch.php?usu_nombre=" + nomUsu;
    }

    // Método para cargar la imagen de perfil del usuario
    private void loadProfileImg() {
        //Petición en formato json para recibir la imagen de perfil del usuario
        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(webhosturl2(), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObject = (JSONObject) response.get(i);
                        String foto_perfil = jsonObject.getString("foto_perfil");

                        //Si no hay imagen de perfil, se carga la imagen por defecto
                        if (foto_perfil.equalsIgnoreCase("") || foto_perfil.equalsIgnoreCase("null")) {
                            Picasso.get().load(foto_perfil).placeholder(R.drawable.ic_no_profile_picture).into(iv_profile_pic);

                        } else {
                            //Cargar la imagen de perfil del usuario en el ImageView
                            Picasso.get().load(foto_perfil).fit().centerInside().into(iv_profile_pic);

                        }
                        //Enviar a CommentsActivity la foto de perfil del usuario

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Snackbar snackbar = Snackbar
                            .make(requireView(), "Error de conexión, inténtalo de nuevo", Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction("REINTENTAR", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Actualizar la lista de posts
                            photoList.clear();
                            loadProfileImg();
                            getUserPosts();
                            clearPostIds();
                        }
                    }).setTextColor(Color.RED);
                    snackbar.show();
                } else {
                    Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(jsonArrayRequest);
    }

    //Método para obtener el nombre de usuario desde sqlite
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

    public int getUserId() {
        HashMap<String, Integer> id_sqlite = db.getUserId();
        int idUsu = id_sqlite.get("usu_id");
        return idUsu;
    }

    public static int getPhotoId(int position) {
        return post_ids.get(position);
    }

    public static void clearPostIds() {
        post_ids.clear();
    }


    public void saveLoginSharedPreferences() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("LoginPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("usu_nombre", newUsername);
        editor.putBoolean("update", true); //guardar nuevos datos de sesión en caso afirmativo de login
        editor.apply(); //guarda todos los cambios
    }

    @Override
    public void onChangeCommentCount(int position, int commentCount) {

    }
}



