package com.example.mydoctor.mydoctor.Navigation;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.mydoctor.mydoctor.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FindDoctors extends Fragment implements View.OnClickListener {

View progressOverlay;

    public FindDoctors() {
        // Required empty public constructor
    }

ImageButton gp, pediatrician, dermatologist, ent, dentist,cardiologist, veterinary, psychiatrist, gynecologist;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_find_doctors, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressOverlay = view.findViewById(R.id.progress_overlay);
        gp = (ImageButton) view.findViewById(R.id.user_select_gp);
        pediatrician = (ImageButton) view.findViewById(R.id.user_select_pedia);
        dermatologist = (ImageButton) view.findViewById(R.id.user_select_derma);
        ent= (ImageButton) view.findViewById(R.id.user_select_ent);
        dentist = (ImageButton) view.findViewById(R.id.user_select_dentist);
        cardiologist = (ImageButton) view.findViewById(R.id.user_select_cardio);
        veterinary = (ImageButton) view.findViewById(R.id.user_select_veterinary);
        psychiatrist = (ImageButton) view.findViewById(R.id.user_select_psyc);
        gynecologist = (ImageButton) view.findViewById(R.id.user_select_gyno);


        gp.setOnClickListener(this);
        pediatrician.setOnClickListener(this);
        dermatologist.setOnClickListener(this);
        ent.setOnClickListener(this);
        dentist.setOnClickListener(this);
        cardiologist.setOnClickListener(this);
        veterinary.setOnClickListener(this);
        psychiatrist.setOnClickListener(this);
        gynecologist.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.user_select_gp:
            //Put the value
           /* ShowAllDoctors fragment = new ShowAllDoctors();
            Bundle args = new Bundle();
            args.putString("spec", "general");
            fragment.setArguments(args);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.patient_fragment_container, fragment).commit();*/
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.patient_fragment_container, new ShowAllDoctors()).commit();
        break;
            case R.id.user_select_pedia:
                //Put the value
                ShowAllDoctors fragment1 = new ShowAllDoctors();
                Bundle args1 = new Bundle();
                args1.putString("spec", "pediatrician");
                fragment1.setArguments(args1);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.patient_fragment_container, fragment1).commit();
                break;
            case R.id.user_select_derma:
                //Put the value
                ShowAllDoctors fragment2 = new ShowAllDoctors();
                Bundle args2 = new Bundle();
                args2.putString("spec", "dermatologist");
                fragment2.setArguments(args2);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.patient_fragment_container, fragment2).commit();
                break;
            case R.id.user_select_ent:
                //Put the value
                ShowAllDoctors fragment3 = new ShowAllDoctors();
                Bundle args3 = new Bundle();
                args3.putString("spec", "ent");
                fragment3.setArguments(args3);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.patient_fragment_container, fragment3).commit();
                break;
            case R.id.user_select_dentist:
                //Put the value
                ShowAllDoctors fragment4 = new ShowAllDoctors();
                Bundle args4 = new Bundle();
                args4.putString("spec", "dentist");
                fragment4.setArguments(args4);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.patient_fragment_container, fragment4).commit();
                break;
            case R.id.user_select_cardio:
                //Put the value
                ShowAllDoctors fragment5 = new ShowAllDoctors();
                Bundle args5 = new Bundle();
                args5.putString("spec", "cardiology");
                fragment5.setArguments(args5);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.patient_fragment_container, fragment5).commit();
                break;
            case R.id.user_select_veterinary:
                //Put the value
                ShowAllDoctors fragment6 = new ShowAllDoctors();
                Bundle args6 = new Bundle();
                args6.putString("spec", "veterinary");
                fragment6.setArguments(args6);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.patient_fragment_container, fragment6).commit();
                break;
            case R.id.user_select_psyc:
                //Put the value
                ShowAllDoctors fragment7 = new ShowAllDoctors();
                Bundle args7 = new Bundle();
                args7.putString("spec", "psychiatrist");
                fragment7.setArguments(args7);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.patient_fragment_container, fragment7).commit();
                break;
            case R.id.user_select_gyno:
                //Put the value
                ShowAllDoctors fragment8 = new ShowAllDoctors();
                Bundle args8 = new Bundle();
                args8.putString("spec", "gynecologist");
                fragment8.setArguments(args8);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.patient_fragment_container, fragment8).commit();
                break;
        }
    }
}
