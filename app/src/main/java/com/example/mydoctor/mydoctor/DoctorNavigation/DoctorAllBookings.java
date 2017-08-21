package com.example.mydoctor.mydoctor.DoctorNavigation;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.mydoctor.mydoctor.AdapterHelpers.DoctorBookingsAdapter;
import com.example.mydoctor.mydoctor.AdapterHelpers.DoctorBookingsObj;
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
import static com.example.mydoctor.mydoctor.Login.CommonParams.MY_PREFS_COMMON_PARAMS_DOCTOR_CHAT;
import static com.example.mydoctor.mydoctor.Login.CommonParams.URL_DOCTOR_OPERATION;
import static com.example.mydoctor.mydoctor.Login.CommonParams.deviceOs;
import static com.example.mydoctor.mydoctor.Login.CommonParams.sha;

/**
 * A simple {@link Fragment} subclass.
 */
public class DoctorAllBookings extends Fragment implements View.OnClickListener {

    private RecyclerView recyclerViewBookings;
    private List<DoctorBookingsObj> bookingsList = new ArrayList<>();
    private DoctorBookingsAdapter myAdapter;

    private View progressOverlay;
    private View mView;
    Button week, today, all;
    private TextView noResult;

    String intervalParam;

    public DoctorAllBookings() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_doctor_all_bookings, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mView = view.findViewById(android.R.id.content);
        noResult = (TextView) view.findViewById(R.id.docAllBookings_noresult);
        ((RadioGroup) view.findViewById(R.id.toggleGroupBookingAll)).setOnCheckedChangeListener(ToggleListener);
        week = (Button) view.findViewById(R.id.btn_bookings_week);
        today = (Button) view.findViewById(R.id.btn_bookings_today);
        all = (Button) view.findViewById(R.id.btn_bookings_all);

        progressOverlay = view.findViewById(R.id.progress_overlay);

        week.setOnClickListener(this);
        today.setOnClickListener(this);
        all.setOnClickListener(this);

        recyclerViewBookings = (RecyclerView) view.findViewById(R.id.recyclerView_doc_allBookings);
        myAdapter = new DoctorBookingsAdapter(bookingsList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerViewBookings.setLayoutManager(mLayoutManager);
        recyclerViewBookings.setItemAnimator(new DefaultItemAnimator());
        recyclerViewBookings.setAdapter(myAdapter);

        recyclerViewBookings.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), recyclerViewBookings, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onLongItemClick(View view, final int position) {

                DoctorBookingsObj obj = bookingsList.get(position);
                final int patientId = Integer.parseInt(obj.getPatientId());
                final String patientUsername = obj.getPatName();
                final String appt_type = obj.getAptType();
                final int aptId = Integer.parseInt(obj.getAptId());


                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                cancelAppointment(patientId, patientUsername, aptId, appt_type, position);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                dialog.dismiss();
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Cancel Appointment ?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();

            }
        }));


        //
        getBookings("today");

    }

    private void cancelAppointment(int patientId, String patientUsername, int apt_id, String appt_type, final int position) {
        ProgressBarUtils.animateView(progressOverlay, View.VISIBLE, 0.4f, 200);
        noResult.setVisibility(View.GONE);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("type", "cancelappointment");
        params.put("id", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS_DOCTOR, MODE_PRIVATE).getInt("doctor_id", 0));
        params.put("patient_id", patientId);
        params.put("doctor_username", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS_DOCTOR, MODE_PRIVATE).getString("doc_username", "User Name"));
        params.put("patient_username", patientUsername);
        params.put("fromChannel_id", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS_DOCTOR_CHAT, MODE_PRIVATE).getInt("doctor_channel_id", 0));
        params.put("apt_id", apt_id);
        params.put("apt_type", appt_type);
        params.put("device", deviceOs);
        params.put("version", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("AppVersion", null));
        params.put("ipaddress", new Splash_Screen().getLocalIpAddress());
        params.put("udid", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("UDID", null));
        params.put("sha", sha);
        Log.d("allcheckDecline", patientId + " " + patientUsername + " " + apt_id + " " + appt_type);
        client.post(URL_DOCTOR_OPERATION, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d("doc_cancel_appt", response.toString());

                switch (response.optInt("success")) {
                    case 1:
                        // get the success response
                        removeAt(position);
                        Toast.makeText(getActivity(), "Cancelled!", Toast.LENGTH_SHORT).show();
                        ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
                        break;
                    case 4:

                        // get the success response
                        //removeAt(getAdapterPosition());
                        Toast.makeText(getActivity(), "Failed!", Toast.LENGTH_SHORT).show();
                        ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
                        break;

                    default:
                        ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
                        Toast.makeText(getActivity(), "Network Error!", Toast.LENGTH_SHORT).show();
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

    static final RadioGroup.OnCheckedChangeListener ToggleListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(final RadioGroup radioGroup, final int i) {
            for (int j = 0; j < radioGroup.getChildCount(); j++) {
                final ToggleButton view = (ToggleButton) radioGroup.getChildAt(j);
                view.setChecked(view.getId() == i);
            }
        }
    };


    @Override
    public void onClick(View view) {
        int selected_btn_id = view.getId();
        ((RadioGroup) view.getParent()).check(selected_btn_id);

        if (selected_btn_id == R.id.btn_bookings_today) {

            getBookings("today");
        } else if (selected_btn_id == R.id.btn_bookings_week) {

            getBookings("week");
        } else {

            getBookings("all");
        }


    }

    private void getBookings(String interval) {
        ProgressBarUtils.animateView(progressOverlay, View.VISIBLE, 0.4f, 200);
        noResult.setVisibility(View.GONE);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("type", "listconfirmbookings");
        params.put("interval", interval);
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

                Log.d("listConfApmntsJson", response.toString());
                JSONArray jArray = response.optJSONArray("appointments");
                JSONObject jObject = new JSONObject();
                String aptId, aptType, dateTime, firstName, lastName, age, city, gender, photoUrl, patientId, patUsername, status, prescription;

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
                                    prescription = jObject.optString("prescription");
                                   /* if (prescription==null){
                                        prescription = "no";
                                    }*/
                                    photoUrl = jObject.optString("photo");
                                    patientId = jObject.optString("patient_id");
                                    patUsername = jObject.optString("username");
                                    status = jObject.optString("status");

                                    //
                                    DoctorBookingsObj appointmentsInfo = new DoctorBookingsObj(firstName, aptId, patientId, status, dateTime, age + " " + gender, photoUrl, aptType);
                                    bookingsList.add(appointmentsInfo);
                                    myAdapter.notifyDataSetChanged();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }
                        }
                        ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
                        break;
                    case 2:
                       /* Snackbar snackbar = Snackbar.make(mView, "No Appointments Found!", Snackbar.LENGTH_LONG);

                        snackbar.show();*/
                       noResult.setVisibility(View.VISIBLE);
                        clearRecyclerView();
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
        int size = this.bookingsList.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.bookingsList.remove(0);
            }


        }
    }


    public void removeAt(int position) {
        bookingsList.remove(position);
        myAdapter.notifyItemRemoved(position);
        myAdapter.notifyItemRangeChanged(position, bookingsList.size());
        myAdapter.notifyDataSetChanged();
    }


}
