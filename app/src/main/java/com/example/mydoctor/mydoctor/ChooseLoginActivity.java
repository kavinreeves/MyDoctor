package com.example.mydoctor.mydoctor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import com.example.mydoctor.mydoctor.Login.Login;

public class ChooseLoginActivity extends AppCompatActivity {

    ImageButton doctor, patient, pharmacist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_choose_login);


        patient = (ImageButton) findViewById(R.id.image_patient);
        doctor = (ImageButton) findViewById(R.id.image_doctor);

        patient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), Login.class);
                intent.putExtra("type", "patient_login");
                startActivity(intent);

                //startActivity(new Intent(getApplicationContext(), PhotoUploadTest.class));

            }
        });

        doctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                intent.putExtra("type", "doctor_login");
                startActivity(intent);
            }
        });
    }


}
