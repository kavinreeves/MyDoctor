package com.example.mydoctor.mydoctor.AdapterHelpers;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mydoctor.mydoctor.R;

import java.util.List;

/**
 * Created by Wenso on 09-Mar-17.
 */

public class AvailableTimeRecyclerAdapter extends RecyclerView.Adapter<AvailableTimeRecyclerAdapter.MyViewHolder> {

    private List<AvailableAppointments> appointmentsList;

    public AvailableTimeRecyclerAdapter(List<AvailableAppointments> appointmentsList) {
        this.appointmentsList = appointmentsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.available_timings_single, parent, false);

        return new AvailableTimeRecyclerAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        AvailableAppointments appointments = appointmentsList.get(position);
        String availability = appointments.getAvailability();
        if (availability.equals("true")){
            holder.linearLayout.setBackgroundColor(Color.rgb(10, 119, 198));
            holder.isAvailable.setText(R.string.available);
        }else {
            holder.linearLayout.setBackgroundColor(Color.rgb(208, 102, 0));
            holder.isAvailable.setText(R.string.booked);
        }
        holder.time.setText(appointments.getTime());


    }

    @Override
    public int getItemCount() {
        return appointmentsList.size();
    }

    // write this viewholder

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView time, isAvailable;
        public LinearLayout linearLayout;

        public MyViewHolder(View view) {
            super(view);
            //
            view.setOnClickListener(this); // invokes the onclick method
            time = (TextView) view.findViewById(R.id.docApptTime);
            isAvailable = (TextView) view.findViewById(R.id.docApptTimeAvailable);
            linearLayout = (LinearLayout) view.findViewById(R.id.LLappointment);

        }

        @Override
        public void onClick(View view) {

        }
    }

    public AvailableAppointments getItem(int position) {


        return appointmentsList.get(position);
    }
}
