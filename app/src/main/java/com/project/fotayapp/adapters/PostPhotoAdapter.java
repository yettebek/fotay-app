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


public class PostPhotoAdapter extends RecyclerView.Adapter<PostPhotoAdapter.ViewHolder> {

    //Variables
     Context adapterContext; //Contexto del adapter
     ArrayList<PostPhoto> postPhotoAdapterList = new ArrayList<PostPhoto>(); //Lista de fotos del post

    //Constructor
    public PostPhotoAdapter(Context adapterContext, ArrayList<PostPhoto> postPhotoAdapterList) {
        this.adapterContext = adapterContext;
        this.postPhotoAdapterList = postPhotoAdapterList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_grid_photo, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        PostPhoto postPhoto = postPhotoAdapterList.get(position);
        //Cargar la imagen en el ImageView con Picasso
        Picasso.get().load(postPhoto.getFoto_ruta()).fit().centerInside().into(viewHolder.iv_photo);

        //Añaadir el listener al pulsar la imagen
        /*viewHolder.iv_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(adapterContext, postPhoto.toString(), Toast.LENGTH_SHORT).show();
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return postPhotoAdapterList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        //Variables
        ImageView iv_photo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //Inicializamos la foto en el ViewHolder
            this.iv_photo = itemView.findViewById(R.id.iv_card_photo);
        }
    }
}