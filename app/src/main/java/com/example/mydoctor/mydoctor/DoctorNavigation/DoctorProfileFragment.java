package com.example.mydoctor.mydoctor.DoctorNavigation;


import android.Manifest;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.mydoctor.mydoctor.AdapterHelpers.DownloadImageTask;
import com.example.mydoctor.mydoctor.Login.Splash_Screen;
import com.example.mydoctor.mydoctor.R;
import com.example.mydoctor.mydoctor.Utils.ProgressBarUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static com.example.mydoctor.mydoctor.Login.CommonParams.MY_PREFS_COMMON_PARAMS;
import static com.example.mydoctor.mydoctor.Login.CommonParams.MY_PREFS_COMMON_PARAMS_DOCTOR;
import static com.example.mydoctor.mydoctor.Login.CommonParams.URL_DOCTOR_PROFILE;
import static com.example.mydoctor.mydoctor.Login.CommonParams.URL_DOCTOR_STEP;
import static com.example.mydoctor.mydoctor.Login.CommonParams.deviceOs;
import static com.example.mydoctor.mydoctor.Login.CommonParams.sha;

/**
 * A simple {@link Fragment} subclass.
 */
public class DoctorProfileFragment extends Fragment implements View.OnClickListener {

    ImageButton editProfilePic, workAddressIB, personalIB, contactIB, professIB, consultIB, phoneIB;
    CircleImageView imageView;
    private Uri fileUri;
    private EditText fnameET, lNameET, ageET, genderET, phoneET, specET, expET, hospitalET,
            qualET, regIdET, amFromET, amToET, pmFromET, pmToET, directFeeET, chatFeeET, videoFeeET,
            hospitalL1ET, hospitalL2T, hospitalCityET, hospitalPostCodeET, line1ET, line2ET, postcodeET, cityET;
    private String fname, lName, age, gender, phone, allergy, treatment, illness, description, line1, line2, postcode, city;

    private boolean perFlag, contactFlag, profFlag, consultFlag, workAddressFlag, photoFlag;
    Button saveChanges;

    private View progressOverlay;

    private Drawable oldDrawable;
    // to avoid NPE for widgets
    protected View mView;


    public DoctorProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_doctor_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imageView = (CircleImageView) view.findViewById(R.id.doc_viewProfile_image);
        editProfilePic = (ImageButton) view.findViewById(R.id.doc_profilePic_edit);
        //saveProfilePic = (ImageButton) view.findViewById(R.id.user_profilePic_save);
        editProfilePic.setOnClickListener(this);
        //saveProfilePic.setOnClickListener(this);
        personalIB = (ImageButton) view.findViewById(R.id.doc_edit_perInfoBtn);
        phoneIB = (ImageButton) view.findViewById(R.id.doc_edit_perPhoneBtn);
        contactIB = (ImageButton) view.findViewById(R.id.doc_edit_contactInfoBtn);
        professIB = (ImageButton) view.findViewById(R.id.doc_edit_ProfInfoBtn);
        consultIB = (ImageButton) view.findViewById(R.id.doc_edit_consultInfoBtn);
        workAddressIB = (ImageButton) view.findViewById(R.id.doc_edit_workInfoBtn);

        saveChanges = (Button) view.findViewById(R.id.doc_edit_profileSaveAllBtn);
        progressOverlay = view.findViewById(R.id.progress_overlay);

        personalIB.setOnClickListener(this);
        phoneIB.setOnClickListener(this);
        professIB.setOnClickListener(this);
        consultIB.setOnClickListener(this);
        workAddressIB.setOnClickListener(this);
        contactIB.setOnClickListener(this);
        saveChanges.setOnClickListener(this);

        fnameET = (EditText) view.findViewById(R.id.doc_fName_edit);
        lNameET = (EditText) view.findViewById(R.id.doc_lName_edit);
        ageET = (EditText) view.findViewById(R.id.doc_age_edit);
        genderET = (EditText) view.findViewById(R.id.doc_gender_edit);
        phoneET = (EditText) view.findViewById(R.id.doc_phone_edit);

        specET = (EditText) view.findViewById(R.id.doc_speciali_edit);
        expET = (EditText) view.findViewById(R.id.doc_exp1_edit);
        qualET = (EditText) view.findViewById(R.id.doc_qual1_edit);
        regIdET = (EditText) view.findViewById(R.id.doc_govid_edit);

        postcodeET = (EditText) view.findViewById(R.id.doc_post_edit);
        cityET = (EditText) view.findViewById(R.id.doc_city_edit);
        line1ET = (EditText) view.findViewById(R.id.doc_line1Per_edit);
        line2ET = (EditText) view.findViewById(R.id.doc_line2Per_edit);

        amFromET = (EditText) view.findViewById(R.id.doc_amFromConsult);
        amToET = (EditText) view.findViewById(R.id.doc_amToConsult);
        pmFromET = (EditText) view.findViewById(R.id.doc_pmFromConsult);
        pmToET = (EditText) view.findViewById(R.id.doc_pmToConsult);

        amFromET.setOnClickListener(this);
        amFromET.setInputType(InputType.TYPE_NULL);
        amToET.setOnClickListener(this);
        amToET.setInputType(InputType.TYPE_NULL);
        pmFromET.setOnClickListener(this);
        pmFromET.setInputType(InputType.TYPE_NULL);
        pmToET.setOnClickListener(this);
        pmToET.setInputType(InputType.TYPE_NULL);


        directFeeET = (EditText) view.findViewById(R.id.doc_direct_fee);
        chatFeeET = (EditText) view.findViewById(R.id.doc_chat_fee);
        videoFeeET = (EditText) view.findViewById(R.id.doc_video_fee);

        hospitalET = (EditText) view.findViewById(R.id.doc_hospital_edit);
        hospitalL1ET = (EditText) view.findViewById(R.id.doc_line1_edit);
        hospitalL2T = (EditText) view.findViewById(R.id.doc_line2_edit);
        hospitalCityET = (EditText) view.findViewById(R.id.doc_line3_edit);
        hospitalPostCodeET = (EditText) view.findViewById(R.id.doc_Hospostcode_edit);

        // all editext fields


        // populate all fields
        showProfile();


        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            //editProfilePic.setEnabled(false);
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }

        if (Build.VERSION.SDK_INT >= 24) {
            try {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //
        oldDrawable = imageView.getDrawable();


    }

    public void setTime(final EditText editText) {
        int hour = 0;
        int minute = 0;
       /* editText.setText("00:00:00");*/
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
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

            case R.id.doc_profilePic_edit:
               /* AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(getActivity());
                myAlertDialog.setTitle("Image Upload Options");
                myAlertDialog.setMessage("Select Picture From");

                myAlertDialog.setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            //getImageBtn.setEnabled(false);
                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                            //imageGallery();
                        }

                    }
                });

                myAlertDialog.setNegativeButton("Take Photo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                        takePicture();
                    }
                });
                myAlertDialog.show();*/

                takePicture();

                break;

            case R.id.doc_edit_profileSaveAllBtn:

                if (imageView.getDrawable() == null) {
                    Toast.makeText(getActivity(), "Plz select a photo to upload!", Toast.LENGTH_SHORT).show();
                } else if (imageView.getDrawable() != oldDrawable && photoFlag) {
                    uploadFiles();
                }

                //uploadFiles();
                saveChanges();
                break;
            case R.id.doc_edit_perPhoneBtn:

                //phoneET.setEnabled(true);

                // no flag
                break;
            case R.id.doc_edit_contactInfoBtn:
                postcodeET.setEnabled(true);
                cityET.setEnabled(true);
                line1ET.setEnabled(true);
                line2ET.setEnabled(true);

                contactFlag = true;

                break;
            case R.id.doc_edit_consultInfoBtn:

                amFromET.setEnabled(true);
                amToET.setEnabled(true);
                pmFromET.setEnabled(true);
                pmToET.setEnabled(true);
                directFeeET.setEnabled(true);
                chatFeeET.setEnabled(true);
                videoFeeET.setEnabled(true);

                consultFlag = true;

                break;
            case R.id.doc_edit_ProfInfoBtn:
                specET.setEnabled(true);
                expET.setEnabled(true);
                qualET.setEnabled(true);
                //regIdET.setEnabled(false); // not editable

                profFlag = true;


                break;
            case R.id.doc_edit_workInfoBtn:

                hospitalET.setEnabled(true);
                hospitalL1ET.setEnabled(true);
                hospitalL2T.setEnabled(true);
                hospitalCityET.setEnabled(true);
                hospitalPostCodeET.setEnabled(true);

                workAddressFlag = true;
                break;
            case R.id.doc_edit_perInfoBtn:
                fnameET.setEnabled(true);
                lNameET.setEnabled(true);
                ageET.setEnabled(true);
                genderET.setEnabled(true);

                perFlag = true;


                break;

            case (R.id.doc_amFromConsult):
                setTime(amFromET);
                break;
            case (R.id.doc_amToConsult):
                setTime(amToET);
                break;
            case (R.id.doc_pmFromConsult):
                setTime(pmFromET);
                break;
            case (R.id.doc_pmToConsult):
                setTime(pmToET);
                break;

        }
    }

    private void uploadFiles() {
        final ProgressDialog progress = new ProgressDialog(getActivity());
        progress.setMessage("Uploading Photo Plz wait...");
        progress.setIndeterminate(false);
        progress.setCancelable(false);
        progress.show();

        AsyncHttpClient client = new AsyncHttpClient();
        //client.addHeader("Content-Type", "application/x-www-form-urlencoded");
        //client.setBasicAuth("jk", "jk");
        RequestParams params = new RequestParams();
        params.put("type", "upload_photo");
        params.put("id", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS_DOCTOR, MODE_PRIVATE).getInt("doctor_id", 0));

        try {
            if (fileUri != null) {
                params.put("image", new File(fileUri.getPath()));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        params.put("device", deviceOs);
        params.put("version", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("AppVersion", null));
        params.put("ipaddress", new Splash_Screen().getLocalIpAddress());
        params.put("udid", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("UDID", null));
        params.put("sha", sha);
        // http://wenso-sms.co.uk/doctor_step.php
        client.post(URL_DOCTOR_STEP, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String JsonResponse = String.valueOf(response);
                Log.d("photoUploadJson", JsonResponse);
                progress.dismiss();

                if (response.optInt("success") == 1) {
                    Toast.makeText(getActivity(), "Photo Upload Successful!", Toast.LENGTH_SHORT).show();
                    getActivity().getSupportFragmentManager().popBackStack();
                } else
                    Toast.makeText(getActivity(), "Photo Upload Unsuccessful!", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                String JsonResponse = String.valueOf(errorResponse);
                Log.d("photoUploadJson", JsonResponse);
                progress.dismiss();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                editProfilePic.setEnabled(true);
            }
        }
    }

    public void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = Uri.fromFile(getOutputMediaFile());
        Log.d("fileUriDoc", String.valueOf(fileUri));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        startActivityForResult(intent, 100);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                imageView.setImageURI(fileUri);

                photoFlag = true;

                Log.d("pathUriDoc", fileUri.getPath());
            }
        }
    }

    private static File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "CameraDemo");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg");
    }

    private void showProfile() {
        ProgressBarUtils.animateView(progressOverlay, View.VISIBLE, 0.4f, 200);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("type", "myprofile");
        params.put("id", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS_DOCTOR, MODE_PRIVATE).getInt("doctor_id", 0));
        params.put("device", deviceOs);
        params.put("version", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("AppVersion", null));
        params.put("ipaddress", new Splash_Screen().getLocalIpAddress());
        params.put("udid", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("UDID", null));
        params.put("sha", sha);
        client.post(URL_DOCTOR_PROFILE, params, new JsonHttpResponseHandler() {



            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                Log.d("showFullProfileDoc", response.toString());
                switch (response.optInt("success")) {

                    case 1:
                        // populate all details into the edittexts
                        fnameET.setText(response.optString("first_name"));
                        lNameET.setText(response.optString("last_name"));
                        ageET.setText(response.optString("age"));
                        genderET.setText(response.optString("gender"));

                        specET.setText(response.optString("specialization"));
                        expET.setText(response.optString("experience"));
                        qualET.setText(response.optString("qualification"));
                        regIdET.setText(response.optString("doctorgovid"));
                        postcodeET.setText(response.optString("personalPostalCode"));
                        cityET.setText(response.optString("personalCity"));
                        line1ET.setText(response.optString("personalAddLine1"));
                        line2ET.setText(response.optString("personalAddLine2"));

                        amFromET.setText(response.optString("time_from"));
                        amToET.setText(response.optString("time_to"));
                        pmFromET.setText(response.optString("time2_from"));
                        pmToET.setText(response.optString("time2_to"));
                        directFeeET.setText(response.optString("consultancy_price"));
                        chatFeeET.setText(response.optString("chat_price"));
                        videoFeeET.setText(response.optString("video_price"));

                        hospitalET.setText(response.optString("workingat"));
                        hospitalL1ET.setText(response.optString("workingAddLine1"));
                        hospitalL2T.setText(response.optString("workingAddLine2"));
                        hospitalCityET.setText(response.optString("workingCity"));
                        hospitalPostCodeET.setText(response.optString("workingPostalCode"));

                        phoneET.setText(response.optString("phone"));
                        new DownloadImageTask(imageView).execute(response.optString("photo"));

                        oldDrawable = imageView.getDrawable(); // to check for image change
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

    private void saveChanges() {

        if (perFlag) {
            // save personal details
            //Toast.makeText(getActivity(), "Personal edited!", Toast.LENGTH_SHORT).show();
            if (isEmpty(fnameET) | isEmpty(lNameET) | isEmpty(ageET) | isEmpty(genderET)) {
                Toast.makeText(getActivity(), "Enter All Details!", Toast.LENGTH_SHORT).show();
            } else {
                savePersonal();
            }

        }
        if (contactFlag) {
            //Toast.makeText(getActivity(), "contact edited!", Toast.LENGTH_SHORT).show();
            if (isEmpty(cityET) | isEmpty(postcodeET)) {

                Toast.makeText(getActivity(), "Enter All Details!", Toast.LENGTH_SHORT).show();
            } else savePersonalContact();
        }
        if (profFlag) {
            //Toast.makeText(getActivity(), "prof edited!", Toast.LENGTH_SHORT).show();
            if (isEmpty(specET) | isEmpty(expET) | isEmpty(qualET)) {

                Toast.makeText(getActivity(), "Enter All Details!", Toast.LENGTH_SHORT).show();
            } else saveProfessionalInfo();
        }
        if (consultFlag) {
            //Toast.makeText(getActivity(), "consult edited!", Toast.LENGTH_SHORT).show();
            if (isEmpty(amFromET) | isEmpty(amToET) | isEmpty(pmFromET) | isEmpty(pmToET) | isEmpty(directFeeET) | isEmpty(chatFeeET) | isEmpty(videoFeeET)) {

                Toast.makeText(getActivity(), "Enter All Details!", Toast.LENGTH_SHORT).show();
            } else saveConsultationInfo();
        }
        if (workAddressFlag) {
            //Toast.makeText(getActivity(), "work address edited!", Toast.LENGTH_SHORT).show();
            if (isEmpty(hospitalCityET) | isEmpty(hospitalPostCodeET)) {

                Toast.makeText(getActivity(), "Enter All Details!", Toast.LENGTH_SHORT).show();
            } else saveWorkInfo();
        }

    }

    private void savePersonal() {
        ProgressBarUtils.animateView(progressOverlay, View.VISIBLE, 0.4f, 200);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("type", "editpersonalinfo");
        params.put("id", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS_DOCTOR, MODE_PRIVATE).getInt("doctor_id", 0));
        params.put("first_name", fnameET.getText().toString());
        params.put("last_name", lNameET.getText().toString());
        params.put("age", ageET.getText().toString());
        params.put("gender", genderET.getText().toString());
        params.put("device", deviceOs);
        params.put("version", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("AppVersion", null));
        params.put("ipaddress", new Splash_Screen().getLocalIpAddress());
        params.put("udid", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("UDID", null));
        params.put("sha", sha);
        client.post(URL_DOCTOR_PROFILE, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d("editPersDocJson", response.toString());
                if (response.optInt("success") == 1) {
                    Toast.makeText(getActivity(), "Succesfully edited!", Toast.LENGTH_SHORT).show();
                    getActivity().getSupportFragmentManager().popBackStack();
                }

                ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
            }
        });

    }

    private void savePersonalContact() {
        ProgressBarUtils.animateView(progressOverlay, View.VISIBLE, 0.4f, 200);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("type", "editaddressinfo");
        params.put("id", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS_DOCTOR, MODE_PRIVATE).getInt("doctor_id", 0));
        params.put("addressline1", line1ET.getText().toString());
        params.put("addressline2", line2ET.getText().toString());
        params.put("city", cityET.getText().toString());
        params.put("postalcode", postcodeET.getText().toString());
        params.put("device", deviceOs);
        params.put("version", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("AppVersion", null));
        params.put("ipaddress", new Splash_Screen().getLocalIpAddress());
        params.put("udid", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("UDID", null));
        params.put("sha", sha);
        client.post(URL_DOCTOR_PROFILE, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d("editPersContactDocJson", response.toString());
                if (response.optInt("success") == 1) {
                    Toast.makeText(getActivity(), "Address Succesfully edited!", Toast.LENGTH_SHORT).show();
                    getActivity().getSupportFragmentManager().popBackStack();
                }
                ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
            }
        });

    }

    private void saveProfessionalInfo() {
        ProgressBarUtils.animateView(progressOverlay, View.VISIBLE, 0.4f, 200);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("type", "editprofessionalinfo");
        params.put("id", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS_DOCTOR, MODE_PRIVATE).getInt("doctor_id", 0));
        params.put("specialization", specET.getText().toString());
        params.put("experience", expET.getText().toString());
        params.put("qualification", qualET.getText().toString());
        params.put("device", deviceOs);
        params.put("version", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("AppVersion", null));
        params.put("ipaddress", new Splash_Screen().getLocalIpAddress());
        params.put("udid", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("UDID", null));
        params.put("sha", sha);
        client.post(URL_DOCTOR_PROFILE, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d("editProfesDocJson", response.toString());
                if (response.optInt("success") == 1) {
                    Toast.makeText(getActivity(), "Profess Succesfully edited!", Toast.LENGTH_SHORT).show();
                    getActivity().getSupportFragmentManager().popBackStack();
                }
                ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
            }
        });

    }

    private void saveConsultationInfo() {
        ProgressBarUtils.animateView(progressOverlay, View.VISIBLE, 0.4f, 200);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("type", "editappointmentsinfo");
        params.put("id", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS_DOCTOR, MODE_PRIVATE).getInt("doctor_id", 0));
        params.put("time_from", amFromET.getText().toString());
        params.put("time_to", amToET.getText().toString());
        params.put("time2_from", pmFromET.getText().toString());
        params.put("time2_to", pmToET.getText().toString());
        params.put("timeinterval", "30");
        params.put("oc_price", chatFeeET.getText().toString());
        params.put("dc_price", directFeeET.getText().toString());
        params.put("vc_price", videoFeeET.getText().toString());
        params.put("device", deviceOs);
        params.put("version", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("AppVersion", null));
        params.put("ipaddress", new Splash_Screen().getLocalIpAddress());
        params.put("udid", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("UDID", null));
        params.put("sha", sha);
        client.post(URL_DOCTOR_PROFILE, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d("editConsultDocJson", response.toString());
                if (response.optInt("success") == 1) {
                    Toast.makeText(getActivity(), "Consult Succesfully edited!", Toast.LENGTH_SHORT).show();
                    getActivity().getSupportFragmentManager().popBackStack();
                }
                ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
            }
        });

    }

    private void saveWorkInfo() {
        ProgressBarUtils.animateView(progressOverlay, View.VISIBLE, 0.4f, 200);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("type", "editworkingddressinfo");
        params.put("id", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS_DOCTOR, MODE_PRIVATE).getInt("doctor_id", 0));
        params.put("workingat", hospitalET.getText().toString());
        params.put("addressline1", hospitalL1ET.getText().toString());
        params.put("addressline2", hospitalL2T.getText().toString());
        params.put("city", hospitalCityET.getText().toString());
        params.put("postalcode", hospitalPostCodeET.getText().toString());
        params.put("device", deviceOs);
        params.put("version", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("AppVersion", null));
        params.put("ipaddress", new Splash_Screen().getLocalIpAddress());
        params.put("udid", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("UDID", null));
        params.put("sha", sha);
        client.post(URL_DOCTOR_PROFILE, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d("editWorkDocJson", response.toString());
                if (response.optInt("success") == 1) {
                    Toast.makeText(getActivity(), "Succesfully edited!", Toast.LENGTH_SHORT).show();
                    getActivity().getSupportFragmentManager().popBackStack();
                }
                ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
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

    public boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() == 0) {
            etText.requestFocus();
            return true;
        } else return false;
    }

}
