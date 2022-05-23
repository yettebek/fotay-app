package com.project.fotayapp.activities;

import static com.project.fotayapp.adapters.PostProfileAdapter.EXTRA_PHOTO;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.ortiz.touchview.TouchImageView;
import com.project.fotayapp.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class FullScreenImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_image);

        TouchImageView imageView = findViewById(R.id.image);
        String photo = getIntent().getStringExtra(EXTRA_PHOTO);
        Picasso.get().load(photo).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                imageView.setImageBitmap(bitmap);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                imageView.setImageDrawable(placeHolderDrawable);
            }
        });

    }
}