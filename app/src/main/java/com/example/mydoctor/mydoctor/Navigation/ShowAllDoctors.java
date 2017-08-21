package com.example.mydoctor.mydoctor.Navigation;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mydoctor.mydoctor.AdapterHelpers.DoctorListRecyclerAdapter;
import com.example.mydoctor.mydoctor.AdapterHelpers.DoctorsInfo;
import com.example.mydoctor.mydoctor.AdapterHelpers.RecyclerItemClickListener;
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
import static com.example.mydoctor.mydoctor.Login.CommonParams.URL_PATIENT_OPERATION;
import static com.example.mydoctor.mydoctor.Login.CommonParams.deviceOs;
import static com.example.mydoctor.mydoctor.Login.CommonParams.sha;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShowAllDoctors extends Fragment implements SearchView.OnQueryTextListener {

    private List<DoctorsInfo> doctorList = new ArrayList<>();
    private RecyclerView recyclerView;
    private DoctorListRecyclerAdapter mAdapter;
    private View mView;
    String spec_search;
    private TextView noResult;

    RecyclerView.LayoutManager mLayoutManager;

    View progressOverlay;

    public ShowAllDoctors() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        //Retrieve the value

        try {
            spec_search = getArguments().getString("spec", "a");
        } catch (Exception e) {
            Log.e("spec_search", e.toString());

        }

        return inflater.inflate(R.layout.fragment_show_all_doctors, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // progress overlay
        progressOverlay = view.findViewById(R.id.progress_overlay);
        mView = view.findViewById(android.R.id.content);
        noResult = (TextView) view.findViewById(R.id.showAllDocs_noresult);
        // searchview
        SearchView searchView = (SearchView) view.findViewById(R.id.searchViewFindDoctors);
        searchView.setOnQueryTextListener(this);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewFindDoctors);
        mAdapter = new DoctorListRecyclerAdapter(doctorList);
        mLayoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        // get default searchkeyword from previous page and search for it

        // Show progress overlay (with animation):
        ProgressBarUtils.animateView(progressOverlay, View.VISIBLE, 0.4f, 200);
        callDoctorSearch(spec_search);


        // add click listener
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Log.d("position", String.valueOf(position));
                DoctorsInfo doctorsInfo = mAdapter.getItem(position);
                String name = doctorsInfo.getName();
                String doctor_id = doctorsInfo.getDoctor_id();
                String spec = doctorsInfo.getSpec();
                String experience = doctorsInfo.getExperience();
                String city = doctorsInfo.getCity();
                String online_status = doctorsInfo.getOnlinestatus();
                Log.d("patName", name);
                Log.d("doctor_id", doctor_id);
                Log.d("specialisation", spec);
                Log.d("exp", experience);
                Log.d("city", city);


                /*Intent intent = new Intent(getApplicationContext(), DoctorProfile.class);
                intent.putExtra("patName", patName);
                intent.putExtra("doctor_id", doctor_id);
                intent.putExtra("specialisation", spec);
                intent.putExtra("experience", doctorsInfo.getExperience());
                intent.putExtra("city", doctorsInfo.getCity());
                startActivity(intent);*/

                //Put the value
                DoctorProfileView ldf = new DoctorProfileView();
                Bundle args = new Bundle();
                args.putString("doctor_id", doctor_id);
                args.putString("doctor_name", name);
                args.putString("online_status", online_status);
                ldf.setArguments(args);

                //Inflate the fragment
                getFragmentManager().beginTransaction().replace(R.id.patient_fragment_container, ldf).addToBackStack(null).commit();
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));


    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        // Show progress overlay (with animation):
        ProgressBarUtils.animateView(progressOverlay, View.VISIBLE, 0.4f, 200);

        callDoctorSearch(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    private void callDoctorSearch(String newText) {

        noResult.setVisibility(View.GONE);

        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams params = new RequestParams();
        params.put("type", "search");
        params.put("search", newText);
        params.put("id", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getInt("patient_id", 0));
        params.put("device", deviceOs);
        params.put("version", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("AppVersion", null));
        params.put("ipaddress", new Splash_Screen().getLocalIpAddress());
        params.put("udid", getActivity().getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("UDID", null));
        params.put("sha", sha);

        client.post(URL_PATIENT_OPERATION, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray jsonArray = new JSONArray();
                JSONObject jsonObject;
                String fName, lName, doctor_id, specialization, experience, city, onlinestatus, photo;
                String JsonStatus = response.optString("success");
                String jsonResult = response.toString();
                Log.d("docSearch_response", jsonResult);

                switch (JsonStatus) {
                    case "1":

                        try {
                            jsonArray = response.getJSONArray("searchresults");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("no_of_doctors", String.valueOf(jsonArray.length()));
                        int count = 0;
                        clearRecyclerView();
                        while (count < jsonArray.length()) {
                            try {
                                jsonObject = jsonArray.getJSONObject(count);
                                fName = jsonObject.getString("first_name");
                                lName = jsonObject.getString("last_name");
                                doctor_id = jsonObject.getString("doctor_id");
                                specialization = jsonObject.getString("specialization");
                                experience = jsonObject.getString("experience");
                                city = jsonObject.getString("city");
                                onlinestatus = jsonObject.getString("onlinestatus");
                                photo = jsonObject.getString("photo");

                                Log.d("fname", fName);
                                Log.d("lname", lName);
                                Log.d("spec", specialization);
                                Log.d("exp", experience);
                                Log.d("city", city);
                                Log.d("onlinestatus", onlinestatus);
                                // put them all in Contacts Object

                                DoctorsInfo doctorsInfo = new DoctorsInfo(fName + " " + lName, doctor_id, specialization, experience, city, onlinestatus, photo);
                                doctorList.add(doctorsInfo);
                                mAdapter.notifyDataSetChanged();


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            count++;
                        }
                        ProgressBarUtils.animateView(progressOverlay, View.GONE, 0, 200);
                        break;

                    case "2":
                        // database error
                        clearRecyclerView();
                        mAdapter.notifyDataSetChanged();
                        ProgressBarUtils.animateView(progressOverlay, View.GONE, 0, 200);
                        break;

                    case "3":
                        // No results found
                        clearRecyclerView();
                        mAdapter.notifyDataSetChanged();
                        /*Snackbar.make(mView, "No results Found", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();*/
                        noResult.setVisibility(View.VISIBLE);

                        ProgressBarUtils.animateView(progressOverlay, View.GONE, 0, 200);
                        break;
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                ProgressBarUtils.animateView(progressOverlay, View.GONE, 0, 200);
            }
        });
    }

    // method to clear the recycler view
    public void clearRecyclerView() {
        int size = this.doctorList.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.doctorList.remove(0);
            }


        }
    }
}
