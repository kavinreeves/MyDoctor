package com.example.mydoctor.mydoctor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.mydoctor.mydoctor.Login.Splash_Screen;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

import static com.example.mydoctor.mydoctor.Login.CommonParams.MY_PREFS_COMMON_PARAMS;
import static com.example.mydoctor.mydoctor.Login.CommonParams.MY_PREFS_COMMON_PARAMS_PATIENT_CHAT;
import static com.example.mydoctor.mydoctor.Login.CommonParams.URL_CHAT;
import static com.example.mydoctor.mydoctor.Login.CommonParams.URL_PATIENT_OPERATION;
import static com.example.mydoctor.mydoctor.Login.CommonParams.deviceOs;
import static com.example.mydoctor.mydoctor.Login.CommonParams.sha;

/**
 * Created by Wenso on 8/18/2016.
 */
public class DoctorProfile extends Activity {

    Button chatNow, getAppointment;
    int doctor_id, patient_id, patient_channelId, doctor_channelId;
    String doc_user_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_profile);
        // initialising widgets
        chatNow = (Button) findViewById(R.id.user_request_chat);
        getAppointment = (Button) findViewById(R.id.user_request_appointment);

        // get the doctor details from intent
        Intent intent = getIntent();
        String name = intent.getStringExtra("patName");
        doctor_id = Integer.parseInt(intent.getStringExtra("doctor_id"));
        String specialisation = intent.getStringExtra("specialisation");
        String experience = intent.getStringExtra("experience");
        String city = intent.getStringExtra("city");
        doc_user_name = "ShajahanBasha4983";

        Log.d("patName", name);
        Log.d("doctor_id", String.valueOf(doctor_id));
        Log.d("specialisation", specialisation);
        Log.d("experience", experience);
        Log.d("city", city);

        chatNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendChatRequest(doctor_id, doc_user_name);
            }
        });


    }

    // to request chat
    public void sendChatRequest(final int doctorID, String doctorUserName) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("type", "request_chat");
        params.put("id", getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getInt("patient_id", 0)); // sender channel
        params.put("pusername", getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("pusername", null));
        params.put("vid", doctorID); // doctors reg id
        params.put("dusername", doctorUserName); // doctor username
        params.put("device", deviceOs); // sender username
        params.put("version", getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("AppVersion", null));
        params.put("ipaddress", new Splash_Screen().getLocalIpAddress());
        params.put("udid", getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("UDID", null));
        params.put("sha", sha);
        client.addHeader("Content-Type", "application/x-www-form-urlencoded");
        client.post(URL_PATIENT_OPERATION, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                String jsonResponse = response.toString();
                Log.d("json reqchat 1", jsonResponse);
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
                        params.put("channel_id", getSharedPreferences(MY_PREFS_COMMON_PARAMS_PATIENT_CHAT, MODE_PRIVATE).getInt("patient_channel_id", 0));
                        Log.d("pat_chanelId params", String.valueOf(getSharedPreferences(MY_PREFS_COMMON_PARAMS_PATIENT_CHAT, MODE_PRIVATE).getInt("patient_channel_id", 0)));
                        params.put("userid", getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getInt("patient_id", 0));
                        params.put("toChannel_id", doctor_channel_id); // doctors channelid  ----------  got this from success 1
                        params.put("nick", getSharedPreferences(MY_PREFS_COMMON_PARAMS_PATIENT_CHAT, MODE_PRIVATE).getString("patient_nick_name", null));
                        Log.d("sender nick params", getSharedPreferences(MY_PREFS_COMMON_PARAMS_PATIENT_CHAT, MODE_PRIVATE).getString("patient_nick_name", null));
                        params.put("apt_id", requestId);
                        params.put("fromuserid", getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getInt("patient_id", 0));
                        params.put("touserid", doctorID);
                        client.post(URL_CHAT, params, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                super.onSuccess(statusCode, headers, response);
                                String jsonResponse = response.toString();
                                Log.d("json reqchat 2 ", jsonResponse);
                                String jsonStatus = response.optString("success");
                                switch (jsonStatus) {

                                    case "1":
                                        Toast.makeText(DoctorProfile.this, "Request Successfully sent!", Toast.LENGTH_SHORT).show();
                                        break;

                                    case "2":
                                        // db error
                                        break;
                                    default:
                                        break;
                                }
                            }
                        });

                        break;

                    case "4":
                        Toast.makeText(DoctorProfile.this, "Insufficient Balance!!", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }


            }
        });


    }

    public void getDoctorFullDetails() {

        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams params = new RequestParams();
        params.put("type", "showprofile");
        params.put("id", "search");// patient id
        params.put("vid", doctor_id); // doctor id
        params.put("device", deviceOs);
        params.put("version", "1.0");
        params.put("ipaddress", "192.168.0.1");
        params.put("udid", "10981243");
        params.put("sha", sha);

        client.post(URL_PATIENT_OPERATION, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray jsonArray = new JSONArray();
                JSONObject jsonObject;
                String fName, lName, doctor_id, specialization, experience, city;
                String JsonStatus = response.optString("success");
                String jsonResult = response.toString();
                Log.d("response", jsonResult);

                switch (JsonStatus) {
                    case "1":

                        fName = response.optString("first_name");

                        Log.d("fname", fName);

                        break;

                    case "2":
                        // database error
                        break;

                    case "3":
                        // No results found
                        break;
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }
}
