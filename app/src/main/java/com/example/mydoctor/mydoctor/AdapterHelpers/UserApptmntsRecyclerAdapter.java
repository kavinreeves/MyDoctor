package com.example.mydoctor.mydoctor.AdapterHelpers;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mydoctor.mydoctor.R;

import java.util.List;

/**
 * Created by Wenso on 01-Mar-17.
 */

public class UserApptmntsRecyclerAdapter extends RecyclerView.Adapter<UserApptmntsRecyclerAdapter.MyViewHolder> {


    private List<UserAppointmentsInfo> userAppointmentsList;

    public UserApptmntsRecyclerAdapter(List<UserAppointmentsInfo> userAppointmentsList) {
        this.userAppointmentsList = userAppointmentsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_appointments_single, parent, false);

        return new UserApptmntsRecyclerAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        //create a UserappointmentListInfo object
        UserAppointmentsInfo appointmentsInfo = userAppointmentsList.get(position);
        holder.name.setText(appointmentsInfo.getDocName());
        holder.time.setText(appointmentsInfo.getTime());
        holder.docSpec.setText(appointmentsInfo.getDocSpec());
        holder.city.setText(appointmentsInfo.getCity());
        holder.status.setText(appointmentsInfo.getStatus());
        String aptType = appointmentsInfo.getAptType();


        if (appointmentsInfo.getStatus().equals("Confirmed")) {
            holder.status.setBackgroundResource(R.drawable.accept_green_button);
        }else if (appointmentsInfo.getStatus().equals("Cancelled")){
            holder.status.setBackgroundResource(R.drawable.decline_red_button);
        }else if (appointmentsInfo.getStatus().equals("Pending")){
            holder.status.setBackgroundResource(R.color.app_dark_blue);
        }


        if (aptType.equals("direct")) {
            holder.imageView.setBackgroundResource(R.drawable.face_face_approval);
        } else if (aptType.equals("chat")) {
            holder.imageView.setBackgroundResource(R.drawable.chat_approval);
        } else holder.imageView.setBackgroundResource(R.drawable.video_approval);
    }


    @Override
    public int getItemCount() {
        return userAppointmentsList.size();
    }

    // write this viewholder

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView name, time, docSpec, city, status;
        public ImageView imageView;

        public MyViewHolder(View view) {
            super(view);
            //
            view.setOnClickListener(this); // invokes the onclick method
            time = (TextView) view.findViewById(R.id.tv_user_app_time);
            name = (TextView) view.findViewById(R.id.tv_user_app_docName);
            docSpec = (TextView) view.findViewById(R.id.tv_user_app_docSpec);
            city = (TextView) view.findViewById(R.id.tv_user_app_docCity);
            status = (TextView) view.findViewById(R.id.user_apt_status);
            imageView = (ImageView) view.findViewById(R.id.imageViewDoc_patApt);
        }

        @Override
        public void onClick(View view) {

        }
    }

    public UserAppointmentsInfo getItem(int position) {

        return userAppointmentsList.get(position);
    }
}
