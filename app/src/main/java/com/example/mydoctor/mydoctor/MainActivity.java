package com.example.mydoctor.mydoctor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.mydoctor.mydoctor.AdapterHelpers.DoctorListRecyclerAdapter;
import com.example.mydoctor.mydoctor.AdapterHelpers.DoctorsInfo;
import com.example.mydoctor.mydoctor.AdapterHelpers.RecyclerItemClickListener;
import com.example.mydoctor.mydoctor.Navigation.MyWallet;
import com.example.mydoctor.mydoctor.Navigation.My_Appointments;
import com.facebook.login.LoginManager;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.mydoctor.mydoctor.Login.CommonParams.MY_PREFS_COMMON_PARAMS;
import static com.example.mydoctor.mydoctor.Login.CommonParams.URL_PATIENT_OPERATION;
import static com.example.mydoctor.mydoctor.Login.CommonParams.deviceOs;
import static com.example.mydoctor.mydoctor.Login.CommonParams.sha;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener {

    private List<DoctorsInfo> doctorList = new ArrayList<>();
    private RecyclerView recyclerView;
    private DoctorListRecyclerAdapter mAdapter;

    CircleImageView userProfilePic;
    TextView userProfileName;

    ArrayList<String> mobileArray, mobileArray1, mobileArray2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarSearchView);
        setSupportActionBar(toolbar);
        // get intent values and store in SP
        getPatientValuesFromIntentToSP();
        // initialise recycler view
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewSearchDoctor);
        //
        mAdapter = new DoctorListRecyclerAdapter(doctorList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        // add click listener
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Log.d("position", String.valueOf(position));
                DoctorsInfo doctorsInfo = mAdapter.getItem(position);
                String name = doctorsInfo.getName();
                String doctor_id = doctorsInfo.getDoctor_id();
                String spec = doctorsInfo.getSpec();
                String experience = doctorsInfo.getExperience();
                String city = doctorsInfo.getCity();
                Log.d("patName", name);
                Log.d("doctor_id", doctor_id);
                Log.d("specialisation", spec);
                Log.d("exp", experience);
                Log.d("city", city);


                Intent intent = new Intent(getApplicationContext(), DoctorProfile.class);
                intent.putExtra("patName", name);
                intent.putExtra("doctor_id", doctor_id);
                intent.putExtra("specialisation", spec);
                intent.putExtra("experience", doctorsInfo.getExperience());
                intent.putExtra("city", doctorsInfo.getCity());
                startActivity(intent);

            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        userProfilePic = (CircleImageView) headerView.findViewById(R.id.user_profile_image_navhead);
        //new DownloadImageTask(userProfilePic).execute("http://static.dnaindia.com/sites/default/files/styles/half/public/2016/05/31/466224-suriya-759.jpg?itok=Ivmz4ALB");
        userProfileName = (TextView) headerView.findViewById(R.id.userName_navhead);
        userProfileName.setText("User Name");
        navigationView.setNavigationItemSelectedListener(this);

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
//System.exit(0);
//        finish();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        // searchview in toolbar
        getMenuInflater().inflate(R.menu.menu_items, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.doc_action_settings) {
            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.settings) {

            //getSupportFragmentManager().beginTransaction().replace(R.layout.);

        } else if (id == R.id.patient_my_appointments) {


            getSupportFragmentManager().beginTransaction().replace(R.id.main, new My_Appointments()).addToBackStack(null).commit();


        } else if (id == R.id.patient_home) {

            //startActivity(new Intent(getApplicationContext(), MainActivity.class));
            //getSupportFragmentManager().beginTransaction().replace(R.id.main, new My_Profile()).addToBackStack(null).commit();

        } /*else if (id == R.id.patient_my_favourites) {

            //getSupportFragmentManager().beginTransaction().replace(R.id.main, new PatientChatsFragment()).addToBackStack(null).commit();

        }*/ else if (id == R.id.patient_logout) {
            logout();
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if(id == R.id.user_maps){
            startActivity(new Intent(getApplicationContext(), MapsActivity.class));
        }

        else if (id == R.id.patient_wallet) {
            getSupportFragmentManager().beginTransaction().replace(R.id.main, new MyWallet()).addToBackStack(null).commit();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void logout() {
        LoginManager.getInstance().logOut();
        Intent login = new Intent(MainActivity.this, ChooseLoginActivity.class);
        startActivity(login);
        finish();
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        // call method to get doctor list
        callDoctorSearch(query);

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    private void callDoctorSearch(String newText) {
        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams params = new RequestParams();
        params.put("type", "search");
        params.put("search", newText);
        params.put("device", deviceOs);
        params.put("version", "1.0");
        params.put("ipaddress", "192.168.0.1");
        params.put("udid", "10981243");
        params.put("sha", sha);

        client.post(URL_PATIENT_OPERATION, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray jsonArray = new JSONArray();
                JSONObject jsonObject;
                String fName, lName, doctor_id, specialization, experience, city;
                String JsonStatus = response.optString("success");
                String jsonResult = response.toString();
                Log.d("response", jsonResult);

                switch (JsonStatus) {
                    case "1":

                        try {
                            jsonArray = response.getJSONArray("searchresults");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("no of doctors", String.valueOf(jsonArray.length()));
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

                                Log.d("fname", fName);
                                Log.d("lname", lName);
                                Log.d("spec", specialization);
                                Log.d("exp", experience);
                                Log.d("city", city);
                                // put them all in Contacts Object

                              /*  DoctorsInfo doctorsInfo = new DoctorsInfo(fName + " " + lName, doctor_id, specialization, experience, city, onlinestatus);
                                doctorList.add(doctorsInfo);
                                mAdapter.notifyDataSetChanged();*/


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            count++;


                        }

                        break;

                    case "2":
                        // database error
                        clearRecyclerView();
                        mAdapter.notifyDataSetChanged();
                        break;

                    case "3":
                        // No results found
                        clearRecyclerView();
                        mAdapter.notifyDataSetChanged();
                        break;
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
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

    public void getPatientValuesFromIntentToSP() {

        Intent intent = getIntent();
        String type = intent.getStringExtra("type");
        String pusername = intent.getStringExtra("pusername");
        String puserpass = intent.getStringExtra("puserpass");
        int patient_id = intent.getIntExtra(("patient_id"), 0);
        String photo = intent.getStringExtra("photo");
        Log.d("patient_id", String.valueOf(patient_id));
        // store in SP
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).edit();
        editor.putString("type", type);
        editor.putString("pusername", pusername);
        editor.putString("puserpass", puserpass);
        editor.putString("photo", photo);
        editor.putInt("patient_id", patient_id);
        editor.apply();

    }


}
