package com.example.mydoctor.mydoctor.DoctorNavigation;


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
import com.example.mydoctor.mydoctor.Navigation.PaymentsHistory;
import com.example.mydoctor.mydoctor.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

import static android.content.Context.MODE_PRIVATE;
import static com.example.mydoctor.mydoctor.Login.CommonParams.MY_PREFS_COMMON_PARAMS;
import static com.example.mydoctor.mydoctor.Login.CommonParams.MY_PREFS_COMMON_PARAMS_DOCTOR;
import static com.example.mydoctor.mydoctor.Login.CommonParams.URL_DOCTOR_WALLET;
import static com.example.mydoctor.mydoctor.Login.CommonParams.deviceOs;
import static com.example.mydoctor.mydoctor.Login.CommonParams.sha;

/**
 * A simple {@link Fragment} subclass.
 */
public class DoctorHomepage extends Fragment implements View.OnClickListener {

    ImageButton myProfile, myPatients, notifications, prescriptions, earnings, chats, pharmacy, diagnostics, appointments;
    TextView balanceHeader;
    String balance;

    public DoctorHomepage() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_doctor_homepage, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        balanceHeader = (TextView) getActivity().findViewById(R.id.tvMainHeaderDocBalance);

        myProfile = (ImageButton) view.findViewById(R.id.doc_select_profile);
        myPatients = (ImageButton) view.findViewById(R.id.doc_my_patients);
        notifications = (ImageButton) view.findViewById(R.id.doc_select_notifs);
        prescriptions = (ImageButton) view.findViewById(R.id.doc_prescriptions);
        earnings = (ImageButton) view.findViewById(R.id.doc_select_earnings);
        chats = (ImageButton) view.findViewById(R.id.doc_chats);
        pharmacy = (ImageButton) view.findViewById(R.id.doc_select_pharmacy);
        diagnostics = (ImageButton) view.findViewById(R.id.doc_diagnostics);
        appointments = (ImageButton) view.findViewById(R.id.doc_select_appointmentss);


        myProfile.setOnClickListener(this);
        myPatients.setOnClickListener(this);
        notifications.setOnClickListener(this);
        prescriptions.setOnClickListener(this);
        earnings.setOnClickListener(this);
        chats.setOnClickListener(this);
        pharmacy.setOnClickListener(this);
        diagnostics.setOnClickListener(this);
        appointments.setOnClickListener(this);

        updateBalance();

    }
    private void updateBalance() {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("type", "balance");
        params.put("id", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS_DOCTOR, MODE_PRIVATE).getInt("doctor_id", 0)); // doctor id
        params.put("device", deviceOs); // sender username
        params.put("version", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("AppVersion", null));
        params.put("ipaddress", new Splash_Screen().getLocalIpAddress());
        params.put("udid", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("UDID", null));
        params.put("sha", sha);
        client.post(URL_DOCTOR_WALLET, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d("getbalanceDocHome", response.toString());
                if (response.optInt("success") == 1) {

                    balance = response.optString("balance");

                    try {
                        balanceHeader.setText(getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS_DOCTOR, MODE_PRIVATE).getString("currency", "")+ " " + balance);
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

            case R.id.doc_select_profile:

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.doctor_fragment_container, new DoctorProfileFragment()).addToBackStack(null).commit();

                break;
            case R.id.doc_my_patients:
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.doctor_fragment_container, new MyPatients()).addToBackStack(null).commit();

                break;
            case R.id.doc_select_notifs:
                PendingRequestsFragment appointmentsFragment = new PendingRequestsFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.doctor_fragment_container, appointmentsFragment).addToBackStack(null).commit();

                break;
            case R.id.doc_prescriptions:
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.doctor_fragment_container, new DoctorAllPrescriptions()).addToBackStack(null).commit();
                break;
            case R.id.doc_select_earnings:
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.doctor_fragment_container, new PaymentsHistory()).addToBackStack(null).commit();
                break;
            case R.id.doc_chats:
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.doctor_fragment_container, new DoctorChatsFragment()).addToBackStack(null).commit();

                break;
            case R.id.doc_select_pharmacy:

                Toast.makeText(getActivity(), "Coming Soon!", Toast.LENGTH_SHORT).show();

                break;
            case R.id.doc_diagnostics:
                Toast.makeText(getActivity(), "Coming Soon!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.doc_select_appointmentss:

               getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.doctor_fragment_container, new DoctorAllBookings()).addToBackStack(null).commit();
                break;

        }

    }
}
