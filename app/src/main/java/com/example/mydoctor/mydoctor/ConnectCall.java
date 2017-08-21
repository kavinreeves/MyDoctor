package com.example.mydoctor.mydoctor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mydoctor.mydoctor.Services.SinchService;
import com.example.mydoctor.mydoctor.VideoCalling.BaseActivity;
import com.example.mydoctor.mydoctor.VideoCalling.PlaceCallActivity;
import com.sinch.android.rtc.SinchError;

public class ConnectCall extends BaseActivity implements SinchService.StartFailedListener {

    private Button mLoginButton;
    private EditText mLoginName;
    private ProgressDialog mSpinner;

    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_connect_call);


        loginClicked();
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
        openPlaceCallActivity();
    }

    private void loginClicked() {
            //String userName = getSharedPreferences(MY_PREFS_LOGINTYPE, MODE_PRIVATE).getString("type", "no");
            /*try {
                if (((getSinchServiceInterface() == null) || (!getSinchServiceInterface().isStarted()))) {
                    getSinchServiceInterface().startClient("user");
                    //Log.d("sinch_login_type", userName);
                    showSpinner();
                } else {
                    openPlaceCallActivity();
                }
            } catch (Exception e) {
                Log.e("getSinchInterface", e.toString());

            }
        */
        if (!getSinchServiceInterface().isStarted()) {
            getSinchServiceInterface().startClient("user");
            showSpinner();
        } else {
            openPlaceCallActivity();
        }

    }

    private void openPlaceCallActivity() {
        Intent mainActivity = new Intent(this, PlaceCallActivity.class);
        startActivity(mainActivity);

    }


    private void showSpinner() {
        mSpinner = new ProgressDialog(this);
        mSpinner.setTitle("Logging in");
        mSpinner.setMessage("Please wait...");
        mSpinner.show();
    }
}

