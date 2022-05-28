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

    public CommentAdapter(Context comContext, ArrayList<Comment> commentList) {
        this.comContext = comContext;
        this.commentAdapterList = commentList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View commentView = LayoutInflater.from(comContext).inflate(R.layout.comment_card, parent, false);
        return new ViewHolder(commentView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //Obtenemos la posición de cada objeto en la lista
        Comment comment = commentAdapterList.get(position);

        //Cargamos los datos en el ViewHolder
        Picasso.get().load(comment.getCommentPhoto()).fit().centerInside().into(holder.commentPhoto);
        holder.commentUser.setText(comment.getCommentUser());
        holder.commentDate.setText(comment.getCommentDate());
        holder.commentText.setText(comment.getCommentText());
    }


    @Override
    public int getItemCount() {

        return commentAdapterList.size();
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
