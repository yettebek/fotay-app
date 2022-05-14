package com.project.fotayapp.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.fotayapp.R;
import com.project.fotayapp.models.PostPhoto;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class PostProfileAdapter extends RecyclerView.Adapter<PostProfileAdapter.ViewHolder> {

    //Variables
    final Context adapterContext; //Contexto del adapter
    private ArrayList<PostPhoto> postPhotoAdapterList = new ArrayList<PostPhoto>(); //Lista de fotos de post
    final PostProfileAdapter.OnItemClickListener listener; //Listener para el click de cada foto de post

    //Interfaz para el manejo de eventos de click del adapter
    public interface OnItemClickListener {
        void onItemClick(PostPhoto post); //Objeto que se pasará al listener
    }

    //Constructor
    public PostProfileAdapter(Context adapterContext, ArrayList<PostPhoto> postPhotoAdapterList, PostProfileAdapter.OnItemClickListener listener) {
        this.adapterContext = adapterContext;
        this.postPhotoAdapterList = postPhotoAdapterList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_grid_photo, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.bindData(postPhotoAdapterList.get(position));

        PostPhoto post = postPhotoAdapterList.get(position);
        //Cargar la imagen en el ImageView con Picasso
        Picasso.get().load(post.getFoto_ruta()).fit().centerInside().into(viewHolder.iv_photo);
    }

    @Override
    public int getItemCount() {
        return postPhotoAdapterList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private static final String EXTRA_PROFILE_PICTURE = "com.project.fotayapp.extra.PROFILE_PICTURE";
        private static final String EXTRA_USERNAME = "com.project.fotayapp.extra.USERNAME";
        private static final String EXTRA_DATE = "com.project.fotayapp.extra.DATE";
        private static final String EXTRA_PHOTO = "com.project.fotayapp.extra.PHOTO";
        private static final String EXTRA_DESCRIPTION = "com.project.fotayapp.extra.DESCRIPTION";
        //Variables
        ImageView iv_photo;

        //Constructor
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //Inicializamos la foto en el ViewHolder
            this.iv_photo = itemView.findViewById(R.id.iv_card_photo);

            //Añaadir el listener al pulsar la imagen
        }

        //Método bindData para enviar los datos del elemento seleccionado
        void bindData(final PostPhoto post) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(post);
                }
            });
        }

    }
}