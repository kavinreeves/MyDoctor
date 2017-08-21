package com.example.mydoctor.mydoctor.AdapterHelpers;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mydoctor.mydoctor.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.mydoctor.mydoctor.Login.CommonParams.MY_PREFS_COMMON_PARAMS_DOCTOR;
import static com.example.mydoctor.mydoctor.Login.CommonParams.MY_PREFS_COMMON_PARAMS_PATIENT;
import static com.example.mydoctor.mydoctor.Login.CommonParams.MY_PREFS_LOGINTYPE;
import static com.example.mydoctor.mydoctor.Login.CommonParams.URL_CHAT;


public class ChatRoomViewOnly extends AppCompatActivity {

    int opponentId;
    int senderId;
    String fromChannel, toChannel, opponentPhoto, opponentName;
    CircleImageView chatViewHeaderIV;

    TextView opponentNameTv;

    RecyclerView recyclerView;
    ChatViewOnlyAdapter myAdapter;
    //ArrayList of messages to store the thread messages
    private ArrayList<ChatMessage> chatHistoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat_room_view_only);

        chatViewHeaderIV = (CircleImageView) findViewById(R.id.chatViewHeaderIV);
        opponentNameTv = (TextView) findViewById(R.id.tvChatHeaderPatNameViewOnly);
        recyclerView = (RecyclerView) findViewById(R.id.chatViewOnlyRV);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(myAdapter);

        chatHistoryList = new ArrayList<>();
        myAdapter = new ChatViewOnlyAdapter(chatHistoryList, this);

        // add dummy data
        /*ChatMessage chatMessage = new ChatMessage(1, "message", "time", "name", "sent");
        chatHistoryList.add(chatMessage);
        myAdapter.notifyDataSetChanged();*/


        opponentId = getIntent().getIntExtra("toUserid", 0);
        opponentPhoto = getIntent().getStringExtra("photo");
        opponentName = getIntent().getStringExtra("name");

        if(getSharedPreferences(MY_PREFS_LOGINTYPE, MODE_PRIVATE).getString("type", "").equals("doctor")) {
            senderId = getSharedPreferences(MY_PREFS_COMMON_PARAMS_DOCTOR, MODE_PRIVATE).getInt("doctor_id", 0);
            fromChannel = "doctor";
            toChannel = "patient";
        }else {
            senderId = getSharedPreferences(MY_PREFS_COMMON_PARAMS_PATIENT, MODE_PRIVATE).getInt("patient_id", 0);
            fromChannel = "patient";
            toChannel = "doctor";
        }

        Log.d("senderId", String.valueOf(senderId));
        Log.d("opponentId", String.valueOf(opponentId));

        new DownloadImageTask(chatViewHeaderIV).execute(opponentPhoto);
        opponentNameTv.setText(opponentName);

        getChatHistory();

    }

    private void getChatHistory() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("type", "chathistory");
        params.put("fromChannel", fromChannel);
        params.put("toChannel", toChannel);
        params.put("fromUserid", senderId);
        params.put("toUserid", opponentId);
        client.post(URL_CHAT, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d("chatHisMsgDoc", response.toString());

                if (response.optInt("success")==1){

                    JSONArray jArray = response.optJSONArray("messages");
                    JSONObject jObject = new JSONObject();
                    String name, message, status, type, time;

                    switch (response.optInt("success")) {

                        case 1:

                            if (response.optJSONArray("messages") != null) {
                                int arrayLength = jArray.length();
                                Log.d("no_of_msgs", String.valueOf(arrayLength));
                                clearRecyclerView();

                                for (int i = 0; i < arrayLength; i++) {
                                    try {
                                        jObject = jArray.getJSONObject(i);
                                        name = jObject.optString("name");
                                        message = jObject.optString("message");
                                        time = jObject.optString("time");
                                        status = jObject.optString("status"); // status will be always "sent"
                                        type = jObject.optString("type");

                                        Log.d("allDeatsils", name+message+time+status+type);

                                        //
                                        ChatMessage chatMessage = new ChatMessage(1, message, time, name, type, opponentPhoto);
                                        chatHistoryList.add(chatMessage);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                                recyclerView.setAdapter(myAdapter);
                                myAdapter.notifyDataSetChanged();
                                scrollToBottom();
                            }
                            //ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
                            break;
                        case 2:
                            Snackbar snackbar = Snackbar.make(recyclerView, "No Appointments Found!", Snackbar.LENGTH_LONG);

                            snackbar.show();
                            clearRecyclerView();
                            // Changing message text color
                            //snackbar.setActionTextColor(Color.RED);
                            //ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
                        default:
                            //ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
                            break;

                    }


                }else if (response.optInt("success")==2){
                    Toast.makeText(ChatRoomViewOnly.this, "No messages Found!", Toast.LENGTH_SHORT).show();
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
        int size = this.chatHistoryList.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.chatHistoryList.remove(0);
            }


        }
    }

    //method to scroll the recyclerview to bottom
    private void scrollToBottom() {
        myAdapter.notifyDataSetChanged();
        if (myAdapter.getItemCount() > 1)
            recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, myAdapter.getItemCount() - 1);
    }
}
