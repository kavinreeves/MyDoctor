package com.example.mydoctor.mydoctor.Login;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mydoctor.mydoctor.Doctor.DoctorMainActivity;
import com.example.mydoctor.mydoctor.DoctorReg3;
import com.example.mydoctor.mydoctor.Patient.PatientMainActivity;
import com.example.mydoctor.mydoctor.R;
import com.example.mydoctor.mydoctor.Utils.ProgressBarUtils;
import com.github.ybq.android.spinkit.SpinKitView;
import com.hbb20.CountryCodePicker;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;

import static com.example.mydoctor.mydoctor.Login.CommonParams.MY_PREFS_COMMON_PARAMS;
import static com.example.mydoctor.mydoctor.Login.CommonParams.MY_PREFS_LOGINTYPE;
import static com.example.mydoctor.mydoctor.Login.CommonParams.URL_DOCTOR_SIGNUP;
import static com.example.mydoctor.mydoctor.Login.CommonParams.URL_PATIENT_SIGNUP;
import static com.example.mydoctor.mydoctor.Login.CommonParams.deviceOs;
import static com.example.mydoctor.mydoctor.Login.CommonParams.sha;


public class Login extends AppCompatActivity {


    private EditText editTextUserName, editTextUserPassword;
    Button loginBtn, signUpbtn;
    TextView tv_forgotPassword;
    private String loginID, loginPassword, userTYpe, url;

    private SpinKitView spinKitView;
    View progressOverlay;
    CountryCodePicker ccp;
    int countryCodeUser, resetId, resetUserId;

    private LinearLayout linearLayout0, linearLayout, linearLayout2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login);

        // get data from the intent
        userTYpe = getIntent().getStringExtra("type");
        if (userTYpe.equals("patient_login")) {
            url = URL_PATIENT_SIGNUP;
            // store value
            SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_LOGINTYPE, MODE_PRIVATE).edit();
            editor.putString("type", "patient");
            editor.apply();
        } else if (userTYpe.equals("doctor_login")) {

            SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_LOGINTYPE, MODE_PRIVATE).edit();
            editor.putString("type", "doctor");
            editor.apply();

            url = URL_DOCTOR_SIGNUP;
        }

        //spinKitView = (SpinKitView) findViewById(R.id.spin_kit);
        // getting the references of all widgets
        editTextUserName = (EditText) findViewById(R.id.editText_UserLoginName);
        editTextUserPassword = (EditText) findViewById(R.id.editTxt_UserLoginPassword);
        // getting references of button
        loginBtn = (Button) findViewById(R.id.userLoginButton);
        signUpbtn = (Button) findViewById(R.id.userSignUpButton);
        //LoginButton Facebookbtn1 = (LoginButton) findViewById(R.id.Facebookbtn);
        tv_forgotPassword = (TextView) findViewById(R.id.forgotPasswordTextViewAllUser);

        tv_forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                forgotPassword();
            }
        });

        // progress overlay
        progressOverlay = findViewById(R.id.progress_overlay);
        //progressOverlay.bringToFront();


        // event handling for login button
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // getting the values from the editText
                loginID = editTextUserName.getText().toString();
                loginPassword = editTextUserPassword.getText().toString();
                // checking for empty fields
                if (TextUtils.isEmpty(loginID)) {
                    editTextUserName.setError("Kindly enter ID");
                    editTextUserName.requestFocus();
                    return;
                }
                // checking for empty fields
                else if (TextUtils.isEmpty(loginPassword)) {
                    editTextUserPassword.setError("Kindly enter Password");
                    editTextUserPassword.requestFocus();
                    return;
                } else {
                    // calling server login function
                    // Show progress overlay (with animation):
                    ProgressBarUtils.animateView(progressOverlay, View.VISIBLE, 0.4f, 200);
                    //progressOverlay.setVisibility(View.VISIBLE);
                    //spinKitView.setVisibility(View.VISIBLE);
                    userLoginServiceCall(userTYpe, url);
                }
            }
        });
        // event handling for signup button
        signUpbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent in = new Intent(Login.this, SignUp.class);
                startActivity(in);*/
                if (userTYpe.equals("patient_login")) {
                    Intent in = new Intent(Login.this, SignUp.class);
                    in.putExtra("signup_type", userTYpe);
                    startActivity(in);


                } else if (userTYpe.equals("doctor_login")) {

                    Intent in = new Intent(Login.this, SignUp.class);
                    in.putExtra("signup_type", userTYpe);
                    startActivity(in);

                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    protected void onStop() {
        super.onStop();

    }

    public void userLoginServiceCall(String login_type, String Url) {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("type", login_type);
        params.put("username", loginID);
        params.put("pass", loginPassword);
        params.put("device", deviceOs);
        params.put("version", getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("AppVersion", null));
        params.put("ipaddress", getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("IP", null));
        params.put("udid", getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("UDID", null));
        params.put("sha", sha);
        client.post(Url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String JsonResponse = String.valueOf(response);
                Log.d("json_response_login", JsonResponse);
                String JsonStatus = response.optString("success");
                switch (JsonStatus) {
                    case "1":
                        // successful login


                        if (userTYpe.equals("patient_login")) {

                            SharedPreferences prefs = getSharedPreferences(MY_PREFS_LOGINTYPE, MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("type", "patient");
                            editor.apply();

                            Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), PatientMainActivity.class);
                            intent.putExtra("type", response.optString("login_type"));
                            intent.putExtra("pusername", response.optString("qur"));
                            intent.putExtra("puserpass", response.optString("prq"));
                            intent.putExtra("patientFullname", response.optString("first_name"));
                            intent.putExtra("patient_id", response.optInt("patient_id")); // integer value
                            intent.putExtra("photo", response.optString("photo"));
                            intent.putExtra("login_type", response.optString("login_type"));
                            intent.putExtra("balance", response.optString("balance"));
                            intent.putExtra("currency", response.optString("currency"));
                            startActivity(intent);

                        } else if (userTYpe.equals("doctor_login")) {

                            SharedPreferences prefs = getSharedPreferences(MY_PREFS_LOGINTYPE, MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("type", "doctor");
                            editor.apply();

                            Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), DoctorMainActivity.class);
                            intent.putExtra("type", response.optString("login_type"));
                            intent.putExtra("doctorusername", response.optString("qur"));
                            intent.putExtra("doctorpass", response.optString("prq"));
                            intent.putExtra("doctorFullname", response.optString("first_name"));
                            intent.putExtra("doctor_id", response.optInt("doctor_id"));
                            Log.d("intent_doctor_ID", String.valueOf(response.optInt("doctor_id")));
                            Log.d("intent_doc_userName", response.optString("qur"));
                            intent.putExtra("photo", response.optString("photo"));
                            intent.putExtra("balance", response.optString("balance"));
                            intent.putExtra("currency", response.optString("currency"));
                            startActivity(intent);

                        }
                        // Hide it (with animation):
                        ProgressBarUtils.animateView(progressOverlay, View.GONE, 0, 200);
                        //spinKitView.setVisibility(View.INVISIBLE);
                        finish();
                        break;
                    case "2":
                        // db error
                        // Hide it (with animation):
                        ProgressBarUtils.animateView(progressOverlay, View.GONE, 0, 200);
                        //spinKitView.setVisibility(View.INVISIBLE);
                        break;
                    case "3":
                        // Hide it (with animation):
                        //ProgressBarUtils.animateView(progressOverlay, View.GONE, 0, 200);

                        //spinKitView.setVisibility(View.INVISIBLE);
                        Toast.makeText(Login.this, "Invalid UserName/Password. Try Again!", Toast.LENGTH_SHORT).show();
                        ProgressBarUtils.animateView(progressOverlay, View.GONE, 0, 200);
                        break;
                    case "4":
                        // Final verification step pending
                        // Hide it (with animation):
                        ProgressBarUtils.animateView(progressOverlay, View.GONE, 0, 200);
                        //spinKitView.setVisibility(View.INVISIBLE);
                        /*Intent intent1 = new Intent(getApplicationContext(), SignUpStep2Final.class);
                        startActivity(intent1);*/
                        if (userTYpe.equals("doctor_login")) {
                            Intent intent = new Intent(getApplicationContext(), DoctorReg3.class);
                            intent.putExtra("doctor_id", response.optInt("doctor_id"));
                            startActivity(intent);
                        }
                        break;
                    case "5":
                        // Hide it (with animation):
                        ProgressBarUtils.animateView(progressOverlay, View.GONE, 0, 200);
                        //spinKitView.setVisibility(View.INVISIBLE);
                        // facebook step1 verification pending
                        break;

                    default:
                        ProgressBarUtils.animateView(progressOverlay, View.GONE, 0, 200);
                        break;
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                String JsonResponse = String.valueOf(errorResponse);
                Log.d("json response", JsonResponse);
                // Hide it (with animation):
                ProgressBarUtils.animateView(progressOverlay, View.GONE, 0, 200);
                Toast.makeText(Login.this, "Try Again!", Toast.LENGTH_SHORT).show();
                //spinKitView.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void forgotPassword() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);

        final View viewInflated = LayoutInflater.from(Login.this).inflate(R.layout.password_reset_dialog, null);

        final EditText phoneNumber = (EditText) viewInflated.findViewById(R.id.editTextUser_phoneOTP);
        final EditText inputOtp = (EditText) viewInflated.findViewById(R.id.resetPswd_otp);
        //final Button cancelOtp = (Button) viewInflated.findViewById(R.id.cancelOtp_pswdReset);
        final Button sendOtp = (Button) viewInflated.findViewById(R.id.sendOTP_paswdReset);
        final Button enterOtp = (Button) viewInflated.findViewById(R.id.enterOtp);
        linearLayout0 = (LinearLayout) viewInflated.findViewById(R.id.LLChangePswd0);
        linearLayout = (LinearLayout) viewInflated.findViewById(R.id.LLfrgPaswd_otp);
        linearLayout2 = (LinearLayout) viewInflated.findViewById(R.id.LLfrgPaswd_pass);
        final EditText newPass = (EditText) viewInflated.findViewById(R.id.resetPswd_newPaswd);
        final EditText newPassConfirm = (EditText) viewInflated.findViewById(R.id.resetPswd_newPaswdConfirm);
        final Button changePswd = (Button) viewInflated.findViewById(R.id.newPswdConfirmBtn);
        linearLayout0.setVisibility(View.VISIBLE);
        linearLayout.setVisibility(View.GONE);
        linearLayout2.setVisibility(View.GONE);

        builder.setView(viewInflated);
        builder.show();
        // country code picker
        ccp = (CountryCodePicker) viewInflated.findViewById(R.id.ccdDoc_country_otpPswReset);
        countryCodeUser = ccp.getSelectedCountryCodeAsInt();
        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {

                String country = ccp.getSelectedCountryName();
                Log.d("country_name", country);
                countryCodeUser = ccp.getSelectedCountryCodeAsInt();
                Toast.makeText(Login.this, "Code is:" + countryCodeUser, Toast.LENGTH_SHORT).show();

            }
        });
        changePswd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPasswordMatching(newPass.getText().toString(), newPassConfirm.getText().toString())) {
                    // when passwords match
                    ProgressBarUtils.animateView(progressOverlay, View.VISIBLE, 0.4f, 200);

                    AsyncHttpClient client = new AsyncHttpClient();
                    RequestParams params = new RequestParams();
                    params.put("type", "mpassword_reset");
                    params.put("id", resetUserId);
                    params.put("pass", newPass.getText().toString());
                    params.put("device", deviceOs);
                    params.put("version", getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("AppVersion", null));
                    params.put("ipaddress", new Splash_Screen().getLocalIpAddress());
                    params.put("udid", getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("UDID", null));
                    params.put("sha", sha);
                    client.post(url, params, new JsonHttpResponseHandler() {



                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);
                            Log.d("changePassword", response.toString());
                            switch (response.optInt("success")) {

                                case 1:
                                    // success
                                    Toast.makeText(Login.this, "Password Changed!", Toast.LENGTH_SHORT).show();
                                    ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
                                    finish();
                                    break;

                                case 2:
                                    Toast.makeText(Login.this, "Password Change Failed!", Toast.LENGTH_SHORT).show();
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
                            ProgressBarUtils.animateView(progressOverlay, View.GONE, 0, 200);
                        }
                    });

                }
            }
        });


        enterOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(SignUpStep2Final.this, "Successful", Toast.LENGTH_SHORT).show();
                if (!isEmpty(inputOtp)) {
                    userEnterOTP(Integer.parseInt(inputOtp.getText().toString()), url);
                } else
                    Toast.makeText(Login.this, "Enter OTP", Toast.LENGTH_SHORT).show();
            }
        });
        /*cancelOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(SignUpStep2Final.this, "Cancelled", Toast.LENGTH_SHORT).show();
                finish();
            }
        });*/
        sendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isEmpty(phoneNumber)) {

                    sendOtp.setEnabled(false);

                    ProgressBarUtils.animateView(progressOverlay, View.VISIBLE, 0.4f, 200);

                    AsyncHttpClient client = new AsyncHttpClient();
                    RequestParams params = new RequestParams();
                    params.put("type", "mobile_reset");
                    params.put("country_code", countryCodeUser);
                    params.put("mobile", phoneNumber.getText().toString());
                    params.put("device", deviceOs);
                    params.put("version", getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("AppVersion", null));
                    params.put("ipaddress", new Splash_Screen().getLocalIpAddress());
                    params.put("udid", getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("UDID", null));
                    params.put("sha", sha);
                    client.post(url, params, new JsonHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);
                            Log.d("sendOtp", response.toString());
                            switch (response.optInt("success")) {

                                case 1:
                                    // success
                                    Toast.makeText(Login.this, "OTP Sent!", Toast.LENGTH_SHORT).show();
                                    resetId = response.optInt("resetid");
                                    linearLayout.setVisibility(View.VISIBLE);
                                    sendOtp.setEnabled(true);
                                    ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
                                    break;

                                case 2:
                                    Toast.makeText(Login.this, "OTP Sending Failed!", Toast.LENGTH_SHORT).show();
                                    sendOtp.setEnabled(true);
                                    ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
                                    break;
                                case 3:
                                    Toast.makeText(Login.this, "Number Not Registered!", Toast.LENGTH_SHORT).show();
                                    sendOtp.setEnabled(true);
                                    ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
                                    break;
                            }

                        }
                    });
                }
            }
        });


    }

    private void userEnterOTP(int otp, String url) {
        ProgressBarUtils.animateView(progressOverlay, View.VISIBLE, 0.4f, 200);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("type", "mobile_reset_verify");
        params.put("resetid", resetId);
        params.put("otp", otp);
        params.put("device", deviceOs);
        params.put("version", getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("AppVersion", null));
        params.put("ipaddress", new Splash_Screen().getLocalIpAddress());
        params.put("udid", getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("UDID", null));
        params.put("sha", sha);
        client.post(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d("resendOtpVerify", response.toString());
                switch (response.optInt("success")) {

                    case 1:
                        // success
                        Toast.makeText(Login.this, "OTP Verified!", Toast.LENGTH_SHORT).show();
                        resetUserId = response.optInt("userid");
                        linearLayout2.setVisibility(View.VISIBLE);
                        linearLayout0.setVisibility(View.GONE);
                        linearLayout.setVisibility(View.GONE);
                        //sendOtp.setEnabled(false);
                        ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
                        break;

                    case 2:
                        Toast.makeText(Login.this, "Verification Failed!", Toast.LENGTH_SHORT).show();
                        ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
                        break;
                    case 3:
                        Toast.makeText(Login.this, "OTP Invalid!", Toast.LENGTH_SHORT).show();
                        ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
                        break;
                }
            }
        });


    }

    public boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

    //  The function checks if the entered password matches with the confirmed password
    //also called in the doctor registration page
    public boolean isPasswordMatching(String password, String confirmPassword) {
        Pattern pattern = Pattern.compile(password, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(confirmPassword);

        if (!matcher.matches()) {

            Toast.makeText(Login.this, "Passwords not matching", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

}
