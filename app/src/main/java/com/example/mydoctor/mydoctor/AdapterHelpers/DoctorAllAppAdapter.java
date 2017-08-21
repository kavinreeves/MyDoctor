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
 * Created by Wenso on 03-May-17.
 */

public class DoctorAllAppAdapter extends RecyclerView.Adapter<DoctorAllAppAdapter.MyViewHolder> {

    private List<DoctorAllAppointmentsObj> appointmentsList;

    public DoctorAllAppAdapter(List<DoctorAllAppointmentsObj> appointmentsList) {
        this.appointmentsList = appointmentsList;
    }

    @Override
    public DoctorAllAppAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.doctor_allapp_single, parent, false);
        return new DoctorAllAppAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        DoctorAllAppointmentsObj allAppointmentsObj = appointmentsList.get(position);
        holder.patName.setText(allAppointmentsObj.getPatName());
        holder.patAgeGender.setText(allAppointmentsObj.getPatAgeGender());
        holder.patLocation.setText(allAppointmentsObj.getPatLocation());
        holder.dateTime.setText(allAppointmentsObj.getDateTime());

        String type = allAppointmentsObj.getAptType();
        if (type.equals("video")){
            holder.type.setBackgroundResource(R.drawable.video_approval);
        }else if (type.equals("chat")){
            holder.type.setBackgroundResource(R.drawable.chat_approval);
        }else if (type.equals("direct")){
            holder.type.setBackgroundResource(R.drawable.face_face_approval);
        }


    }

    @Override
    public int getItemCount() {
        return appointmentsList.size();
    }


    // write this viewholder

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView patName, patAgeGender, patLocation, aptType, aptStatus, dateTime;
       public ImageView type;

        public MyViewHolder(View view) {
            super(view);
            //
            view.setOnClickListener(this); // invokes the onclick method
            patName = (TextView) view.findViewById(R.id.tv_user_name_aptAll);
            patAgeGender = (TextView) view.findViewById(R.id.tv_user_app_gender_age);
            patLocation = (TextView) view.findViewById(R.id.tv_user_location);
            dateTime = (TextView) view.findViewById(R.id.tv_user_app_docTimeDate);
            aptStatus = (TextView) view.findViewById(R.id.aptDoc_status);
            type = (ImageView) view.findViewById(R.id.docAll_appt_typeIV);

        }

        @Override
        public void onClick(View view) {

        }
    }

    public DoctorAllAppointmentsObj getItem(int position) {


        return appointmentsList.get(position);
    }
}
