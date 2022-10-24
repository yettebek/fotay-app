package com.project.fotayapp.activities;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
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
    public UserDataSQLite db;
    private ArrayList<Chat> chatList;
    private ChatConversationAdapter chatAdapter;
    private String receiverName, receiverId, senderId, senderName;
    RelativeLayout relativeLayout;
    public final Handler handler = new Handler();
    public boolean stopThread = false;
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
        relativeLayout = findViewById(R.id.relativeLayout);
        db = new UserDataSQLite(getApplicationContext());

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

        chatAdapter = new ChatConversationAdapter(this, chatList, Integer.parseInt(senderId));

        try {
            rv_chat.setLayoutManager(new WrapContentLinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
            rv_chat.setItemAnimator(null);
            rv_chat.setAdapter(chatAdapter);
        }catch (IndexOutOfBoundsException e){
            e.printStackTrace();
        }


        //Enviar mensaje al receptor
        iv_chat_btn_send.setOnClickListener(v -> {

            addMessageToChat();

        });

        chatToolbar.setNavigationOnClickListener(v -> {
            stopThread = true;
            finish();
        });



        //Obtener conversacion del receptor periodicamente
        showMessagesFromChat();

    }
    //Detectar si se presiona la tecla atras y detener el hilo
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            stopThread = true;
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    //Actualizar la lista de mensajes del chat
    private void refreshMessages() {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() throws IndexOutOfBoundsException{
                chatList.clear();
                showMessagesFromChat();
            }
        };
        handler.postDelayed(runnable, 6000);

        if (stopThread) {
            handler.removeCallbacks(runnable);
        }
    }


    private void addMessageToChat() {
        //Mensaje del emisor
        final String senderMessage = et_chat_message.getText().toString();
        if (!senderMessage.isEmpty()) {
            //Obtener la fecha actual
            Date date = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            String dateComment = simpleDateFormat.format(date);

            //Borrar el contenido del editText despues de enviar el comentario
            et_chat_message.setText("");
            et_chat_message.clearFocus();

            String webhostURL = "https://fotay.000webhostapp.com/insertToChat.php";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, webhostURL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (response.equals("Exito")) {

                        //Actualizar la lista de comentarios al añadir uno nuevo en la base de datos
                        Chat chat = new Chat(Integer.parseInt(senderId), senderName, receiverName, dateComment, senderMessage);

                        chatList.add(chat);

                        //Actualizar el recyclerView
                        chatAdapter.notifyItemInserted(chatList.size() - 1);

                        //Scroll hacia abajo de la vista de comentarios para ver el nuevo comentario
                        if (chatAdapter.getItemCount() > 1) {
                            rv_chat.getLayoutManager().smoothScrollToPosition(rv_chat, null, chatAdapter.getItemCount() - 1);
                            chatAdapter.notifyItemInserted(chatList.size() - 1);
                        }

                        //Cerrar teclado virtual al enviar comentario
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(et_chat_message.getWindowToken(), 0);

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
        }
    }

    private void showMessagesFromChat() {
        String webhostURL = "https://fotay.000webhostapp.com/showMessagesFromChat.php?emisor=" + senderName + "&receptor=" + receiverName;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, webhostURL, null, new Response.Listener<JSONObject>() {
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

                        //chatList.add(chat);
                        chatList.add(chat);
                        chatAdapter.notifyItemChanged(chatAdapter.getItemCount(), chatList.size());

                    }

                    if (chatAdapter.getItemCount() > 1) {
                        rv_chat.getLayoutManager().smoothScrollToPosition(rv_chat, null, chatAdapter.getItemCount() - 1);
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
                            .make(findViewById(R.id.relativeLayout), "Error de conexión", Snackbar.LENGTH_LONG);
                    snackbar.setAction("REINTENTAR", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showMessagesFromChat();
                        }
                    }).setTextColor(Color.RED);
                    snackbar.show();
                } else {
                    showMessagesFromChat();
                }
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);

        refreshMessages();

    }

    public class WrapContentLinearLayoutManager extends LinearLayoutManager {
        public WrapContentLinearLayoutManager(Context context) {
            super(context);
        }

        //...other constructors
        public WrapContentLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }
        public WrapContentLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            try {
                super.onLayoutChildren(recycler, state);
            } catch (IndexOutOfBoundsException e) {
                Log.e("IOOE", "IndexOutOfBoundsException");
            }
        }

        @Override
        public boolean supportsPredictiveItemAnimations() {
            return false;
        }
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
