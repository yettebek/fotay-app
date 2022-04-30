package com.project.fotayapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.project.fotayapp.R;
import com.project.fotayapp.adapters.PostPhotoAdapter;
import com.project.fotayapp.models.PostPhoto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


@SuppressWarnings("CommentedOutCode")
public class homeFragment extends Fragment {

    //Declarar variables
    private RecyclerView recyclerView;
    private PostPhotoAdapter adapter;
    private ArrayList<PostPhoto> photoList = new ArrayList<PostPhoto>();
    private static final int NUM_COLUMNS = 2;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //Inicializar adaptador
        adapter = new PostPhotoAdapter(getContext(), photoList);

        recyclerView = view.findViewById(R.id.recyclerView);
        //GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        //declare a StaggeredGridLayoutManager with 2 columns
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(NUM_COLUMNS, LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        //Inicializar la lista de fotos
        photoList = new ArrayList<>();

        //Método para obtener los posts del usuario desde la base de datos
        getUserPosts();

        /*fab_pic = view.findViewById(R.id.fab_pic);

        fab_pic.setOnClickListener(v -> {

            //Abir UploadActivity para elegir la imagen a subir a la app:
            Intent uploadIntent = new Intent(getActivity(), UploadActivity.class);
            startActivity(uploadIntent);
        });*/
        return view;
    }

    /*@Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getUserPosts();

    }*/

    private void getUserPosts() {
        //[Volley API]
        String webhostURL = "https://fotay.000webhostapp.com/fetchDataHome.php";
        JsonArrayRequest JSONRequest = new JsonArrayRequest(Request.Method.GET, webhostURL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //Toast.makeText(getContext(), "Cantidad de posts: " + response.length(), Toast.LENGTH_LONG).show();
                        StringBuilder jsonResponse = new StringBuilder(" ");
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = (JSONObject) response.get(i);
                                /*String usu_nombre = jsonObject.getString("user");
                                String foto_fecha = jsonObject.getString("type");
                                String foto_coment = jsonObject.getString("tags");*/
                                String foto_ruta = jsonObject.getString("foto_ruta");

                                //Toast.makeText(getContext(), "Fotos: " + foto_ruta, Toast.LENGTH_SHORT).show();

                                jsonResponse.append("Foto nº").append(i + 1).append(": ").append(foto_ruta).append("\n\n");
                                //textView.setText(jsonResponse);

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
                            Toast.makeText(getContext(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                        //adapter.notifyDataSetChanged();
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
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(JSONRequest);
    }
}