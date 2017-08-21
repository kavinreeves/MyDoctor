package com.example.mydoctor.mydoctor.Doctor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mydoctor.mydoctor.AdapterHelpers.ChatMessage;
import com.example.mydoctor.mydoctor.AdapterHelpers.ThreadAdapter;
import com.example.mydoctor.mydoctor.Login.Splash_Screen;
import com.example.mydoctor.mydoctor.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

import static com.example.mydoctor.mydoctor.Login.CommonParams.MY_PREFS_COMMON_PARAMS;
import static com.example.mydoctor.mydoctor.Login.CommonParams.MY_PREFS_COMMON_PARAMS_DOCTOR;
import static com.example.mydoctor.mydoctor.Login.CommonParams.MY_PREFS_COMMON_PARAMS_DOCTOR_CHAT;
import static com.example.mydoctor.mydoctor.Login.CommonParams.URL_CHAT;
import static com.example.mydoctor.mydoctor.Login.CommonParams.URL_DOCTOR_OPERATION;
import static com.example.mydoctor.mydoctor.Login.CommonParams.deviceOs;
import static com.example.mydoctor.mydoctor.Login.CommonParams.sha;

public class DoctorChatRoom extends AppCompatActivity {

    private IntentFilter mIntentFilter;
    RecyclerView recyclerView;
    ThreadAdapter myAdapter;
    //ArrayList of messages to store the thread messages
    private ArrayList<ChatMessage> messages;
    //Button to send new message on the thread
    private ImageButton buttonSend, btnEndChat;

    //EditText to send new message on the thread
    private EditText editTextMessage;

    private boolean typingStarted;
    private int patient_userId, patient_channelId, appointment_id, doctor_user_id, doctor_channelId;
    private String doctor_nickName, patientNickName, opponentPhotoUrl;
    private TextView tvTypingStatus, patientName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_doctor_chat_room);

        // get data from INtent and store in SP
        getIntentDataToSP();
        // widgets
        editTextMessage = (EditText) findViewById(R.id.editTextDocChatRoom);
        buttonSend = (ImageButton) findViewById(R.id.buttonSendDocChatRoom);
        btnEndChat = (ImageButton) findViewById(R.id.endChatDocChatRoom);
        tvTypingStatus = (TextView) findViewById(R.id.tvTypingStatusDoc);
        patientName = (TextView) findViewById(R.id.tvChatHeaderPatName);
        patientName.setText(patientNickName);

        // store all params
        doctor_user_id = getSharedPreferences(MY_PREFS_COMMON_PARAMS_DOCTOR, MODE_PRIVATE).getInt("doctor_id", 0);
        doctor_channelId = getSharedPreferences(MY_PREFS_COMMON_PARAMS_DOCTOR_CHAT, MODE_PRIVATE).getInt("doctor_channel_id", 0);
        doctor_nickName = getSharedPreferences(MY_PREFS_COMMON_PARAMS_DOCTOR_CHAT, MODE_PRIVATE).getString("doc_nick_name", "null");
        // check params
        Log.d("DCRoom_doctor_user_id", String.valueOf(doctor_user_id));
        Log.d("DCRom_doctor_channel_id", String.valueOf(doctor_channelId));
        Log.d("DCRoom_appointment_id", String.valueOf(appointment_id));
        Log.d("DCRoom_doctor_nickName", doctor_nickName);
        Log.d("DCRoom_patient_user_id", String.valueOf(patient_userId));
        Log.d("DCRoom_patient_ch_id", String.valueOf(patient_channelId));
        Log.d("DCRoom_patient_nickname", patientNickName);


                // register receiver
                mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("ChatMessages");
        //Initializing recyclerview
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_doctorChatRoom);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(myAdapter);
        //Initializing message arraylist
        messages = new ArrayList<>();
        myAdapter = new ThreadAdapter(this, messages); // enter your userid here


        editTextMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString()) && s.toString().trim().length() == 1) {
                    //Log.i(TAG, “typing started event…”);
                    typingStarted = true;
                    //send typing started status
                    typingStartedService();
                    Log.d("typing started", "true");
                } else if (s.toString().trim().length() == 0 && typingStarted) {
                    //Log.i(TAG, “typing stopped event…”);
                    typingStarted = false;
                    Log.d("typing started", "false");
                    //send typing stopped status
                    typingEndedService();
                }
            }
        });
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  call service
                if (!isEmpty(editTextMessage)) {
                    sendMessages(editTextMessage.getText().toString());
                }else Toast.makeText(DoctorChatRoom.this, "Empty Text!", Toast.LENGTH_SHORT).show();
            }
        });
        btnEndChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // call method
                endChatDoctor();
            }
        });

    }
    private boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0)
            return false;

        return true;
    }

    private void typingEndedService() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("type", "typingremove");
        params.put("channel", "patient");// opposite user channel
        params.put("channel_id", doctor_channelId);
        params.put("userid", doctor_user_id);
        params.put("toChannel_id", patient_channelId);
        params.put("nick", doctor_nickName);
        params.put("appointment_id", appointment_id);
        params.put("fromuserid", doctor_user_id);
        params.put("touserid", patient_userId);
        client.post(URL_CHAT, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                String jsonResponse = response.toString();
                Log.d("typing_off", jsonResponse);
            }
        });
    }

    private void typingStartedService() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("type", "typing");
        params.put("channel", "patient");// opposite user channel
        params.put("channel_id", doctor_channelId);
        params.put("userid", doctor_user_id);
        params.put("toChannel_id", patient_channelId);
        params.put("nick", doctor_nickName);
        params.put("appointment_id", appointment_id);
        params.put("fromuserid",doctor_user_id);
        params.put("touserid", patient_userId);
        client.post(URL_CHAT, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                String jsonResponse = response.toString();
                Log.d("typing_on", jsonResponse);
            }
        });
    }

    private void endChatDoctor() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("type", "end_chat");
        params.put("id", doctor_user_id);
        params.put("request_id", appointment_id);
        params.put("device", deviceOs); // sender username
        params.put("version", getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("AppVersion", null));
        params.put("ipaddress", new Splash_Screen().getLocalIpAddress());
        params.put("udid", getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("UDID", null));
        params.put("sha", sha);
        client.post(URL_DOCTOR_OPERATION, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d("json_endchat_doc", response.toString());
                int jsonStatus = response.optInt("success");
                if (jsonStatus == 1) {
                    //Toast.makeText(DoctorChatRoom.this, "Session will be closed after patient ends it!", Toast.LENGTH_SHORT).show();

                   finish();
                } else if (jsonStatus == 3) {
                    //Toast.makeText(DoctorChatRoom.this, "Session already end by patient!", Toast.LENGTH_SHORT).show();

                    finish();

                } else {
                    // error
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });


    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        // show a dialog to leave chatroom

       /* AlertDialog.Builder builder = new AlertDialog.Builder(DoctorChatRoom.this);
        builder.setMessage("End Chat Now?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // navigate to the chatroom
                        endChatDoctor();

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();*/

            new AlertDialog.Builder(this)
                    .setMessage("Are you sure you want to exit?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            endChatDoctor();
                            DoctorChatRoom.super.onBackPressed();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();

        }


    private void getIntentDataToSP() {

        Intent intent = getIntent();
        patient_userId = intent.getIntExtra("patient_userId", 0);
        patient_channelId = intent.getIntExtra("patient_channelId", 0);
        appointment_id = intent.getIntExtra("apt_id", 0);
        patientNickName = intent.getStringExtra("patientNickName");
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // receive the chat messages here
            String jsonMessages = intent.getStringExtra("message");
            Log.d("docMessagesReceived", jsonMessages);
            try {
                JSONObject obj = new JSONObject(jsonMessages);
                Log.d("json_message_received", String.valueOf(obj));
                String type = obj.optString("type");
                Log.d("type_of_message", type);
                // get values in chat
                String status = obj.optString("status");
                Log.d("receive_or_sent?", status);
                String text = obj.optString("text");
                String time = obj.optString("time");
                String msg_id = obj.optString("msg_id");
                String apt_id = obj.optString("appointment_id");
                String patientName = obj.optString("from");
                if (type.equals("message")) {
                    // set the values in adapter
                    ChatMessage messagObject = new ChatMessage(1, text, time, patientName, status, opponentPhotoUrl);
                    messages.add(messagObject);
                } else if (type.equals("typing")) {
                    tvTypingStatus.setText("typing...");

                } else if (type.equals("typingremove")) {
                    tvTypingStatus.setText("");
                }

            } catch (JSONException e) {
                e.printStackTrace();

            }
            recyclerView.setAdapter(myAdapter);
            myAdapter.notifyDataSetChanged();
            scrollToBottom();

        }
    };

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    protected void onPause() {
        unregisterReceiver(mReceiver);
        super.onPause();
    }


    private void sendMessages(String text) {


        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("type", "sendmessage");
        params.put("channel", "patient");
        params.put("channel_id", doctor_channelId);
        params.put("userid", doctor_user_id);
        params.put("toChannel_id", patient_channelId);
        params.put("nick", doctor_nickName);
        params.put("msgid", getMsgIdDoctor());// time stamp
        params.put("fromuserid", doctor_user_id);
        params.put("touserid", patient_userId);
        params.put("text", text);
        params.put("apt_id", appointment_id);
        client.post(URL_CHAT, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                String jsonResponse = response.toString();
                Log.d("send_chat_msgDoc", jsonResponse);
                int jsonStatus = response.optInt("success");

                if (jsonStatus == 1) {
                    editTextMessage.setText("");
                    Toast.makeText(DoctorChatRoom.this, "Message Sent !", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }


    //method to scroll the recyclerview to bottom
    private void scrollToBottom() {
        myAdapter.notifyDataSetChanged();
        if (myAdapter.getItemCount() > 1)
            recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, myAdapter.getItemCount() - 1);
    }

    private String getMsgIdDoctor() {
        Long tsLong = System.currentTimeMillis();
        String timeStamp = tsLong.toString();
        Log.d("timestamp", String.valueOf(timeStamp));
        int senderType = 1;
        int senderChannelId = doctor_channelId;
        int receiver = 1;
        int receiverChannelId = patient_channelId;
        int apt_id = appointment_id;
        String msgId = concatenateDigits(timeStamp, senderType, senderChannelId, receiver, receiverChannelId, apt_id);

        return msgId;
    }

    public static String concatenateDigits(String timestamp, int... digits) {
        StringBuilder sb = new StringBuilder(digits.length);
        sb.append(timestamp);
        for (int digit : digits) {
            sb.append(digit);
        }
        return sb.toString();
    }
}
