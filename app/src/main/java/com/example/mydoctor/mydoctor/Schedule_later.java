package com.example.mydoctor.mydoctor;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;


public class Schedule_later extends Fragment {

    RecyclerView recyclerView;
    ArrayList<String> mobileArray1;


    public Schedule_later(){
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

         mobileArray1=new ArrayList<String>();

        mobileArray1.add("Cardiologist");
        mobileArray1.add("Dermatologist");
        mobileArray1.add("Dentist");
        mobileArray1.add("Gynecologist");
        mobileArray1.add("Microbiologist");
        mobileArray1.add("Immunologist");
        mobileArray1.add("Neonatologist");
        mobileArray1.add("Physiologist");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.schedule_later_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView=(RecyclerView)view.findViewById(R.id.recyclerSchedule);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(),1);
        recyclerView.setLayoutManager(layoutManager);
        adapter adptr=new adapter();
        recyclerView.setAdapter(adptr);

    }

    public class adapter extends RecyclerView.Adapter<adapter.viewHolder>{

        public class viewHolder extends RecyclerView.ViewHolder{
            TextView TV1;

            public viewHolder(View itemView) {
                super(itemView);

                TV1=(TextView)itemView.findViewById(R.id.textview);


            }
        }

        @Override
        public viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view=LayoutInflater.from(getContext()).inflate(R.layout.schedule_later_single,parent,false);
            return new viewHolder(view);
        }

        @Override
        public void onBindViewHolder(final viewHolder holder, int position) {

           // holder.tv.setText(mobileArray1.get(position));
            holder.TV1.setText(mobileArray1.get(position));

            holder.TV1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   // getFragmentManager().beginTransaction().replace(R.id.login_container, new DoctorList()).addToBackStack(null).commit();

                }
            });

        }

        @Override
        public int getItemCount() {
            return mobileArray1.size();
        }
    }
}
