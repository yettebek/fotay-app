package com.project.fotayapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.fotayapp.R;
import com.project.fotayapp.models.Chat;

import java.util.ArrayList;

public class ChatConversationAdapter extends RecyclerView.Adapter<ChatConversationAdapter.ViewHolder> {

    //Variables
    private int userId;
    private Context context;
    private int sender = 100;
    private ArrayList<Chat> chatList = new ArrayList<>();

    public ChatConversationAdapter(Context context, ArrayList<Chat> chatList /*int userId*/) {
        this.context = context;
        this.chatList = chatList;
        //this.userId = userId;
    }

    //Identificaar el emisor del mensaje
    @Override
    public int getItemViewType(int position) {
        Chat chat = chatList.get(position);
        if(chat.getUsu_id() == userId){
            return sender;
        }
        return position;
    }


    @NonNull
    @Override
    public ChatConversationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View chatView;
        if(viewType == sender){
            chatView = LayoutInflater.from(context).inflate(R.layout.chat_sender_item, parent, false);
        }else {
            chatView = LayoutInflater.from(context).inflate(R.layout.chat_receiver_item, parent, false);
        }
        return new ViewHolder(chatView);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatConversationAdapter.ViewHolder holder, int position) {
        //Anadir mensaje a la vista
        Chat chat = chatList.get(position);
        holder.tvMessage.setText(chat.getMensaje());
        holder.tvDate.setText(chat.getFecha());

    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //Variables
        TextView tvMessage, tvDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.chat_tv_sender_message);
            tvDate = itemView.findViewById(R.id.chat_tv_sender_date);
        }
    }

    //
}
