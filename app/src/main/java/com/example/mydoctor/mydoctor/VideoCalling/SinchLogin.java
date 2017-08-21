package com.example.mydoctor.mydoctor.VideoCalling;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mydoctor.mydoctor.R;
import com.example.mydoctor.mydoctor.Services.SinchService;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.calling.Call;

import java.util.HashMap;
import java.util.Map;

import static com.example.mydoctor.mydoctor.Login.CommonParams.MY_PREFS_COMMON_PARAMS_PATIENT;

public class SinchLogin extends BaseActivity implements SinchService.StartFailedListener {

    private Button mLoginButton;
    private EditText mLoginName;
    private ProgressDialog mSpinner;

    String userName, userType, docName;
    Map<String, String> headers = new HashMap<String, String>();

    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_sinch_login);

        try {
            docName = getIntent().getStringExtra("docName");
            Log.d("callSinchDocName", docName);
        }catch (Exception e){
            e.printStackTrace();
        }


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
            loginClicked();

            }
        }, 1000);

    }

    @Override
    public void onServiceConnected() {
        //mLoginButton.setEnabled(true);
        getSinchServiceInterface().setStartListener(this);
    }


    @Override
    protected void onPause() {
        if (mSpinner != null) {
            mSpinner.dismiss();
        }
        super.onPause();
    }

    @Override
    public void onStartFailed(SinchError error) {
        Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show();
        if (mSpinner != null) {
            mSpinner.dismiss();
        }
    }

    @Override
    public void onStarted() {

        makeCall();
    }

    private void loginClicked() {

        userName = getSharedPreferences(MY_PREFS_COMMON_PARAMS_PATIENT, MODE_PRIVATE).getString("pusername", "patient");

        try {
                    if (((getSinchServiceInterface() == null) || (!getSinchServiceInterface().isStarted()))) {
                        getSinchServiceInterface().startClient(userName);
                        Log.d("sinch_login_type", userType);
                        showSpinner();
                    } else {
                        makeCall();
                    }
                } catch (Exception e) {
                    Log.e("getSinchInterface", e.toString());

                }



    }


    private void makeCall() {
        // ask permission
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                    == PackageManager.PERMISSION_DENIED) {

                Log.d("permission", "permission denied to SEND_SMS - requesting it");
                String[] permissions = {Manifest.permission.RECORD_AUDIO};

                requestPermissions(permissions, PERMISSION_REQUEST_CODE);

            }else callButtonClicked();
        }else callButtonClicked();
    }

    private void callButtonClicked() {
        headers.put("username", getSharedPreferences(MY_PREFS_COMMON_PARAMS_PATIENT, MODE_PRIVATE).getString("pusername", "patient"));
        headers.put("userid", String.valueOf(getSharedPreferences(MY_PREFS_COMMON_PARAMS_PATIENT, MODE_PRIVATE).getInt("patient_id", 0)));
        Call call = getSinchServiceInterface().callUserVideo(docName, headers);
        String callId = call.getCallId();

        Intent callScreen = new Intent(this, CallScreenActivity.class);
        callScreen.putExtra(SinchService.CALL_ID, callId);
        startActivity(callScreen);

        finish();
    }

    private void showSpinner() {
        mSpinner = new ProgressDialog(this);
        mSpinner.setTitle("Logging in");
        mSpinner.setMessage("Please wait...");
        mSpinner.show();
    }
}
