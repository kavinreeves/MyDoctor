package com.example.mydoctor.mydoctor.Navigation;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.mydoctor.mydoctor.AdapterHelpers.DownloadImageTask;
import com.example.mydoctor.mydoctor.Login.Splash_Screen;
import com.example.mydoctor.mydoctor.R;
import com.example.mydoctor.mydoctor.Utils.ProgressBarUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static com.example.mydoctor.mydoctor.Login.CommonParams.MY_PREFS_COMMON_PARAMS;
import static com.example.mydoctor.mydoctor.Login.CommonParams.MY_PREFS_COMMON_PARAMS_PATIENT;
import static com.example.mydoctor.mydoctor.Login.CommonParams.URL_PATIENT_PROFILE;
import static com.example.mydoctor.mydoctor.Login.CommonParams.URL_PATIENT_STEP;
import static com.example.mydoctor.mydoctor.Login.CommonParams.deviceOs;
import static com.example.mydoctor.mydoctor.Login.CommonParams.sha;


public class My_Profile extends Fragment implements View.OnClickListener {


    ImageButton editProfilePic, saveProfilePic, personalIB, othersIB, healthIB, contactIB, phoneIB;
    CircleImageView imageView;
    private Uri fileUri, file;
    private EditText fnameET, lNameET, ageET, genderET, phoneET, allergyET, treatmentET, illnessET, bloodgroupET, line1ET, line2ET, postcodeET, cityET;
    private String fname, lName, age, gender, phone, allergy, treatment, illness, description, line1, line2, postcode, city;
    protected View mView;
    Button saveChanges;
    View progressOverlay;

    private Drawable oldDrawable;
    private Bitmap bitmap;

    private boolean perFlag, contactFlag, healthFlag, othersFlag, photoFlag;

    private static final int MAX_HEIGHT = 1024;
    private static final int MAX_WIDTH = 1024;

    public My_Profile() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.my__profile, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressOverlay = view.findViewById(R.id.progress_overlay);

        imageView = (CircleImageView) view.findViewById(R.id.user_viewProfile_imageView);
        editProfilePic = (ImageButton) view.findViewById(R.id.user_profilePic_edit);
        //saveProfilePic = (ImageButton) view.findViewById(R.id.user_profilePic_save);
        editProfilePic.setOnClickListener(this);
        //saveProfilePic.setOnClickListener(this);
        personalIB = (ImageButton) view.findViewById(R.id.user_edit_perInfoBtn);
        phoneIB = (ImageButton) view.findViewById(R.id.user_edit_perPhoneBtn);
        healthIB = (ImageButton) view.findViewById(R.id.user_edit_healthInfoBtn);
        contactIB = (ImageButton) view.findViewById(R.id.user_edit_contactInfoBtn);
        saveChanges = (Button) view.findViewById(R.id.user_edit_profileSaveAllBtn);

        personalIB.setOnClickListener(this);
        phoneIB.setOnClickListener(this);
        healthIB.setOnClickListener(this);
        contactIB.setOnClickListener(this);
        saveChanges.setOnClickListener(this);

        fnameET = (EditText) view.findViewById(R.id.user_fName_edit);
        lNameET = (EditText) view.findViewById(R.id.user_lName_edit);
        ageET = (EditText) view.findViewById(R.id.user_age_edit);
        genderET = (EditText) view.findViewById(R.id.user_gender_edit);
        phoneET = (EditText) view.findViewById(R.id.user_phone_edit);
        allergyET = (EditText) view.findViewById(R.id.user_allergy_edit);
        treatmentET = (EditText) view.findViewById(R.id.user_treatment_edit);
        illnessET = (EditText) view.findViewById(R.id.user_illness_edit);
        bloodgroupET = (EditText) view.findViewById(R.id.user_description_edit);
        postcodeET = (EditText) view.findViewById(R.id.user_postcode_edit);
        cityET = (EditText) view.findViewById(R.id.user_city_edit);
        line1ET = (EditText) view.findViewById(R.id.user_line1_edit);
        line2ET = (EditText) view.findViewById(R.id.user_line2_edit);




        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

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

        // populate all fields
        showProfile();

        oldDrawable = imageView.getDrawable();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.user_profilePic_edit:

                takePicture();

                break;

            case R.id.user_edit_perPhoneBtn:

                //phoneET.setEnabled(true);

                break;
            case R.id.user_edit_healthInfoBtn:
                allergyET.setEnabled(true);
                treatmentET.setEnabled(true);
                illnessET.setEnabled(true);
                bloodgroupET.setEnabled(true);

                healthFlag = true;

                break;
            case R.id.user_edit_contactInfoBtn:
                postcodeET.setEnabled(true);
                cityET.setEnabled(true);
                line1ET.setEnabled(true);
                line2ET.setEnabled(true);

                contactFlag = true;

                break;
            case R.id.user_edit_perInfoBtn:
                fnameET.setEnabled(true);
                lNameET.setEnabled(true);
                ageET.setEnabled(true);
                genderET.setEnabled(true);

                perFlag = true;

                break;
            case R.id.user_edit_profileSaveAllBtn:

                if (imageView.getDrawable() == null) {
                    Toast.makeText(getActivity(), "Plz select a photo to upload!", Toast.LENGTH_SHORT).show();
                } else if (imageView.getDrawable() != oldDrawable && photoFlag) {
                    uploadFiles();
                }

                saveChanges();
                // get all changed values and enter
                break;


        }
    }

    private void takePicture() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri= Uri.fromFile(getOutputMediaFile());
        // create a bitmap from the uri
       /* try {
            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), fileUri);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        // or
        try {
            bitmap = decodeSampledBitmap(getContext(), fileUri);
        } catch (IOException e) {
            e.printStackTrace();
        }


        Log.d("fileUri", String.valueOf(fileUri));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        startActivityForResult(intent, 100);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {

                // set the new image to the imageview
                Bitmap bitmapFinal = rotateImageIfRequired(getContext(), this.bitmap, fileUri);
                imageView.setImageURI(fileUri);
                //imageView.setImageBitmap(bitmapFinal);

                photoFlag = true;
                Log.d("path", fileUri.getPath());
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
            }else saveContactInfo();
        }

        if (healthFlag) {
            // Toast.makeText(getActivity(), "health edited!", Toast.LENGTH_SHORT).show();
            if (isEmpty(allergyET) | isEmpty(treatmentET) | isEmpty(illnessET) | isEmpty(bloodgroupET)) {

                Toast.makeText(getActivity(), "Enter All Details!", Toast.LENGTH_SHORT).show();
            }else saveHealthInfo();
        }

    }

    private void savePersonal() {

        ProgressBarUtils.animateView(progressOverlay, View.VISIBLE, 0.4f, 200);

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("type", "editpersonalinfo");
        params.put("id", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS_PATIENT, MODE_PRIVATE).getInt("patient_id", 0));
        params.put("first_name", fnameET.getText().toString());
        params.put("last_name", lNameET.getText().toString());
        params.put("age", ageET.getText().toString());
        params.put("gender", genderET.getText().toString());
        params.put("device", deviceOs);
        params.put("version", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("AppVersion", null));
        params.put("ipaddress", new Splash_Screen().getLocalIpAddress());
        params.put("udid", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("UDID", null));
        params.put("sha", sha);
        client.post(URL_PATIENT_PROFILE, params, new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d("editPersPatJson", response.toString());
                if (response.optInt("success") == 1){
                    Toast.makeText(getActivity(), "Succesfully edited!", Toast.LENGTH_SHORT).show();

                    ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
                    getActivity().getSupportFragmentManager().popBackStack();

                }else ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
            }
        });
    }
    private void saveHealthInfo() {
        ProgressBarUtils.animateView(progressOverlay, View.VISIBLE, 0.4f, 200);

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("type", "edithealthinfo");
        params.put("id", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS_PATIENT, MODE_PRIVATE).getInt("patient_id", 0));
        params.put("alergicto", allergyET.getText().toString());
        params.put("knownillness", illnessET.getText().toString());
        params.put("treatments", treatmentET.getText().toString());
        params.put("bloodgroup", bloodgroupET.getText().toString());
        params.put("device", deviceOs);
        params.put("version", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("AppVersion", null));
        params.put("ipaddress", new Splash_Screen().getLocalIpAddress());
        params.put("udid", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("UDID", null));
        params.put("sha", sha);
        client.post(URL_PATIENT_PROFILE, params, new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d("editHealthPatJson", response.toString());
                if (response.optInt("success") == 1){
                    Toast.makeText(getActivity(), "Succesfully edited!", Toast.LENGTH_SHORT).show();
                    ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
                    getActivity().getSupportFragmentManager().popBackStack();

                }else ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
            }
        });
    }
    private void saveContactInfo() {
        ProgressBarUtils.animateView(progressOverlay, View.VISIBLE, 0.4f, 200);

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("type", "editaddressinfo");
        params.put("id", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS_PATIENT, MODE_PRIVATE).getInt("patient_id", 0));
        params.put("addressline1", line1ET.getText().toString());
        params.put("addressline2", line2ET.getText().toString());
        params.put("city", cityET.getText().toString());
        params.put("postalcode", postcodeET.getText().toString());
        params.put("device", deviceOs);
        params.put("version", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("AppVersion", null));
        params.put("ipaddress", new Splash_Screen().getLocalIpAddress());
        params.put("udid", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("UDID", null));
        params.put("sha", sha);
        client.post(URL_PATIENT_PROFILE, params, new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d("editContactPatJson", response.toString());
                if (response.optInt("success") == 1){
                    Toast.makeText(getActivity(), "Succesfully edited!", Toast.LENGTH_SHORT).show();
                    ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
                    getActivity().getSupportFragmentManager().popBackStack();

                }else ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
            }
        });
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
        params.put("id", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS_PATIENT, MODE_PRIVATE).getInt("patient_id", 0));

        try {
            /*if(fileUri != null) {
                params.put("image", new File(fileUri.getPath()));
            }*/
            params.put("image", new File(fileUri.getPath()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        params.put("device", deviceOs);
        params.put("version", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("AppVersion", null));
        params.put("ipaddress", new Splash_Screen().getLocalIpAddress());
        params.put("udid", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("UDID", null));
        params.put("sha", sha);
        // http://wenso-sms.co.uk/doctor_step.php
        client.post(URL_PATIENT_STEP, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String JsonResponse = String.valueOf(response);
                Log.d("photoUploadJson", JsonResponse);
                progress.dismiss();

                if (response.optInt("success") == 1){
                    Toast.makeText(getContext(), "Profile Picture Changed!", Toast.LENGTH_SHORT).show();
                    getActivity().getSupportFragmentManager().popBackStack();
                }else Toast.makeText(getActivity(), "Try Again!", Toast.LENGTH_SHORT).show();

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



    private void showProfile() {

        ProgressBarUtils.animateView(progressOverlay, View.VISIBLE, 0.4f, 200);

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("type", "myprofile");
        params.put("id", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS_PATIENT, MODE_PRIVATE).getInt("patient_id", 0));
        params.put("device", deviceOs);
        params.put("version", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("AppVersion", null));
        params.put("ipaddress", new Splash_Screen().getLocalIpAddress());
        params.put("udid", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("UDID", null));
        params.put("sha", sha);
        client.post(URL_PATIENT_PROFILE, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                Log.d("showFullProfilePat", response.toString());
                switch (response.optInt("success")) {

                    case 1:

                        new DownloadImageTask(imageView).execute(response.optString("photo"));

                        fnameET.setText(response.optString("first_name"));
                        lNameET.setText(response.optString("last_name"));
                        ageET.setText(response.optString("age"));
                        genderET.setText(response.optString("gender"));
                        phoneET.setText(response.optString("phone"));

                        allergyET.setText(response.optString("alergicto"));
                        treatmentET.setText(response.optString("treatments"));
                        illnessET.setText(response.optString("knownillness"));
                        bloodgroupET.setText(response.optString("bloodgroup"));


                        line1ET.setText(response.optString("addressline1"));
                        line2ET.setText(response.optString("addressline2"));
                        postcodeET.setText(response.optString("postalcode"));
                        cityET.setText(response.optString("city"));


                        oldDrawable = imageView.getDrawable(); // to check for image change

                        ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);

                        break;

                    default:
                        ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
                        break;
                }
            }
        });


    }

    public boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() == 0) {
            etText.requestFocus();
            return true;
        } else return false;
    }

    // ***********************  for solving image rotation issues  ******************************

    /**
     * Rotate an image if required.
     * @param img
     * @param selectedImage
     * @return
     */
    private  Bitmap rotateImageIfRequired(Context context,Bitmap img, Uri selectedImage) {

        // Detect rotation
        int rotation = getRotation(context, selectedImage);
        if (rotation != 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotation);
            Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
            img.recycle();
            return rotatedImg;
        }
        else{
            return img;
        }
    }

    /**
     * Get the rotation of the last image added.
     * @param context
     * @param selectedImage
     * @return
     */
    private  int getRotation(Context context, Uri selectedImage) {

        int rotation = 0;
        ContentResolver content = context.getContentResolver();

        Cursor mediaCursor = content.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { "orientation", "date_added" },
                null, null, "date_added desc");

        if (mediaCursor != null && mediaCursor.getCount() != 0) {
            while(mediaCursor.moveToNext()){
                rotation = mediaCursor.getInt(0);
                break;
            }
        }
        mediaCursor.close();
        return rotation;
    }

    public  Bitmap decodeSampledBitmap(Context context, Uri selectedImage)
            throws IOException {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        InputStream imageStream = context.getContentResolver().openInputStream(selectedImage);
        BitmapFactory.decodeStream(imageStream, null, options);
        imageStream.close();

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, MAX_WIDTH, MAX_HEIGHT);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        imageStream = context.getContentResolver().openInputStream(selectedImage);
        Bitmap img = BitmapFactory.decodeStream(imageStream, null, options);

        img = rotateImageIfRequired(getContext(), img, selectedImage);
        return img;
    }
    public  int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {

        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee a final image
            // with both dimensions larger than or equal to the requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger inSampleSize).

            final float totalPixels = width * height;

            // Anything more than 2x the requested pixels we'll sample down further
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }

}
