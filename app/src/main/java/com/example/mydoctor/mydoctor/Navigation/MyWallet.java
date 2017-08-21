package com.example.mydoctor.mydoctor.Navigation;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mydoctor.mydoctor.AdapterHelpers.WalletRecyclerAdapter;
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
import static com.example.mydoctor.mydoctor.Login.CommonParams.URL_PATIENT_WALLET;
import static com.example.mydoctor.mydoctor.Login.CommonParams.deviceOs;
import static com.example.mydoctor.mydoctor.Login.CommonParams.sha;

public class MyWallet extends Fragment {

    Button addMoney, getWalletHistory;
    TextView availableBalance, nameHeader, balanceHeader, currencyTv;
    Float money;

    private View progressOverlay;

    String balance, currency;

    private RecyclerView recyclerViewwallet;
    private List<WalletItem> walletInfoList = new ArrayList<>();
    private WalletRecyclerAdapter myAdapter;


    // public constructor
    public MyWallet() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_my_wallet, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // initialize widgets
        availableBalance = (TextView) view.findViewById(R.id.tvUserWalletBalance);
        currencyTv = (TextView) view.findViewById(R.id.pat_currencyTV);
        addMoney = (Button) view.findViewById(R.id.userWalletAddMoneyBtn);
        getWalletHistory = (Button) view.findViewById(R.id.userWalletHistoryBtn);
        // recycler view amd adapter
        recyclerViewwallet = (RecyclerView) view.findViewById(R.id.listViewUserWalletHistory);
        myAdapter = new WalletRecyclerAdapter(walletInfoList);

        progressOverlay = view.findViewById(R.id.progress_overlay);

        // top bar items
        nameHeader = (TextView) getActivity().findViewById(R.id.tvHeader_PatName);
        balanceHeader = (TextView) getActivity().findViewById(R.id.tvHeaderPat_balance);

        //nameHeader.setText("Hi "+getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS_PATIENT, MODE_PRIVATE).getString("pusername", null));
        //balanceHeader.setText(getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS_PATIENT, MODE_PRIVATE).getString("currency", "")+getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS_PATIENT, MODE_PRIVATE).getString("balance", ""));
        //currencyTv.setText(getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS_PATIENT, MODE_PRIVATE).getString("currency", ""));
        currencyTv.setText("GBP");
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerViewwallet.setLayoutManager(mLayoutManager);
        recyclerViewwallet.setItemAnimator(new DefaultItemAnimator());
        recyclerViewwallet.setAdapter(myAdapter);

        availableBalance.setText(getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS_PATIENT, MODE_PRIVATE).getString("balance", "0.00"));


        addMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addMoneyDialog();

            }
        });

        getWalletHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userWalletHistory();

            }
        });

        getBalance();

        // finsh to end this fragment

    }

    private void getBalance() {
        ProgressBarUtils.animateView(progressOverlay, View.VISIBLE, 0.4f, 200);

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("type", "balance");
        params.put("id", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS_PATIENT, MODE_PRIVATE).getInt("patient_id", 0)); // doctor id
        params.put("device", deviceOs); // sender username
        params.put("version", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("AppVersion", null));
        params.put("ipaddress", new Splash_Screen().getLocalIpAddress());
        params.put("udid", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("UDID", null));
        params.put("sha", sha);
        client.post(URL_PATIENT_WALLET, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d("getbalancePat", response.toString());
                if (response.optInt("success") == 1) {

                    balance = response.optString("balance");
                    //currency = response.optString("currency");

                    try {
                        balanceHeader.setText(getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS_PATIENT, MODE_PRIVATE).getString("currency", "") + balance);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    availableBalance.setText(balance);

                }

                ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
            }
        });

    }

    public void userAddMoney(Float moneyValue) {
        ProgressBarUtils.animateView(progressOverlay, View.VISIBLE, 0.4f, 200);
        //
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("type", "addMoneyFromBank");
        params.put("id", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS_PATIENT, MODE_PRIVATE).getInt("patient_id", 0));
        params.put("currency", "INR");
        params.put("amount", moneyValue);
        params.put("device", deviceOs);
        params.put("version", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("AppVersion", null));
        params.put("ipaddress", new Splash_Screen().getLocalIpAddress());
        params.put("udid", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("UDID", null));
        params.put("sha", sha);
        client.post(URL_PATIENT_WALLET, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String JsonResponse = String.valueOf(response);
                Log.d("json_response_wallet", response.toString());
                String JsonStatus = response.optString("success");
                switch (JsonStatus) {
                    case "1":
                        String prevBalance = response.optString("previous_balance");
                        String newBalance = response.optString("wallet_amount");
                        // remove comma from the string
                        newBalance = newBalance.replaceAll(",", "");
                        Log.d("pBalance", prevBalance);
                        Log.d("new_Balance", newBalance);
                        availableBalance.setText(newBalance);

                        balanceHeader.setText(getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS_PATIENT, MODE_PRIVATE).getString("currency", "") +" "+ newBalance);


                        Toast.makeText(getActivity(), "Added Successfully!", Toast.LENGTH_SHORT).show();
                        ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
                        break;
                    case "2": // failed due to database error
                        ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
                        break;
                    case "3": // failed due to payment gateway error
                        ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
                        break;
                    default:
                        ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
                        break;
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
            }
        });
    }


    public void addMoneyDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Enter Amount");
        // I'm using fragment here so I'm using getView() to provide ViewGroup
        // but you can provide here any other instance of ViewGroup from your Fragment / Activity
        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.user_wallet_addmoney, (ViewGroup) getView(), false);
        // Set up the input
        final EditText input = (EditText) viewInflated.findViewById(R.id.userAddMoneyPopupDialog);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        builder.setView(viewInflated);

        // Set up the buttons
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                money = Float.valueOf(input.getText().toString());
                dialog.dismiss();
                userAddMoney(money);

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void userWalletHistory() {

        ProgressBarUtils.animateView(progressOverlay, View.VISIBLE, 0.4f, 200);

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("type", "passbook");
        params.put("id", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS_PATIENT, MODE_PRIVATE).getInt("patient_id", 0));
        params.put("device", deviceOs);
        params.put("version", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("AppVersion", null));
        params.put("ipaddress", new Splash_Screen().getLocalIpAddress());
        params.put("udid", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("UDID", null));
        params.put("sha", sha);
        client.post(URL_PATIENT_WALLET, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String JsonResponse = String.valueOf(response);
                Log.d("json_response_walletHis", JsonResponse);
                String JsonStatus = response.optString("success");
                switch (JsonStatus) {
                    case "1":
                        // success  -----   read the json array and show in scroll view
                        JSONArray jsonArray = response.optJSONArray("wallet");
                        JSONObject jsonObject = new JSONObject();
                        Log.d("wallet_Json", String.valueOf(jsonArray));
                        Log.d("no_of_wallet_trans:", String.valueOf(jsonArray.length()));

                        int count = 0;
                        clearRecyclerView();

                        while (count < jsonArray.length()) {
                            try {
                                JSONObject obj = jsonArray.getJSONObject(count);
                                String time = obj.optString("time");
                                String referenceId = obj.optString("referenceId");
                                String desc = obj.optString("description");
                                String amount = obj.optString("amount");
                                String balance = obj.optString("balance");
                                String status = obj.optString("status");
                                String transType = obj.optString("transactionType");
                                Log.d("time", time);
                                Log.d("refid", referenceId);
                                Log.d("desc", desc);
                                Log.d("amount", amount);
                                Log.d("balance", balance);
                                Log.d("status", status);
                                // put them in walletitem object
                                WalletItem walletItem = new WalletItem(time, referenceId, desc, amount, balance, status, transType);
                                walletInfoList.add(walletItem);
                                myAdapter.notifyDataSetChanged();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            count++;


                        }
                        ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
                        break;
                    case "2": // failed due to database error
                        ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
                        break;
                    case "3": // failed due to payment gateway error
                        Toast.makeText(getActivity(), "No Data Found!", Toast.LENGTH_SHORT).show();
                        ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
                        break;
                    default:
                        ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
                        break;
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
            }
        });


    }

    // method to clear the recycler view
    public void clearRecyclerView() {
        int size = this.walletInfoList.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.walletInfoList.remove(0);
            }


        }
    }


}


