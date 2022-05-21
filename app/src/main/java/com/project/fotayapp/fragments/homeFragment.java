package com.project.fotayapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hendraanggrian.appcompat.widget.SocialTextView;
import com.project.fotayapp.R;
import com.project.fotayapp.adapters.HomeAdapter;
import com.project.fotayapp.models.PostPhoto;
import com.project.fotayapp.models.UserDataSQLite;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


@SuppressWarnings("CommentedOutCode")
public class homeFragment extends Fragment {

    //Declarar variables
    private ImageView iv_profile_pic, iv_post_photo;
    private TextView tv_username, tv_post_date, tv_post_likes, tv_post_comments;
    private SocialTextView tv_post_description;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton fab_up;
    private RecyclerView recyclerView;
    private HomeAdapter adapter;
    private ArrayList<PostPhoto> photoList = new ArrayList<PostPhoto>();
    public UserDataSQLite db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        iv_profile_pic = view.findViewById(R.id.home_profile_image);
        iv_post_photo = view.findViewById(R.id.home_photo);
        tv_username = view.findViewById(R.id.home_username);
        tv_post_date = view.findViewById(R.id.home_date);
        tv_post_description = view.findViewById(R.id.description);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        fab_up = view.findViewById(R.id.fab_up);

        //Inicializar adaptador
        adapter = new HomeAdapter(getContext(), photoList);

        //Inicializar recyclerView
        recyclerView = view.findViewById(R.id.homeRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        //Inicializar la lista de fotos
        photoList = new ArrayList<>();

        // Inicializar base de datos sqlite
        db = new UserDataSQLite(getContext());

        //Método para obtener los posts del usuario desde la base de datos
        getUserPosts();

        //Actualizar la lista de fotos al actualizar la vista
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Actualizar la lista de posts
                photoList.clear();
                getUserPosts();
                swipeRefreshLayout.setRefreshing(false);
                /*getUserPosts();
                adapter.notifyDataSetChanged();
                adapter.notifyItemChanged(4);
                adapter.notifyItemInserted(4);
                swipeRefreshLayout.setRefreshing(false);*/
            }
        });

        //Boton flotante para desplazarse hacia la parte superior de la pantalla
        fab_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.fling(0, 1000);
                recyclerView.scrollToPosition(0);
            }
        });

        return view;
    }

    public void getUserPosts() {
        //[Volley API]
        String webhostURL = "https://fotay.000webhostapp.com/fetchDataHome.php";
        JsonObjectRequest JSONRequest = new JsonObjectRequest(Request.Method.GET, webhostURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("home_posts");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject post = jsonArray.getJSONObject(i);
                                //recuperar los datos de la tabla fotos
                                int foto_id = post.getInt("foto_id");
                                String usu_nombre = post.getString("usu_nombre");
                                String foto_fecha = post.getString("foto_fecha");
                                String foto_coment = post.getString("foto_coment");
                                String foto_ruta = post.getString("foto_ruta");

                                //recuperar imagen de perfil de la tabla usuarios
                                String foto_perfil = post.getString("foto_perfil");

                                if (!usu_nombre.equalsIgnoreCase(getSessionUsername())) {
                                    //Agregar el objeto a la lista de objetos
                                    photoList.add(new PostPhoto(foto_id, usu_nombre, foto_fecha, foto_coment, foto_ruta, foto_perfil));
                                }
                            }
                            Toast.makeText(getContext(), "Cargando " + photoList.size() + " posts...", Toast.LENGTH_SHORT).show();
                            //RecyclerAdapter
                            adapter = new HomeAdapter(getContext(), photoList);
                            //Pasar la lista de objetos a la vista del RecyclerView
                            recyclerView.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getContext(), "Se ha perdido la conexion.\nIntentelo mas tarde.", Toast.LENGTH_SHORT).show();
                } else {
                    //Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
        );
        //Timeout de la petición
        JSONRequest.setRetryPolicy(new DefaultRetryPolicy(
                8000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(JSONRequest);
    }

    public String getSessionUsername() {
        HashMap<String, String> user_sqlite = db.getUserInfo();
        //Integer idUsu = Integer.parseInt(Objects.requireNonNull(user_sqlite.get("usu_id")));
        String nomUsu = user_sqlite.get("usu_nombre");
        return nomUsu;
    }

}