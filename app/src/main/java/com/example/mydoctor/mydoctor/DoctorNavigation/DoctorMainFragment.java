package com.example.mydoctor.mydoctor.DoctorNavigation;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mydoctor.mydoctor.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DoctorMainFragment extends Fragment {




    public DoctorMainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_doctor_main_, container, false);
    }



}
