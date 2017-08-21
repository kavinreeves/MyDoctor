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
 * Created by Wenso on 05-May-17.
 */

public class DoctorBookingsAdapter extends RecyclerView.Adapter<DoctorBookingsAdapter.MyViewHolder>{

    private List<DoctorBookingsObj> bookingsList;

    public DoctorBookingsAdapter(List<DoctorBookingsObj> bookingsList) {
        this.bookingsList = bookingsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.doc_bookings_single, parent, false);

        return new DoctorBookingsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        // populate the binder view
        DoctorBookingsObj obj = bookingsList.get(position);
        holder.patName.setText(obj.getPatName());
        holder.time.setText(obj.getDateTime());
        holder.status.setText(obj.getStatus());

        String aptType = obj.getAptType();

        if (aptType.equals("direct")){

            holder.imageView.setBackgroundResource(R.drawable.face_face_approval);
        }else if (aptType.equals("chat")){

            holder.imageView.setBackgroundResource(R.drawable.chat_approval);
        }else if (aptType.equals("video")){
            holder.imageView.setBackgroundResource(R.drawable.video_approval);
        }
    }

    @Override
    public int getItemCount() {
        return bookingsList.size();
    }


    // write this viewholder

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView patName, time, status;
        public ImageView imageView;

        public MyViewHolder(View view) {
            super(view);
            //
            view.setOnClickListener(this); // invokes the onclick method
            imageView = (ImageView) view.findViewById(R.id.doc_bookings_cardviewIV);
            patName = (TextView) view.findViewById(R.id.patname_doc_bookingsAll);
            time = (TextView) view.findViewById(R.id.aptTime_doc_bookingsAll);
            status = (TextView) view.findViewById(R.id.status_doc_bookingsAll);
        }

        @Override
        public void onClick(View view) {


        }
    }

    public DoctorBookingsObj getItem(int position){

        return bookingsList.get(position);
    }
}
