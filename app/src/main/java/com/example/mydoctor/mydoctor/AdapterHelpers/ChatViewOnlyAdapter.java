package com.example.mydoctor.mydoctor.AdapterHelpers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mydoctor.mydoctor.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;
import static com.example.mydoctor.mydoctor.Login.CommonParams.MY_PREFS_COMMON_PARAMS_DOCTOR;
import static com.example.mydoctor.mydoctor.Login.CommonParams.MY_PREFS_COMMON_PARAMS_PATIENT;
import static com.example.mydoctor.mydoctor.Login.CommonParams.MY_PREFS_LOGINTYPE;

/**
 * Created by Wenso on 17-May-17.
 */

public class ChatViewOnlyAdapter extends RecyclerView.Adapter<ChatViewOnlyAdapter.ViewHolder> {

    private ArrayList<ChatMessage> messages;
    private Context context;
    String imageViewUrl, imageViewOtherUrl;

    private int SELF = 78;

    public ChatViewOnlyAdapter(ArrayList<ChatMessage> messages, Context context) {
        this.messages = messages;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Creating view
        View itemView;

        //if view type is self
        if (viewType == SELF) {
            //Inflating the layout self
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_thread, parent, false);

            //  populate the imageview
            if (context.getSharedPreferences(MY_PREFS_LOGINTYPE, MODE_PRIVATE).getString("type", "").equals("doctor")) {
                imageViewUrl = context.getSharedPreferences(MY_PREFS_COMMON_PARAMS_DOCTOR, MODE_PRIVATE).getString("doctor_photo", "");

            } else {
                imageViewUrl = context.getSharedPreferences(MY_PREFS_COMMON_PARAMS_PATIENT, MODE_PRIVATE).getString("photo", "");

            }


        } else {
            //else inflating the layout others
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_thread_other, parent, false);


        }
        //returing the view
        return new ViewHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {

        //getting message object of current position
        ChatMessage message = messages.get(position);

        if (message.getStatusType().equals("from")) {
            return SELF;
        } else return position;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        holder.textViewMessage.setText(message.getMessage());
        holder.textViewTime.setText(message.getName() + ", " + message.getSentAt());

        //new DownloadImageTask(holder.imageView).execute(imageViewUrl);

        if (getItemViewType(position) == SELF) {
            new DownloadImageTask(holder.imageView).execute(imageViewUrl);
        } else {
            imageViewOtherUrl = message.getOpponentPhotoUrl();
            new DownloadImageTask(holder.imageView).execute(imageViewOtherUrl);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }


    //Initializing views
    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewMessage;
        public TextView textViewTime;
        public CircleImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewMessage = (TextView) itemView.findViewById(R.id.textViewMessage);
            textViewTime = (TextView) itemView.findViewById(R.id.textViewTime);
            imageView = (CircleImageView) itemView.findViewById(R.id.imageViewChatRoom);


        }
    }


}
