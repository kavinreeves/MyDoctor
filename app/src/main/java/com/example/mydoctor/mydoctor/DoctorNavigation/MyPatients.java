package com.example.mydoctor.mydoctor.DoctorNavigation;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mydoctor.mydoctor.AdapterHelpers.DoctorListRecyclerAdapter;
import com.example.mydoctor.mydoctor.AdapterHelpers.DoctorsInfo;
import com.example.mydoctor.mydoctor.AdapterHelpers.RecyclerItemClickListener;
import com.example.mydoctor.mydoctor.Login.Splash_Screen;
import com.example.mydoctor.mydoctor.R;
import com.example.mydoctor.mydoctor.Utils.ProgressBarUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import static android.content.Context.MODE_PRIVATE;
import static com.example.mydoctor.mydoctor.Login.CommonParams.MY_PREFS_COMMON_PARAMS;
import static com.example.mydoctor.mydoctor.Login.CommonParams.MY_PREFS_COMMON_PARAMS_DOCTOR;
import static com.example.mydoctor.mydoctor.Login.CommonParams.URL_DOCTOR_OPERATION;
import static com.example.mydoctor.mydoctor.Login.CommonParams.deviceOs;
import static com.example.mydoctor.mydoctor.Login.CommonParams.sha;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyPatients extends Fragment {

    private List<DoctorsInfo> doctorList = new ArrayList<>();
    private RecyclerView recyclerView;
    private DoctorListRecyclerAdapter mAdapter;

    private Context context;

    private View progressOverlay;

    String pat_id;
    private TextView noResult;
    RecyclerView.LayoutManager mLayoutManager;


    public MyPatients() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_patients, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = view.getContext();
        noResult = (TextView) view.findViewById(R.id.myPats_noresult);
        progressOverlay = view.findViewById(R.id.progress_overlay);
        recyclerView = (RecyclerView) view.findViewById(R.id.myPatientsDisplay);
        mAdapter = new DoctorListRecyclerAdapter(doctorList);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

       /* for (int i=0; i<4; i++) {
            DoctorsInfo doctorsInfo = new DoctorsInfo("patName", "1", "specialization", "experience", "city", "onlinestatus", "http://wenso-sms.co.uk/uploads/users/thumbnails/doctor_1_14897609597908.jpg");
            doctorList.add(doctorsInfo);
            mAdapter.notifyDataSetChanged();
        }*/

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                // put values in arguments
                PatientProfileView patientProfileView = new PatientProfileView();
                Bundle args = new Bundle();
                args.putString("patient_id", pat_id);
                patientProfileView.setArguments(args);

                //Inflate the fragment
                getFragmentManager().beginTransaction().replace(R.id.doctor_fragment_container, patientProfileView).addToBackStack(null).commit();

            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

        getFavList();
    }


    private void getFavList() {

        ProgressBarUtils.animateView(progressOverlay, View.VISIBLE, 0.4f, 200);
        noResult.setVisibility(View.GONE);
        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams params = new RequestParams();
        params.put("type", "mypatients");
        params.put("id", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS_DOCTOR, MODE_PRIVATE).getInt("doctor_id", 0));// patient id
        params.put("device", deviceOs);
        params.put("version", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("AppVersion", null));
        params.put("ipaddress", new Splash_Screen().getLocalIpAddress());
        params.put("udid", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("UDID", null));
        params.put("sha", sha);
        client.post(URL_DOCTOR_OPERATION, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                JSONArray jsonArray = new JSONArray();
                JSONObject jsonObject;
                String fullName, lastSeen, age, gender, city, onlinestatus, photo;
                String JsonStatus = response.optString("success");
                String jsonResult = response.toString();
                Log.d("response_myPatients", jsonResult);

                switch (JsonStatus) {
                    case "1":

                        try {
                            jsonArray = response.getJSONArray("patients");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("no_of_patients", String.valueOf(jsonArray.length()));
                        int count = 0;
                        clearRecyclerView();
                        while (count < jsonArray.length()) {
                            try {
                                jsonObject = jsonArray.getJSONObject(count);
                                fullName = jsonObject.getString("name");
                                pat_id = jsonObject.getString("patient_id");
                                age = jsonObject.getString("age");
                                gender = jsonObject.getString("gender");
                                city = jsonObject.getString("city");
                                photo = jsonObject.getString("photo");

                                // put them all in Contacts Object

                                DoctorsInfo doctorsInfo = new DoctorsInfo(fullName, pat_id, gender, age, city, "", photo);
                                doctorList.add(doctorsInfo);
                                mAdapter.notifyDataSetChanged();


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            count++;
                        }
                        ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
                        break;

                    case "5":
                        // no results
                        //clearRecyclerView();
                        mAdapter.notifyDataSetChanged();
                        /*Snackbar.make(getActivity().findViewById(R.id.myPatientsRL), "No Patients List Found", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();*/
                        noResult.setVisibility(View.VISIBLE);
                        ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
                        break;
                    default:
                        ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
                        break;
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
            }
        });
    }

    // method to clear the recycler view
    public void clearRecyclerView() {
        int size = this.doctorList.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.doctorList.remove(0);
            }


        }
    }

}
