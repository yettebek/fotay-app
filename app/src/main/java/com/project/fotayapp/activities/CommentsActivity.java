package com.project.fotayapp.activities;

import static android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;
import static com.project.fotayapp.activities.PostActivity.POST_ACTIVITY;
import static com.project.fotayapp.activities.PostActivity.tv_count_comments;
import static com.project.fotayapp.adapters.HomeAdapter.HOME_ADAPTER;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.project.fotayapp.R;
import com.project.fotayapp.adapters.CommentAdapter;
import com.project.fotayapp.fragments.homeFragment;
import com.project.fotayapp.fragments.profileFragment;
import com.project.fotayapp.models.Comment;
import com.project.fotayapp.models.UserDataSQLite;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class CommentsActivity extends AppCompatActivity {

    //Variables
    public profileFragment profileFragment;
    public homeFragment homeFragment;
    public PostActivity postActivity;
    public UserDataSQLite db;
    private Toolbar toolbar;
    private ImageView iv_send;
    private EditText et_comment;
    public static int id_photo, default_profile_photo, position;
    public static int comment_count;
    private RecyclerView commentRecyclerView;
    private CommentAdapter commentAdapter;
    public static ArrayList<Comment> commentList = new ArrayList<>();
    public static String foto_perfil;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coments);

        toolbar = findViewById(R.id.comment_toolbar);
        et_comment = findViewById(R.id.comment_edit);
        iv_send = findViewById(R.id.comment_send);
        commentRecyclerView = findViewById(R.id.comment_recyclerView);
        commentList = new ArrayList<>();

        commentAdapter = new CommentAdapter(getApplicationContext(), commentList);

        commentRecyclerView.setHasFixedSize(true);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentRecyclerView.setAdapter(commentAdapter);

        //Colocar el editText por encima del teclado s
        CommentsActivity.this.getWindow().setSoftInputMode(SOFT_INPUT_ADJUST_RESIZE);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(Html.fromHtml("<font color='#ffffff'>" + "Comentarios" + "</font>"));
        //Flecha de regreso en la barra de herramientas de la actividad
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setIcon(R.drawable.ic_back);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        profileFragment = new profileFragment();
        homeFragment = new homeFragment();
        postActivity = new PostActivity();
        db = new UserDataSQLite(this);


        //Método que muestra los comentarios de una foto en HomeAdapter
        showCommentsFromIntent();

        //Boton de enviar comentario en la actividad y enviarlo a la base de datos
        iv_send.setOnClickListener(v -> {
            String userComment = et_comment.getText().toString().trim();
            getSessionUsername();
            getSessionId();

            //Obtener la fecha actual
            Date date = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            String dateComment = simpleDateFormat.format(date);

            if (!userComment.isEmpty()) {
                //Si la publicación viene de la actividad de post (PostActivity) o del adapter (HomeAdapter)
                getFotoIdFromIntent(getSessionId(), getSessionUsername(), dateComment, userComment);

                //Borrar el contenido del editText despues de enviar el comentario
                et_comment.setText("");
                et_comment.clearFocus();

                //Cerrar teclado virtual al enviar comentario
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(et_comment.getWindowToken(), 0);

            } else {
                Toast.makeText(this, "No puedes enviar un comentario vacío", Toast.LENGTH_SHORT).show();
            }
        });

        //Boton de cerra la actividad y regresa a la actividad anterior
        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(CommentsActivity.this, MenuActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
    }

    public void getFotoIdFromIntent(String userId, String userName, String dateComment, String userComment) {
        String classIntent = getIntent().getStringExtra("Class");

        if (classIntent.equals("PostActivity")) {
            position = getIntent().getIntExtra(POST_ACTIVITY, 0);
            int IDPhotoPost = postActivity.id;

            //Método que añaade los comentarios a la base de datos
            addCommentToDB(IDPhotoPost, userId, userName, dateComment, userComment);

        } else if (classIntent.equals("HomeAdapter")) {
            id_photo = getIntent().getIntExtra(HOME_ADAPTER, 0);
            int IDPhotoHome = id_photo;

            //Método que añaade los comentarios a la base de datos
            addCommentToDB(IDPhotoHome, userId, userName, dateComment, userComment);
        }
    }

    private void addCommentToDB(int idPhoto, String userId, String userName, String dateComment, String userComment) {
        String webhostURL = "https://fotay.000webhostapp.com/insertComment.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, webhostURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("Exito")) {
                    Toast.makeText(getApplicationContext(), "Comentario añadido", Toast.LENGTH_SHORT).show();

                    //Cargar la imagen de perfil del usuario en el comentario
                    default_profile_photo = R.drawable.ic_no_profile_picture;

                    //Actualizar la lista de comentarios al añadir uno nuevo en la base de datos
                    commentList.add(new Comment(String.valueOf(default_profile_photo), userName, dateComment, userComment));
                    commentAdapter.notifyItemInserted(commentList.size());
                    commentAdapter.notifyDataSetChanged();

                    //Scroll hacia abajo de la vista de comentarios para ver el nuevo comentario
                    commentRecyclerView.fling(0, 1000);
                    commentRecyclerView.smoothScrollToPosition(commentList.size() - 1);

                    //Actualizar contador de comentarios en el PostActivity

                    tv_count_comments.setText(String.valueOf(commentList.size()));


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
                params.put("foto_id", String.valueOf(idPhoto));
                params.put("usu_id", userId);
                params.put("usu_nombre", userName);
                params.put("fecha_coment", dateComment);
                params.put("txt_coment", userComment);
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


    public void showCommentsFromIntent() {
        String classIntent = getIntent().getStringExtra("Class");

        if (classIntent.equals("PostActivity")) {
            //position = getIntent().getIntExtra(POST_ACTIVITY, 0);
            int IDPhotoPost = PostActivity.id;

            //Método que muestra los comentarios de una foto en PostActivity
            String webhostURL = "https://fotay.000webhostapp.com/showComments.php?foto_id=" + IDPhotoPost;
            showComments(webhostURL);

        } else if (classIntent.equals("HomeAdapter")) {
            id_photo = getIntent().getIntExtra(HOME_ADAPTER, 0);
            int IDPhotoHome = id_photo;

            //Método que muestra los comentarios de una foto en HomeAdapter
            String webhostURL = "https://fotay.000webhostapp.com/showComments.php?foto_id=" + IDPhotoHome;
            showComments(webhostURL);
        }
    }

    public void showComments(String SHOW_COMMENTS_URL) {
        //Voley api
        JsonObjectRequest JSONRequest = new JsonObjectRequest(Request.Method.GET, SHOW_COMMENTS_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("comments");
                            //Contador que muestra el número de comentarios
                            comment_count = jsonArray.length();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject post = jsonArray.getJSONObject(i);
                                //recuperar los datos de la tabla fotos
                                foto_perfil = post.getString("foto_perfil");
                                String usu_nombre = post.getString("usu_nombre");
                                String fecha_coment = post.getString("fecha_coment");
                                String txt_coment = post.getString("txt_coment");

                                //Agregar el objeto a la lista de objetos
                                commentList.add(new Comment(foto_perfil, usu_nombre, fecha_coment, txt_coment));
                            }

                            //RecyclerAdapter
                            commentAdapter = new CommentAdapter(getApplicationContext(), commentList);
                            //Pasar la lista de objetos a la vista del RecyclerView
                            commentRecyclerView.setAdapter(commentAdapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getApplicationContext(), "Se ha perdido la conexion.\nIntentelo mas tarde.", Toast.LENGTH_SHORT).show();
                }
            }
        }
        );
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(JSONRequest);

    }
}