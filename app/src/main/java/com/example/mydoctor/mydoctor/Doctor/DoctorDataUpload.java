package com.example.mydoctor.mydoctor.Doctor;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mydoctor.mydoctor.ChooseLoginActivity;
import com.example.mydoctor.mydoctor.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

import static com.example.mydoctor.mydoctor.Login.CommonParams.sha;

public class DoctorDataUpload extends AppCompatActivity {

    public static final int IMAGE_GALLERY_REQUEST_CODE = 30;
    ImageView imageView;
    Button selectImage, next;
    File pictureDirectory;
    Uri data, imageUri;
    String picturePath;

    private Uri file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_data_upload);

        imageView = (ImageView) findViewById(R.id.imageViewDocProfilePic);
        selectImage = (Button) findViewById(R.id.doc_profile_pic);
        next = (Button) findViewById(R.id.button_register_next);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            selectImage.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }

        if (Build.VERSION.SDK_INT >= 24) {
            try {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(DoctorDataUpload.this);
                myAlertDialog.setTitle("Image Upload Options");
                myAlertDialog.setMessage("Select Picture From");

                myAlertDialog.setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (ContextCompat.checkSelfPermission(DoctorDataUpload.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            //getImageBtn.setEnabled(false);
                            ActivityCompat.requestPermissions(DoctorDataUpload.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
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
                myAlertDialog.show();

            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (imageView.getDrawable() == null) {

                    Toast.makeText(DoctorDataUpload.this, "Plz select a photo to upload!", Toast.LENGTH_SHORT).show();
                } else uploadFiles();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                selectImage.setEnabled(true);
            }
        }
    }

    public void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        file = Uri.fromFile(getOutputMediaFile());
        Log.d("fileUri", String.valueOf(file));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, file);

        startActivityForResult(intent, 100);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                imageView.setImageURI(file);
                Log.d("path", file.getPath());
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

    private void uploadFiles() {
        final ProgressDialog progress = new ProgressDialog(DoctorDataUpload.this);
        progress.setMessage("Uploading Plz wait...");
        progress.setIndeterminate(false);
        progress.setCancelable(false);
        progress.show();

        AsyncHttpClient client = new AsyncHttpClient();
        //client.addHeader("Content-Type", "application/x-www-form-urlencoded");
        //client.setBasicAuth("jk", "jk");
        RequestParams params = new RequestParams();
        params.put("type", "upload_photo");
        params.put("id", 1); // change the id later // TODO: 17-Mar-17  
        // File file = new File(imageUri.getPath());
        try {
            params.put("image", new File(file.getPath()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        params.put("device", "Android");
        params.put("version", "1.0");
        params.put("ipaddress", "192.168.0.1");
        params.put("udid", "10981243");
        params.put("sha", sha);
        client.post("http://wenso-sms.co.uk/doctor_step.php", params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String JsonResponse = String.valueOf(response);
                Log.d("photoUploadJson", JsonResponse);
                progress.dismiss();
                startActivity(new Intent(getApplicationContext(), ChooseLoginActivity.class));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                String JsonResponse = String.valueOf(errorResponse);
                Log.d("photoUploadJson", JsonResponse);
                progress.dismiss();
            }
        });
    }



}
