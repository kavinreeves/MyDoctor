package com.example.mydoctor.mydoctor.Login;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mydoctor.mydoctor.ChooseLoginActivity;
import com.example.mydoctor.mydoctor.DoctorReg3;
import com.example.mydoctor.mydoctor.R;
import com.example.mydoctor.mydoctor.Utils.ProgressBarUtils;
import com.hbb20.CountryCodePicker;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

import static com.example.mydoctor.mydoctor.Login.CommonParams.MY_PREFS_COMMON_PARAMS;
import static com.example.mydoctor.mydoctor.Login.CommonParams.URL_DOCTOR_SIGNUP;
import static com.example.mydoctor.mydoctor.Login.CommonParams.URL_DOCTOR_STEP;
import static com.example.mydoctor.mydoctor.Login.CommonParams.URL_PATIENT_SIGNUP;
import static com.example.mydoctor.mydoctor.Login.CommonParams.URL_PATIENT_STEP;
import static com.example.mydoctor.mydoctor.Login.CommonParams.deviceOs;
import static com.example.mydoctor.mydoctor.Login.CommonParams.sha;

public class SignUpStep2Final extends AppCompatActivity {

    EditText userAge, userCity, userPostcode, userCountry, userPhone;

    Button register;
    CountryCodePicker ccp;
    int countryCodeUser, otp, regId;
    Spinner spin;
    String userGender, fName, lName, email, password, userType, url, urlOtp, city, postcode, phone, age;
    String[] gender = {"Male", "Female", "Other"};
    View progressOverlay;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.reg2_del);

        setTitle("Register");
        // referencing all widgets
        userAge = (EditText) findViewById(R.id.editTextUserAge);
        userCity = (EditText) findViewById(R.id.editText_user_city);
        userPostcode = (EditText) findViewById(R.id.editText_user_postcode);
        userPhone = (EditText) findViewById(R.id.editTextUser_phone);
        register = (Button) findViewById(R.id.user_register2Btn);
        spin = (Spinner) findViewById(R.id.spinner1);
        progressOverlay = findViewById(R.id.progress_overlay);
        //String[] items = new String[]{"Gender", "Male", "Female", "Other"};

        try {
            Intent intent = getIntent();
            userType = intent.getStringExtra("userType");
            fName = intent.getStringExtra("fName");
            lName = intent.getStringExtra("lName");
            email = intent.getStringExtra("email");
            password = intent.getStringExtra("password");
        } catch (Exception e) {
            Log.e("IntentDataMissing", e.toString());
        }
        if (userType.equals("patient_login")) {
            url = URL_PATIENT_SIGNUP;
            urlOtp = URL_PATIENT_STEP;
        } else if (userType.equals("doctor_login")) {
            url = URL_DOCTOR_SIGNUP;
            urlOtp = URL_DOCTOR_STEP;
            //register.setText("Next");
        }


        // country code picker
        ccp = (CountryCodePicker) findViewById(R.id.ccdDoc_country);
        countryCodeUser = ccp.getSelectedCountryCodeAsInt();
        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {

                String country = ccp.getSelectedCountryName();
                Log.d("country_name", country);
                countryCodeUser = ccp.getSelectedCountryCodeAsInt();
                //Toast.makeText(SignUpStep2Final.this, "Code is:" + countryCodeUser, Toast.LENGTH_SHORT).show();

            }
        });


        //setting array adaptors to spinners
        //ArrayAdapter is a BaseAdapter that is backed by an array of arbitrary objects
        ArrayAdapter<String> spin_adapter = new ArrayAdapter<String>(SignUpStep2Final.this, R.layout.gender_spinner, gender);

        // setting adapters to spinners
        spin.setPrompt("Gender");
        spin.setAdapter(spin_adapter);

        //Register a callback to be invoked when an item in this AdapterView has been selected
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int position, long id) {
                userGender = gender[position];//saving the value selected
                //Toast.makeText(SignUpStep2Final.this, "selected : " + userGender, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }

        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRegisterButtonclicked();
            }
        });


    }

    private void onRegisterButtonclicked() {
        // getting values from edittexts
        age = userAge.getText().toString();
        city = userCity.getText().toString();
        postcode = userPostcode.getText().toString();
        phone = userPhone.getText().toString();

        // ********************checking for empty fields ********************
       /* checkEmptyEditTexts(userAge, age);
        checkEmptyEditTexts(userCity, city);
        checkEmptyEditTexts(userPostcode, postcode);
        checkEmptyEditTexts(userPhone, phone);*/

        if (isEmpty(userAge) | isEmpty(userCity) | isEmpty(userPostcode) | isEmpty(userPhone)) {

            Toast.makeText(this, "Please Complete all Fields", Toast.LENGTH_SHORT).show();
        } else {
            signUpServiceCall();

        }
    }

    private void signUpServiceCall() {
        // make server connection and check for email and phone number availability
        ProgressBarUtils.animateView(progressOverlay, View.VISIBLE, 0.4f, 200);
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Content-Type", "application/x-www-form-urlencoded"); // not necessary to add because it is set by default
        RequestParams params = new RequestParams();
        params.put("type", "checkdetails");
        params.put("email", email);
        params.put("mobile", phone);
        params.put("country_code", countryCodeUser);
        params.put("sha", sha);
        client.post(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                String JsonResponse = String.valueOf(response);
                Log.d("json_phone_email", JsonResponse);
                String JsonStatus = response.optString("success");
                switch (JsonStatus) {
                    case "1":
                        // service to register and generate OTP

                        AsyncHttpClient client = new AsyncHttpClient();
                        RequestParams params = new RequestParams();
                        checkParams();
                        params.put("type", "signup");
                        params.put("first_name", fName);
                        params.put("last_name", lName);
                        params.put("email", email);
                        params.put("mobile", phone);
                        params.put("country_code", countryCodeUser);
                        params.put("pass", password);
                        params.put("gender", userGender);
                        params.put("age", age);
                        params.put("city", city);
                        params.put("postalcode", postcode);
                        params.put("device", deviceOs);
                        params.put("version", getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("AppVersion", null));
                        params.put("ipaddress", new Splash_Screen().getLocalIpAddress());
                        params.put("udid", getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("UDID", null));
                        params.put("sha", sha);
                        client.post(url, params, new JsonHttpResponseHandler() {

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                super.onSuccess(statusCode, headers, response);
                                Log.d("signup_json", response.toString());       // TODO: 15-May-17   // send the currency in this pesponse
                                int jsonStatus = response.optInt("success");
                                switch (jsonStatus) {

                                    case 1:
                                        // success
                                        // call otp dialog
                                        if (userType.equals("patient_login")) {
                                            regId = response.optInt("wnspnt_regid");
                                        } else if (userType.equals("doctor_login")) {
                                            regId = response.optInt("wnsdct_regid");

                                        }
                                       ;
                                        Log.d("regid", String.valueOf(regId));
                                        enterOtp();

                                        //finish();
                                        break;
                                    case 2:
                                        Toast.makeText(SignUpStep2Final.this, "Network Error !", Toast.LENGTH_SHORT).show();
                                        break;
                                    case 3:
                                        Toast.makeText(SignUpStep2Final.this, "Phone Number Already Registered !", Toast.LENGTH_LONG).show();
                                        break;

                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                super.onFailure(statusCode, headers, throwable, errorResponse);
                                //Log.d("signup_json", errorResponse.toString());
                            }
                        });
                        ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
                        break;

                    case "2":
                        Toast.makeText(SignUpStep2Final.this, "Mobile Number Already Registered!", Toast.LENGTH_SHORT).show();
                        ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
                        break;

                    case "3":
                        ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
                        Toast.makeText(SignUpStep2Final.this, "Invalid Mobile Number!", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
                        break;
                }
            }

            private void checkParams() {
                Log.d("fname", fName);
                Log.d("lname", lName);
                Log.d("email", email);
                Log.d("mobile", phone);
                Log.d("country_code", String.valueOf(countryCodeUser));
                Log.d("pass", password);
                Log.d("gender", userGender);
                Log.d("age_user", age);
                Log.d("city", city);
                Log.d("postalcode", postcode);

            }
        });
    }


    public boolean checkEmptyEditTexts(EditText editText, String data) {
        if (TextUtils.isEmpty(data)) {
            editText.setError("Mandatory Field");
            editText.requestFocus();
            return true;
        } else return false;
    }

    public boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

    public void enterOtp() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(SignUpStep2Final.this);
        // builder.setTitle("Enter OTP");
        // I'm using fragment here so I'm using getView() to provide ViewGroup
        // but you can provide here any other instance of ViewGroup from your Fragment / Activity
        final View viewInflated = LayoutInflater.from(SignUpStep2Final.this).inflate(R.layout.enter_otp_dialog, null);
        // Set up the input
        final EditText input = (EditText) viewInflated.findViewById(R.id.userOTPPopupDialog);
        final TextView resendOtp = (TextView) viewInflated.findViewById(R.id.user_resendOTP);
        final Button cancelOtp = (Button) viewInflated.findViewById(R.id.cancelOtp);
        final Button enterOtp = (Button) viewInflated.findViewById(R.id.enterOtp);

        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        builder.setView(viewInflated);

        enterOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(SignUpStep2Final.this, "Successful", Toast.LENGTH_SHORT).show();
                if (!isEmpty(input)) {
                    userEnterOTP(Integer.parseInt(input.getText().toString()), urlOtp);
                } else
                    Toast.makeText(SignUpStep2Final.this, "Enter OTP", Toast.LENGTH_SHORT).show();
            }
        });
        cancelOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(SignUpStep2Final.this, "Cancelled", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        resendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ProgressBarUtils.animateView(progressOverlay, View.VISIBLE, 0.4f, 200);

                AsyncHttpClient client = new AsyncHttpClient();
                RequestParams params = new RequestParams();
                params.put("type", "resend_otp");
                params.put("id", regId);
                params.put("device", deviceOs);
                params.put("version", getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("AppVersion", null));
                params.put("ipaddress", new Splash_Screen().getLocalIpAddress());
                params.put("udid", getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("UDID", null));
                params.put("sha", sha);
                client.post(urlOtp, params, new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        Log.d("resendOtp", response.toString());
                        switch (response.optInt("success")) {

                            case 1:
                                // success
                                ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
                                Toast.makeText(SignUpStep2Final.this, "Enter new OTP", Toast.LENGTH_SHORT).show();
                                break;

                            case 2:
                                ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
                                Toast.makeText(SignUpStep2Final.this, "OTP Sending Failed!", Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
                                break;
                        }

                    }
                });
            }
        });

        builder.show();

    }

    private void userEnterOTP(int otp, String urlOTP) {
        ProgressBarUtils.animateView(progressOverlay, View.VISIBLE, 0.4f, 200);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("type", "otp_verify");
        params.put("id", regId);
        params.put("otp", otp);
        params.put("device", deviceOs);
        params.put("version", getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("AppVersion", null));
        params.put("ipaddress", new Splash_Screen().getLocalIpAddress());
        params.put("udid", getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("UDID", null));
        params.put("sha", sha);
        client.post(urlOTP, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d("otp_verReg_json", response.toString());

                switch (response.optInt("success")) {

                    case 1:
                        // verified
                        Toast.makeText(SignUpStep2Final.this, "Registration Successful!", Toast.LENGTH_SHORT).show();

                        if (userType.equals("patient_login")) {
                            /*AlertDialog.Builder builder = new AlertDialog.Builder(SignUpStep2Final.this);
                            builder.setTitle("Registration Successful!");
                            builder.setMessage("Use your email/phone as ID to login")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            //do things
                                            startActivity(new Intent(getApplicationContext(), ChooseLoginActivity.class));
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();*/

                            Intent intent = new Intent(getApplicationContext(), ChooseLoginActivity.class);
                            startActivity(intent);

                            finish();

                        } else if (userType.equals("doctor_login")) {

                            Intent intent = new Intent(getApplicationContext(), DoctorReg3.class);
                            intent.putExtra("userType", userType);
                            intent.putExtra("regId", regId);
                            startActivity(intent);

                            finish();
                        }
                        ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);

                        break;

                    case 2:
                        ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
                        Toast.makeText(SignUpStep2Final.this, "Network Error!", Toast.LENGTH_SHORT).show();
                        break;

                    case 3:
                        ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
                        Toast.makeText(SignUpStep2Final.this, "Wrong OTP!", Toast.LENGTH_SHORT).show();
                        break;
                    default:
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

}

