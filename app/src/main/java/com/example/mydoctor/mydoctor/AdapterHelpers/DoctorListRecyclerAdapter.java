package com.example.mydoctor.mydoctor.AdapterHelpers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mydoctor.mydoctor.R;

import java.util.List;

import static com.example.mydoctor.mydoctor.Login.CommonParams.MY_PREFS_LOGINTYPE;

/**
 * Created by Wenso on 20-Feb-17.
 */

public class DoctorListRecyclerAdapter extends RecyclerView.Adapter<DoctorListRecyclerAdapter.MyViewHolder> {

    private List<DoctorsInfo> doctorsInfoList;
    private Context context;

    // public constructor
    public DoctorListRecyclerAdapter(List<DoctorsInfo> doctorsInfoList) {
        this.doctorsInfoList = doctorsInfoList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.doctor_list_single, parent, false);

        context = parent.getContext();

        return new MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        // create doctorsinfo object
        DoctorsInfo doctorsInfo = doctorsInfoList.get(position);
        holder.name.setText(doctorsInfo.getName());
        holder.spec.setText(doctorsInfo.getSpec());
        if (context.getSharedPreferences(MY_PREFS_LOGINTYPE, Context.MODE_PRIVATE).getString("type", "null").equals("doctor")) {
            holder.experience.setText("Age: " + doctorsInfo.getExperience() + " years");
        }else holder.experience.setText("Experience: " + doctorsInfo.getExperience() + " years");


        holder.city.setText("City: " + doctorsInfo.getCity());
        // get photo to imageview
        new DownloadImageTask(holder.docPhoto).execute(doctorsInfo.getPhoto());

        String status = doctorsInfo.getOnlinestatus();
        if (!status.equals("online")) {
            holder.ImgVonlineStatus.setBackgroundResource(R.drawable.doc_offline);
        }
        // set click event listeners for the individual cardview elements
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });
    }


    @Override
    public int getItemCount() {
        return doctorsInfoList.size();
    }


    // write this viewholder

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView name, spec, experience, city;
        public ImageView ImgVonlineStatus, docPhoto;

        public MyViewHolder(View view) {
            super(view);
            //
            view.setOnClickListener(this); // invokes the onclick method
            name = (TextView) view.findViewById(R.id.tv1);
            spec = (TextView) view.findViewById(R.id.tv2);
            experience = (TextView) view.findViewById(R.id.tv3);
            city = (TextView) view.findViewById(R.id.tv4);
            ImgVonlineStatus = (ImageView) view.findViewById(R.id.imageViewOnlineStatus);
            docPhoto = (ImageView) view.findViewById(R.id.doc_profile_imageFavDoc);

        }

        @Override
        public void onClick(View view) {

        }
    }

    // method to get the selected recycler item by sending its position in the List
    public DoctorsInfo getItem(int position) {
        return doctorsInfoList.get(position);
    }
}
