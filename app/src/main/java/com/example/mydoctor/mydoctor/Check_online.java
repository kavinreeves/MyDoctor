package com.example.mydoctor.mydoctor;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;


public class Check_online extends Fragment {


    TextView TVV,TVDermatologist,TVavailableDermatologist,TVCardiologist,TVavailableCardiologist,TVDentist,TVavailableDentist,TVGynecologist,TVavailableGynecologist;
    TextView TVMicrobiologist,TVavailableMicrobiologist,TVImmunologist,TVavailableImmunologist,TVNeonatologist,TVavailableNeonatologist,TVPhysiologist,TVavailablePhysiologist;
    RadioGroup RG2, RG1;
    RadioButton RB1,RB2,RB3,RB4,RB5,RB6,RB7,RB8;
    LinearLayout LL11,LL22,LL33,LL44, LL55, LL66, LL77, LL88, LLtextView;
    public static String itemSelectedd;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.check_online_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LLtextView=(LinearLayout)view.findViewById(R.id.LLtextView);

        TVDermatologist=(TextView)view.findViewById(R.id.Dermatologist);
        TVavailableDermatologist=(TextView)view.findViewById(R.id.availableDermatologist);
        TVCardiologist=(TextView)view.findViewById(R.id.Cardiologist);
        TVavailableCardiologist=(TextView)view.findViewById(R.id.availableCardiologist);
        TVDentist=(TextView)view.findViewById(R.id.Dentist);
        TVavailableDentist=(TextView)view.findViewById(R.id.availableDentist);
        TVGynecologist=(TextView)view.findViewById(R.id.Gynecologist);
        TVavailableGynecologist=(TextView)view.findViewById(R.id.availableGynecologist);
        TVMicrobiologist=(TextView)view.findViewById(R.id.Microbiologist);
        TVavailableMicrobiologist=(TextView)view.findViewById(R.id.availableMicrobiologist);
        TVImmunologist=(TextView)view.findViewById(R.id.Immunologist);
        TVavailableImmunologist=(TextView)view.findViewById(R.id.availableImmunologist);
        TVNeonatologist=(TextView)view.findViewById(R.id.Neonatologist);
        TVavailableNeonatologist=(TextView)view.findViewById(R.id.availableNeonatologist);
        TVPhysiologist=(TextView)view.findViewById(R.id.Physiologist);
        TVavailablePhysiologist=(TextView)view.findViewById(R.id.availablePhysiologist);



        LL11=(LinearLayout)view.findViewById(R.id.LL11);
        LL22=(LinearLayout)view.findViewById(R.id.LL22);
        LL33=(LinearLayout)view.findViewById(R.id.LL33);
        LL44=(LinearLayout)view.findViewById(R.id.LL44);
        LL55=(LinearLayout)view.findViewById(R.id.LL55);
        LL66=(LinearLayout)view.findViewById(R.id.LL66);
        LL77=(LinearLayout)view.findViewById(R.id.LL77);
        LL88=(LinearLayout)view.findViewById(R.id.LL88);


        LL11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // LL11.setBackgroundColor(getResources().getColor(R.color.colorOrange));
                clearAllBackgroundColors();
                TVDermatologist.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
                itemSelectedd=TVDermatologist.getText().toString();
                //  TVV.setText(TVDermatologist.getText().toString());
                getFragmentManager().beginTransaction().replace(R.id.login_container, new DoctorList()).addToBackStack(null).commit();
            }
        });
        LL22.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearAllBackgroundColors();
                TVCardiologist.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
                // TVV.setText(TVCardiologist.getText().toString());
                itemSelectedd=TVCardiologist.getText().toString();
                getFragmentManager().beginTransaction().replace(R.id.login_container, new DoctorList()).addToBackStack(null).commit();

            }
        });
        LL33.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearAllBackgroundColors();
                TVDentist.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
                //  TVV.setText(TVDentist.getText().toString());
                itemSelectedd=TVDentist.getText().toString();
                getFragmentManager().beginTransaction().replace(R.id.login_container, new DoctorList()).addToBackStack(null).commit();

            }
        });
        LL44.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearAllBackgroundColors();
                TVGynecologist.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
                //   TVV.setText(TVGynecologist.getText().toString());
                itemSelectedd=TVGynecologist.getText().toString();
                getFragmentManager().beginTransaction().replace(R.id.login_container, new DoctorList()).addToBackStack(null).commit();

            }
        });
        LL55.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearAllBackgroundColors();
                TVMicrobiologist.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
                //   TVV.setText(TVMicrobiologist.getText().toString());
                itemSelectedd=TVMicrobiologist.getText().toString();
                getFragmentManager().beginTransaction().replace(R.id.login_container, new DoctorList()).addToBackStack(null).commit();


            }
        });
        LL66.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearAllBackgroundColors();
                TVImmunologist.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
                //    TVV.setText(TVImmunologist.getText().toString());
                itemSelectedd=TVImmunologist.getText().toString();
                getFragmentManager().beginTransaction().replace(R.id.login_container, new DoctorList()).addToBackStack(null).commit();


            }
        });
        LL77.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearAllBackgroundColors();
                TVNeonatologist.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
                //     TVV.setText(TVNeonatologist.getText().toString());
                itemSelectedd=TVNeonatologist.getText().toString();
                getFragmentManager().beginTransaction().replace(R.id.login_container, new DoctorList()).addToBackStack(null).commit();

            }
        });
        LL88.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearAllBackgroundColors();
                TVPhysiologist.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
                //   TVV.setText(TVPhysiologist.getText().toString());
                itemSelectedd=TVPhysiologist.getText().toString();
                getFragmentManager().beginTransaction().replace(R.id.login_container, new DoctorList()).addToBackStack(null).commit();

            }
        });

    }

    private void clearAllChecks() {
        RG1.clearCheck();
        RG2.clearCheck();
    }

    private void clearAllBackgroundColors(){
        TVDermatologist.setTextColor(getResources().getColor(R.color.colorBlack));
        TVCardiologist.setTextColor(getResources().getColor(R.color.colorBlack));
        TVDentist.setTextColor(getResources().getColor(R.color.colorBlack));
        TVGynecologist.setTextColor(getResources().getColor(R.color.colorBlack));
        TVMicrobiologist.setTextColor(getResources().getColor(R.color.colorBlack));
        TVImmunologist.setTextColor(getResources().getColor(R.color.colorBlack));
        TVNeonatologist.setTextColor(getResources().getColor(R.color.colorBlack));
        TVPhysiologist.setTextColor(getResources().getColor(R.color.colorBlack));

    }
}
