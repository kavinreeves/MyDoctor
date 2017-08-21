package com.example.mydoctor.mydoctor.DoctorNavigation;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mydoctor.mydoctor.AdapterHelpers.DoctorAllAppAdapter;
import com.example.mydoctor.mydoctor.AdapterHelpers.DoctorAllAppointmentsObj;
import com.example.mydoctor.mydoctor.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class DoctorAllAppointments extends Fragment {

    private RecyclerView recyclerViewappointments;
    private List<DoctorAllAppointmentsObj> doctorAppointmentsInfoList = new ArrayList<>();
    private DoctorAllAppAdapter myAdapter;


    public DoctorAllAppointments() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_doctor_all_appointments, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerViewappointments = (RecyclerView) view.findViewById(R.id.all_apptmnts_RV);
        myAdapter = new DoctorAllAppAdapter(doctorAppointmentsInfoList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerViewappointments.setLayoutManager(mLayoutManager);
        recyclerViewappointments.setItemAnimator(new DefaultItemAnimator());
        recyclerViewappointments.setAdapter(myAdapter);

        DoctorAllAppointmentsObj appointmentsInfo = new DoctorAllAppointmentsObj("dateTime", "firstName", "gender", "video", "city", "status");
        doctorAppointmentsInfoList.add(appointmentsInfo);
        myAdapter.notifyDataSetChanged();
    }
}
