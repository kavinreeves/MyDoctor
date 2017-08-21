package com.example.mydoctor.mydoctor.Patient;

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
import static com.example.mydoctor.mydoctor.Login.CommonParams.MY_PREFS_COMMON_PARAMS_PATIENT;
import static com.example.mydoctor.mydoctor.Login.CommonParams.MY_PREFS_COMMON_PARAMS_PATIENT_CHAT;
import static com.example.mydoctor.mydoctor.Login.CommonParams.URL_CHAT;
import static com.example.mydoctor.mydoctor.Login.CommonParams.URL_PATIENT_OPERATION;
import static com.example.mydoctor.mydoctor.Login.CommonParams.deviceOs;
import static com.example.mydoctor.mydoctor.Login.CommonParams.sha;

public class PatientChatRoom extends AppCompatActivity {

    private IntentFilter mIntentFilter;
    RecyclerView recyclerView;
    ThreadAdapter myAdapter;
    //ArrayList of messages to store the thread messages
    private ArrayList<ChatMessage> messages;
    //Button to send new message on the thread
    private ImageButton buttonSend, endChat;

    //EditText to send new message on the thread
    private EditText editTextMessage;

    private TextView tvTypingStatus, doctorName;

    private boolean typingStarted;
    // all params for chat
    private String patient_nick_name, doctor_nickName, opponentPhotoUrl;
    private int patient_user_id, doctor_user_id, appointment_id, patient_channel_id, doctor_channel_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_chat_room);
        // get intent data
        getIntentDataToSP();
        // initialise widgets
        buttonSend = (ImageButton) findViewById(R.id.buttonSendPatChatRoom);
        endChat = (ImageButton) findViewById(R.id.endChatPatChatRoom);
        editTextMessage = (EditText) findViewById(R.id.editTextPatChatRoom);
        tvTypingStatus = (TextView) findViewById(R.id.tvTypingStatusPat);
        doctorName = (TextView) findViewById(R.id.tvChatHeaderDocName);
        doctorName.setText(doctor_nickName);

        // show all common data
        patient_user_id = getSharedPreferences(MY_PREFS_COMMON_PARAMS_PATIENT, MODE_PRIVATE).getInt("patient_id", 0);
        patient_channel_id = getSharedPreferences(MY_PREFS_COMMON_PARAMS_PATIENT_CHAT, MODE_PRIVATE).getInt("patient_channel_id", 0);
        patient_nick_name = getSharedPreferences(MY_PREFS_COMMON_PARAMS_PATIENT_CHAT, MODE_PRIVATE).getString("patient_nick_name", null);
        Log.d("PCRoom_patient_user_id", String.valueOf(patient_user_id));
        Log.d("PCRoom_pat_channel_id", String.valueOf(patient_channel_id));
        Log.d("PCRoom_patient_nick", patient_nick_name);
        Log.d("PCRoom_doctor_user_id", String.valueOf(doctor_user_id));
        Log.d("PCRom_doctor_channel_id", String.valueOf(doctor_channel_id));
        Log.d("PCRoom_appointment_id", String.valueOf(appointment_id));
        Log.d("PCRoom_doctor_nickName", doctor_nickName);

        // intent filter
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("PatientChatMessages");
        //Initializing recyclerview
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_patChatRoom);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        // to update delivery status and read receipt
        /*int childCount = layoutManager.getChildCount();
        for (int i=0; ){

        }*/

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(myAdapter);
        //Initializing message arraylist
        messages = new ArrayList<>();
        myAdapter = new ThreadAdapter(this, messages); // enter your userid here

        // iterate the recyclerview
        /*final int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
        final int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
        for (int i = firstVisibleItemPosition; i <= lastVisibleItemPosition; ++i) {
            ViewHolder holder = (ViewHolder) mRecyclerView.findViewHolderForAdapterPosition(i);

        }*/
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
                }else Toast.makeText(PatientChatRoom.this, "Empty Message!", Toast.LENGTH_SHORT).show();
            }
        });
        endChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // call service
                endChatPatient();
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
        params.put("channel", "doctor");// opposite user channel
        params.put("channel_id", patient_channel_id);
        params.put("userid", patient_user_id);
        params.put("toChannel_id", doctor_channel_id);
        params.put("nick", patient_nick_name);
        params.put("apt_id", appointment_id);
        params.put("fromuserid", patient_user_id);
        params.put("touserid", doctor_user_id);
        client.post(URL_CHAT, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                String jsonResponse = response.toString();
                Log.d("typing_off_pat", jsonResponse);
            }
        });
    }

    private void typingStartedService() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("type", "typing");
        params.put("channel", "doctor");// opposite user channel
        params.put("channel_id", patient_channel_id);
        params.put("userid", patient_user_id);
        params.put("toChannel_id", doctor_channel_id);
        params.put("nick", patient_nick_name);
        params.put("apt_id", appointment_id);
        params.put("fromuserid", patient_user_id);
        params.put("touserid", doctor_user_id);
        client.post(URL_CHAT, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                String jsonResponse = response.toString();
                Log.d("typing_on_pat", jsonResponse);
            }
        });
    }

    private void getIntentDataToSP() {

        Intent intent = getIntent();
        doctor_user_id = intent.getIntExtra("doctor_userId", 0);
        doctor_channel_id = intent.getIntExtra("doctor_channelId", 0);
        appointment_id = intent.getIntExtra("apt_id", 0);
        doctor_nickName = intent.getStringExtra("doctor_nickName");

        // storein SP
        /*SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_COMMON_PARAMS_PATIENT_CHAT, MODE_PRIVATE).edit();
        editor.putInt("doctor_userId", doctor_userId);
        editor.putInt("doctor_channelId", doctor_channelId);
        editor.putInt("apt_id", apt_id);
        editor.apply();*/
    }

    private void sendMessages(String text) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("type", "sendmessage");
        params.put("channel", "doctor");
        params.put("channel_id", patient_channel_id);
        params.put("userid", patient_user_id);
        params.put("toChannel_id", doctor_channel_id);
        params.put("nick", patient_nick_name);
        params.put("msgid", getMsgIdPatient());// timeFinal stamp
        params.put("fromuserid", patient_user_id);
        params.put("touserid", doctor_user_id);
        params.put("text", text);
        params.put("apt_id", appointment_id);
        client.post(URL_CHAT, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                String jsonResponse = response.toString();
                Log.d("send_chat_msgPat", jsonResponse);
                int jsonStatus = response.optInt("success");

                if (jsonStatus == 1) {
                    editTextMessage.setText("");
                    Toast.makeText(PatientChatRoom.this, "Message Sent !", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    private void endChatPatient() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("type", "endchat");
        params.put("id", patient_user_id);
        params.put("request_id", appointment_id);
        params.put("device", deviceOs); // sender username
        params.put("version", getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("AppVersion", null));
        params.put("ipaddress", new Splash_Screen().getLocalIpAddress());
        params.put("udid", getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("UDID", null));
        params.put("sha", sha);
        client.post(URL_PATIENT_OPERATION, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d("json_endchat_pat", response.toString());
                int jsonStatus = response.optInt("success");
                if (jsonStatus == 1) {

                    String wallet_amount = response.optString("wallet_amount");
                    Toast.makeText(PatientChatRoom.this, "Session End!\n" + "Balance: " + wallet_amount, Toast.LENGTH_SHORT).show();

                    finish();
                } else if (jsonStatus == 3) {
                    Toast.makeText(PatientChatRoom.this, "Session already end by Doctor!", Toast.LENGTH_SHORT).show();
                    // dont clear sp or start a new activity to the mainactivity
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

        AlertDialog.Builder builder = new AlertDialog.Builder(PatientChatRoom.this);
        builder.setMessage("End Chat Now?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // navigate to the chatroom
                        endChatPatient();


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

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String jsonChatMessages = intent.getStringExtra("jsonChatMessages");
            Log.d("patMessagesReceived", jsonChatMessages);
            try {
                JSONObject obj = new JSONObject(jsonChatMessages);
                // get the type value again
                String type = obj.optString("type");
                Log.d("Messagetype", type);
                // get values in chat
                String status = obj.optString("status");
                Log.d("receive_or_sent?", status);
                String text = obj.optString("text");
                String time = obj.optString("timeFinal");
                String msg_id = obj.optString("msg_id");
                String apt_id = obj.optString("apt_id");
                String docName = obj.optString("from");

                if (type.equals("message")) {
                    // set the values in adapter
                    ChatMessage messagObject = new ChatMessage(1, text, time, docName, status, opponentPhotoUrl);
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

    //method to scroll the recyclerview to bottom
    private void scrollToBottom() {
        myAdapter.notifyDataSetChanged();
        if (myAdapter.getItemCount() > 1)
            recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, myAdapter.getItemCount() - 1);
    }


    private String getMsgIdPatient() {
        Long tsLong = System.currentTimeMillis();
        String timeStamp = tsLong.toString();
        Log.d("timestamp", String.valueOf(timeStamp));
        int senderType = 0;
        int senderChannelId = patient_channel_id;
        int receiver = 0;
        int receiverChannelId = doctor_channel_id;
        int apt_id = appointment_id;

        return concatenateDigits(timeStamp, senderType, senderChannelId, receiver, receiverChannelId, apt_id);
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
