package com.example.mydoctor.mydoctor.Doctor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mydoctor.mydoctor.Login.Splash_Screen;
import com.example.mydoctor.mydoctor.R;
import com.example.mydoctor.mydoctor.Utils.ProgressBarUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

import static com.example.mydoctor.mydoctor.Login.CommonParams.MY_PREFS_COMMON_PARAMS;
import static com.example.mydoctor.mydoctor.Login.CommonParams.MY_PREFS_COMMON_PARAMS_DOCTOR;
import static com.example.mydoctor.mydoctor.Login.CommonParams.URL_DOCTOR_OPERATION;
import static com.example.mydoctor.mydoctor.Login.CommonParams.deviceOs;
import static com.example.mydoctor.mydoctor.Login.CommonParams.sha;

public class WritePrescription extends AppCompatActivity implements View.OnClickListener {

    ImageButton addMedicine;
    LinearLayout ll1, ll2, ll3, ll4, ll5;
    EditText drugName1, drugName2, drugName3, drugName4, drugName5;
    EditText drugDosage1, drugDuration1, drugQuantity2, drugDosage2, drugDuration2,
            drugQuantity3, drugDosage3, drugDuration3, drugQuantity4, drugDosage4, drugDuration4, drugQuantity5, drugDosage5, drugDuration5;
    EditText drugQuantity1, symptoms;

    TextView remove1, remove2, remove3, remove4, remove5, patientIdTv, patientNameTv, dateTv, docName;

    Button send, preview;
    private View progressOverlay;
    WebView webView;

    String base64Medicine;

    String[] quantity = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};

    int patient_id, apt_id;
    String apt_type, presdate, symptomps, encodedMedicine, presPDF, patientName, date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_write_prescription);

        try {
            patient_id = Integer.parseInt(getIntent().getStringExtra("patient_id"));
            apt_id = Integer.parseInt(getIntent().getStringExtra("apt_id"));
            apt_type = getIntent().getStringExtra(("apt_type"));
            patientName = getIntent().getStringExtra(("patient_name"));
            date = getIntent().getStringExtra(("apt_date"));

        } catch (Exception e) {
            e.printStackTrace();
        }

        //webView = (WebView) findViewById(R.id.previewPrescriptionDoctor);

        progressOverlay = findViewById(R.id.progress_overlay);
        addMedicine = (ImageButton) findViewById(R.id.add_medicine_Btn);
        ll1 = (LinearLayout) findViewById(R.id.pp_LL1);
        ll2 = (LinearLayout) findViewById(R.id.pp_LL2);
        ll3 = (LinearLayout) findViewById(R.id.pp_LL3);
        ll4 = (LinearLayout) findViewById(R.id.pp_LL4);
        ll5 = (LinearLayout) findViewById(R.id.pp_LL5);

        docName = (TextView) findViewById(R.id.pres_docName);
        patientIdTv = (TextView) findViewById(R.id.patientIdpres);
        patientNameTv = (TextView) findViewById(R.id.patNamepres);
        dateTv = (TextView) findViewById(R.id.datePresc);

        docName.setText("Dr "+getSharedPreferences(MY_PREFS_COMMON_PARAMS_DOCTOR, MODE_PRIVATE).getString("doc_username", ""));
        patientIdTv.setText("Patient ID: "+patient_id);
        patientNameTv.setText("Patient Name: "+patientName);
        dateTv.setText(date);

        symptoms = (EditText) findViewById(R.id.symptoms_docPresc);
        drugName1 = (EditText) findViewById(R.id.pres_drugName1);
        drugQuantity1 = (EditText) findViewById(R.id.pres_Quantity1);
        drugDosage1 = (EditText) findViewById(R.id.pres_dosage1);
        drugDuration1 = (EditText) findViewById(R.id.pres_duration1);

        drugName2 = (EditText) findViewById(R.id.pres_drugName2);
        drugQuantity2 = (EditText) findViewById(R.id.pres_Quantity2);
        drugDosage2 = (EditText) findViewById(R.id.pres_dosage2);
        drugDuration2 = (EditText) findViewById(R.id.pres_duration2);

        drugName3 = (EditText) findViewById(R.id.pres_drugName3);
        drugQuantity3 = (EditText) findViewById(R.id.pres_Quantity3);
        drugDosage3 = (EditText) findViewById(R.id.pres_dosage3);
        drugDuration3 = (EditText) findViewById(R.id.pres_duration3);

        drugName4 = (EditText) findViewById(R.id.pres_drugName4);
        drugQuantity4 = (EditText) findViewById(R.id.pres_Quantity4);
        drugDosage4 = (EditText) findViewById(R.id.pres_dosage4);
        drugDuration4 = (EditText) findViewById(R.id.pres_duration4);

        drugName5 = (EditText) findViewById(R.id.pres_drugName5);
        drugQuantity5 = (EditText) findViewById(R.id.pres_Quantity5);
        drugDosage5 = (EditText) findViewById(R.id.pres_dosage5);
        drugDuration5 = (EditText) findViewById(R.id.pres_duration5);

        remove1 = (TextView) findViewById(R.id.removepp_LL1);
        remove2 = (TextView) findViewById(R.id.removepp_LL2);
        remove3 = (TextView) findViewById(R.id.removepp_LL3);
        remove4 = (TextView) findViewById(R.id.removepp_LL4);
        remove5 = (TextView) findViewById(R.id.removepp_LL5);

        send = (Button) findViewById(R.id.sendPrescDoc);
        preview = (Button) findViewById(R.id.previewPrescDoc);

        addMedicine.setOnClickListener(this);
        send.setOnClickListener(this);
        preview.setOnClickListener(this);

        remove2.setOnClickListener(this);
        remove3.setOnClickListener(this);
        remove4.setOnClickListener(this);
        remove5.setOnClickListener(this);

       /* ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_singlechoice, quantity);
        drugQuantity1.setThreshold(1);
        //Set the adapter
        drugQuantity1.setAdapter(adapter);


        String quan = drugQuantity1.getText().toString();
        Log.d("quantity", quan);*/

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_medicine_Btn:
                addMedicineFields();
                break;
            case R.id.removepp_LL2:
                ll2.setVisibility(View.GONE);
                break;
            case R.id.removepp_LL3:
                ll3.setVisibility(View.GONE);
                break;
            case R.id.removepp_LL4:
                ll4.setVisibility(View.GONE);
                break;
            case R.id.removepp_LL5:
                ll5.setVisibility(View.GONE);
                break;
            case R.id.sendPrescDoc:
                callService("send");
                break;
            case R.id.previewPrescDoc:
                callService("preview");
                break;
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void preview() {
        ProgressBarUtils.animateView(progressOverlay, View.VISIBLE, 0.4f, 200);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("type", "previewprescription");
        params.put("id", getSharedPreferences(MY_PREFS_COMMON_PARAMS_DOCTOR, MODE_PRIVATE).getInt("doctor_id", 0));//
        params.put("patient_id", patient_id);
        params.put("apt_id", apt_id);
        params.put("apt_type", apt_type);
        params.put("presdate", getDateTime());
        // Log.d("time_date", new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance()));
        params.put("symptomps", symptoms.getText().toString());
        params.put("medicine", base64Medicine);
        params.put("device", deviceOs);
        params.put("version", getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("AppVersion", null));
        params.put("ipaddress", new Splash_Screen().getLocalIpAddress());
        params.put("udid", getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("UDID", null));
        params.put("sha", sha);
        client.post(URL_DOCTOR_OPERATION, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                Log.d("prescriptionPreviewJson", response.toString());
                switch (response.optInt("success")) {

                    case 1:
                        // prescription succesfully generated
                        //Toast.makeText(WritePrescription.this, "Prescription Sent!", Toast.LENGTH_SHORT).show();

                        presPDF = response.optString("presurl");

                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(presPDF));
                        startActivity(browserIntent);

                        //showPreview(presPDF);
                        //finish();
                        ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
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

    private void addMedicineFields() {

        if (ll1.getVisibility() == View.GONE) {
            ll1.setVisibility(View.VISIBLE);
        } else if (ll2.getVisibility() == View.GONE) {
            ll2.setVisibility(View.VISIBLE);
        } else if (ll3.getVisibility() == View.GONE) {
            ll3.setVisibility(View.VISIBLE);
        } else if (ll4.getVisibility() == View.GONE) {
            ll4.setVisibility(View.VISIBLE);
        } else if (ll5.getVisibility() == View.GONE) {
            ll5.setVisibility(View.VISIBLE);
        }

    }


    private void drugJson() {

        int count = 0;

        if (ll1.getVisibility() == View.VISIBLE) {
            count++;
        }
        if (ll2.getVisibility() == View.VISIBLE) {
            count++;
        }
        if (ll3.getVisibility() == View.VISIBLE) {
            count++;
        }
        if (ll4.getVisibility() == View.VISIBLE) {
            count++;
        }
        if (ll5.getVisibility() == View.VISIBLE) {
            count++;
        }

        Log.d("no_of_drugs", String.valueOf(count));


        JSONObject drug1 = new JSONObject();
        try {
            drug1.put("drug", drugName1.getText().toString());
            drug1.put("quantity", drugQuantity1.getText().toString());
            drug1.put("dosage", drugDosage1.getText().toString());
            drug1.put("duration", drugDuration1.getText().toString());


        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject drug2 = new JSONObject();
        try {
            drug2.put("drug", drugName2.getText().toString());
            drug2.put("quantity", drugQuantity2.getText().toString());
            drug2.put("dosage", drugDosage2.getText().toString());
            drug2.put("duration", drugDuration2.getText().toString());

        } catch (JSONException e) {

            e.printStackTrace();
        }

        JSONObject drug3 = new JSONObject();
        try {
            drug3.put("drug", drugName3.getText().toString());
            drug3.put("quantity", drugQuantity3.getText().toString());
            drug3.put("dosage", drugDosage3.getText().toString());
            drug3.put("duration", drugDuration3.getText().toString());

        } catch (JSONException e) {

            e.printStackTrace();
        }
        JSONObject drug4 = new JSONObject();
        try {
            drug4.put("drug", drugName4.getText().toString());
            drug4.put("quantity", drugQuantity4.getText().toString());
            drug4.put("dosage", drugDosage4.getText().toString());
            drug4.put("duration", drugDuration4.getText().toString());

        } catch (JSONException e) {

            e.printStackTrace();
        }
        JSONObject drug5 = new JSONObject();
        try {
            drug5.put("drug", drugName5.getText().toString());
            drug5.put("quantity", drugQuantity5.getText().toString());
            drug5.put("dosage", drugDosage5.getText().toString());
            drug5.put("duration", drugDuration5.getText().toString());

        } catch (JSONException e) {

            e.printStackTrace();
        }

        JSONArray jsonArray = new JSONArray();

        jsonArray.put(drug1);
        jsonArray.put(drug2);
        jsonArray.put(drug3);
        jsonArray.put(drug4);
        jsonArray.put(drug5);

        JSONObject drugsObj = new JSONObject();
        try {
            drugsObj.put("drugs", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        String jsonStr = drugsObj.toString();

        System.out.println("jsonString: " + jsonStr);

        // base64
        byte[] data = new byte[0];
        try {
            data = jsonStr.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        base64Medicine = Base64.encodeToString(data, Base64.DEFAULT);

        Log.d("base64_drugsJson", base64Medicine);


        try {
            encodedMedicine = URLEncoder.encode(base64Medicine, "UTF-8");
            Log.d("urlencodedjson", encodedMedicine);


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


    }


    private boolean validate(EditText[] fields) {
        for (int i = 0; i < fields.length; i++) {
            EditText currentField = fields[i];
            if ((currentField.getText().toString().length() <= 0) && (currentField.getVisibility() == View.VISIBLE)) {
                currentField.setError("Missing Fields");
                return false;
            }
        }
        return true;
    }

    private void callService(String type) {

        drugJson();

        boolean fieldsOk = validate(new EditText[]{drugDosage1, drugDuration1, drugQuantity2, drugDosage2, drugDuration2,
                drugQuantity3, drugDosage3, drugDuration3, drugQuantity4, drugDosage4, drugDuration4, drugQuantity5, drugDosage5, drugDuration5
                , drugQuantity1, symptoms, drugName1, drugName2, drugName3, drugName4, drugName5});
        if (!fieldsOk) {

            if (type.equals("send")) {
                send();
            } else if (type.equals("preview")) {
                preview();
            }
        } else Toast.makeText(this, "Fields Not Filled", Toast.LENGTH_SHORT).show();
    }


    private void send() {
        ProgressBarUtils.animateView(progressOverlay, View.VISIBLE, 0.4f, 200);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("type", "writeprescription");
        params.put("id", getSharedPreferences(MY_PREFS_COMMON_PARAMS_DOCTOR, MODE_PRIVATE).getInt("doctor_id", 0));//
        params.put("patient_id", patient_id);
        params.put("apt_id", apt_id);
        params.put("apt_type", apt_type);
        params.put("presdate", "2017-05-14"); // new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance());
        params.put("symptomps", symptoms.getText().toString());
        params.put("medicine", base64Medicine);

        params.put("device", deviceOs);
        params.put("version", getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("AppVersion", null));
        params.put("ipaddress", new Splash_Screen().getLocalIpAddress());
        params.put("udid", getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("UDID", null));
        params.put("sha", sha);
        client.addHeader("Content-Type", "application/x-www-form-urlencoded");
        client.post(URL_DOCTOR_OPERATION, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                Log.d("prescriptionJson", response.toString());
                switch (response.optInt("success")) {

                    case 1:
                        // prescription succesfully generated
                        Toast.makeText(WritePrescription.this, "Prescription Sent!", Toast.LENGTH_SHORT).show();

                       /* presPDF = response.optString("presurl");

                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(presPDF));
                        startActivity(browserIntent);
*/

                        finish();
                        ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
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

    private void showPreview(String url) {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setTitle(getApplicationContext().getString(R.string.app_name));
        pDialog.setMessage("Loading...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        //WebView webView = (WebView) findViewById(R.id.previewPrescriptionDoctor);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                pDialog.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                pDialog.dismiss();
            }
        });
        String pdf = "http://www.adobe.com/devnet/acrobat/pdfs/pdf_open_parameters.pdf";
        webView.loadUrl("http://drive.google.com/viewerng/viewer?embedded=true&url=" + url);
    }
}
