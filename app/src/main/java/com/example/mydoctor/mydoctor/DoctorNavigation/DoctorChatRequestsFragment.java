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
public class DoctorChatRequestsFragment extends Fragment {


    public DoctorChatRequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_doctor_chat_requests, container, false);
    }

}
