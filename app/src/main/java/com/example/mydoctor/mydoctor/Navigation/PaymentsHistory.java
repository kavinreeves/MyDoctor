package com.example.mydoctor.mydoctor.Navigation;


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

import com.example.mydoctor.mydoctor.AdapterHelpers.PaymentsHistoryAdapter;
import com.example.mydoctor.mydoctor.AdapterHelpers.PaymentsHistoryObject;
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
import static com.example.mydoctor.mydoctor.Login.CommonParams.MY_PREFS_COMMON_PARAMS_PATIENT;
import static com.example.mydoctor.mydoctor.Login.CommonParams.MY_PREFS_LOGINTYPE;
import static com.example.mydoctor.mydoctor.Login.CommonParams.URL_DOCTOR_WALLET;
import static com.example.mydoctor.mydoctor.Login.CommonParams.URL_PATIENT_WALLET;
import static com.example.mydoctor.mydoctor.Login.CommonParams.deviceOs;
import static com.example.mydoctor.mydoctor.Login.CommonParams.sha;

/**
 * A simple {@link Fragment} subclass.
 */
public class PaymentsHistory extends Fragment {

    private List<PaymentsHistoryObject> paymentsList = new ArrayList<>();
    private RecyclerView recyclerView;
    private PaymentsHistoryAdapter mAdapter;

    private View progressOverlay;
    private View mView;
    RecyclerView.LayoutManager mLayoutManager;

    private int id;
    private TextView noResult;
    private String url;

    public PaymentsHistory() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_payments_history, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mView = view.findViewById(android.R.id.content);
        noResult = (TextView) view.findViewById(R.id.payments_noresult);
        progressOverlay = view.findViewById(R.id.progress_overlay);
        recyclerView = (RecyclerView) view.findViewById(R.id.user_payments_historyRV);
        mAdapter = new PaymentsHistoryAdapter(paymentsList);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);


        if (getActivity().getSharedPreferences(MY_PREFS_LOGINTYPE, MODE_PRIVATE).getString("type", "null").equals("doctor")) {
            id = getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS_DOCTOR, MODE_PRIVATE).getInt("doctor_id", 0);
            url = URL_DOCTOR_WALLET;

        } else if (getActivity().getSharedPreferences(MY_PREFS_LOGINTYPE, MODE_PRIVATE).getString("type", "null").equals("patient")) {
            id = getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS_PATIENT, MODE_PRIVATE).getInt("patient_id", 0);
            url = URL_PATIENT_WALLET;
        }

        callService(id, url);

    }

    private void callService(int userid, String walletUrl) {

        ProgressBarUtils.animateView(progressOverlay, View.VISIBLE, 0.4f, 200);
        noResult.setVisibility(View.GONE);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("type", "getreceipts");
        params.put("id", userid);
        params.put("device", deviceOs); // sender username
        params.put("version", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("AppVersion", null));
        params.put("ipaddress", new Splash_Screen().getLocalIpAddress());
        params.put("udid", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("UDID", null));
        params.put("sha", sha);
        client.post(walletUrl, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                Log.d("getreceiptsJson", response.toString());
                JSONArray jArray = response.optJSONArray("wallet");
                JSONObject jObject = new JSONObject();
                String aptId, aptType, dateTime, transId, lastName, age, city, gender, amount, patientId, patUsername, status, description, prescriptionDoneStatus = null;

                switch (response.optInt("success")) {

                    case 1:

                        if (response.optJSONArray("wallet") != null) {
                            int arrayLength = jArray.length();
                            Log.d("no_of_wallet", String.valueOf(arrayLength));
                            clearRecyclerView();

                            for (int i = 0; i < arrayLength; i++) {
                                try {
                                    jObject = jArray.getJSONObject(i);


                                    dateTime = jObject.optString("time");
                                    description = jObject.optString("description");
                                    amount = jObject.optString("amount");
                                    transId = jObject.optString("referenceId");


                                    //
                                    PaymentsHistoryObject appointmentsInfo = new PaymentsHistoryObject(amount, transId, dateTime, description);
                                    paymentsList.add(appointmentsInfo);
                                    mAdapter.notifyDataSetChanged();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }
                        }
                        ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
                        break;
                    case 2:
                        /*Snackbar snackbar = Snackbar.make(mView, "No Receipts Found!", Snackbar.LENGTH_LONG);

                        snackbar.show();*/
                        noResult.setVisibility(View.VISIBLE);
                        // Changing message text color
                        //snackbar.setActionTextColor(Color.RED);
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

    private void clearRecyclerView() {
        int size = this.paymentsList.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.paymentsList.remove(0);
            }

        }
    }
}
