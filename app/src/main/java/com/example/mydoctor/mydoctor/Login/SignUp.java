package com.example.mydoctor.mydoctor.Login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.mydoctor.mydoctor.R;
import com.example.mydoctor.mydoctor.Utils.ProgressBarUtils;
import com.hbb20.CountryCodePicker;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;

import static com.example.mydoctor.mydoctor.Login.CommonParams.URL_DOCTOR_SIGNUP;
import static com.example.mydoctor.mydoctor.Login.CommonParams.URL_PATIENT_SIGNUP;
import static com.example.mydoctor.mydoctor.Login.CommonParams.sha;

public class SignUp extends AppCompatActivity {

    EditText editxtFirstname, editxtLastname, editxt_phone, userAge, editxt_email, editxt_password, editxt_cnf_password, editText_otp;
    Button getUserOtpBtn, otpUserVerifyBtn, next;
    CountryCodePicker ccp;
    int countryCodeUser;
    RadioGroup userGender;
    RadioButton radioButton;
    String userName, userPassword, userType, url;

    View progressOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_sign_up);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.reg_del);

        setTitle("Register");

        userType = getIntent().getStringExtra("signup_type");
        if (userType.equals("patient_login")) {
            url = URL_PATIENT_SIGNUP;

        } else if (userType.equals("doctor_login")) {
            url = URL_DOCTOR_SIGNUP;
        }

      /*  android.support.v7.app.ActionBar menu = getSupportActionBar();
        menu.setDisplayShowHomeEnabled(true);
        menu.setLogo(R.drawable.icon_launcher);
        menu.setTitle("Register");
        menu.setDisplayUseLogoEnabled(true);
*/
        progressOverlay = findViewById(R.id.progress_overlay);
        editxtFirstname = (EditText) findViewById(R.id.userFirstName);
        editxtLastname = (EditText) findViewById(R.id.userLastName);
        editxt_email = (EditText) findViewById(R.id.editTextUserEmail);
        editxt_password = (EditText) findViewById(R.id.editTextUserPassword);
        editxt_cnf_password = (EditText) findViewById(R.id.editTextUserCnfPassword);
        next = (Button) findViewById(R.id.userRegbtn);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                nextButtonClicked();

            }
        });

        editxt_password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    String value = editxt_password.getText().toString();
                    if (value.length() < 6) {

                        //Toast.makeText(SignUp.this, "Minimum 6 Characters Required!", Toast.LENGTH_SHORT).show();
                        editxt_password.setError("Minimum 6 Characters");
                        editxt_password.requestFocus();
                    } else editxt_password.setError(null);
                } else if (!hasFocus) {
                    String value = editxt_password.getText().toString();
                    if (value.length() < 6) {

                        //Toast.makeText(SignUp.this, "Minimum 6 Characters Required!", Toast.LENGTH_SHORT).show();
                        editxt_password.setError("Minimum 6 Characters");

                    } else editxt_password.setError(null);
                }
            }
        });

    }

    private void nextButtonClicked() {

        // get all edittext values
        final String fName = editxtFirstname.getText().toString();
        final String lName = editxtLastname.getText().toString();
        final String email = editxt_email.getText().toString();
        final String password = editxt_password.getText().toString();
        final String cnfPassword = editxt_cnf_password.getText().toString();
        // ********************checking for empty fields ********************

        checkEmptyEditTexts(editxtFirstname, fName);
        checkEmptyEditTexts(editxtLastname, lName);
        checkEmptyEditTexts(editxt_email, email);
        checkEmptyEditTexts(editxt_password, password);
        checkEmptyEditTexts(editxt_cnf_password, cnfPassword);

        if (isEmpty(editxtFirstname) | isEmpty(editxtLastname) | isEmpty(editxt_email) | isEmpty(editxt_password) | isEmpty(editxt_cnf_password)) {

            Toast.makeText(this, "Please Complete all Fields", Toast.LENGTH_SHORT).show();
        } else {

            // *********** checking for email validation *************
            boolean emailStatus = isEmailValid(email);
            if (!emailStatus) {
                editxt_email.setError("Enter a valid email");
                editxt_email.requestFocus();

            }
            // *************** checking for minimum 6 characters ******************
            else if(editxt_password.getText().toString().length() < 6){
                editxt_password.setError("Minimum 6 Characters");
                editxt_password.requestFocus();
            }
            // ************* checking password and confirm password match*****************
            else if (isPasswordMatching(password, cnfPassword)) {
                // when passwords match

                validateEmail(email, fName, lName, password);
            }

        }
    }



    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Getting the references of all editTexts
        editxtFirstname = (EditText) findViewById(R.id.userFirstName);
        editxtLastname = (EditText) findViewById(R.id.userLastName);
        editxt_phone = (EditText) findViewById(R.id.editTextUser_phone);
        editxt_email = (EditText) findViewById(R.id.editTextUserEmail);
        editxt_password = (EditText) findViewById(R.id.editTextUserPassword);
        editxt_cnf_password = (EditText) findViewById(R.id.editTextUserCnfPassword);
        getUserOtpBtn = (Button) findViewById(R.id.userRegbtn);



        // country code picker
        ccp = (CountryCodePicker) findViewById(R.id.ccdDoc_country);

        countryCodeUser = ccp.getSelectedCountryCodeAsInt();

        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {

                String country = ccp.getSelectedCountryName();
                Log.d("country patName", country);
                countryCodeUser = ccp.getSelectedCountryCodeAsInt();
                Toast.makeText(SignUp.this, "Code is:" + countryCodeUser, Toast.LENGTH_SHORT).show();

            }
        });


        getUserOtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                // get all edittext values
                final String fName = editxtFirstname.getText().toString();
                final String lName = editxtLastname.getText().toString();
                final String email = editxt_email.getText().toString();

                final String password = editxt_password.getText().toString();
                final String cnfPassword = editxt_cnf_password.getText().toString();
                final String phone = editxt_phone.getText().toString();



                // ********************checking for empty fields ********************
                checkEmptyEditTexts(editxtFirstname,fName);
                checkEmptyEditTexts(editxtLastname,lName);
                checkEmptyEditTexts(editxt_email,email);
                checkEmptyEditTexts(editxt_password,password);
                checkEmptyEditTexts(editxt_cnf_password,cnfPassword);
                checkEmptyEditTexts(editxt_phone, phone);

                // *********** checking for email validation *************
                boolean emailStatus = isEmailValid(email);
                if (emailStatus != true) {
                    editxt_email.setError("Enter a valid email");
                    editxt_email.requestFocus();
                }
                // ************* checking password and confirm password match*****************
                boolean status = isPasswordMatching(password, cnfPassword);
                if (status){
                    // when passwords match
                    // make server connection and check for email and phone number availability
                    AsyncHttpClient client = new AsyncHttpClient();
                    client.addHeader("Content-Type", "application/x-www-form-urlencoded"); // not necessary to add because it is set by default
                    RequestParams params = new RequestParams();
                    params.put("type", "checkdetails");
                    params.put("email", email);
                    params.put("mobile", phone);
                    params.put("country_code", countryCodeUser);
                    params.put("sha", sha);
                    client.post(URL_PATIENT_SIGNUP, params, new JsonHttpResponseHandler(){
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                            String JsonResponse = String.valueOf(response);
                            Log.d("json response", JsonResponse);
                            String JsonStatus = response.optString("success");
                            switch (JsonStatus) {
                                case "1":
                                    // go to registering user details
                                    String ipAddress = new Splash_Screen().getLocalIpAddress();
                                    AsyncHttpClient client1 = new AsyncHttpClient();
                                    RequestParams params = new RequestParams();
                                    params.put("type", "signup");
                                    params.put("first_name", fName);
                                    params.put("last_name", lName);
                                    params.put("email", email);
                                    params.put("mobile", phone);
                                    params.put("country_code", countryCodeUser);
                                    params.put("pass", password);
                                    params.put("device", deviceOs);
                                    params.put("version", getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("AppVersion", null));
                                    params.put("ipaddress", ipAddress);
                                    params.put("udid", getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("UDID", null));
                                    params.put("sha", sha);

                                    client1.post(URL_PATIENT_SIGNUP, params, new JsonHttpResponseHandler(){
                                        @Override
                                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                            String JsonResponse = String.valueOf(response);
                                            Log.d("json response", JsonResponse);
                                            String JsonStatus = response.optString("success");
                                            String userId = response.optString("wnspnt_regid");
                                            // final because it will stored in sp after otp verification
                                            userName = response.optString("wnspnt_username");
                                            userPassword = response.optString("prq");
                                            Log.d("wnspnt_regid", userId);
                                            Log.d("wnspnt_username", userName);
                                            Log.d("prq", userPassword);
                                            switch (JsonStatus) {
                                                case "1":

                                                    // store type, userid, password in common shared preferences for autologin from splash screen page
                                                    SharedPreferences.Editor editor1 = getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).edit();
                                                    editor1.putString("type", "patient_login");
                                                    editor1.putString("wnspnt_regid", userId);
                                                    editor1.putString("wnspnt_username", userName);
                                                    editor1.putString("prq", userPassword);
                                                    editor1.apply();

                                                    Log.d("data stored in sp ", "true");

                                                    Toast.makeText(SignUp.this, "Verify Otp Now", Toast.LENGTH_SHORT).show();

                                                    // now go to verify otp page
                                                    Intent intent = new Intent(getApplicationContext(), UserOtpVerifySignUpStep2.class);
                                                    intent.putExtra("type", "patient_login");
                                                    intent.putExtra("wnspnt_regid", userId);
                                                    intent.putExtra("wnspnt_username", userName);
                                                    intent.putExtra("prq", userPassword);
                                                    startActivity(intent);

                                                    break;
                                                case "2":
                                                    // db error
                                                    break;
                                                case "3":
                                                    // invalid mobile numkber emailID
                                                    break;
                                            }
                                        }

                                        @Override
                                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                            super.onFailure(statusCode, headers, throwable, errorResponse);
                                        }
                                    });


                                    break;
                                case "2":
                                    // email/phone number already registered
                                    Toast.makeText(SignUp.this, "Email/Phone number already registered !", Toast.LENGTH_SHORT).show();

                                    break;
                                case "3":
                                    // invalid mobile number

                                    break;

                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            Toast.makeText(SignUp.this, "Network Error Try Again!", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });

        
    }


    //  The function checks if the entered password matches with the confirmed password
    //also called in the doctor registration page
    public boolean isPasswordMatching(String password, String confirmPassword) {
        Pattern pattern = Pattern.compile(password, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(confirmPassword);

        if (!matcher.matches()) {

            Toast.makeText(SignUp.this, "Passwords not matching", Toast.LENGTH_SHORT).show();

            return false;
        }

        return true;
    }

public void checkEmptyEditTexts(EditText editText, String data){
    if (TextUtils.isEmpty(data)) {
        editText.setError("Mandatory Field");
        editText.requestFocus();
        return;
    }
}
*/

    //  The function checks if the entered password matches with the confirmed password
    //also called in the doctor registration page
    public boolean isPasswordMatching(String password, String confirmPassword) {
        Pattern pattern = Pattern.compile(password, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(confirmPassword);

        if (!matcher.matches()) {

            Toast.makeText(SignUp.this, "Passwords not matching", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
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

    private void validateEmail(final String email, final String fName, final String lName, final String password) {

        ProgressBarUtils.animateView(progressOverlay, View.VISIBLE, 0.4f, 200);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("type", "checkemail");
        params.put("email", email);
        params.put("sha", sha);
        client.post(url, params, new JsonHttpResponseHandler() {


            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d("checkemailJson", response.toString());

                switch (response.optInt("success")) {
                    case 1:

                        Intent intent = new Intent(getApplicationContext(), SignUpStep2Final.class);
                        intent.putExtra("userType", userType);
                        intent.putExtra("fName", fName);
                        intent.putExtra("lName", lName);
                        intent.putExtra("email", email);
                        intent.putExtra("password", password);
                        startActivity(intent);
                        ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
                        break;
                    case 2:

                        Toast.makeText(SignUp.this, "Email Already Registered!", Toast.LENGTH_SHORT).show();
                        ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
                        break;
                    case 100:
                        // invalid request type
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

    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

}
