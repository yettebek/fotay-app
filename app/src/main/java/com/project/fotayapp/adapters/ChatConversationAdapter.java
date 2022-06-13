package com.project.fotayapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.project.fotayapp.R;
import com.project.fotayapp.models.Chat;

import java.util.ArrayList;

public class ChatConversationAdapter extends RecyclerView.Adapter<ChatConversationAdapter.ViewHolder> {

    //Variables
    private int userId;
    private int sender = 100;
    private Context context;
    private ArrayList<Chat> chatList = new ArrayList<>();

    public class ViewHolder extends RecyclerView.ViewHolder {
        //Variables
        public TextView tvMessage, tvDate;

        public ViewHolder(View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.chat_tv_sender_message);
            tvDate = itemView.findViewById(R.id.chat_tv_sender_date);
        }
    }

    public ChatConversationAdapter(Context context, ArrayList<Chat> chatList, int userId) {
        this.context = context;
        this.chatList = chatList;
        this.userId = userId;
    }


    @Override
    public ChatConversationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View chatView;
        if (viewType == sender) {
            chatView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_sender_item, parent, false);
        } else {
            chatView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_receiver_item, parent, false);
        }
        return new ViewHolder(chatView);
    }

    //Identificaar el emisor del mensaje
    @Override
    public int getItemViewType(int position) {
        Chat chat = chatList.get(position);
        if (chat.getUsu_id() == userId) { //getusu_id() es el id del usuario que recibe el mensaje no quien lo envia CORREGIRLO
            return sender; //muestra el viewtype VERDE
        }
        return position; //muestra el viewtype AZUL

    }

    @Override
    public void onBindViewHolder( ChatConversationAdapter.ViewHolder holder, int position) {
        //Anadir mensaje a la vista
        Chat chat = chatList.get(position);
        ((ViewHolder)holder).tvMessage.setText(chat.getMensaje());
        ((ViewHolder)holder).tvDate.setText(chat.getFecha());

    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

}
