package com.example.mydoctor.mydoctor.DoctorNavigation;


import android.app.AlertDialog;
import android.content.SharedPreferences;
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
import com.example.mydoctor.mydoctor.Navigation.WalletItem;
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
import static com.example.mydoctor.mydoctor.Login.CommonParams.URL_DOCTOR_WALLET;
import static com.example.mydoctor.mydoctor.Login.CommonParams.deviceOs;
import static com.example.mydoctor.mydoctor.Login.CommonParams.sha;

/**
 * A simple {@link Fragment} subclass.
 */
public class DoctorWalletFragment extends Fragment {

    private RecyclerView recyclerViewwallet;
    private List<WalletItem> walletInfoList = new ArrayList<>();
    private WalletRecyclerAdapter myAdapter;

    private View progressOverlay;

    Button sendMoney, getWalletHistory;
    TextView availableBalance, currencyTv, addBankDetails, balanceHeader;
    Float money;

    String currency, balance, bankDetails;


    public DoctorWalletFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_doctor_wallet, container, false);

        // Inflate the layout for this fragment
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        availableBalance = (TextView) view.findViewById(R.id.tvDocWalletBalance);
        currencyTv = (TextView) view.findViewById(R.id.tvDocCurrency);
        addBankDetails = (TextView) view.findViewById(R.id.addBankDetailsTV);
        sendMoney = (Button) view.findViewById(R.id.docWalletSendMoneyBtn);
        getWalletHistory = (Button) view.findViewById(R.id.docWalletHistoryBtn);
        // recycler view amd adapter
        recyclerViewwallet = (RecyclerView) view.findViewById(R.id.RecycViewDocWalletHistory);
        myAdapter = new WalletRecyclerAdapter(walletInfoList);
        //
        progressOverlay = view.findViewById(R.id.progress_overlay);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerViewwallet.setLayoutManager(mLayoutManager);
        recyclerViewwallet.setItemAnimator(new DefaultItemAnimator());
        recyclerViewwallet.setAdapter(myAdapter);

        // top bar items
        balanceHeader = (TextView) getActivity().findViewById(R.id.tvMainHeaderDocBalance);
        //currencyTv.setText(getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS_DOCTOR, MODE_PRIVATE).getString("currency", ""));
        currencyTv.setText("GBP");

        getWalletHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userWalletHistory();
            }
        });
        sendMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMoneyToAccount();
            }
        });

        addBankDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addAccountDetails();
            }
        });

        getBalance();

    }

    private void addAccountDetails() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final View viewInflated = LayoutInflater.from(getActivity()).inflate(R.layout.add_account_dialog, null);

        final EditText accname = (EditText) viewInflated.findViewById(R.id.accHolderName);
        final EditText bankname = (EditText) viewInflated.findViewById(R.id.accBankName);
        final EditText sortcode = (EditText) viewInflated.findViewById(R.id.enterSortCode);
        final EditText accnumber = (EditText) viewInflated.findViewById(R.id.enterAccNumber);

        final Button addDetails = (Button) viewInflated.findViewById(R.id.addAccDetailsBtn);

        builder.setView(viewInflated);
        builder.show();


        addDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isEmpty(accname) | isEmpty(bankname) | isEmpty(sortcode) | isEmpty(accnumber)) {
                    Toast.makeText(getActivity(), "Fill All Fields!", Toast.LENGTH_SHORT).show();
                } else {
                    String accName = accname.getText().toString();
                    String bankName = bankname.getText().toString();
                    String sortCode = sortcode.getText().toString();
                    String accNumber = accnumber.getText().toString();

                }
            }
        });
    }

    public boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

    private void sendMoneyToAccount() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final View viewInflated = LayoutInflater.from(getActivity()).inflate(R.layout.send_money_dialog, null);

        final EditText accNickname = (EditText) viewInflated.findViewById(R.id.chooseAccNickname);
        final EditText amount = (EditText) viewInflated.findViewById(R.id.sendMoneyAmount);
        //final Button cancelOtp = (Button) viewInflated.findViewById(R.id.cancelOtp_pswdReset);
        final Button sendMoney = (Button) viewInflated.findViewById(R.id.sendMoneyBtn);

        builder.setView(viewInflated);
        builder.show();


        sendMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void getBalance() {
        ProgressBarUtils.animateView(progressOverlay, View.VISIBLE, 0.4f, 200);

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("type", "balance");
        params.put("id", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS_DOCTOR, MODE_PRIVATE).getInt("doctor_id", 0)); // doctor id
        params.put("device", deviceOs); // sender username
        params.put("version", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("AppVersion", null));
        params.put("ipaddress", new Splash_Screen().getLocalIpAddress());
        params.put("udid", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("UDID", null));
        params.put("sha", sha);
        client.post(URL_DOCTOR_WALLET, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d("getbalance", response.toString());
                if (response.optInt("success") == 1) {

                    balance = response.optString("balance");
                    currency = response.optString("currency");
                    bankDetails = response.optString("bankdetails");

                    //balanceHeader.setText(getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS_DOCTOR, MODE_PRIVATE).getString("currency", "")+" " + balance);
                    availableBalance.setText(balance);

                    if (!bankDetails.equals("null")) {
                        addBankDetails.setText("View Details");
                    }

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

    public void userWalletHistory() {

        ProgressBarUtils.animateView(progressOverlay, View.VISIBLE, 0.4f, 200);

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("type", "passbook");
        params.put("id", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS_DOCTOR, MODE_PRIVATE).getInt("doctor_id", 0));
        params.put("device", deviceOs);
        params.put("version", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("AppVersion", null));
        params.put("ipaddress", new Splash_Screen().getLocalIpAddress());
        params.put("udid", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("UDID", null));
        params.put("sha", sha);
        client.post(URL_DOCTOR_WALLET, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String JsonResponse = String.valueOf(response);
                Log.d("jsonDoc_response_wallet", JsonResponse);
                String JsonStatus = response.optString("success");
                switch (JsonStatus) {
                    case "1":
                        // success  -----   read the json array and show in scroll view
                        JSONArray jsonArray = response.optJSONArray("wallet");
                        JSONObject jsonObject = new JSONObject();
                        Log.d("docwallet_Json", String.valueOf(jsonArray));
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

    public void userWithdrawMoney(Float moneyValue) {
        ProgressBarUtils.animateView(progressOverlay, View.VISIBLE, 0.4f, 200);
        //
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("type", "withdraw");
        params.put("id", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS_DOCTOR, MODE_PRIVATE).getInt("doctor_id", 0));
        params.put("currency", "INR");
        params.put("amount", moneyValue);
        params.put("device", deviceOs);
        params.put("version", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("AppVersion", null));
        params.put("ipaddress", new Splash_Screen().getLocalIpAddress());
        params.put("udid", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("UDID", null));
        params.put("sha", sha);
        client.post(URL_DOCTOR_WALLET, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String JsonResponse = String.valueOf(response);
                Log.d("json_docWithdraw", response.toString());
                String JsonStatus = response.optString("success");
                switch (JsonStatus) {
                    case "1":
                        String prevBalance = response.optString("previous_balance");
                        String newBalance = response.optString("wallet_amount");
                        Log.d("pBalance", prevBalance);
                        Log.d("new_Balance", newBalance);
                        availableBalance.setText(newBalance);
                        // update new balance in SP
                        SharedPreferences.Editor editor = getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS_PATIENT, MODE_PRIVATE).edit();
                        editor.putString("balance", newBalance).apply();

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
