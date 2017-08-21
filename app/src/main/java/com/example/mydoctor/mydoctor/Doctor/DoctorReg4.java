package com.example.mydoctor.mydoctor.Doctor;

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
import android.widget.Toast;

import com.example.mydoctor.mydoctor.ChooseLoginActivity;
import com.example.mydoctor.mydoctor.Login.Splash_Screen;
import com.example.mydoctor.mydoctor.R;
import com.hbb20.CountryCodePicker;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

import static com.example.mydoctor.mydoctor.Login.CommonParams.MY_PREFS_COMMON_PARAMS;
import static com.example.mydoctor.mydoctor.Login.CommonParams.URL_DOCTOR_STEP;
import static com.example.mydoctor.mydoctor.Login.CommonParams.deviceOs;
import static com.example.mydoctor.mydoctor.Login.CommonParams.sha;

public class DoctorReg4 extends AppCompatActivity {

    CountryCodePicker ccp;
    // for autocomplete
    private static final String LOG_TAG = "GPlaces Autocomplete";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyDC-Wv_-hdx8a5l3gSRviKSTg7KIcDNHY4";
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    // Edittexts
    EditText hosName, addLine1, addLine2, cityName, postcode;
    int doctorId;
    Button docRegister1Btn;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.doc_reg4);

        try {
            doctorId = getIntent().getIntExtra("regId", 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        hosName = (EditText) findViewById(R.id.hospitalName);
        addLine1 = (EditText) findViewById(R.id.hospitalNameline1);
        addLine2 = (EditText) findViewById(R.id.hospitalNameline2);
        cityName = (EditText) findViewById(R.id.hospitalNameCity);
        postcode = (EditText) findViewById(R.id.hospitalNamePostcode);

        docRegister1Btn = (Button) findViewById(R.id.docAlldoneRegBtn);
        docRegister1Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                callService();
            }
        });

    }

    private void callService() {
        final String hospName = hosName.getText().toString();
        final String line1 = addLine1.getText().toString();
        final String line2 = addLine2.getText().toString();
        final String city = cityName.getText().toString();
        final String postalcode = postcode.getText().toString();

        // ********************checking for empty fields ********************

        checkEmptyEditTexts(hosName, hospName);
        checkEmptyEditTexts(addLine1, line1);
        checkEmptyEditTexts(addLine2, line2);
        checkEmptyEditTexts(cityName, city);
        checkEmptyEditTexts(postcode, postalcode);

        if (isEmpty(hosName) | isEmpty(addLine1) | isEmpty(addLine2) | isEmpty(cityName) | isEmpty(postcode)) {

            Toast.makeText(this, "Fill All Fields!", Toast.LENGTH_SHORT).show();
        } else {

            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            params.put("type", "finish_step");
            params.put("id", doctorId);
            params.put("workingat", hospName);
            params.put("add_line1", line1);
            params.put("add_line2", line2);
            params.put("add_line3", city);
            params.put("postalcode", postalcode);
            params.put("device", deviceOs);
            params.put("version", getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("AppVersion", null));
            params.put("ipaddress", new Splash_Screen().getLocalIpAddress());
            params.put("udid", getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("UDID", null));
            params.put("sha", sha);
            client.post(URL_DOCTOR_STEP, params, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    super.onSuccess(statusCode, headers, response);
                    Log.d("jsonreg4Doc", response.toString());
                    switch (response.optInt("success")) {
                        case 1:
                            Toast.makeText(DoctorReg4.this, "Registration Completed!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), ChooseLoginActivity.class);
                            startActivity(intent);

                            break;
                        default:
                            Toast.makeText(DoctorReg4.this, "Network Error!", Toast.LENGTH_SHORT).show();
                            break;
                    }

                }
            });

        }


    }

    public boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }


    public void checkEmptyEditTexts(EditText editText, String data) {
        if (TextUtils.isEmpty(data)) {
            editText.setError("Mandatory Field");
            editText.requestFocus();
            return;
        }
    }


    private void isEditTextEmpty(EditText editText, String data) {
        if (TextUtils.isEmpty(data)) {
            editText.setError("Mandatory Field");
            editText.requestFocus();
            return;
        }


    }
}











