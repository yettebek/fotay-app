package com.project.fotayapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.hendraanggrian.appcompat.widget.SocialTextView;
import com.project.fotayapp.R;
import com.project.fotayapp.adapters.HomeAdapter;
import com.project.fotayapp.models.PostPhoto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


@SuppressWarnings("CommentedOutCode")
public class homeFragment extends Fragment {

    //Declarar variables
    private ImageView iv_profile_pic, iv_post_photo;
    private TextView tv_username, tv_post_date, tv_post_likes, tv_post_comments;
    private SocialTextView tv_post_description;

    private RecyclerView recyclerView;
    private HomeAdapter adapter;
    private ArrayList<PostPhoto> photoList = new ArrayList<PostPhoto>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        iv_profile_pic = view.findViewById(R.id.home_profile_image);
        iv_post_photo = view.findViewById(R.id.home_photo);
        tv_username = view.findViewById(R.id.home_username);
        tv_post_date = view.findViewById(R.id.home_date);
        tv_post_description = view.findViewById(R.id.description);

        //Inicializar adaptador
        adapter = new HomeAdapter(getContext(), photoList);

        //Inicializar recyclerView
        recyclerView = view.findViewById(R.id.homeRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        //Inicializar la lista de fotos
        photoList = new ArrayList<>();

        //Método para obtener los posts del usuario desde la base de datos
        getUserPosts();


        return view;
    }

    private void getUserPosts() {
        //[Volley API]
        String webhostURL = "https://fotay.000webhostapp.com/fetchDataHome.php";
        JsonObjectRequest JSONRequest = new JsonObjectRequest(Request.Method.GET, webhostURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Toast.makeText(getContext(), "Cantidad de posts: " + response.length(), Toast.LENGTH_LONG).show();
                        try {
                            JSONArray jsonArray = response.getJSONArray("posts");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject post = jsonArray.getJSONObject(i);
                                //recuperar los datos de la tabla fotos
                                String usu_nombre = post.getString("usu_nombre");
                                String foto_fecha = post.getString("foto_fecha");
                                String foto_coment = post.getString("foto_coment");
                                String foto_ruta = post.getString("foto_ruta");

                                //recuperar imagen de perfil de la tabla usuarios
                                String foto_perfil = post.getString("foto_perfil");

                                //Agregar el objeto a la lista de objetos
                                photoList.add(new PostPhoto(usu_nombre, foto_fecha, foto_coment, foto_ruta, foto_perfil));

                                //Agregar a sqlite
                                //db.addUserTableFotos(usu_nombre, foto_fecha, foto_coment, foto_ruta);
                            }

                            //RecyclerAdapter
                            adapter = new HomeAdapter(getContext(), photoList);
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
                //Toast.makeText(getContext(), error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        }
        );/* {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<String, String>();
                param.put("usu_nombre", nomUsu);
                return param;
            }
        };*/
        //Timeout de la petición
        JSONRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(JSONRequest);
    }
}