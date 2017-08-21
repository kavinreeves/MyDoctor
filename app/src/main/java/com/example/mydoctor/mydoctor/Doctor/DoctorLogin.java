package com.example.mydoctor.mydoctor.Doctor;

import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.widget.LoginButton;

public class DoctorLogin extends AppCompatActivity {

    Button docLogin, docRegister;
    EditText docLoginUserID, docLoginPassword;
    TextView docForgotPassword;
    String loginID, loginPassword;
    // for facebook
    LoginButton loginButton;
    String docFBuserId, docFirstName, docLastName, docFBemailId;
    AccessToken accessToken;
    Profile profile;
    CallbackManager callbackManager;
    ProfileTracker profileTracker;

/*

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // helps inflating the fb button in the layout
        FacebookSdk.sdkInitialize(getApplicationContext());  // do it before the setContentView
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_doctor_sign_in);
        // get references for all widgets
        docLoginUserID = (EditText) findViewById(R.id.editText_DocId);
        docLoginPassword = (EditText) findViewById(R.id.editTxt_DocPassword);
        docLogin = (Button) findViewById(R.id.buttonDocSignIn);
        docRegister = (Button) findViewById(R.id.buttonDocSignUp);

       // loginButton = (LoginButton) findViewById(R.id.facebookbtnDoctorLogin);

        // Register button
        docRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), DoctorReg4.class);
                startActivity(intent);
            }
        });
        // login button
        docLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginID = docLoginUserID.getText().toString();
                loginPassword = docLoginPassword.getText().toString();
                checkEmptyEditText(docLoginUserID, loginID);
                checkEmptyEditText(docLoginPassword, loginPassword);
                doctorLoginServiceCall();
            }
        });

        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                setFacebookData(loginResult);
                // call service for facebook step1 registration
                callFacebookStep1();

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });





    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //callbackManager.onActivityResult(requestCode, resultCode, data);

    }



    public void doctorLoginServiceCall(){

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("type", "doctor_login");
        params.put("username", loginID);
        params.put("pass",loginPassword);
        params.put("device", deviceOs);
        params.put("version", getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("AppVersion", null));
        params.put("ipaddress", getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("IP", null) );
        params.put("udid", getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("UDID", null));
        params.put("sha", sha);
        client.post(URL_DOCTOR_SIGNUP, params, new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String JsonResponse = String.valueOf(response);
                Log.d("json response", JsonResponse);
                String JsonStatus = response.optString("success");
                switch (JsonStatus){
                    case "1":
                        // successful login
                        Toast.makeText(DoctorLogin.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), DoctorMainActivity.class);
                        intent.putExtra("type",response.optString("login_type"));
                        intent.putExtra("doctorusername",response.optString("qur"));
                        intent.putExtra("doctorpass", "prq");
                        intent.putExtra("doctor_id", response.optInt("doctor_id"));
                        Log.d("intent_doctor_ID", String.valueOf(response.optInt("doctor_id")));
                        Log.d("intent_doc_userName", response.optString("qur"));
                        intent.putExtra("photo", response.optString("photo"));
                        intent.putExtra("balance", response.optString("balance"));
                        startActivity(intent);
                        break;
                    case "2":
                        // db error
                        break;
                    case "3":
                        Toast.makeText(DoctorLogin.this, "Invalid UserName/Password. Try Again!", Toast.LENGTH_SHORT).show();
                        break;
                    case "4":
                        // Final verification step pending
                        break;
                    case "5":
                        // facebook step2 verification pending
                        break;
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                String JsonResponse = String.valueOf(errorResponse);
                Log.d("json response", JsonResponse);
            }
        });
    }

    public void checkEmptyEditText (EditText editText, String data){

        if (TextUtils.isEmpty(data)) {
            editText.setError("Mandatory Field");
            editText.requestFocus();
            return;
        }
    }

    private void setFacebookData(final LoginResult loginResult) {
        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        // Application code
                        try {
                            Log.i("Response", response.toString());

                            docFBemailId = response.getJSONObject().getString("email");
                            docFirstName = response.getJSONObject().getString("first_name");
                            docLastName = response.getJSONObject().getString("last_name");
                            //String gender = response.getJSONObject().getString("gender");
                            //String bday = response.getJSONObject().getString("birthday");

                            profile = Profile.getCurrentProfile();
                            docFBuserId = profile.getId();
                            final String link = profile.getProfilePictureUri(200, 200).toString();
                            //profilePictureView.setProfileId(userid);
                            //String link = profile.getLinkUri().toString();
                            Log.i("Link", link);
                            if (Profile.getCurrentProfile() != null) {
                                Log.i("Login", "ProfilePic" + Profile.getCurrentProfile().getProfilePictureUri(100, 100));
                                //fbPic.setImageURI();
                            }
                            Log.i("Login " + " id ", docFBuserId);
                            Log.i("Login" + "Email", docFBemailId);
                            Log.i("Login" + "FirstName", docFirstName);
                            Log.i("Login" + "LastName", docLastName);
                            //Log.i("Login" + "Gender", gender);
                            //Log.i("Login" + "Bday", bday);
//


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,email,first_name,last_name,gender, birthday");
        request.setParameters(parameters);
        request.executeAsync();
    }

    protected void callFacebookStep1(){

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("type", "fbregister");
        params.put("first_name", docFirstName);
        params.put("last_name", docLastName);
        params.put("email", docFBemailId);
        params.put("device", deviceOs);
        params.put("version",getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("AppVersion", null) );
        params.put("ipaddress",getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("IP", null) );
        params.put("udid", getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("UDID", null));
        params.put("fbid", docFBuserId);
        params.put("sha", sha);

        client.post(URL_DOCTOR_SIGNUP, params, new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String JsonResponse = String.valueOf(response);
                Log.d("json response", JsonResponse);
                String JsonStatus = response.optString("success");
                switch (JsonStatus) {
                    case "1":
                        String id = response.optString("wnsdct_regid");
                        // success and store details in shared prefrences
                        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_DOCTOR_LOGIN, MODE_PRIVATE).edit();
                        editor.putString("id", id);
                        editor.commit();

                        break;
                    case "2":
                        // db error
                        break;
                    case "3":
                        // email sending failed
                        break;
                    case "4":
                        // already registered
                        //call login service
                        String username = response.optString("username");
                        String password = response.optString("password");

                        AsyncHttpClient client = new AsyncHttpClient();
                        RequestParams params = new RequestParams();
                        params.put("type", "fbregister");
                        params.put("username", username);
                        params.put("pass", password);
                        params.put("device", deviceOs);
                        params.put("version", getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("AppVersion", null));
                        params.put("ipaddress", getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("IP", null));
                        params.put("udid", getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("UDID", null));
                        params.put("sha", sha);

                        client.post(URL_DOCTOR_SIGNUP, params, new JsonHttpResponseHandler(){

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                String JsonResponse = String.valueOf(response);
                                Log.d("json response", JsonResponse);
                                String JsonStatus = response.optString("success");
                                switch (JsonStatus) {
                                    case "1":
                                        // success and store details in shared prefrences
                                        Intent intent = new Intent(getApplicationContext(), DoctorMainActivity.class);
                                        startActivity(intent);
                                        break;
                                    case "2":
                                        // db error
                                        break;
                                    case "3":
                                        // invalid username password
                                        break;
                                    case "4":
                                        //final step is pending
                                        break;
                                    case "5":
                                        // step2 reg pending
                                        Intent intent1 = new Intent(getApplicationContext(), DoctorReg2.class);
                                        startActivity(intent1);
                                        break;
                                }

                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                super.onFailure(statusCode, headers, throwable, errorResponse);
                            }
                        });


                        break;
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });


    }
*/


}


