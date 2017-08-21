package com.example.mydoctor.mydoctor.Login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.mydoctor.mydoctor.ChooseLoginActivity;
import com.example.mydoctor.mydoctor.R;
import com.example.mydoctor.mydoctor.Utils.ProgressBarUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import static com.example.mydoctor.mydoctor.Login.CommonParams.MY_PREFS_COMMON_PARAMS;
import static com.example.mydoctor.mydoctor.Login.CommonParams.MY_PREFS_DOCTOR_LOGIN;

public class Splash_Screen extends AppCompatActivity {

    ProgressDialog progressDialog;
    View progressOverlay;
    String userPasswordDecoded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash__screen);

        // progress overlay
        progressOverlay = findViewById(R.id.progress_overlay);

        // Show progress overlay (with animation):
        ProgressBarUtils.animateView(progressOverlay, View.VISIBLE, 0.4f, 200);


        // get the ipaddress, appversion and UDID
        String ip = getLocalIpAddress();
        String appVersion = getVersionInfo();
        if(ip != null) {
            Log.d("IP", ip);
        }
        Log.d("appVersion", appVersion);
        final String unique_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d("UDID", unique_id);
        // put all these data in shared preferences
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).edit();
        editor.putString("IP", ip);
        editor.putString("AppVersion", appVersion);
        editor.putString("UDID", unique_id);
        editor.apply();


        // get already stored data from shared preferences
        SharedPreferences pref = getSharedPreferences(MY_PREFS_DOCTOR_LOGIN, MODE_PRIVATE);
        String emailVerified = pref.getString("wnsdct_emailstatus", "no data");
        //Toast.makeText(this, "verified?" + emailVerified, Toast.LENGTH_SHORT).show();

        Thread thread = new Thread() {
            public void run() {
                try {
                    sleep(3000);



                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {

                    ProgressBarUtils.animateView(progressOverlay, View.GONE, 0, 200);

                    startActivity(new Intent(getApplicationContext(), ChooseLoginActivity.class));
                }
            }
        };
        thread.start();

/*

        // autologin      sp added in SignUp.class line:251   its better to put this sp in main activity after first login
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE);
        String userType = prefs.getString("type", null);
        String userName = prefs.getString("username", null);
        // decode the base64 password
        String userPassword = prefs.getString("pass", null);
        if (userPassword != null) {
            byte[] data = Base64.decode(userPassword, Base64.DEFAULT);
            try {
                userPasswordDecoded = new String(data, "UTF-8");
                Log.d("decoded password:", userPasswordDecoded);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        Log.d("data in sp:", userType + " " + userName + " " + userPassword);
        if ((userType != null) && (userName != null) && (userPasswordDecoded != null)) {

            // start progress dialog
            progressDialog = new ProgressDialog(Splash_Screen.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            //progressDialog.setTitle("Auto Login");
            progressDialog.setMessage("Signing In...");
            //progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFD4D9D0")));
            progressDialog.setIndeterminate(false);
            //progressDialog.show();

            // call login service for autologin
            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            params.put("type", userType);
            params.put("username", userName);
            params.put("pass", userPasswordDecoded);
            params.put("device", deviceOs);
            params.put("version", getVersionInfo());
            params.put("ipaddress", getLocalIpAddress());
            params.put("udid", unique_id);
            params.put("sha", sha);

            if (userType.equals("patient_login"))
                client.post(URL_PATIENT_SIGNUP, params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        String JsonResponse = String.valueOf(response);
                        Log.d("json response autologin", JsonResponse);
                        String JsonStatus = response.optString("success");
                        switch (JsonStatus) {
                            case "1":
                                //progressDialog.dismiss();
                                // successful login
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.putExtra("type", response.optString("login_type"));
                                intent.putExtra("pusername", response.optString("qur"));
                                intent.putExtra("puserpass", "prq");
                                intent.putExtra("patient_id", response.optString("patient_id"));
                                intent.putExtra("photo", response.optString("photo"));
                                startActivity(intent);
                                break;
                            case "2":
                                //progressDialog.dismiss();
                                // db error
                                break;
                            case "3":
                                //progressDialog.dismiss();
                                // invalid username / password
                                Log.d("invalid username", "autologin failed");
                                Toast.makeText(Splash_Screen.this, "Autologin failed! try again", Toast.LENGTH_SHORT).show();
                                Intent intent1 = new Intent(getApplicationContext(), ChooseLoginActivity.class);
                                startActivity(intent1);
                                break;
                            case "4":
                                //progressDialog.dismiss();
                                // profile completion pending
                                Toast.makeText(Splash_Screen.this, "Complete your profile!", Toast.LENGTH_SHORT).show();
                                Intent intent2 = new Intent(getApplicationContext(), UserOtpVerifySignUpStep2.class);
                                startActivity(intent2);
                                break;
                            case "5":
                                // prof
                                // progressDialog.dismiss();ile completion pending for fb users
                                break;

                                  */
/*  default:
                                        Intent intent2 = new Intent(getApplicationContext(), ChooseLoginActivity.class);
                                        startActivity(intent2);*//*

                        }

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        String JsonResponse = String.valueOf(errorResponse);
                        Log.d("json response", JsonResponse);
                        //progressDialog.dismiss();
                        Intent intent = new Intent(getApplicationContext(), ChooseLoginActivity.class);
                        //startActivity(intent);
                    }
                });
            else if (userType.equals("doctor_login")) {
                //progressDialog.dismiss();
                // client.post()
            }
        } else {
            //progressDialog.dismiss();
// it executes when the sp values are null
            Intent intent = new Intent(getApplicationContext(), ChooseLoginActivity.class);
            startActivity(intent);
        }
*/


    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

// Method to get some common params

    //get the current version number and patName
    public String getVersionInfo() {
        String versionName = "";

        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName = packageInfo.versionName;
            //
            // Toast.makeText(this, "patName:" + versionName, Toast.LENGTH_SHORT).show();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }


    // method to get device IP address
    public String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
                 en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (Exception ex) {
            Log.e("IP Address", ex.toString());
        }
        return null;
    }


}
