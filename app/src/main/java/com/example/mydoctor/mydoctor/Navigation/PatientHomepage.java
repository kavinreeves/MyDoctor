package com.example.mydoctor.mydoctor.Navigation;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mydoctor.mydoctor.Login.Splash_Screen;
import com.example.mydoctor.mydoctor.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

import static android.content.Context.MODE_PRIVATE;
import static com.example.mydoctor.mydoctor.Login.CommonParams.MY_PREFS_COMMON_PARAMS;
import static com.example.mydoctor.mydoctor.Login.CommonParams.MY_PREFS_COMMON_PARAMS_PATIENT;
import static com.example.mydoctor.mydoctor.Login.CommonParams.URL_PATIENT_WALLET;
import static com.example.mydoctor.mydoctor.Login.CommonParams.deviceOs;
import static com.example.mydoctor.mydoctor.Login.CommonParams.sha;

/**
 * A simple {@link Fragment} subclass.
 */
public class PatientHomepage extends Fragment implements View.OnClickListener {

    TextView balanceHeader;
    String balance;

    public PatientHomepage() {
        // Required empty public constructor
    }

    ImageButton myProfile, myDoctors, findDoctors, prescriptions, payments, chats, pharmacy, diagnostics, apptmnts;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_patient_homepage, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        balanceHeader = (TextView) getActivity().findViewById(R.id.tvHeaderPat_balance);
        myProfile = (ImageButton) view.findViewById(R.id.user_my_profile);
        myDoctors = (ImageButton) view.findViewById(R.id.user_my_doctors);
        findDoctors = (ImageButton) view.findViewById(R.id.user_find_doctors);
        prescriptions = (ImageButton) view.findViewById(R.id.user_prescriptions);
        payments = (ImageButton) view.findViewById(R.id.user_payments);
        chats = (ImageButton) view.findViewById(R.id.user_chats);
        pharmacy = (ImageButton) view.findViewById(R.id.user_select_pharmacy);
        diagnostics = (ImageButton) view.findViewById(R.id.user_diagnostics);
        apptmnts = (ImageButton) view.findViewById(R.id.user_appointments_home);


        myProfile.setOnClickListener(this);
        myDoctors.setOnClickListener(this);
        findDoctors.setOnClickListener(this);
        prescriptions.setOnClickListener(this);
        payments.setOnClickListener(this);
        chats.setOnClickListener(this);
        pharmacy.setOnClickListener(this);
        diagnostics.setOnClickListener(this);
        apptmnts.setOnClickListener(this);

        updateBalance();
    }

    private void updateBalance() {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("type", "balance");
        params.put("id", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS_PATIENT, MODE_PRIVATE).getInt("patient_id", 0)); // doctor id
        params.put("device", deviceOs); // sender username
        params.put("version", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("AppVersion", null));
        params.put("ipaddress", new Splash_Screen().getLocalIpAddress());
        params.put("udid", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("UDID", null));
        params.put("sha", sha);
        client.post(URL_PATIENT_WALLET, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d("getbalancePatHome", response.toString());
                if (response.optInt("success") == 1) {

                    balance = response.optString("balance");

                    try {
                        balanceHeader.setText(getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS_PATIENT, MODE_PRIVATE).getString("currency", "")+" " + balance);
                    }catch (Exception e){
                        e.printStackTrace();
                    }



                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.user_my_profile:

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.patient_fragment_container, new My_Profile()).addToBackStack(null).commit();

                break;
            case R.id.user_my_doctors:
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.patient_fragment_container, new MyDoctors()).addToBackStack(null).commit();

                break;
            case R.id.user_find_doctors:

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.patient_fragment_container, new FindDoctors()).addToBackStack(null).commit();
                break;
            case R.id.user_prescriptions:
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.patient_fragment_container, new MyPrescriptions()).addToBackStack(null).commit();

                //getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.doctor_fragment_container, new DoctorAllPrescriptions()).addToBackStack(null).commit();
                break;
            case R.id.user_payments:
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.patient_fragment_container, new PaymentsHistory()).addToBackStack(null).commit();
                break;
            case R.id.user_chats:
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.patient_fragment_container, new PatientChatsFragment()).addToBackStack(null).commit();

                break;
            case R.id.user_select_pharmacy:
                Toast.makeText(getActivity(), "Coming Soon!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.user_diagnostics:
                Toast.makeText(getActivity(), "Coming Soon!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.user_appointments_home:
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.patient_fragment_container, new My_Appointments()).addToBackStack(null).commit();
                break;
        }

    }
}
