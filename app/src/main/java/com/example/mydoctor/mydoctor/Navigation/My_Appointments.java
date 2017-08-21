package com.example.mydoctor.mydoctor.Navigation;

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

import com.example.mydoctor.mydoctor.AdapterHelpers.RecyclerItemClickListener;
import com.example.mydoctor.mydoctor.AdapterHelpers.UserAppointmentsInfo;
import com.example.mydoctor.mydoctor.AdapterHelpers.UserApptmntsRecyclerAdapter;
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
import static com.example.mydoctor.mydoctor.Login.CommonParams.MY_PREFS_COMMON_PARAMS_PATIENT;
import static com.example.mydoctor.mydoctor.Login.CommonParams.URL_PATIENT_OPERATION;
import static com.example.mydoctor.mydoctor.Login.CommonParams.deviceOs;
import static com.example.mydoctor.mydoctor.Login.CommonParams.sha;

public class My_Appointments extends Fragment implements View.OnClickListener {

    private RecyclerView recyclerViewappointments;
    private List<UserAppointmentsInfo> userAppointmentsInfoList = new ArrayList<>();
    private UserApptmntsRecyclerAdapter myAdapter;
    private View progressOverlay;
    private View mView;
    private TextView noResult;
    Button month, today, all;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        return inflater.inflate(R.layout.my_appointmentss, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        mView = view.findViewById(R.id.rLmyappointments);
        ((RadioGroup) view.findViewById(R.id.toggleGroupPatApptmnts)).setOnCheckedChangeListener(ToggleListener);
        month = (Button) view.findViewById(R.id.btn_patApp_month);
        noResult = (TextView) view.findViewById(R.id.myAppntmts_noresult);
        today = (Button) view.findViewById(R.id.btn_patApp_today);
        all = (Button) view.findViewById(R.id.btn_patApp_all);
        progressOverlay = view.findViewById(R.id.progress_overlay);

        month.setOnClickListener(this);
        today.setOnClickListener(this);
        all.setOnClickListener(this);


        recyclerViewappointments = (RecyclerView) view.findViewById(R.id.recyclerView_user_appointments);
        myAdapter = new UserApptmntsRecyclerAdapter(userAppointmentsInfoList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerViewappointments.setLayoutManager(mLayoutManager);
        recyclerViewappointments.setItemAnimator(new DefaultItemAnimator());
        recyclerViewappointments.setAdapter(myAdapter);


        // add click listener
        recyclerViewappointments.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), recyclerViewappointments, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                //Toast.makeText(getActivity(), "Check Appointment", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onLongItemClick(View view, int position) {

                //Toast.makeText(getActivity(), "Long Press !!!", Toast.LENGTH_SHORT).show();
                // show dialog
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                Toast.makeText(getActivity(), "Cannot Cancel Appointment!", Toast.LENGTH_SHORT).show();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
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

        if (selected_btn_id == R.id.btn_patApp_today) {

            getBookings("today");
        } else if (selected_btn_id == R.id.btn_patApp_month) {

            getBookings("month");
        } else {

            getBookings("all");
        }

    }

    private void getBookings(String interval) {

        ProgressBarUtils.animateView(progressOverlay, View.VISIBLE, 0.4f, 200);
        noResult.setVisibility(View.INVISIBLE);

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("type", "viewallappointments");
        params.put("id", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS_PATIENT, MODE_PRIVATE).getInt("patient_id", 0));
        params.put("interval", interval); // today / week / month / year / all
        Log.d("intervalChat", interval);

        params.put("device", deviceOs);
        params.put("version", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("AppVersion", null));
        params.put("ipaddress", new Splash_Screen().getLocalIpAddress());
        params.put("udid", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("UDID", null));
        params.put("sha", sha);
        client.post(URL_PATIENT_OPERATION, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                String docChatHistory_json = response.toString();
                Log.d("patAppointments_json ", docChatHistory_json);

                JSONArray jArray = response.optJSONArray("appointments");
                JSONObject jObject = new JSONObject();
                String aptId, aptType, dateTime, docName, qual, lastName, spec, age, city, gender, photoUrl, patientId, patUsername, status, prescription;

                int jsonStatus = response.optInt("success");
                switch (jsonStatus) {

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
                                    dateTime = jObject.optString("apt_datetime");
                                    docName = jObject.optString("doctor_name");
                                    spec = jObject.optString("doctor_specialization");
                                    qual = jObject.optString("doctor_qualification");
                                    city = jObject.optString("personalCity");

                                    prescription = jObject.optString("prescription");
                                   /* if (prescription==null){
                                        prescription = "no";
                                    }*/
                                    photoUrl = jObject.optString("photo");
                                    patientId = jObject.optString("patient_id");
                                    patUsername = jObject.optString("username");
                                    status = jObject.optString("status");

                                    //
                                    UserAppointmentsInfo appointmentsInfo = new UserAppointmentsInfo(dateTime, dateTime, docName + " " + qual, spec, city, photoUrl, status, aptType);
                                    userAppointmentsInfoList.add(appointmentsInfo);
                                    myAdapter.notifyDataSetChanged();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }
                        }
                        ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
                        break;
                    case 2:
                       /* Snackbar.make(mView, "No results Found", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();*/
                        noResult.setVisibility(View.VISIBLE);
                        clearRecyclerView();

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

    private void clearRecyclerView() {
        int size = this.userAppointmentsInfoList.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.userAppointmentsInfoList.remove(0);
            }


        }
    }


}
