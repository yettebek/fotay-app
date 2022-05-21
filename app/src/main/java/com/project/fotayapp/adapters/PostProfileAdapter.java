package com.project.fotayapp.adapters;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

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
    private OnItemClickListener mListener; //Listener del adapter
    final PostProfileAdapter.OnItemClickListener listener; //Listener para el click de cada foto de post
    static PostPhoto post;

    public static final String EXTRA_PHOTO = "com.project.fotayapp.extra.PHOTO";
    public static final String EXTRA_ID_PHOTO = "com.project.fotayapp.extra.ID_PHOTO";
    public static final String EXTRA_USERNAME = "com.project.fotayapp.extra.USERNAME";
    public static final String EXTRA_DATE = "com.project.fotayapp.extra.DATE";
    public static final String EXTRA_DESCRIPTION = "com.project.fotayapp.extra.DESCRIPTION";


    //RecyclerView de la otra actividad (PostActivity)
    RecyclerView recyclerViewPost;

    //Interfaz para el manejo de eventos de click del adapter
    public interface OnItemClickListener {
        void onItemClick(PostPhoto post); //Objeto que se pasará al listener
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    //Constructor
    public PostProfileAdapter(Context adapterContext, ArrayList<PostPhoto> item, PostProfileAdapter.OnItemClickListener listener) {
        this.adapterContext = adapterContext;
        this.postPhotoAdapterList = item;
        this.listener = listener;
    }

    //Infla el layout del item de la lista
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_grid_photo, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);


        //Listener para el click de cada foto de post
        viewHolder.iv_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getAbsoluteAdapterPosition();

                Intent intent = new Intent(adapterContext, PostActivity.class);
                post = postPhotoAdapterList.get(viewHolder.getAbsoluteAdapterPosition());

                intent.putExtra(EXTRA_PHOTO, post.getFoto_ruta());
                intent.putExtra(EXTRA_ID_PHOTO, position);
                adapterContext.startActivity(intent);

            }
        });
        return viewHolder;
    }

    //Método para cargar las fotos de post en el RecyclerView
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        //Obtener posición del elemento
        post = postPhotoAdapterList.get(position);
        //Cargar la imagen en el ImageView con Picasso
        Picasso.get().load(post.getFoto_ruta()).fit().centerInside().into(viewHolder.iv_photo);
    }

    //Método para obtener el número de elementos de la lista
    @Override
    public int getItemCount() {
        return postPhotoAdapterList.size();
    }

    //Clase ViewHolder para el RecyclerView
    public class ViewHolder extends RecyclerView.ViewHolder {
        //Variables
        ImageView iv_photo;
        RelativeLayout rl_photo;

        //Constructor
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //Inicializamos la foto en el ViewHolder
            this.iv_photo = itemView.findViewById(R.id.iv_card_photo);
            rl_photo = (RelativeLayout) itemView.findViewById(R.id.rl_grid_photo);

        }
    }
}