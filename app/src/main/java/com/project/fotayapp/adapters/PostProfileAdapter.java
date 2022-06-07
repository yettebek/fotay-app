package com.project.fotayapp.adapters;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.fotayapp.R;
import com.project.fotayapp.activities.PostActivity;
import com.project.fotayapp.models.PostPhoto;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class PostProfileAdapter extends RecyclerView.Adapter<PostProfileAdapter.ViewHolder> {

    //Variables
    final Context adapterContext; //Contexto del adapter
    private final ArrayList<PostPhoto> postPhotoAdapterList; //Lista de fotos de post
    static PostPhoto post;
    private static PostProfileAdapter pInstance;

    public static final String EXTRA_PROFILE_PHOTO = "com.project.fotayapp.extra.PROFILE_PHOTO";
    public static final String EXTRA_USERNAME = "com.project.fotayapp.extra.USERNAME";
    public static final String EXTRA_DATE = "com.project.fotayapp.extra.DATE";
    public static final String EXTRA_PHOTO_POSITION = "com.project.fotayapp.extra.EXTRA_PHOTO_POSITION";
    public static final String EXTRA_ID_PHOTO = "com.project.fotayapp.extra.ID_PHOTO";
    public static final String EXTRA_PHOTO = "com.project.fotayapp.extra.PHOTO";
    public static final String EXTRA_DESCRIPTION = "com.project.fotayapp.extra.DESCRIPTION";
    public static final String EXTRA_COMMENT_COUNT = "com.project.fotayapp.extra.COMMENT_COUNT";
    private PostProfileAdapter.AdapterCallback callback;

    //Interfaz para comunicar con el fragment
    public interface AdapterCallback {
        //public void onChangeCommentCount();
        public void onChangeCommentCount(int position, int commentCount);
    }

    //Constructor
    public PostProfileAdapter(Context adapterContext, ArrayList<PostPhoto> item, PostProfileAdapter.AdapterCallback callback) {
        this.adapterContext = adapterContext;
        this.postPhotoAdapterList = item;
        this.callback = callback;
    }

    //Infla el layout del item de la lista
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_grid_photo, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        pInstance = this;
        return viewHolder;
    }

    public static synchronized PostProfileAdapter getInstance() {
        return pInstance;
    }

    //Método para cargar las fotos de post en el RecyclerView
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        //Obtener posición del elemento
        post = postPhotoAdapterList.get(position);

        //Cargar la imagen en el ImageView con Picasso
        Picasso.get().load(post.getFoto_ruta()).fit().centerInside().into(viewHolder.iv_photo);

        viewHolder.iv_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getAbsoluteAdapterPosition();
                Intent intent = new Intent(adapterContext, PostActivity.class);
                post = postPhotoAdapterList.get(viewHolder.getAbsoluteAdapterPosition());
                intent.putExtra(EXTRA_PROFILE_PHOTO, post.getFoto_perfil());
                intent.putExtra(EXTRA_USERNAME, post.getUsu_nombre());
                intent.putExtra(EXTRA_DATE, post.getFoto_fecha());
                intent.putExtra(EXTRA_PHOTO_POSITION, position);
                intent.putExtra(EXTRA_ID_PHOTO, post.getFoto_id());
                intent.putExtra(EXTRA_PHOTO, post.getFoto_ruta());
                intent.putExtra(EXTRA_COMMENT_COUNT, post.getTotal_comentarios());
                intent.putExtra(EXTRA_DESCRIPTION, post.getFoto_coment());
                adapterContext.startActivity(intent);
                //Toast que muestra la posición del elemento
                //Toast.makeText(adapterContext, "Foto nº: " + position, Toast.LENGTH_SHORT).show();
                Toast.makeText(adapterContext, "ID Foto: " + post.getFoto_id(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Método para eliminar una foto de post
    public void removeItem(int position) {
        postPhotoAdapterList.remove(position);
        notifyItemRemoved(position);
        //notifyDataSetChanged();
        notifyItemRangeChanged(position, postPhotoAdapterList.size()); //o getItemCount()
    }

    //Método para obtener el número de elementos de la lista
    @Override
    public int getItemCount() {
        return postPhotoAdapterList.size();
    }

    //Método para obtener el número de comentarios de una foto de post
    public int getCommentCount(int position) {
        notifyDataSetChanged();
        //Update the comment count
        //callback.onChangeCommentCount(position);
        return (postPhotoAdapterList.get(position).getTotal_comentarios());
    }

    //Clase ViewHolder para el RecyclerView
    public class ViewHolder extends RecyclerView.ViewHolder {
        //Variables
        public ImageView iv_photo;


        //Constructor
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //Inicializamos la foto en el ViewHolder
            this.iv_photo = itemView.findViewById(R.id.iv_card_photo);
        }
    }
}