package com.project.fotayapp.adapters;

import static com.project.fotayapp.adapters.PostProfileAdapter.EXTRA_PHOTO;
import static com.project.fotayapp.adapters.PostProfileAdapter.EXTRA_PROFILE_PHOTO;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hendraanggrian.appcompat.widget.SocialTextView;
import com.project.fotayapp.R;
import com.project.fotayapp.activities.CommentsActivity;
import com.project.fotayapp.activities.FullScreenImageActivity;
import com.project.fotayapp.models.PostPhoto;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    //Variables
    private final Context hContext;
    private ArrayList<PostPhoto> hAdapterList = new ArrayList<PostPhoto>();
    public static final String HOME_ADAPTER = "HomeAdapter";

    //Constructor
    public HomeAdapter(Context homeContext, ArrayList<PostPhoto> homePhotosAdapterList) {
        this.hContext = homeContext;
        this.hAdapterList = homePhotosAdapterList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(hContext).inflate(R.layout.layout_home_photo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //Obtenemos la posición de cada objeto en la lista
        PostPhoto postPhoto = hAdapterList.get(position);


        //Si no hay imagen de perfil, se carga la imagen por defecto
        if (postPhoto.getFoto_perfil().equalsIgnoreCase("null")) {
            //Imagen de perfil por defecto
            //holder.iv_profile_pic.setImageResource(R.drawable.ic_no_profile_picture);
            Picasso.get().load(postPhoto.getFoto_perfil()).placeholder(R.drawable.ic_no_profile_picture).into(holder.iv_profile_pic);
        } else {
            Picasso.get().load(postPhoto.getFoto_perfil()).fit().centerInside().into(holder.iv_profile_pic);
        }
        //Cargar el nombre de usuario
        String userName = postPhoto.getUsu_nombre();
        //Cargar la feche de publicación
        String date = postPhoto.getFoto_fecha();
        //Cargar la imagen en el ImageView con Picasso
        Picasso.get().load(postPhoto.getFoto_ruta()).fit().centerInside().into(holder.iv_post_photo);
        //Cargar la descripción
        String description = postPhoto.getFoto_coment();

        holder.tv_username.setText(userName);
        holder.tv_post_date.setText(date);
        holder.tv_post_description.setText(description);

        //Acceder a la imagen en pantalla completa
        holder.iv_post_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(hContext, FullScreenImageActivity.class);
                intent.putExtra(EXTRA_PHOTO, postPhoto.getFoto_ruta());
                hContext.startActivity(intent);
            }
        });
        //Acceder a la actividad de comentarios
        holder.tv_post_comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id_photo = holder.getAbsoluteAdapterPosition();
                Intent intent = new Intent(hContext, CommentsActivity.class);
                intent.putExtra("Class", "HomeAdapter");
                intent.putExtra(HOME_ADAPTER, id_photo);
                intent.putExtra(EXTRA_PROFILE_PHOTO, postPhoto.getFoto_perfil());
                intent.putExtra(EXTRA_PHOTO, postPhoto.getFoto_ruta());
                intent.setClass(hContext, CommentsActivity.class);
                hContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return hAdapterList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        //Variables
        public ImageView iv_profile_pic, iv_post_photo, iv_comment, iv_more;
        public TextView tv_username, tv_post_date, tv_post_comments;
        public SocialTextView tv_post_description;

        public ViewHolder(View itemView) {
            super(itemView);

            //Inicializar las variables
            this.iv_profile_pic = itemView.findViewById(R.id.home_profile_image);
            this.iv_post_photo = itemView.findViewById(R.id.home_photo);
            //this.iv_like = itemView.findViewById(R.id.iv_like);
            this.iv_comment = itemView.findViewById(R.id.iv_comment);
            this.tv_username = itemView.findViewById(R.id.home_username);
            this.tv_post_date = itemView.findViewById(R.id.home_date);
            //this.tv_post_likes = itemView.findViewById(R.id.tv_likes);
            this.tv_post_comments = itemView.findViewById(R.id.tv_comments);
            this.tv_post_description = itemView.findViewById(R.id.description);
        }
    }
}
