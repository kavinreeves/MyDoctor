package com.example.mydoctor.mydoctor.Navigation;


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
import static com.example.mydoctor.mydoctor.Login.CommonParams.URL_PATIENT_OPERATION;
import static com.example.mydoctor.mydoctor.Login.CommonParams.deviceOs;
import static com.example.mydoctor.mydoctor.Login.CommonParams.sha;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyDoctors extends Fragment {

    private List<DoctorsInfo> doctorList = new ArrayList<>();
    private RecyclerView recyclerView;
    private DoctorListRecyclerAdapter mAdapter;
    View progressOverlay;
    private TextView noResult;
    RecyclerView.LayoutManager mLayoutManager;

    public MyDoctors() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_doctors, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        noResult = (TextView) view.findViewById(R.id.myDocs_noresult);
        recyclerView = (RecyclerView) view.findViewById(R.id.myFavDoctorsDisplay);
        progressOverlay = view.findViewById(R.id.progress_overlay);
        mAdapter = new DoctorListRecyclerAdapter(doctorList);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        getFavList();
        // add dummy data
       /* for (int i=0; i<4; i++) {
            DoctorsInfo doctorsInfo = new DoctorsInfo("patName", "1", "specialization", "experience", "city", "onlinestatus", "http://wenso-sms.co.uk/uploads/users/thumbnails/doctor_1_14897609597908.jpg");
            doctorList.add(doctorsInfo);
            mAdapter.notifyDataSetChanged();
        }*/
        // add click listener
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Log.d("position", String.valueOf(position));
                DoctorsInfo doctorsInfo = mAdapter.getItem(position);
                String name = doctorsInfo.getName();
                String doctor_id = doctorsInfo.getDoctor_id();
                String spec = doctorsInfo.getSpec();
                String experience = doctorsInfo.getExperience();
                String city = doctorsInfo.getCity();
                String online_status = doctorsInfo.getOnlinestatus();
                Log.d("patName", name);
                Log.d("doctor_id", doctor_id);
                Log.d("specialisation", spec);
                Log.d("exp", experience);
                Log.d("city", city);

                //Put the value
                DoctorProfileView profileView = new DoctorProfileView();
                Bundle args = new Bundle();
                args.putString("doctor_id", doctor_id);
                args.putString("doctor_name", name);
                args.putString("online_status", online_status);
                profileView.setArguments(args);

                //Inflate the fragment
                getFragmentManager().beginTransaction().add(R.id.patient_fragment_container, profileView).commit();
            }

            @Override
            public void onLongItemClick(View view, int position) {


            }
        }));
    }

    private void getFavList() {

        ProgressBarUtils.animateView(progressOverlay, View.VISIBLE, 0.4f, 200);
        noResult.setVisibility(View.GONE);
        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams params = new RequestParams();
        params.put("type", "favoriteslist");
        params.put("id", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getInt("patient_id", 0));// patient id
        params.put("device", deviceOs);
        params.put("version", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("AppVersion", null));
        params.put("ipaddress", new Splash_Screen().getLocalIpAddress());
        params.put("udid", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("UDID", null));
        params.put("sha", sha);

        client.post(URL_PATIENT_OPERATION, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                JSONArray jsonArray = new JSONArray();
                JSONObject jsonObject;
                String fName, lastSeen, doctor_id, specialization, experience, city, onlinestatus, photo;
                String JsonStatus = response.optString("success");
                String jsonResult = response.toString();
                Log.d("response_favDoctors", jsonResult);

                switch (JsonStatus) {
                    case "1":

                        try {
                            jsonArray = response.getJSONArray("favourates");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("no_of_FavDoctors", String.valueOf(jsonArray.length()));
                        int count = 0;
                        clearRecyclerView();
                        while (count < jsonArray.length()) {
                            try {
                                jsonObject = jsonArray.getJSONObject(count);
                                fName = jsonObject.getString("name");
                                lastSeen = jsonObject.getString("lastonline");
                                doctor_id = jsonObject.getString("doctor_id");
                                specialization = jsonObject.getString("specialization");
                                experience = jsonObject.getString("experience");
                                //city = jsonObject.getString("city");
                                onlinestatus = jsonObject.getString("onlinestatus");
                                photo = jsonObject.getString("photo");

                                Log.d("fname", fName);
                                //Log.d("lname", lName);
                                Log.d("spec", specialization);
                                Log.d("exp", experience);
                                //Log.d("city", city);
                                Log.d("onlinestatus", onlinestatus);
                                // put them all in Contacts Object

                                DoctorsInfo doctorsInfo = new DoctorsInfo(fName, doctor_id, specialization, experience, "city", onlinestatus, photo);
                                doctorList.add(doctorsInfo);
                                mAdapter.notifyDataSetChanged();


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            count++;
                        }

                        ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
                        break;

                    case "2":
                        // no results
                        //clearRecyclerView();
                        mAdapter.notifyDataSetChanged();
                        /*Snackbar.make(getView(), "No results Found", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();*/
                        noResult.setVisibility(View.VISIBLE);
                        ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
                        break;

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
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
