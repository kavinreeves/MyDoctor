package com.example.mydoctor.mydoctor.DoctorNavigation;


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

import com.example.mydoctor.mydoctor.AdapterHelpers.AppointmentsListAdapter;
import com.example.mydoctor.mydoctor.AdapterHelpers.DoctorAppointmentsInfo;
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
public class PendingRequestsFragment extends Fragment {

    private RecyclerView recyclerViewappointments;
    private List<DoctorAppointmentsInfo> doctorAppointmentsInfoList = new ArrayList<>();
    private AppointmentsListAdapter myAdapter;
    private View progressOverlay;
    private TextView noResult;

    public PendingRequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_doctor_appointments, container, false);

        /*Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_doctor);
        toolbar.setEnabled(true);
        toolbar.setCollapsible(true);*/

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        noResult = (TextView) view.findViewById(R.id.pendingRequests_noresult);
        recyclerViewappointments = (RecyclerView) view.findViewById(R.id.recyclerView_doc_appointments);
        myAdapter = new AppointmentsListAdapter(doctorAppointmentsInfoList);
        progressOverlay = view.findViewById(R.id.progress_overlay);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerViewappointments.setLayoutManager(mLayoutManager);
        recyclerViewappointments.setItemAnimator(new DefaultItemAnimator());
        recyclerViewappointments.setAdapter(myAdapter);

        // add click listener

       /* recyclerViewappointments.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), recyclerViewappointments, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Toast.makeText(getActivity(), "Check Appointment", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));*/

        listAllNotifications();
    }

    private void listAllNotifications() {

        ProgressBarUtils.animateView(progressOverlay, View.VISIBLE, 0.4f, 200);
        noResult.setVisibility(View.GONE);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("type", "listappointments");
        params.put("id", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS_DOCTOR, MODE_PRIVATE).getInt("doctor_id", 0));
        params.put("device", deviceOs); // sender username
        params.put("version", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("AppVersion", null));
        params.put("ipaddress", new Splash_Screen().getLocalIpAddress());
        params.put("udid", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("UDID", null));
        params.put("sha", sha);
        client.post(URL_DOCTOR_OPERATION, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                Log.d("listAppointments_json", response.toString());
                JSONArray jArray = response.optJSONArray("appointments");
                JSONObject jObject = new JSONObject();
                String aptId, aptType, dateTime, firstName, lastName, age, city, gender, photoUrl, patientId, patUsername, status;

                switch (response.optInt("success")) {

                    case 1:

                        if (response.optJSONArray("appointments") != null) {
                            int arrayLength = jArray.length();
                            Log.d("no_of_requests", String.valueOf(arrayLength));
                            clearRecyclerView();

                            for (int i = 0; i < arrayLength; i++) {
                                try {
                                    jObject = jArray.getJSONObject(i);
                                    aptId = jObject.optString("apt_id");
                                    aptType = jObject.optString("apt_type");
                                    dateTime = jObject.optString("datetime");
                                    firstName = jObject.optString("first_name");
                                    lastName = jObject.optString("last_name");
                                    age = jObject.optString("age");
                                    city = jObject.optString("city");
                                    gender = jObject.optString("gender");
                                    photoUrl = jObject.optString("photo");
                                    patientId = jObject.optString("patient_id");
                                    patUsername = jObject.optString("username");
                                    status = jObject.optString("status");

                                    //
                                    DoctorAppointmentsInfo appointmentsInfo = new DoctorAppointmentsInfo(dateTime, firstName, gender, age, city, aptType, photoUrl, patientId, aptId, patUsername, status);
                                    doctorAppointmentsInfoList.add(appointmentsInfo);
                                    myAdapter.notifyDataSetChanged();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }
                        }
                        ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
                        break;
                    case 2:
                        /*Snackbar snackbar = Snackbar.make(getView(), "No Pending Requests!", Snackbar.LENGTH_LONG);

                        snackbar.show();*/
                        noResult.setVisibility(View.VISIBLE);
                        // Changing message text color
                        //snackbar.setActionTextColor(Color.RED);
                        ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);

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

    private void clearRecyclerView() {
        int size = this.doctorAppointmentsInfoList.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.doctorAppointmentsInfoList.remove(0);
            }


        }
    }
}

