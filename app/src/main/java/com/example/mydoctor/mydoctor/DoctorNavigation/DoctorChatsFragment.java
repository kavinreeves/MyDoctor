package com.example.mydoctor.mydoctor.DoctorNavigation;


import android.content.Intent;
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
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.mydoctor.mydoctor.AdapterHelpers.ChatHistory;
import com.example.mydoctor.mydoctor.AdapterHelpers.ChatHistoryAdapter;
import com.example.mydoctor.mydoctor.AdapterHelpers.ChatRoomViewOnly;
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
import static com.example.mydoctor.mydoctor.Login.CommonParams.URL_DOCTOR_OPERATION;
import static com.example.mydoctor.mydoctor.Login.CommonParams.deviceOs;
import static com.example.mydoctor.mydoctor.Login.CommonParams.sha;

/**
 * A simple {@link Fragment} subclass.
 */
public class DoctorChatsFragment extends Fragment implements View.OnClickListener {

    Button week, month, all;
    RecyclerView recyclerView;
    ChatHistoryAdapter mAdapter;
    private View progressOverlay;
    private List<ChatHistory> chatHistoryList = new ArrayList<>();
    private TextView noResult;
    private View mView;

    public DoctorChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_doctor_chats, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mView = view.findViewById(android.R.id.content);
        ((RadioGroup) view.findViewById(R.id.toggleGroupChatsDoc)).setOnCheckedChangeListener(ToggleListener);
        noResult = (TextView) view.findViewById(R.id.docChats_noresult);
        week = (Button) view.findViewById(R.id.btn_chat_thisweekDoc);
        month = (Button) view.findViewById(R.id.btn_chat_thisMonthDoc);
        all = (Button) view.findViewById(R.id.btn_chat_allDoc);
        progressOverlay = view.findViewById(R.id.progress_overlay);
        recyclerView = (RecyclerView) view.findViewById(R.id.chatHistory_recyclerView);
        mAdapter = new ChatHistoryAdapter(chatHistoryList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        // add click listener
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //Toast.makeText(getActivity(), "Data Not Available1", Toast.LENGTH_SHORT).show();
                ChatHistory chatHistory = mAdapter.getItem(position);
                int toUserid = chatHistory.getToUserid();
                String name = chatHistory.getSenderName();
                String photo = chatHistory.getPhoto();
                Log.d("touserid_chat", String.valueOf(toUserid));
                Intent intent = new Intent(getActivity(), ChatRoomViewOnly.class);
                intent.putExtra("toUserid", toUserid);
                intent.putExtra("name", name);
                intent.putExtra("photo", photo);
                startActivity(intent);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
        week.setOnClickListener(this);
        month.setOnClickListener(this);
        all.setOnClickListener(this);


        //
        getChatHistory("week");
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

        switch (selected_btn_id) {
            case R.id.btn_chat_thisweekDoc:
                getChatHistory("week");
                break;
            case R.id.btn_chat_thisMonthDoc:
                getChatHistory("month");
                break;
            case R.id.btn_chat_allDoc:
                getChatHistory("all");
                break;
        }

        //Toast.makeText(getActivity(), "id selected is" + selected_btn_id, Toast.LENGTH_SHORT).show();
    }

    private void getChatHistory(String interval) {
        ProgressBarUtils.animateView(progressOverlay, View.VISIBLE, 0.4f, 200);
        noResult.setVisibility(View.GONE);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("type", "mychatlist");
        params.put("id", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS_DOCTOR, MODE_PRIVATE).getInt("doctor_id", 0));
        params.put("interval", interval);
        params.put("device", deviceOs); // sender username
        params.put("version", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("AppVersion", null));
        params.put("ipaddress", new Splash_Screen().getLocalIpAddress());
        params.put("udid", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("UDID", null));
        params.put("sha", sha);
        client.post(URL_DOCTOR_OPERATION, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                String docChatHistory_json = response.toString();
                Log.d("docChatHistory_json ", docChatHistory_json);

                int jsonStatus = response.optInt("success");
                switch (jsonStatus) {

                    case 1:
                        // populate the recycler view
                        String name, gender, lastonline, onlinestatus, photo, dateTime;
                        int patient_id;
                        JSONArray jsonArray = new JSONArray();
                        JSONObject jsonObject;
                        try {
                            jsonArray = response.getJSONArray("chats");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("no_of_chats", String.valueOf(jsonArray.length()));
                        int count = 0;
                        clearRecyclerView();
                        while (count < jsonArray.length()) {
                            try {
                                jsonObject = jsonArray.getJSONObject(count);
                                name = jsonObject.getString("name");
                                gender = jsonObject.getString("gender");
                                lastonline = jsonObject.getString("lastonline");
                                onlinestatus = jsonObject.getString("onlinestatus");
                                photo = jsonObject.getString("photo");
                                patient_id = jsonObject.optInt("patient_id");

                                Log.d("pat_id_check", String.valueOf(patient_id));
                                // put them all in ChatHistory Object

                                ChatHistory chatHistory = new ChatHistory(name, gender, photo, onlinestatus, lastonline, patient_id);
                                chatHistoryList.add(chatHistory);
                                mAdapter.notifyDataSetChanged();


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            count++;
                        }
                        ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
                        break;
                    case 2:
                        /*Snackbar.make(mView, "No results Found", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();*/

                        noResult.setVisibility(View.VISIBLE);
                        clearRecyclerView();
                        mAdapter.notifyDataSetChanged();

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

    // method to clear the recycler view
    public void clearRecyclerView() {
        int size = this.chatHistoryList.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.chatHistoryList.remove(0);
            }


        }
    }
}
