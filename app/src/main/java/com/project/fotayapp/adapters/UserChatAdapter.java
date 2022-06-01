package com.project.fotayapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.fotayapp.R;
import com.project.fotayapp.activities.ChatActivity;
import com.project.fotayapp.models.Comment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
//adaptador de lista de usuarios
public class UserChatAdapter extends RecyclerView.Adapter<UserChatAdapter.ViewHolder> {


    //Variables
    private Context chatContext;
    private ArrayList<Comment> chatAdapterList = new ArrayList<>();
    public static final String EXTRA_PROFILE_PHOTO_U= "com.project.fotayapp.PROFILE_PHOTO";
    public static final String EXTRA_USER_NAME_U = "com.project.fotayapp.EXTRA_USER_NAME";

    //Constructor
    public UserChatAdapter(Context chatContext, ArrayList<Comment> chatAdapterList) {
        this.chatContext = chatContext;
        this.chatAdapterList = chatAdapterList;
    }

    @NonNull
    @Override
    public UserChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_user_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserChatAdapter.ViewHolder holder, int position) {
        Comment comment = chatAdapterList.get(position);

        //Si no hay imagen de perfil, se carga la imagen por defecto
        if (comment.getCommentPhoto().equalsIgnoreCase("null")) {
            //Imagen de perfil por defecto
            Picasso.get().load(comment.getCommentPhoto()).placeholder(R.drawable.ic_no_profile_picture).into(holder.iv_user_photo);
        } else {
            Picasso.get().load(comment.getCommentPhoto()).fit().centerInside().into(holder.iv_user_photo);
        }
        //Cargar el nombre de usuario
        String userName = comment.getCommentUser();
        holder.tv_user_name.setText(userName);

        //Abrir la actividad del chat
        holder.iv_user_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Abrir la actividad del chat
                Toast.makeText(chatContext, userName, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(chatContext, ChatActivity.class);
                intent.putExtra(EXTRA_PROFILE_PHOTO_U, comment.getCommentPhoto());
                intent.putExtra(EXTRA_USER_NAME_U, userName);
                chatContext.startActivity(intent);
            }
        });

        holder.tv_user_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Abrir la actividad del chat
                Toast.makeText(chatContext, userName, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(chatContext, ChatActivity.class);
                intent.putExtra(EXTRA_PROFILE_PHOTO_U, comment.getCommentPhoto());
                intent.putExtra(EXTRA_USER_NAME_U, userName);
                chatContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return chatAdapterList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        //Variables
        public ImageView iv_user_photo;
        public TextView tv_user_name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_user_photo = itemView.findViewById(R.id.chat_profile_image);
            tv_user_name = itemView.findViewById(R.id.chat_username);

        }
    }
}
