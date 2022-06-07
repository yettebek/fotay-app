package com.project.fotayapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.fotayapp.R;
import com.project.fotayapp.models.Comment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    //Variables
    private final Context comContext;
    private ArrayList<Comment> commentAdapterList = new ArrayList<Comment>();
    private static CommentAdapter cInstance;

    //Interfaz para comunicar con el fragment
    public interface AdapterCallback {
        public void onChangeCommentCount();
        //public void onChangeCommentCount(int position);
    }
    public CommentAdapter(Context comContext, ArrayList<Comment> commentList) {
        this.comContext = comContext;
        this.commentAdapterList = commentList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View commentView = LayoutInflater.from(comContext).inflate(R.layout.comment_card, parent, false);
        cInstance = this;
        return new ViewHolder(commentView);
    }

    public static synchronized CommentAdapter getInstance() {
        return cInstance;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //Obtenemos la posición de cada objeto en la lista
        Comment comment = commentAdapterList.get(position);

        if (!comment.getCommentPhoto().equalsIgnoreCase("null")) {
            //Imagen de perfil del usuario
            Picasso.get().load(comment.getCommentPhoto()).fit().centerInside().into(holder.commentPhoto);
        }
        //Si no hay imagen de perfil, se carga la imagen por defecto
        Picasso.get().load(comment.getCommentPhoto()).placeholder(R.drawable.ic_no_profile_picture).into(holder.commentPhoto);

        //Toast.makeText(comContext, "Nº de comentarios: " + getItemCount(), Toast.LENGTH_SHORT).show();

        holder.commentUser.setText(comment.getCommentUser());
        holder.commentDate.setText(comment.getCommentDate());
        holder.commentText.setText(comment.getCommentText());

    }


    @Override
    public int getItemCount() {
        if (commentAdapterList != null) {
            return commentAdapterList.size();
        }
        return 0;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        //Variables
        ImageView commentPhoto;
        TextView commentUser, commentDate, commentText;

        public ViewHolder(View itemView) {
            super(itemView);

            //Asignar variables
            commentPhoto = itemView.findViewById(R.id.iv_comment_profile_image);
            commentUser = itemView.findViewById(R.id.comment_username);
            commentDate = itemView.findViewById(R.id.comment_date);
            commentText = itemView.findViewById(R.id.comment_text);

        }
    }
}
