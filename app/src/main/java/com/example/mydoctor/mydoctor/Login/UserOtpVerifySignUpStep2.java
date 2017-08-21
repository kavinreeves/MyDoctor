package com.example.mydoctor.mydoctor.Login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mydoctor.mydoctor.ChooseLoginActivity;
import com.example.mydoctor.mydoctor.MainActivity;
import com.example.mydoctor.mydoctor.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.Base64;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;

import static com.example.mydoctor.mydoctor.Login.CommonParams.MY_PREFS_COMMON_PARAMS;
import static com.example.mydoctor.mydoctor.Login.CommonParams.URL_PATIENT_SIGNUP;
import static com.example.mydoctor.mydoctor.Login.CommonParams.URL_PATIENT_STEP;
import static com.example.mydoctor.mydoctor.Login.CommonParams.deviceOs;
import static com.example.mydoctor.mydoctor.Login.CommonParams.sha;

public class UserOtpVerifySignUpStep2 extends AppCompatActivity {

    EditText otpEditText;
    Button otpVerifyBtn, getOtpSkipBtn;
    String userPasswordDecoded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification_sign_up_step2);

        otpEditText = (EditText) findViewById(R.id.editTextUserOTP);
        otpVerifyBtn = (Button) findViewById(R.id.userOtpVerifyBtn);
        getOtpSkipBtn = (Button) findViewById(R.id.userOtpSkipBtn);

        // skip button
        getOtpSkipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //patientLoginservice();
                // call patient login service
                AsyncHttpClient client = new AsyncHttpClient();
                //client.addHeader("Content-Type", "application/x-www-form-urlencoded"); // not necessary to add because it is set by default
                RequestParams params = new RequestParams();
                params.put("type", "patient_login");
                params.put("username", getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("wnspnt_username", "null"));
                params.put("pass", decryptPasswordPRQ(getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("prq", "null")));
                params.put("device", deviceOs);
                params.put("version", getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("AppVersion", null));
                params.put("ipaddress", new Splash_Screen().getLocalIpAddress());
                params.put("udid", getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("UDID", null));
                params.put("sha", sha);

                client.post(URL_PATIENT_SIGNUP, params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        String JsonResponse = String.valueOf(response);
                        Log.d("json response", JsonResponse);
                        String JsonStatus = response.optString("success");
                        switch (JsonStatus) {
                            case "1":
                                // completed all other steps
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                break;
                            case "2": // db error
                                Toast.makeText(UserOtpVerifySignUpStep2.this, "Network Error Try Again!", Toast.LENGTH_SHORT).show();
                                break;
                            case "3": // invalid username / password
                                startActivity(new Intent(getApplicationContext(), SignUpStep2Final.class));
                               /* Toast.makeText(UserOtpVerifySignUpStep2.this, "Invalid username/password!", Toast.LENGTH_SHORT).show();
                                Intent intent2 = new Intent(getApplicationContext(), ChooseLoginActivity.class);
                                startActivity(intent2);*/

                                break;
                            case "4": // final step of reg is pending
                                Intent intent1 = new Intent(getApplicationContext(), SignUpStep2Final.class);
                                startActivity(intent1);
                                break;
                            case "5": // facebook step2 reg pending
                                break;
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                    }
                });
            }
        });

        // otp verify button event handler
        otpVerifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String otp = otpEditText.getText().toString();
                checkEmptyEditTexts(otpEditText, otp);
                // check the user id from json is empty . if not empty then we can verify the otp and move to the user main page
                SharedPreferences pref = getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE);
                final String userId = pref.getString("wnspnt_regid", "null");
                Log.d("wnspnt_regid", userId);
                if (userId != null) {
                    // make server connection to verify the OTP and move to main page
                    AsyncHttpClient client2 = new AsyncHttpClient();
                    client2.addHeader("Content-Type", "application/x-www-form-urlencoded"); // not necessary to add because it is set by default
                    RequestParams params = new RequestParams();
                    params.put("type", "otp_verify");
                    params.put("id", userId);
                    params.put("otp", otp);
                    params.put("device", deviceOs);
                    params.put("version", getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("AppVersion", null));
                    params.put("ipaddress", new Splash_Screen().getLocalIpAddress());
                    params.put("udid", getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("UDID", null));
                    params.put("sha", sha);
                    client2.post(URL_PATIENT_STEP, params, new JsonHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            String JsonResponse = String.valueOf(response);
                            Log.d("json response", JsonResponse);
                            String JsonStatus = response.optString("success");
                            switch (JsonStatus) {
                                case "1":
                                    // successful now call the login service again
                                    patientLoginservice();
                                    break;
                                case "2":
                                    // db error
                                    break;
                                case "3":
                                    // wrong otp
                                    Toast.makeText(UserOtpVerifySignUpStep2.this, "Wrong OTP try again!", Toast.LENGTH_SHORT).show();
                                    break;
                            }

                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

                        }
                    });

                } else
                    Toast.makeText(UserOtpVerifySignUpStep2.this, "OTP not generated", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void patientLoginservice() {
        // call patient login service
        AsyncHttpClient client = new AsyncHttpClient();
        //client.addHeader("Content-Type", "application/x-www-form-urlencoded"); // not necessary to add because it is set by default
        RequestParams params = new RequestParams();
        params.put("type", "patient_login");
        params.put("username", getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("wnspnt_username", "null"));
        params.put("pass", decryptPasswordPRQ(getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("prq", "null")));
        params.put("device", deviceOs);
        params.put("version", getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("AppVersion", null));
        params.put("ipaddress", new Splash_Screen().getLocalIpAddress());
        params.put("udid", getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("UDID", null));
        params.put("sha", sha);

        client.post(URL_PATIENT_SIGNUP, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String JsonResponse = String.valueOf(response);
                Log.d("json response", JsonResponse);
                String JsonStatus = response.optString("success");
                switch (JsonStatus) {
                    case "1":
                        // completed all other steps
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        break;
                    case "2": // db error
                        Toast.makeText(UserOtpVerifySignUpStep2.this, "Network Error Try Again!", Toast.LENGTH_SHORT).show();
                        break;
                    case "3": // invalied username / password
                        Intent intent2 = new Intent(getApplicationContext(), ChooseLoginActivity.class);
                        startActivity(intent2);

                        break;
                    case "4": // final step of reg is pending
                        Intent intent1 = new Intent(getApplicationContext(), SignUpStep2Final.class);
                        startActivity(intent1);
                        break;
                    case "5": // facebook step2 reg pending
                        break;
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    public void checkEmptyEditTexts(EditText editText, String data) {
        if (TextUtils.isEmpty(data)) {
            editText.setError("Mandatory Field");
            editText.requestFocus();
            return;
        }
    }

    // method for password decryption
    public String decryptPasswordPRQ(String encryptedPassword) {
        // String passEncrypt = getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("prq", "null");
        // decode the base64 password
        if (encryptedPassword != null) {
            byte[] data = Base64.decode(encryptedPassword, Base64.DEFAULT);
            try {
                String userPasswordDecoded = new String(data, "UTF-8");
                Log.d("decoded password:", userPasswordDecoded);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return userPasswordDecoded;
    }
}
