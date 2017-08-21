package com.example.mydoctor.mydoctor.Navigation;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mydoctor.mydoctor.AdapterHelpers.PrescriptionAdapter;
import com.example.mydoctor.mydoctor.AdapterHelpers.PrescriptionsListObject;
import com.example.mydoctor.mydoctor.Login.Splash_Screen;
import com.example.mydoctor.mydoctor.R;
import com.example.mydoctor.mydoctor.Utils.ProgressBarUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import static android.content.Context.MODE_PRIVATE;
import static com.example.mydoctor.mydoctor.Login.CommonParams.MY_PREFS_COMMON_PARAMS;
import static com.example.mydoctor.mydoctor.Login.CommonParams.MY_PREFS_COMMON_PARAMS_PATIENT;
import static com.example.mydoctor.mydoctor.Login.CommonParams.URL_PATIENT_OPERATION;
import static com.example.mydoctor.mydoctor.Login.CommonParams.deviceOs;
import static com.example.mydoctor.mydoctor.Login.CommonParams.sha;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyPrescriptions extends Fragment {


    private List<PrescriptionsListObject> prescriptionsList = new ArrayList<>();
    private RecyclerView recyclerView;
    private PrescriptionAdapter mAdapter;
    private View progressOverlay;
    private View mView;
    private TextView noResult;
    RecyclerView.LayoutManager mLayoutManager;


    public MyPrescriptions() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_prescriptions, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mView = view.findViewById(R.id.lLmypresc);
        noResult = (TextView) view.findViewById(R.id.myPresc_noresult);
        progressOverlay = view.findViewById(R.id.progress_overlay);
        recyclerView = (RecyclerView) view.findViewById(R.id.myPrescriptions_patientRV);
        mAdapter = new PrescriptionAdapter(prescriptionsList);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);


        /*recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                //getFragmentManager().beginTransaction().replace(R.id.patient_fragment_container, new PrescriptionFullView()).addToBackStack(null).commit();

                PrescriptionsListObject prescriptionObj = mAdapter.getItem(position);
                String prescription = prescriptionObj.getPrescription();
                if (prescription.equals("")){

                    Toast.makeText(getActivity(), "No Prescription Found!", Toast.LENGTH_SHORT).show();


                }else {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(prescription));
                    startActivity(browserIntent);
                }
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
*/
        getData();
    }

    private void getData() {
        ProgressBarUtils.animateView(progressOverlay, View.VISIBLE, 0.4f, 200);
        noResult.setVisibility(View.GONE);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("type", "viewallappointments");
        //params.put("interval", interval);
        params.put("id", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS_PATIENT, MODE_PRIVATE).getInt("patient_id", 0));
        params.put("device", deviceOs);
        params.put("interval", "all");// sender username //
        params.put("version", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("AppVersion", null));
        params.put("ipaddress", new Splash_Screen().getLocalIpAddress());
        params.put("udid", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("UDID", null));
        params.put("sha", sha);
        client.post(URL_PATIENT_OPERATION, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                Log.d("listCompletedApmntsJson", response.toString());
                JSONArray jArray = response.optJSONArray("appointments");
                JSONObject jObject = new JSONObject();
                String aptId, aptType, dateTime, firstName, patient_id, lastName, age, city, gender, photoUrl, patientId, patUsername, status, prescription, prescriptionDoneStatus = null;

                switch (response.optInt("success")) {

                    case 1:

                        if (response.optJSONArray("appointments") != null) {
                            int arrayLength = jArray.length();
                            Log.d("no_of_requests", String.valueOf(arrayLength));
                            clearRecyclerView();

                            for (int i = 0; i < arrayLength; i++) {
                                try {
                                    jObject = jArray.getJSONObject(i);
                                    aptId = jObject.optString("apt_id");
                                    aptType = jObject.optString("apt_type");
                                    dateTime = jObject.optString("apt_datetime");
                                    firstName = jObject.optString("doctor_name");
                                    lastName = jObject.optString("last_name");
                                    age = jObject.optString("age");
                                    city = jObject.optString("city");
                                    gender = jObject.optString("gender");
                                    patient_id = jObject.optString("patient_id");
                                    prescription = jObject.optString("prescription");
                                    /*if (prescription.equals("") || prescription==null){
                                        prescriptionDoneStatus = "no";
                                    }*/
                                    photoUrl = jObject.optString("photo");
                                    patientId = jObject.optString("patient_id");
                                    patUsername = jObject.optString("username");
                                    status = jObject.optString("status");

                                    //
                                    PrescriptionsListObject appointmentsInfo = new PrescriptionsListObject(firstName, aptId, aptType, dateTime, photoUrl, prescription, patient_id);
                                    prescriptionsList.add(appointmentsInfo);
                                    mAdapter.notifyDataSetChanged();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }
                        }
                        ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
                        break;
                    case 2:
                        /*Snackbar snackbar = Snackbar.make(mView, "No Completed Appointments!", Snackbar.LENGTH_LONG);

                        snackbar.show();*/
                        noResult.setVisibility(View.VISIBLE);
                        // Changing message text color
                        //snackbar.setActionTextColor(Color.RED);
                        ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
            }
        });
    }

    // method to clear the recycler view
    public void clearRecyclerView() {
        int size = this.prescriptionsList.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.prescriptionsList.remove(0);
            }


        }
    }
}
