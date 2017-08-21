package com.example.mydoctor.mydoctor;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.mydoctor.mydoctor.Doctor.DoctorReg4;
import com.example.mydoctor.mydoctor.Login.Splash_Screen;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

import static com.example.mydoctor.mydoctor.Login.CommonParams.MY_PREFS_COMMON_PARAMS;
import static com.example.mydoctor.mydoctor.Login.CommonParams.URL_DOCTOR_STEP;
import static com.example.mydoctor.mydoctor.Login.CommonParams.deviceOs;
import static com.example.mydoctor.mydoctor.Login.CommonParams.sha;

public class DoctorReg3 extends AppCompatActivity implements View.OnClickListener {

    EditText edtSpec, edtExp, edtQual, edtId, edtAmfrom, edtPmfrom, edtAmTo, edtPmTo, edtF_Fee, edtVideoFee, edtChatFee;
    Button next, skip;
    String spec, qual, exp, govId, amFrom, amTo, pmFrom, pmTo, currency;
    EditText spinCurrency;
    String dcPrice, vcPrice, ocPrice;
    int doctorId;
    String[] currencyList = {"GBP", "INR", "AED"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_doctor_reg3);

        // get data from intent
        try {
            doctorId = getIntent().getIntExtra("regId", 0);
            currency = getIntent().getStringExtra("currency");
        } catch (Exception e) {
            e.printStackTrace();
        }
        edtSpec = (EditText) findViewById(R.id.editTextDocSpec);
        edtExp = (EditText) findViewById(R.id.editTextDocExp);
        edtQual = (EditText) findViewById(R.id.editTextDocQual);
        edtId = (EditText) findViewById(R.id.editTextDocRegId);
        edtAmfrom = (EditText) findViewById(R.id.editTextDocAMfrom);
        edtAmTo = (EditText) findViewById(R.id.editTextDocAMTo);
        edtPmfrom = (EditText) findViewById(R.id.editTextDocPMfrom);
        edtPmTo = (EditText) findViewById(R.id.editTextDocPMto);
        // edtCurrency = (EditText) findViewById(R.id.doc_currency);
        edtF_Fee = (EditText) findViewById(R.id.editTextDocF_fee);
        edtVideoFee = (EditText) findViewById(R.id.editTextDocVideo_fee);
        edtChatFee = (EditText) findViewById(R.id.editTextDocChat_fee);

        next = (Button) findViewById(R.id.docReg3Next);
        skip = (Button) findViewById(R.id.docReg3Skip);

        // set listener
        edtAmfrom.setOnClickListener(this);
        edtAmfrom.setInputType(InputType.TYPE_NULL);
        edtAmfrom.setKeyListener(null);
        edtAmTo.setOnClickListener(this);
        edtAmTo.setInputType(InputType.TYPE_NULL);
        edtAmTo.setKeyListener(null);
        edtPmfrom.setOnClickListener(this);
        edtPmfrom.setInputType(InputType.TYPE_NULL);
        edtPmfrom.setKeyListener(null);
        edtPmTo.setOnClickListener(this);
        edtPmTo.setInputType(InputType.TYPE_NULL);
        edtPmTo.setKeyListener(null);

        next.setOnClickListener(this);


        spinCurrency = (EditText) findViewById(R.id.doc_currency);
        spinCurrency.setText(currency);   // get this from intent

       /* ArrayAdapter<String> spin_adapter = new ArrayAdapter<String>(DoctorReg3.this, R.layout.gender_spinner, currencyList);
        spinCurrency.setAdapter(spin_adapter);
        //Register a callback to be invoked when an item in this AdapterView has been selected
        spinCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int position, long id) {
                currency = currencyList[position];//saving the value selected
                //Toast.makeText(DoctorReg3.this, "selected : " + currency, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }

        });*/

        // set event handling gor from and to timings

    }

    public void setTime(final EditText editText) {
        int hour = 0;
        int minute = 0;
       /* editText.setText("00:00:00");*/
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(DoctorReg3.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                String hour = String.format("%02d", selectedHour);
                String minute = String.format("%02d", selectedMinute);
                String timeFormat = "" + hour + ":" + minute + ":00";
                editText.setText(timeFormat);
                editText.setError(null);




            }
        }, hour, minute, false);//No 24 hour time

        mTimePicker.setTitle("Select Time");
        mTimePicker.show();



    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case (R.id.editTextDocAMfrom):

                setTime(edtAmfrom);

                break;
            case (R.id.editTextDocPMfrom):
                setTime(edtPmfrom);
                break;
            case (R.id.editTextDocAMTo):
                setTime(edtAmTo);
                break;
            case (R.id.editTextDocPMto):
                setTime(edtPmTo);
                break;
            case (R.id.docReg3Next):
                callService();
                break;
            case (R.id.docReg3Skip):
                startActivity(new Intent(getApplicationContext(), DoctorReg4.class));
                break;

        }
    }

    private void callService() {

        // check all fields
        spec = edtSpec.getText().toString();
        exp = edtExp.getText().toString();
        qual = edtQual.getText().toString();
        govId = edtId.getText().toString();

        amFrom = edtAmfrom.getText().toString();
        amTo = edtAmTo.getText().toString();
        pmFrom = edtPmfrom.getText().toString();
        pmTo = edtPmTo.getText().toString();

        dcPrice = (edtF_Fee.getText().toString());
        vcPrice = (edtVideoFee.getText().toString());
        ocPrice = (edtChatFee.getText().toString());

        checkEmptyEditTexts(edtSpec, spec);
        checkEmptyEditTexts(edtExp, exp);
        checkEmptyEditTexts(edtQual, qual);
        checkEmptyEditTexts(edtId, govId);
        checkEmptyEditTexts(edtAmfrom, amFrom);
        checkEmptyEditTexts(edtAmTo, amTo);
        checkEmptyEditTexts(edtPmfrom, pmFrom);
        checkEmptyEditTexts(edtPmTo, pmTo);


        checkEmptyEditTexts(edtF_Fee, dcPrice);
        checkEmptyEditTexts(edtVideoFee, vcPrice);
        checkEmptyEditTexts(edtChatFee, ocPrice);

        /*dcPrice = Float.parseFloat(edtF_Fee.getText().toString());
        vcPrice = Float.parseFloat(edtVideoFee.getText().toString());
        ocPrice = Float.parseFloat(edtChatFee.getText().toString());*/

        // check all fields
        if ((isEmpty(edtSpec) | isEmpty(edtExp) | isEmpty(edtQual) | isEmpty(edtId) | isEmpty(edtAmfrom) | isEmpty(edtAmTo)
                | isEmpty(edtPmfrom) | isEmpty(edtPmTo) | isEmpty(edtF_Fee) | isEmpty(edtVideoFee) | isEmpty(edtChatFee))){

            Toast.makeText(this, "Enter All Mandatory Fields", Toast.LENGTH_SHORT).show();

        } else {


            //Toast.makeText(this, "validated", Toast.LENGTH_SHORT).show();

            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            params.put("type", "step2");
            params.put("id", doctorId);
            params.put("specialization", spec);
            params.put("experience", exp);
            params.put("qualification", qual);
            params.put("doctorgovid", govId);
            params.put("time_from", amFrom);
            params.put("time_to", amTo);
            params.put("time2_from", pmFrom);
            params.put("time2_to", pmTo);
            //params.put("currency", currency);
            params.put("dc_price", dcPrice);
            params.put("vc_price", vcPrice);
            params.put("oc_price", ocPrice);

            params.put("device", deviceOs);
            params.put("version", getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("AppVersion", null));
            params.put("ipaddress", new Splash_Screen().getLocalIpAddress());
            params.put("udid", getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("UDID", null));
            params.put("sha", sha);
            client.post(URL_DOCTOR_STEP, params, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    Log.d("jsonreg3Doc", response.toString());
                    switch (response.optInt("success")) {
                        case 1:
                            Intent intent = new Intent(getApplicationContext(), DoctorReg4.class);
                            intent.putExtra("regId", doctorId);
                            startActivity(intent);

                            break;
                        default:
                            Toast.makeText(DoctorReg3.this, "Network Error!", Toast.LENGTH_SHORT).show();

                            /*Intent intent1 = new Intent(getApplicationContext(), DoctorReg4.class);
                            startActivity(intent1);*/
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

    private boolean validate(EditText[] fields) {
        for (int i = 0; i < fields.length; i++) {
            EditText currentField = fields[i];
            if (currentField.getText().toString().length() <= 0) {
                currentField.setError("Mandatory Field");
                currentField.requestFocus();
                return false;
            }
        }
        return true;
    }
}
