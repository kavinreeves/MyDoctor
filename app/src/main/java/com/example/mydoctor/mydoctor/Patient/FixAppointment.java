package com.example.mydoctor.mydoctor.Patient;


import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.mydoctor.mydoctor.AdapterHelpers.AvailableAppointments;
import com.example.mydoctor.mydoctor.AdapterHelpers.AvailableTimeRecyclerAdapter;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import static android.content.Context.MODE_PRIVATE;
import static com.example.mydoctor.mydoctor.Login.CommonParams.MY_PREFS_COMMON_PARAMS;
import static com.example.mydoctor.mydoctor.Login.CommonParams.MY_PREFS_COMMON_PARAMS_PATIENT;
import static com.example.mydoctor.mydoctor.Login.CommonParams.MY_PREFS_COMMON_PARAMS_PATIENT_CHAT;
import static com.example.mydoctor.mydoctor.Login.CommonParams.URL_PATIENT_OPERATION;
import static com.example.mydoctor.mydoctor.Login.CommonParams.deviceOs;
import static com.example.mydoctor.mydoctor.Login.CommonParams.sha;

/**
 * A simple {@link Fragment} subclass.
 */
public class FixAppointment extends Fragment {

    EditText chooseDate;
    private int mYear, mMonth, mDay;
    int doctor_id;
    String doctor_username, bookDate, apptTime, timeFinal, appt_type, fee;
    private RecyclerView recyclerView;
    private AvailableTimeRecyclerAdapter myAdapter;
    private List<AvailableAppointments> appointmentsList = new ArrayList<>();
    Button bookNow;

    View progressOverlay;

    DatePicker datePicker;

    Spinner shift1_spinner, shift2_spinner;
    String[] shift1_array = {"select"};
    String[] shift2_array = {"select"};


    String shift1_time, shift2_time;

    ArrayAdapter<String> shift1_adapter, shift2_adapter;

    public FixAppointment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Retrieve the value
        try {
            doctor_id = (getArguments().getInt("doctor_id"));
            doctor_username = getArguments().getString("doctor_username");
            appt_type = getArguments().getString("appt_type");
            fee = getArguments().getString("fee");
            Log.d("docId_timings", String.valueOf(doctor_id));
            Log.d("doctor_username", doctor_username);
            Log.d("appt_type", appt_type);
            Log.d("feeSelected", fee);
        } catch (Exception e) {
            Log.e("arguments_missing", e.toString());
        }

        return inflater.inflate(R.layout.fragment_fix_appointment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        chooseDate = (EditText) view.findViewById(R.id.editTextDatePicker);
        //recyclerView = (RecyclerView) view.findViewById(R.id.recylerViewFixAppointment);
        shift1_spinner = (Spinner) view.findViewById(R.id.shift1_spinner);
        shift2_spinner = (Spinner) view.findViewById(R.id.shift2_spinner);
        bookNow = (Button) view.findViewById(R.id.book_apptnmt_fixAptmnt);
        progressOverlay = view.findViewById(R.id.progress_overlay);

        chooseDate.setInputType(InputType.TYPE_NULL);
        chooseDate.setTextIsSelectable(true);

        /*myAdapter = new AvailableTimeRecyclerAdapter(appointmentsList);
        //datePicker = (DatePicker) view.findViewById(R.id.datePicker_fix_appointment);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(myAdapter);*/

        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        //


        chooseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DatePickerDialog dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {

                        chooseDate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                        fetchTimings();

                    }
                }, mYear, mMonth, mDay);
                dialog.getDatePicker().setMinDate(new Date().getTime());
                dialog.show();

            }
        });


        bookNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(Html.fromHtml("<font color='#FF7F27'>Confirm Appointment and Pay?</font>"));
                builder.setMessage("Date: " + bookDate + "\nTime: " + timeFinal + "\nConsultation Fee: "+fee)
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // call service
                                //confirmAppointment();
                                //Toast.makeText(getActivity(), "Appointment Confirmed", Toast.LENGTH_SHORT).show();
                                dialog.cancel();
                                resetSpinners();
                                confirmAppointment(appt_type);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    private void resetSpinners() {
        shift1_spinner.setAdapter(null);
        shift2_spinner.setAdapter(null);
    }

    private void confirmAppointment(String appt_type) {
        ProgressBarUtils.animateView(progressOverlay, View.INVISIBLE, 0.4f, 200);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("type", appt_type);
        params.put("bookdate", bookDate);// enquiry date
        params.put("appointmenttime", timeFinal);
        params.put("fromChannel_id", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS_PATIENT_CHAT, MODE_PRIVATE).getInt("patient_channel_id", 0));
        params.put("id", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS_PATIENT, MODE_PRIVATE).getInt("patient_id", 0)); // patient id
        params.put("doctor_id", doctor_id);
        params.put("patient_username", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS_PATIENT, MODE_PRIVATE).getString("pusername", null));
        params.put("doctor_username", doctor_username);
        params.put("device", deviceOs);
        params.put("version", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("AppVersion", null));
        params.put("ipaddress", new Splash_Screen().getLocalIpAddress());
        params.put("udid", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("UDID", null));
        params.put("sha", sha);
        client.post(URL_PATIENT_OPERATION, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                String jsonResponse = response.toString();
                Log.d("json_confirm_apptmnt", jsonResponse);
                if (response.optInt("success") == 1) {
                    Snackbar.make(getView(), "Request Sent To Doctor!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
                    getActivity().getSupportFragmentManager().popBackStack();
                    //getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.patient_fragment_container, new PatientHomepage()).addToBackStack(null).commit();
                } else if (response.optInt("success") == 2) {
                    //
                    Snackbar.make(getView(), "Booking Failed!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
                } else if (response.optInt("success") == 3) {
                    //
                    ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
                } else if (response.optInt("success") == 4) {
                    //
                    Snackbar.make(getView(), "Insufficient Balance!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
                } else if (response.optInt("success") == 5) {
                    //
                    Snackbar.make(getView(), "Booking Failed! Network error!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
            }
        });


    }

    private void fetchTimings() {

        ProgressBarUtils.animateView(progressOverlay, View.VISIBLE, 0.4f, 200);

        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams params = new RequestParams();
        params.put("type", "checkconsultation");
        params.put("id", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getInt("patient_id", 0));// patient id
        params.put("doctor_id", doctor_id); // doctor id
        params.put("checkdate", chooseDate.getText().toString()); // doctor id
        params.put("device", deviceOs);
        params.put("version", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("AppVersion", null));
        params.put("ipaddress", new Splash_Screen().getLocalIpAddress());
        params.put("udid", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("UDID", null));
        params.put("sha", sha);

        client.post(URL_PATIENT_OPERATION, params, new JsonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                String JsonStatus = response.optString("success");
                String jsonResult = response.toString();
                Log.d("docTimingsView_json", jsonResult);

                switch (JsonStatus) {
                    case "1":
                        if (response.optString("apointments") != null) {
                            JSONArray shift1 = new JSONArray();
                            JSONArray shift2 = new JSONArray();
                            JSONObject appointments = new JSONObject();
                            String aptime, available;
                            try {
                                appointments = response.getJSONObject("apointments");
                                Log.d("apptmnts_json", String.valueOf(appointments));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Log.d("noOfShifts", String.valueOf(appointments.length()));

                            try {
                                shift1 = appointments.getJSONArray("shift1");
                                shift2 = appointments.getJSONArray("shift2");
                                Log.d("shift1_array", String.valueOf(shift1));
                                Log.d("shift2_array", String.valueOf(shift2));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            int count = 0;
                            int array_count = 0;
                            shift1_array = new String[shift1.length()];
                            while (count < shift1.length()) {
                                try {
                                    JSONObject jsonObject = shift1.getJSONObject(count);
                                    aptime = jsonObject.getString("aptime");
                                    available = jsonObject.getString("available");

                                    Log.d("aptime", aptime);
                                    Log.d("available", available);

                                    if (available.equals("true")) {
                                        //array_count= 0;
                                        String[] strArray = aptime.split(" ");
                                        // get bookDate and apptTime
                                        bookDate = strArray[0];
                                        apptTime = strArray[1];
                                        // put the value into the string array
                                        shift1_array[array_count] = apptTime;
                                        array_count++;
                                    }


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                count++;


                            }

                            int count2 = 0;
                            int array_count2 = 0;
                            shift2_array = new String[shift2.length()];
                            while (count2 < shift2.length()) {
                                try {
                                    JSONObject jsonObject = shift2.getJSONObject(count2);
                                    aptime = jsonObject.getString("aptime");
                                    available = jsonObject.getString("available");

                                    Log.d("aptime", aptime);
                                    Log.d("available", available);

                                    if (available.equals("true")) {
                                        //array_count= 0;
                                        String[] strArray = aptime.split(" ");
                                        // get bookDate and apptTime
                                        bookDate = strArray[0];
                                        apptTime = strArray[1];
                                        // put the value into the string array
                                        shift2_array[array_count2] = apptTime;
                                        array_count2++;
                                    }


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                count2++;


                            }
                            //Log.d("shift1array", shift1_array.toString());
                            Log.d("shift1array", Arrays.deepToString(shift1_array));
                            // set adapter for the shift1_spinner spinner
                            shift1_adapter = new ArrayAdapter<String>(getActivity(), R.layout.gender_spinner, shift1_array);
                            shift1_spinner.setAdapter(shift1_adapter);
                            shift1_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                                    timeFinal = shift1_array[position];
                                    TextView selectedText = (TextView) adapterView.getChildAt(0);
                                    if (selectedText != null) {
                                        selectedText.setTextColor(getResources().getColor(R.color.app_dark_blue));
                                        //shift2_spinner.setBackgroundResource(R.color.app_blue);

                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {

                                }
                            });

                            Log.d("shift2array", Arrays.deepToString(shift2_array));
                            // set adapter for the shift1_spinner spinner
                            shift2_adapter = new ArrayAdapter<String>(getActivity(), R.layout.gender_spinner, shift2_array);
                            shift2_spinner.setAdapter(shift2_adapter);
                            shift2_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                                    timeFinal = shift2_array[position];
                                    TextView selectedText = (TextView) adapterView.getChildAt(0);
                                    if (selectedText != null) {
                                        selectedText.setTextColor(getResources().getColor(R.color.app_dark_blue));
                                        //shift1_spinner.setBackgroundResource(R.color.app_blue);
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {

                                }
                            });
                        }

                        ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
                        break;

                    case "2":
                        // database error
                        ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
                        break;

                    case "3":
                        // No results found
                        ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
                        break;

                    default:
                        ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
                        break;
                }


            }


        });

    }


    // method to clear the recycler view
    public void clearRecyclerView() {
        int size = this.appointmentsList.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.appointmentsList.remove(0);
            }


        }
    }

}
