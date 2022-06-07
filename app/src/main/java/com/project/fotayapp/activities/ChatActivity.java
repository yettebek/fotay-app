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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.project.fotayapp.R;
import com.project.fotayapp.adapters.ChatConversationAdapter;
import com.project.fotayapp.models.Chat;
import com.project.fotayapp.models.UserDataSQLite;
import com.squareup.picasso.Picasso;

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
    private ArrayList<Chat> chatList = new ArrayList<>();
    private ChatConversationAdapter chatAdapter;


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

        //Recibir datos del intent proveniente de UserChatAdapter
        Bundle bundle = getIntent().getExtras();
        String userPhoto = bundle.getString("ChatUserProfilePhoto");
        //Nombre del receptor
        String receiver = bundle.getString("ChatUserName");
        //Id del receptor
        String receiverId = bundle.getString("ChatUserId");

        //Setear toolbar
        setSupportActionBar(chatToolbar);
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>" + receiver + "</font>"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        chatAdapter = new ChatConversationAdapter(getApplicationContext(), chatList, 1);

        //Inicializar recyclerView
        rv_chat = (RecyclerView) findViewById(R.id.chat_recyclerView);
        rv_chat.setHasFixedSize(true);
        rv_chat.setLayoutManager(new LinearLayoutManager(this));
        layoutManager = new LinearLayoutManager(this);
        rv_chat.setLayoutManager(layoutManager);
        rv_chat.setAdapter(chatAdapter);

        //Inicializar lista de chat
        chatList = new ArrayList<>();

        db = new UserDataSQLite(this);

        Picasso.get().load(userPhoto).placeholder(R.drawable.ic_no_profile_picture).into(iv_user_photo);

        tv_user_name.setText(receiver);

        iv_chat_btn_send.setOnClickListener(v -> {
            //Mensaje del emisor
            final String chatMessage = et_chat_message.getText().toString().trim();
            if (!chatMessage.isEmpty()) {
                //Id del emisor
                getSessionId();
                //Nombre del emisor
                getSessionUsername();
                //Obtener la fecha actual
                Date date = new Date();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                String dateComment = simpleDateFormat.format(date);

                addToChat(Integer.parseInt(getSessionId()), getSessionUsername(), receiver, dateComment, chatMessage);


            } else {
                Toast.makeText(this, "No puedes enviar un comentario vacío", Toast.LENGTH_SHORT).show();
                Toast.makeText(this, receiver, Toast.LENGTH_SHORT).show();
                Toast.makeText(this, getSessionUsername(), Toast.LENGTH_SHORT).show();
            }
        });

        chatToolbar.setNavigationOnClickListener(v -> {
            finish();
        });
    }

    private void addToChat(int sessionId, String sessionUsername, String receiver, String dateMessage, String message) {
        String INSERT_CHAT_URL = "https://fotay.000webhostapp.com/insertToChat.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, INSERT_CHAT_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("Exito")) {
                    Toast.makeText(getApplicationContext(), "Comentario añadido", Toast.LENGTH_SHORT).show();

                    //Actualizar la lista de comentarios al añadir uno nuevo en la base de datos
                    Chat chat = new Chat(Integer.parseInt("1"), "John", receiver, dateMessage, message);
                    chatList.add(chat);
                    chatAdapter.notifyItemInserted(chatList.size());
                    chatAdapter.notifyDataSetChanged();

                    //Borrar el contenido del editText despues de enviar el comentario
                    et_chat_message.setText("");
                    et_chat_message.clearFocus();

                    //Scroll hacia abajo de la vista de comentarios para ver el nuevo comentario
                    rv_chat.fling(0, 1000);
                    rv_chat.smoothScrollToPosition(chatList.size() - 1);

                    //Cerrar teclado virtual al enviar comentario
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(et_chat_message.getWindowToken(), 0);

                    Toast.makeText(ChatActivity.this, message, Toast.LENGTH_SHORT).show();

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
                params.put("usu_id", getSessionId());
                params.put("emisor", getSessionUsername());
                params.put("receptor", receiver);
                params.put("mensaje", message);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public String getSessionUsername() {
        HashMap<String, String> user_sqlite = db.getUserInfo();
        //Integer idUsu = Integer.parseInt(Objects.requireNonNull(user_sqlite.get("usu_id")));
        String nomUsu = user_sqlite.get("usu_nombre");
        return nomUsu;
    }

    public String getSessionId() {
        HashMap<String, String> user_sqlite = db.getUserInfo();
        String idUsu = user_sqlite.get("usu_id");
        return idUsu;
    }

}
