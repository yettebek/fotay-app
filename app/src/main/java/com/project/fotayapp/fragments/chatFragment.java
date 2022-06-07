package com.project.fotayapp.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
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
import com.google.android.material.snackbar.Snackbar;
import com.project.fotayapp.R;
import com.project.fotayapp.activities.ChatActivity;
import com.project.fotayapp.adapters.UserChatAdapter;
import com.project.fotayapp.models.Chat;
import com.project.fotayapp.models.ChatUser;
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
    private ArrayList<ChatUser> chatUsersList = new ArrayList<>();
    public UserDataSQLite db;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        //Inicializar variables
        recyclerView = view.findViewById(R.id.chat_recycler_view);
        swipeRefreshLayout = view.findViewById(R.id.chat_swipe_refresh);

        // Inicializar base de datos sqlite
        db = new UserDataSQLite(getContext());

        // Inicializar lista de usuarios
        chatUsersList = new ArrayList<>();

        //Inicializar adaptador
        userChatAdapter = new UserChatAdapter(getContext(), chatUsersList);


        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(userChatAdapter);

        //Dirigirse al chat de la persona seleccionada
        recyclerView.addOnItemTouchListener(new UserChatAdapter.RecyclerTouchListener(getContext(), recyclerView, new UserChatAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                ChatUser chatUser = chatUsersList.get(position);
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("ChatUserProfilePhoto", chatUser.getUserPhoto());
                intent.putExtra("ChatUserName", chatUser.getUserName());
                intent.putExtra("ChatUserId", chatUser.getUserChatId());
                startActivity(intent);
                Toast.makeText(getActivity(), chatUser.getUserName() + " is selected!\nID receptor: " + chatUser.getUserChatId(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        //Inicializar lista de usuarios
        chatUsersList = new ArrayList<>();

        //Método para obtener los posts del usuario desde la base de datos
        getUsersInfo();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(requireContext(), "Actualizando...", Toast.LENGTH_SHORT).show();
                chatUsersList.clear();
                getUsersInfo();
                swipeRefreshLayout.setRefreshing(false);

            }
        });


        return view;
    }

    //Método par actualizar el contador de mensajes y el último mensaje
    public void updateChatUserContent(String userId, Chat chat) {

        for (ChatUser chatUser : chatUsersList) {
            if (chatUser.getUserChatId().equals(userId)) {
                int index = chatUsersList.indexOf(chatUser);
                chatUser.setLastMessage(chat.getMensaje());
                chatUser.setCountMessages(chatUser.getCountMessages() + 1);
                chatUsersList.remove(index);
                chatUsersList.add(index, chatUser);
                break;
            }
        }
        userChatAdapter.notifyDataSetChanged();

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

                                String userChatId = post.getString("usu_id");
                                String foto_perfil = post.getString("foto_perfil");
                                String usu_nombre = post.getString("usu_nombre");

                                if (!usu_nombre.equalsIgnoreCase(getSessionUsername())) {
                                    //Agregar el objeto a la lista de objetos
                                    chatUsersList.add(new ChatUser(userChatId, foto_perfil, usu_nombre, "", 0));
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
                        userChatAdapter.notifyDataSetChanged();
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
                            chatUsersList.clear();
                            getUsersInfo();
                        }
                    }).setTextColor(Color.RED);
                    snackbar.show();
                } else {
                    getUsersInfo();
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