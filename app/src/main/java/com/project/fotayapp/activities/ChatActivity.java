package com.project.fotayapp.activities;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.project.fotayapp.R;
import com.project.fotayapp.adapters.ChatConversationAdapter;
import com.project.fotayapp.models.Chat;
import com.project.fotayapp.models.UserDataSQLite;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    //Variables
    private ImageView iv_user_photo, iv_chat_btn_send;
    private EditText et_chat_message;
    private TextView tv_user_name;
    private Toolbar chatToolbar;
    private RecyclerView rv_chat;
    private RecyclerView.LayoutManager layoutManager;
    //private RecyclerView.Adapter chatAdapter;
    public UserDataSQLite db;
    private ArrayList<Chat> chatList;
    private ChatConversationAdapter chatAdapter;
    private String receiverName, receiverId, senderId,senderName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //Inicializar variables
        iv_user_photo = findViewById(R.id.chat_profile_image);
        tv_user_name = findViewById(R.id.chat_username);
        chatToolbar = findViewById(R.id.chat_toolbar);
        et_chat_message = findViewById(R.id.chat_edit);
        iv_chat_btn_send = findViewById(R.id.chat_send);

        db = new UserDataSQLite(getApplicationContext());
        //db = new UserDataSQLite(this);

        //Recibir datos del intent proveniente de chatFragment
        Bundle bundle = getIntent().getExtras();

        String userPhoto = bundle.getString("ChatUserProfilePhoto");
        Picasso.get().load(userPhoto).placeholder(R.drawable.ic_no_profile_picture).into(iv_user_photo);

        //Nombre del receptor
        receiverName = bundle.getString("ChatUserName");
        tv_user_name.setText(receiverName);

        //Id del receptor (Nos sirve para identificar los mensajes que recibimos)
        receiverId = bundle.getString("ChatUserId");

        if (receiverName == null) {
            Toast.makeText(getApplicationContext(), "Error al recibir datos del receptor", Toast.LENGTH_SHORT).show();
            finish();
        }

        //Setear toolbar
        setSupportActionBar(chatToolbar);
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>" + receiverName + "</font>"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Inicializar recyclerView
        rv_chat = (RecyclerView) findViewById(R.id.chat_recyclerView);

        //Inicializar lista de chat
        chatList = new ArrayList<>();

        //Id del emisor
         senderId = getSessionId();
        //Nombre del emisor
         senderName = getSessionUsername();

        Toast.makeText(getApplicationContext(), "Id del emisor: " + senderId + "\nNombre del emisor: " + senderName, Toast.LENGTH_SHORT).show();

        chatAdapter = new ChatConversationAdapter(this, chatList, Integer.parseInt(senderId));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rv_chat.setLayoutManager(linearLayoutManager);
        rv_chat.setItemAnimator(new DefaultItemAnimator());
        rv_chat.setAdapter(chatAdapter);

        //Enviar mensaje al receptor
        iv_chat_btn_send.setOnClickListener(v -> {

            addMessageToChat();

        });

        chatToolbar.setNavigationOnClickListener(v -> {
            finish();
        });

        //Obtener conversacion del receptor
        showMessagesFromChat();
    }



    private void addMessageToChat() {
        //Mensaje del emisor
        final String senderMessage = et_chat_message.getText().toString();
        if (!senderMessage.isEmpty()) {
            //Obtener la fecha actual
            Date date = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String dateComment = simpleDateFormat.format(date);

            //Borrar el contenido del editText despues de enviar el comentario
            et_chat_message.setText("");
            et_chat_message.clearFocus();

            String INSERT_CHAT_URL = "https://fotay.000webhostapp.com/insertToChat.php";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, INSERT_CHAT_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (response.equals("Exito")) {
                        Toast.makeText(getApplicationContext(), "Comentario añadido", Toast.LENGTH_SHORT).show();

                        //Actualizar la lista de comentarios al añadir uno nuevo en la base de datos
                        Chat chat = new Chat(Integer.parseInt(senderId), senderName, receiverName, dateComment,senderMessage);
                        chatList.add(chat);
                        //chatAdapter.notifyItemInserted(chatList.size());
                        chatAdapter.notifyDataSetChanged();

                        if (chatAdapter.getItemCount() > 1) {
                            rv_chat.getLayoutManager().smoothScrollToPosition(rv_chat, null, chatAdapter.getItemCount() - 1);
                            //Scroll hacia abajo de la vista de comentarios para ver el nuevo comentario
                            //rv_chat.fling(0, 1000);
                            //rv_chat.smoothScrollToPosition(chatList.size() - 1);
                        }

                        //Cerrar teclado virtual al enviar comentario
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(et_chat_message.getWindowToken(), 0);

                        Toast.makeText(ChatActivity.this, senderMessage, Toast.LENGTH_SHORT).show();

                    } else if (response.equals("Error")) {
                        Toast.makeText(getApplicationContext(), "Error al añadir comentario", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Error con la B.D", Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("usu_id", senderId);
                    params.put("emisor", senderName);
                    params.put("receptor", receiverName);
                    params.put("mensaje", senderMessage);
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);


        } else {
            Toast.makeText(this, "No puedes enviar un comentario vacío", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, senderId, Toast.LENGTH_SHORT).show();
            Toast.makeText(this, senderName, Toast.LENGTH_SHORT).show();
        }
    }

    private void showMessagesFromChat() {
        String SHOW_MESSAGES_URL = "https://fotay.000webhostapp.com/showToChat.php?emisor=" + senderName + "&receptor=" + receiverName;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, SHOW_MESSAGES_URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("chat_messages");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        int usu_id = jsonObject.getInt("usu_id");
                        String sender = jsonObject.getString("emisor");
                        String receiver = jsonObject.getString("receptor");
                        String date = jsonObject.getString("fecha_mensaje");
                        String message = jsonObject.getString("mensaje");

                        Chat chat = new Chat(usu_id, sender, receiver, date, message);

                        chatList.add(chat);

                    }
                    chatAdapter.notifyDataSetChanged();
                    if (chatAdapter.getItemCount() > 1) {
                        rv_chat.getLayoutManager().smoothScrollToPosition(rv_chat, null, chatAdapter.getItemCount() - 1);
                        //Scroll hacia abajo de la vista de comentarios para ver el nuevo comentario
                        //rv_chat.fling(0, 1000);
                        //rv_chat.smoothScrollToPosition(chatList.size() - 1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);

    }



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

}
