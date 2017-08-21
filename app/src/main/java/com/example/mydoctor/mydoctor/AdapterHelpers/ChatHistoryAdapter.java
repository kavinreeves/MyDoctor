package com.example.mydoctor.mydoctor.AdapterHelpers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mydoctor.mydoctor.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Wenso on 16-Mar-17.
 */

public class ChatHistoryAdapter extends RecyclerView.Adapter<ChatHistoryAdapter.MyViewHolder> {

    private List<ChatHistory> chatsList;
    private Context context;

    public ChatHistoryAdapter(List<ChatHistory> chatsList) {
        this.chatsList = chatsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chats_doc_single, parent, false);

        context = parent.getContext();

        return new ChatHistoryAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // populate the binder view
        ChatHistory chatHistory = chatsList.get(position);
        holder.name.setText(chatHistory.getSenderName());
        holder.time.setText(chatHistory.getGender());
        holder.status.setText(chatHistory.getOnlinestatus());
        new DownloadImageTask(holder.imageView).execute(chatHistory.getPhoto());

    }


    @Override
    public int getItemCount() {
        return chatsList.size();
    }

    // write this viewholder

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, time, status;
        public CircleImageView imageView;
        String fromChannel, toChannel;


        public MyViewHolder(View view) {
            super(view);
            //
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            }); // invokes the onclick method
            name = (TextView) view.findViewById(R.id.senderNameChatHistory);
            time = (TextView) view.findViewById(R.id.date_time_chat_history);
            status = (TextView) view.findViewById(R.id.chat_history_status);
            imageView = (CircleImageView) view.findViewById(R.id.senderPhotoChatHistoryIV);
        }

    }

    public ChatHistory getItem(int position) {

        return chatsList.get(position);
    }
}
