package com.example.mydoctor.mydoctor.DoctorNavigation;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mydoctor.mydoctor.AdapterHelpers.DownloadImageTask;
import com.example.mydoctor.mydoctor.Login.Splash_Screen;
import com.example.mydoctor.mydoctor.R;
import com.example.mydoctor.mydoctor.Utils.ProgressBarUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;
import static com.example.mydoctor.mydoctor.Login.CommonParams.MY_PREFS_COMMON_PARAMS;
import static com.example.mydoctor.mydoctor.Login.CommonParams.URL_PATIENT_PROFILE;
import static com.example.mydoctor.mydoctor.Login.CommonParams.deviceOs;
import static com.example.mydoctor.mydoctor.Login.CommonParams.sha;


/**
 * A simple {@link Fragment} subclass.
 */
public class PatientProfileView extends Fragment {


    View progressOverlay;
    TextView name, age, sex, city, postcode, allergy, illness, treatments;
    String patient_id;
    CircleImageView profilePic;


    public PatientProfileView() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        try {
            patient_id = getArguments().getString("patient_id");
        } catch (Exception e) {
            e.printStackTrace();
        }


        return inflater.inflate(R.layout.fragment_patient_profile_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        progressOverlay = view.findViewById(R.id.progress_overlay);
        profilePic = (CircleImageView) view.findViewById(R.id.patImagePatProfileView);
        name = (TextView) view.findViewById(R.id.name_patProfileView);
        age = (TextView) view.findViewById(R.id.age_patProfileView);
        sex = (TextView) view.findViewById(R.id.sex_patProfileView);
        city = (TextView) view.findViewById(R.id.city_patProfileView);
        postcode = (TextView) view.findViewById(R.id.postcode_patProfileView);
        allergy = (TextView) view.findViewById(R.id.allergies_patView);
        illness = (TextView) view.findViewById(R.id.illness_patView);
        treatments = (TextView) view.findViewById(R.id.treatment_patView);


        showProfile();


    }

    private void showProfile() {

        ProgressBarUtils.animateView(progressOverlay, View.VISIBLE, 0.4f, 200);

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("type", "myprofile");
        params.put("id", patient_id);
        params.put("device", deviceOs);
        params.put("version", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("AppVersion", null));
        params.put("ipaddress", new Splash_Screen().getLocalIpAddress());
        params.put("udid", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("UDID", null));
        params.put("sha", sha);
        client.post(URL_PATIENT_PROFILE, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                Log.d("showProfilePat", response.toString());
                switch (response.optInt("success")) {

                    case 1:

                        new DownloadImageTask(profilePic).execute(response.optString("photo"));

                        name.setText("Name: "+response.optString("first_name")+" "+response.optString("last_name"));
                        age.setText("Age: "+response.optString("age"));
                        sex.setText("Gender: "+response.optString("gender"));
                        city.setText("City: "+response.optString("city"));
                        postcode.setText("Postcode: "+response.optString("postalcode"));

                        allergy.setText("Allergic To: "+response.optString("alergicto"));
                        treatments.setText("Treatments: "+response.optString("treatments"));
                        illness.setText("Known Illness: "+response.optString("knownillness"));
                        //bloodgroupET.setText(response.optString("bloodgroup"));

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

}
