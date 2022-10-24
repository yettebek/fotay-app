package com.project.fotayapp.adapters;

import android.content.Context;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.fotayapp.R;
import com.project.fotayapp.models.ChatUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

//adaptador de lista de usuarios
public class UserChatAdapter extends RecyclerView.Adapter<UserChatAdapter.ViewHolder> {

    /**
     * Adaptador de lista de usuarios
     */
    //Variables
    private Context chatContext;
    private ArrayList<ChatUser> chatAdapterList;

    public static final String EXTRA_PROFILE_PHOTO_U = "com.project.fotayapp.PROFILE_PHOTO";
    public static final String EXTRA_USER_NAME_U = "com.project.fotayapp.EXTRA_USER_NAME";

    public class ViewHolder extends RecyclerView.ViewHolder {

        //Variables
        public ImageView iv_user_photo;
        public TextView tv_user_name, tv_count_messages, tv_last_message;

        public ViewHolder(View itemView) {
            super(itemView);

            iv_user_photo = itemView.findViewById(R.id.chat_profile_image);
            tv_user_name = itemView.findViewById(R.id.chat_username);
            tv_count_messages = itemView.findViewById(R.id.chat_count);
            tv_last_message = itemView.findViewById(R.id.chat_last_message);
        }
    }

    //Constructor
    public UserChatAdapter(Context chatContext, ArrayList<ChatUser> chatAdapterList) {
        this.chatContext = chatContext;
        this.chatAdapterList = chatAdapterList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_user_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ChatUser chatUser = chatAdapterList.get(position);

        //Cargar imagen de perfil
        if (!chatUser.getUserPhoto().equalsIgnoreCase("null")) {

            Picasso.get().load(chatUser.getUserPhoto()).fit().centerInside().into(holder.iv_user_photo);
        }
        //Si no hay imagen de perfil, se carga la imagen por defecto
        Picasso.get().load(chatUser.getUserPhoto()).placeholder(R.drawable.ic_no_profile_picture).into(holder.iv_user_photo);

        //Cargar el nombre de usuario
        String userName = chatUser.getUserName();
        holder.tv_user_name.setText(userName);

        //Cargar el ultimo mensaje
        String lastMessage = chatUser.getLastMessage();
        holder.tv_last_message.setText(lastMessage);

        //Cargar contador de nuevos mensajes
        int newMessages = chatUser.getCountMessages();
        if (newMessages > 0) {
            holder.tv_count_messages.setText(String.valueOf(newMessages));
            holder.tv_count_messages.setVisibility(View.VISIBLE);
        } else {
            holder.tv_count_messages.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return chatAdapterList.size();
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private UserChatAdapter.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final UserChatAdapter.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                        //Toast.makeText(context, "onLongClick", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildAdapterPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }

    }
}
