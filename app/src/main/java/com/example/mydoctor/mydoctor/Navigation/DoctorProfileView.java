package com.example.mydoctor.mydoctor.Navigation;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mydoctor.mydoctor.AdapterHelpers.DownloadImageTask;
import com.example.mydoctor.mydoctor.Login.Splash_Screen;
import com.example.mydoctor.mydoctor.Patient.FixAppointment;
import com.example.mydoctor.mydoctor.R;
import com.example.mydoctor.mydoctor.Utils.ProgressBarUtils;
import com.example.mydoctor.mydoctor.VideoCalling.SinchLogin;
import com.github.ybq.android.spinkit.SpinKitView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

import static android.content.Context.MODE_PRIVATE;
import static com.example.mydoctor.mydoctor.Login.CommonParams.MY_PREFS_COMMON_PARAMS;
import static com.example.mydoctor.mydoctor.Login.CommonParams.MY_PREFS_COMMON_PARAMS_PATIENT_CHAT;
import static com.example.mydoctor.mydoctor.Login.CommonParams.URL_CHAT;
import static com.example.mydoctor.mydoctor.Login.CommonParams.URL_PATIENT_OPERATION;
import static com.example.mydoctor.mydoctor.Login.CommonParams.deviceOs;
import static com.example.mydoctor.mydoctor.Login.CommonParams.sha;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class DoctorProfileView extends Fragment {

    int doctor_userId;
    String doctor_username, online_status, doc_fullName, isFavDoc, isOnline, dfee, vfee, cfee;
    ImageView docprofilePic;
    TextView docName, docExp, docSpec, aboutDoc, directFee, videoFee, chatFee, city;
    Button chatNow, getAppointment, videoCallNow, goBack, chatLater, videoCallLater;
    ImageButton addFavDoctor;
    SpinKitView spinKitView;
    View progressOverlay;

    public DoctorProfileView() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Retrieve the value
        String doctor_id = getArguments().getString("doctor_id");
        doctor_userId = Integer.parseInt(doctor_id);
        online_status = getArguments().getString("online_status");
        doc_fullName = getArguments().getString("doctor_name");
        Log.d("online_status", online_status);
        //doctor_username = getArguments().getString("doctor_name");
        Log.d("doctor_id", String.valueOf(doctor_userId));
        //Log.d("doctor_username", doctor_username);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_doctor_profile_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // get all widgets
        docprofilePic = (ImageView) view.findViewById(R.id.docImageDocProfileView);
        docName = (TextView) view.findViewById(R.id.docName_docProfileView);
        docSpec = (TextView) view.findViewById(R.id.docSpec_docProfileView);
        docExp = (TextView) view.findViewById(R.id.docExp_docProfileView);
        city = (TextView) view.findViewById(R.id.cityDocprofview);
        aboutDoc = (TextView) view.findViewById(R.id.docDesc_docProfileView);
        videoFee = (TextView) view.findViewById(R.id.videoCallFeeTV);
        chatFee = (TextView) view.findViewById(R.id.chatFeeTV);
        directFee = (TextView) view.findViewById(R.id.appointmentFeeTV);

        progressOverlay = view.findViewById(R.id.progress_overlay);

        chatNow = (Button) view.findViewById(R.id.user_request_chatBtn);
        getAppointment = (Button) view.findViewById(R.id.face_face_book_appointment);
        addFavDoctor = (ImageButton) view.findViewById(R.id.addDocAsFavourite);
        videoCallNow = (Button) view.findViewById(R.id.user_request_videoBtn);
        goBack = (Button) view.findViewById(R.id.docProfView_backButton);
        chatLater = (Button) view.findViewById(R.id.user_request_chatLaterBtn);
        videoCallLater = (Button) view.findViewById(R.id.user_request_videoChatLaterBtn);


        // call service to get full profile
        getDoctorFullDetails(doctor_userId);
        // button click events
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //getActivity().getSupportFragmentManager().popBackStack();
                onBackPressed();
            }
        });
        chatNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (online_status.equals("online")) {
                    sendChatRequest(doctor_userId);
                } else {
                    Snackbar.make(getView(), "Doctor Offline!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    chatNow.setEnabled(false);
                }
            }
        });
        getAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Put the value
                FixAppointment fragment = new FixAppointment();
                Bundle args = new Bundle();
                args.putInt("doctor_id", doctor_userId);
                args.putString("doctor_username", doctor_username);
                args.putString("appt_type", "bookappointment");
                args.putString("fee", dfee);
                //args.putString("doctor_name", patName);
                fragment.setArguments(args);
                // navigate to next fragment
                getFragmentManager().beginTransaction().replace(R.id.patient_fragment_container, fragment).addToBackStack(null).commit();
            }
        });
        addFavDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // show dialog and call service to add doctor as favourite

                addFav();

            }
        });
        videoCallNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), SinchLogin.class);
                intent.putExtra("docName", doctor_username);
                startActivity(intent);
                //startActivity(new Intent(getApplicationContext(), ConnectCall.class));


            }
        });
        chatLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Put the value
                FixAppointment fragment = new FixAppointment();
                Bundle args = new Bundle();
                args.putInt("doctor_id", doctor_userId);
                args.putString("doctor_username", doctor_username);
                args.putString("appt_type", "bookchatappointment");
                args.putString("fee", cfee);
                fragment.setArguments(args);
                // navigate to next fragment
                getFragmentManager().beginTransaction().replace(R.id.patient_fragment_container, fragment).addToBackStack(null).commit();
            }
        });
        videoCallLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Put the value
                FixAppointment fragment = new FixAppointment();
                Bundle args = new Bundle();
                args.putInt("doctor_id", doctor_userId);
                args.putString("doctor_username", doctor_username);
                args.putString("fee", vfee);
                args.putString("appt_type", "bookvideoappointment");
                fragment.setArguments(args);
                // navigate to next fragment
                getFragmentManager().beginTransaction().replace(R.id.patient_fragment_container, fragment).addToBackStack(null).commit();
            }
        });
    }


    private void addFav() {

        ProgressBarUtils.animateView(progressOverlay, View.VISIBLE, 0, 200);

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("type", "addfavorites");
        params.put("id", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getInt("patient_id", 0));
        params.put("uname", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("pusername", "nodata"));
        params.put("vid", doctor_userId);
        params.put("vuname", doctor_username);
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
                int JsonStatus = response.optInt("success");
                Log.d("addfav", jsonResponse);
                if (JsonStatus == 1) {
                    addFavDoctor.setBackgroundResource(R.drawable.fav_added);
                    Snackbar.make(getView(), "Added to My Favourites List!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    ProgressBarUtils.animateView(progressOverlay, View.GONE, 0, 200);


                } else if (JsonStatus == 2) {
                    Snackbar.make(getView(), "Already in Favourites List!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    ProgressBarUtils.animateView(progressOverlay, View.GONE, 0, 200);

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                ProgressBarUtils.animateView(progressOverlay, View.GONE, 0, 200);
            }
        });

    }

    public void getDoctorFullDetails(int doctor_id) {

        ProgressBarUtils.animateView(progressOverlay, View.VISIBLE, 0.4f, 200);

        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams params = new RequestParams();
        params.put("type", "showprofile");
        params.put("id", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getInt("patient_id", 0));// patient id
        params.put("vid", doctor_id); // doctor id
        params.put("device", deviceOs);
        params.put("version", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("AppVersion", null));
        params.put("ipaddress", new Splash_Screen().getLocalIpAddress());
        params.put("udid", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("UDID", null));
        params.put("sha", sha);

        client.post(URL_PATIENT_OPERATION, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                String JsonStatus = response.optString("success");
                String jsonResult = response.toString();
                Log.d("docProfileView_json", jsonResult);

                switch (JsonStatus) {
                    case "1":
                        // populate the view
                        dfee = response.optString("consultation_price");
                        cfee = response.optString("chat_price");
                        vfee = response.optString("videochat_price");
                        new DownloadImageTask(docprofilePic).execute(response.optString("photo"));
                        docName.setText("Dr " + doc_fullName);
                        docSpec.setText("Specialization: " + response.optString("specialization"));
                        docExp.setText("Experience: " + response.optString("experience") + "years");
                        videoFee.setText("Video Chat: " + vfee);
                        chatFee.setText("Text Chat: " + cfee);
                        directFee.setText("Face-Face Appointment: " + dfee);
                        aboutDoc.setText(response.optString("about"));
                        city.setText("City: " + response.optString("address3"));
                        isFavDoc = response.optString("favorite");
                        isOnline = response.optString("onlinestatus");

                        if (isFavDoc.equals("true")) {

                            addFavDoctor.setBackgroundResource(R.drawable.fav_added);
                        } else addFavDoctor.setBackgroundResource(R.drawable.fav_not_added);

                        if (isOnline.equals("online")) {

                            chatNow.setEnabled(true);
                            videoCallNow.setEnabled(true);
                        }
                        //aboutDoc.setText(response.optString("about"));

                        // store doc username in SP

                        doctor_username = response.optString("username");

                        ProgressBarUtils.animateView(progressOverlay, View.GONE, 0, 200);
                        break;

                    case "2":
                        // database error
                        ProgressBarUtils.animateView(progressOverlay, View.GONE, 0, 200);
                        break;

                    case "3":
                        // No results found
                        ProgressBarUtils.animateView(progressOverlay, View.GONE, 0, 200);
                        break;

                    default:
                        ProgressBarUtils.animateView(progressOverlay, View.GONE, 0, 200);
                        break;

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                ProgressBarUtils.animateView(progressOverlay, View.GONE, 0, 200);
            }


        });
    }

    // to request chat
    public void sendChatRequest(final int doctorID) {

        ProgressBarUtils.animateView(progressOverlay, View.VISIBLE, 0, 200);

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("type", "request_chat");
        params.put("id", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getInt("patient_id", 0)); // sender channel
        params.put("pusername", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("pusername", null));
        params.put("vid", doctorID); // doctors reg id
        params.put("dusername", doctor_username); // doctor username
        params.put("device", deviceOs); // sender username
        params.put("version", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("AppVersion", null));
        params.put("ipaddress", new Splash_Screen().getLocalIpAddress());
        params.put("udid", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("UDID", null));
        params.put("sha", sha);
        client.addHeader("Content-Type", "application/x-www-form-urlencoded");
        client.post(URL_PATIENT_OPERATION, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                String jsonResponse = response.toString();
                Log.d("json_reqchat_1", jsonResponse);
                String jsonStatus = response.optString("success");

                switch (jsonStatus) {
                    case "1":
                        // call another chat request
                        int requestId = response.optInt("request_id");
                        Log.d("requestId", String.valueOf(requestId));
                        int doctor_channel_id = response.optInt("doctor_channel_id");
                        Log.d("doctor_channel_id", String.valueOf(doctor_channel_id));
                        // call next service
                        AsyncHttpClient client = new AsyncHttpClient();
                        RequestParams params = new RequestParams();
                        params.put("type", "chatrequest");
                        params.put("channel", "doctor"); // receiver channel
                        params.put("channel_id", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS_PATIENT_CHAT, MODE_PRIVATE).getInt("patient_channel_id", 0));
                        Log.d("pat_chanelId params", String.valueOf(getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS_PATIENT_CHAT, MODE_PRIVATE).getInt("patient_channel_id", 0)));
                        params.put("userid", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getInt("patient_id", 0));
                        params.put("toChannel_id", doctor_channel_id); // doctors channelid  ----------  got this from success 1
                        params.put("nick", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS_PATIENT_CHAT, MODE_PRIVATE).getString("patient_nick_name", null));
                        Log.d("sender nick params", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS_PATIENT_CHAT, MODE_PRIVATE).getString("patient_nick_name", null));
                        params.put("apt_id", requestId);
                        params.put("fromuserid", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getInt("patient_id", 0));
                        params.put("touserid", doctorID);
                        client.post(URL_CHAT, params, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                super.onSuccess(statusCode, headers, response);
                                String jsonResponse = response.toString();
                                Log.d("json_reqchat_2 ", jsonResponse);
                                String jsonStatus = response.optString("success");
                                switch (jsonStatus) {

                                    case "1":
                                        //Toast.makeText(getActivity(), "Request Successfully sent!", Toast.LENGTH_SHORT).show();
                                        Snackbar.make(getView(), "Request Successfully sent!", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                        chatNow.setEnabled(false);

                                        ProgressBarUtils.animateView(progressOverlay, View.GONE, 0, 200);
                                        break;

                                    case "2":
                                        // db error
                                        ProgressBarUtils.animateView(progressOverlay, View.GONE, 0, 200);
                                        break;
                                    default:
                                        ProgressBarUtils.animateView(progressOverlay, View.GONE, 0, 200);
                                        break;
                                }
                            }
                        });
                        //spinKitView.setVisibility(View.INVISIBLE);
                        ProgressBarUtils.animateView(progressOverlay, View.GONE, 0, 200);
                        break;

                    case "4":
                        Toast.makeText(getActivity(), "Insufficient Balance!!", Toast.LENGTH_SHORT).show();

                        ProgressBarUtils.animateView(progressOverlay, View.GONE, 0, 200);
                        break;
                    default:
                        ProgressBarUtils.animateView(progressOverlay, View.GONE, 0, 200);
                        break;
                }


            }
        });


    }

    public void onBackPressed() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        fm.popBackStack();
    }
}
