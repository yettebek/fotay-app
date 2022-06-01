package com.project.fotayapp.activities;

import static com.project.fotayapp.adapters.UserChatAdapter.EXTRA_PROFILE_PHOTO_U;
import static com.project.fotayapp.adapters.UserChatAdapter.EXTRA_USER_NAME_U;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    //Variables
    private ImageView iv_user_photo, iv_chat_btn_send;
    private EditText et_chat_message;
    private TextView tv_user_name;
    private Toolbar chatToolbar;
    private RecyclerView rv_chat;
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
        rv_chat = findViewById(R.id.chat_recyclerView);

        //Inicializar adaptador
        chatAdapter = new ChatConversationAdapter(getApplicationContext(), chatList /*getSessionId()*/);

        db = new UserDataSQLite(this);

        //Recibir datos del intent proveniente de UserChatAdapter
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        String userPhoto = bundle.getString(EXTRA_PROFILE_PHOTO_U);
        //Nombre del receptor
        String receiver = bundle.getString(EXTRA_USER_NAME_U);

        //Setear toolbar
        setSupportActionBar(chatToolbar);
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>" + receiver + "</font>"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Picasso.get().load(userPhoto).fit().centerCrop().into(iv_user_photo);
        tv_user_name.setText(receiver);

        iv_chat_btn_send.setOnClickListener(v -> {
            //Mensaje del emisor
            String chatMessage = et_chat_message.getText().toString().trim();
            if (!chatMessage.isEmpty()) {
                //Id del emisor
                getSessionId();
                //Nombre del emisor
                getSessionUsername();

                addToChat(getSessionId(), getSessionUsername(), receiver, chatMessage);

                //Borrar el contenido del editText despues de enviar el comentario
                et_chat_message.setText("");
                et_chat_message.clearFocus();

                //Cerrar teclado virtual al enviar comentario
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(et_chat_message.getWindowToken(), 0);

                Toast.makeText(this, chatMessage, Toast.LENGTH_SHORT).show();

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

    private void addToChat(String sessionId, String sessionUsername, String receiver, String message) {
        String INSERT_CHAT_URL = "https://fotay.000webhostapp.com/insertToChat.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, INSERT_CHAT_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("Exito")) {
                    Toast.makeText(getApplicationContext(), "Comentario añadido", Toast.LENGTH_SHORT).show();

                    //Actualizar la lista de comentarios al añadir uno nuevo en la base de datos
                    chatList.add(new Chat(1, sessionUsername, receiver,"", message));
                    chatAdapter.notifyDataSetChanged();

                    //Scroll hacia abajo de la vista de comentarios para ver el nuevo comentario
                    rv_chat.fling(0, 1000);
                    rv_chat.smoothScrollToPosition(chatList.size() - 1);

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
