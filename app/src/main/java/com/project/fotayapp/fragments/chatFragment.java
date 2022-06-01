package com.project.fotayapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.project.fotayapp.R;
import com.project.fotayapp.adapters.UserChatAdapter;
import com.project.fotayapp.models.Comment;
import com.project.fotayapp.models.UserDataSQLite;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class chatFragment extends Fragment {

    //Declarar variables
    private SwipeRefreshLayout swipeRefreshLayout;
    private UserChatAdapter userChatAdapter;
    private RecyclerView recyclerView;
    private ArrayList<Comment> chatUsersList = new ArrayList<>();
    public UserDataSQLite db;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        // Inicializar base de datos sqlite
        db = new UserDataSQLite(getContext());

        //Inicializar adaptador
        userChatAdapter = new UserChatAdapter(getContext(), chatUsersList);

        //Inicializar variables
        recyclerView = view.findViewById(R.id.chat_recycler_view);
        swipeRefreshLayout = view.findViewById(R.id.chat_swipe_refresh);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(userChatAdapter);

        //Inicializar lista de usuarios
        chatUsersList = new ArrayList<>();

        //Método para obtener los posts del usuario desde la base de datos
        getUsersInfo();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                chatUsersList.clear();
                getUsersInfo();
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(requireContext(), "Actualizado", Toast.LENGTH_SHORT).show();
            }   //Fin onRefresh
        });



        return view;
    }

    private void getUsersInfo() {
        String webhostURL = "https://fotay.000webhostapp.com/showUsers.php";
        JsonObjectRequest JSONRequest = new JsonObjectRequest(Request.Method.GET, webhostURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("users");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject post = jsonArray.getJSONObject(i);

                                String foto_perfil = post.getString("foto_perfil");
                                String usu_nombre = post.getString("usu_nombre");

                                if (!usu_nombre.equalsIgnoreCase(getSessionUsername())) {
                                    //Agregar el objeto a la lista de objetos
                                    chatUsersList.add(new Comment(foto_perfil, usu_nombre, "", ""));
                                }
                            }
                            //RecyclerAdapter
                            userChatAdapter = new UserChatAdapter(getContext(), chatUsersList);
                            //Pasar la lista de objetos a la vista del RecyclerView
                            recyclerView.setAdapter(userChatAdapter);

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