package com.example.mydoctor.mydoctor.Patient;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mydoctor.mydoctor.AdapterHelpers.DownloadImageTask;
import com.example.mydoctor.mydoctor.ChooseLoginActivity;
import com.example.mydoctor.mydoctor.Login.Support;
import com.example.mydoctor.mydoctor.Navigation.FindDoctors;
import com.example.mydoctor.mydoctor.Navigation.MyDoctors;
import com.example.mydoctor.mydoctor.Navigation.MyPrescriptions;
import com.example.mydoctor.mydoctor.Navigation.MyWallet;
import com.example.mydoctor.mydoctor.Navigation.My_Appointments;
import com.example.mydoctor.mydoctor.Navigation.My_Profile;
import com.example.mydoctor.mydoctor.Navigation.PatientChatsFragment;
import com.example.mydoctor.mydoctor.Navigation.PatientHomepage;
import com.example.mydoctor.mydoctor.Navigation.ShowAllDoctors;
import com.example.mydoctor.mydoctor.R;
import com.example.mydoctor.mydoctor.Services.MyPatientService;
import com.example.mydoctor.mydoctor.Services.SinchService;
import com.example.mydoctor.mydoctor.VideoCalling.BaseActivity;
import com.facebook.login.LoginManager;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sinch.android.rtc.SinchError;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.mydoctor.mydoctor.Login.CommonParams.MY_PREFS_COMMON_PARAMS;
import static com.example.mydoctor.mydoctor.Login.CommonParams.MY_PREFS_COMMON_PARAMS_PATIENT;
import static com.example.mydoctor.mydoctor.Login.CommonParams.MY_PREFS_COMMON_PARAMS_PATIENT_CHAT;
import static com.example.mydoctor.mydoctor.Login.CommonParams.SUPPORT_PHONENUMBER;
import static com.example.mydoctor.mydoctor.Login.CommonParams.URL_CHAT;

public class PatientMainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, SinchService.StartFailedListener {

    // for actionview gallery
    TextView walletCount, appointmentsCount, myFavCount, myChatCount;

    String sinchUsername;
    // for navigation header
    CircleImageView userProfilePic;
    TextView userProfileName, nameHeader, balanceHeader;
    private IntentFilter mIntentFilter;
    private ProgressDialog mSpinner;
    ImageButton home, wallet, support, bookings;

    NavigationView navigationView;

    private static int REQUEST_AUDIO = 10;
    private static int REQUEST_PHONE_CALL = 20;

    public SinchService.SinchServiceInterface mSinchServiceInterface;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       /* requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
*/
        setContentView(R.layout.activity_patient_main);


        // store user details in SP from login intent
        getPatientValuesFromIntentToSP();

        // set the home fragment
        goToHomeFragment();


        // start sinch service
        /*getApplicationContext().bindService(new Intent(this, SinchService.class), this,
                BIND_AUTO_CREATE);*/


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_patient);
        toolbar.setTitle("Welcome " + getSharedPreferences(MY_PREFS_COMMON_PARAMS_PATIENT, MODE_PRIVATE).getString("pusername", "User Name"));
        setSupportActionBar(toolbar);

        home = (ImageButton) findViewById(R.id.imageButton_home_bottom);
        bookings = (ImageButton) findViewById(R.id.imageButton_appointment_bottom);
        wallet = (ImageButton) findViewById(R.id.imageButton_wallet_bottom);
        support = (ImageButton) findViewById(R.id.imageButton_support_bottom);

        // top bar items
        nameHeader = (TextView) findViewById(R.id.tvHeader_PatName);
        balanceHeader = (TextView) findViewById(R.id.tvHeaderPat_balance);

        nameHeader.setText("Hi! " + getSharedPreferences(MY_PREFS_COMMON_PARAMS_PATIENT, MODE_PRIVATE).getString("patientFullname", null));
        balanceHeader.setText(getSharedPreferences(MY_PREFS_COMMON_PARAMS_PATIENT, MODE_PRIVATE).getString("currency", "")+" " + getSharedPreferences(MY_PREFS_COMMON_PARAMS_PATIENT, MODE_PRIVATE).getString("balance", "0.00"));


        home.setOnClickListener(this);
        bookings.setOnClickListener(this);
        wallet.setOnClickListener(this);
        support.setOnClickListener(this);

        // sinch login
        //sinchLogin();

        // get patient channelID
        getPatientChannelID();

        // start background service
        Intent intent = new Intent(this, MyPatientService.class);
        startService(intent);
        // register receiver with intent filter
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("ChatRequestAccepted");
        mIntentFilter.addAction("AppointmentsUpdate");

      /*  FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.patient_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.patient_nav_view);
        View headerView = navigationView.getHeaderView(0);
        userProfilePic = (CircleImageView) headerView.findViewById(R.id.user_profile_image_navhead);
        new DownloadImageTask(userProfilePic).execute(getSharedPreferences(MY_PREFS_COMMON_PARAMS_PATIENT, MODE_PRIVATE).getString("photo", null));
        userProfileName = (TextView) headerView.findViewById(R.id.userName_navhead);
        userProfileName.setText(getSharedPreferences(MY_PREFS_COMMON_PARAMS_PATIENT, MODE_PRIVATE).getString("pusername", null));
        navigationView.setNavigationItemSelectedListener(this);

        // for actionview
        walletCount = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                findItem(R.id.patient_wallet));
        appointmentsCount = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                findItem(R.id.patient_my_appointments));
        myFavCount = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                findItem(R.id.patient_my_favourites));
        myChatCount = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                findItem(R.id.patient_my_chats));
        //This method will initialize the count value
        //initializeCountDrawer();

        // check permission for audio
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                    == PackageManager.PERMISSION_DENIED) {

                Log.d("permission", "permission denied to SEND_SMS - requesting it");
                String[] permissions = {Manifest.permission.RECORD_AUDIO};

                requestPermissions(permissions, REQUEST_AUDIO);

            }
        }
        // sinch login
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                sinchLogin();

            }
        }, 100);
    }


    private void initializeCountDrawer() {
        //Gravity property aligns the text
        walletCount.setGravity(Gravity.CENTER_VERTICAL);
        walletCount.setTypeface(null, Typeface.BOLD);
        walletCount.setTextColor(getResources().getColor(R.color.app_dark_blue));
        walletCount.setText("£10400");

        myFavCount.setGravity(Gravity.CENTER_VERTICAL);
        myFavCount.setTypeface(null, Typeface.BOLD);
        myFavCount.setTextColor(getResources().getColor(R.color.app_orange));
        myFavCount.setText("3");

        myChatCount.setGravity(Gravity.CENTER_VERTICAL);
        myChatCount.setTypeface(null, Typeface.BOLD);
        myChatCount.setTextColor(getResources().getColor(R.color.app_dark_blue));
        myChatCount.setText("10+");

        appointmentsCount.setGravity(Gravity.CENTER_VERTICAL);
        appointmentsCount.setTypeface(null, Typeface.BOLD);
        appointmentsCount.setTextColor(getResources().getColor(R.color.app_orange));
        appointmentsCount.setText("7 new");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.patient_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

        int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
        if (backStackEntryCount == 0) {
            // write your code to switch between fragments.
            new AlertDialog.Builder(this)
                    .setMessage("Are you sure you want to exit?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            PatientMainActivity.this.finish();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.patient_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.doc_action_logoutPat) {
            logout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.patient_home_findDoctors) {

            getSupportFragmentManager().beginTransaction().replace(R.id.patient_fragment_container, new ShowAllDoctors()).addToBackStack(null).commit();

        } else if (id == R.id.patient_wallet) {

            getSupportFragmentManager().beginTransaction().replace(R.id.patient_fragment_container, new MyWallet()).commit();

        } else if (id == R.id.patient_user_profile) {

            getSupportFragmentManager().beginTransaction().replace(R.id.patient_fragment_container, new My_Profile()).commit();

        } else if (id == R.id.patient_my_appointments) {

            getSupportFragmentManager().beginTransaction().replace(R.id.patient_fragment_container, new My_Appointments()).commit();

        } else if (id == R.id.patient_my_favourites) {

            getSupportFragmentManager().beginTransaction().replace(R.id.patient_fragment_container, new MyDoctors()).commit();

        } else if (id == R.id.patient_my_chats) {

            getSupportFragmentManager().beginTransaction().replace(R.id.patient_fragment_container, new PatientChatsFragment()).commit();

        } else if (id == R.id.patient_my_prescriptions) {
            getSupportFragmentManager().beginTransaction().replace(R.id.patient_fragment_container, new MyPrescriptions()).commit();
        }

        /*else if (id == R.id.user_maps) {
            startActivity(new Intent(getApplicationContext(), MapsActivity.class));
        }*/
        else if (id == R.id.patient_logout) {
            logout();
        } else if (id == R.id.nav_patient_call) {

            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel: "+ SUPPORT_PHONENUMBER));

            if (ActivityCompat.checkSelfPermission(PatientMainActivity.this,
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(PatientMainActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
            } else startActivity(callIntent);

        } else if (id == R.id.nav_patient_send) {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", "support@consultglobaldoctors.com", null));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Support");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
            startActivity(Intent.createChooser(emailIntent, "Send email..."));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.patient_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void logout() {
        LoginManager.getInstance().logOut();
        stopService(new Intent(this, MyPatientService.class));
        // remove SP
        getSharedPreferences(MY_PREFS_COMMON_PARAMS_PATIENT_CHAT, MODE_PRIVATE).edit().clear().apply();
        Intent login = new Intent(PatientMainActivity.this, ChooseLoginActivity.class);
        startActivity(login);
        finish();
    }

    public void getPatientValuesFromIntentToSP() {

        Intent intent = getIntent();
        try {
            String type = intent.getStringExtra("type");
            String pusername = intent.getStringExtra("pusername");
            String puserpass = intent.getStringExtra("puserpass");
            int patient_id = intent.getIntExtra(("patient_id"), 0);
            String patientFullname = intent.getStringExtra("patientFullname");
            String photo = intent.getStringExtra("photo");
            String balance = intent.getStringExtra("balance");
            String loginType = intent.getStringExtra("login_type");
            String currency = intent.getStringExtra("currency");
            Log.d("patient_id", String.valueOf(patient_id));
            if (currency.equals("GBP")) {
                currency = "£ ";
            } else if (currency.equals("INR")) {
                currency = "Rs ";
            } else if (currency.equals("AED")) {
                currency = "Dhs ";
            }

            // store in SP
            SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_COMMON_PARAMS_PATIENT, MODE_PRIVATE).edit();
            editor.putString("type", type);
            editor.putString("pusername", pusername);
            editor.putString("puserpass", puserpass);
            editor.putString("photo", photo);
            editor.putString("patientFullname", patientFullname);
            editor.putString("balance", balance);
            editor.putInt("patient_id", patient_id);
            editor.putString("currency", "£");  // TODO: 01-Jun-17 use the variable "currency" here
            editor.apply();
            // store logintype in common params
            SharedPreferences.Editor editor1 = getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).edit();
            editor1.putString("login_type", loginType);
            editor1.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getPatientChannelID() {

        String user_name = getSharedPreferences(MY_PREFS_COMMON_PARAMS_PATIENT, MODE_PRIVATE).getString("pusername", "null");
        int user_id = getSharedPreferences(MY_PREFS_COMMON_PARAMS_PATIENT, MODE_PRIVATE).getInt("patient_id", 0);
        Log.d("user_id_in_SP", String.valueOf(user_id));
        Log.d("username_in_SP", user_name);
        // call service to get channelID
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("type", "receive");
        params.put("channel", "patient"); // sender channel
        params.put("channel_id", 0); // for first timeFinal send 0 to get channel id
        params.put("userid", user_id); // sender userid
        params.put("username", user_name); // sender username
        client.addHeader("Content-Type", "application/x-www-form-urlencoded");
        client.post(URL_CHAT, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                String jsonResponse = response.toString();
                Log.d("json_patChannelId", jsonResponse);
                // will get "channel_msg":"Channel Reopened. if called again
                String jsonStatus = response.optString("success");

                switch (jsonStatus) {

                    case "1":
                        // get the new channel id and store it in the same SP
                        int newPatientChannelID = response.optInt("channel_id");
                        String newPatientNickName = response.optString("my_nick");
                        Log.d("new_pat_channelid", String.valueOf(newPatientChannelID));
                        Log.d("pat_nick_name", newPatientNickName);

                        SharedPreferences prefs = getSharedPreferences(MY_PREFS_COMMON_PARAMS_PATIENT_CHAT, MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putInt("patient_channel_id", newPatientChannelID);
                        editor.putString("patient_nick_name", newPatientNickName);
                        editor.apply();

                        Log.d("patientchanlID_in_SP", String.valueOf(getSharedPreferences(MY_PREFS_COMMON_PARAMS_PATIENT_CHAT, MODE_PRIVATE).getInt("patient_channel_id", 0)));
                        Log.d("patient_nick_name_in_SP", getSharedPreferences(MY_PREFS_COMMON_PARAMS_PATIENT_CHAT, MODE_PRIVATE).getString("patient_nick_name", null));
                        break;

                    default:
                        break;
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });


    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals("ChatRequestAccepted")) {

                String json = intent.getStringExtra("json");
                final int apt_id = intent.getIntExtra("apt_id", 0);
                final int doctor_channelId = intent.getIntExtra("fromId", 0);
                final int doctor_userId = intent.getIntExtra("fromuserid", 0);
                final String doctor_nickName = intent.getStringExtra("doctor_nickName");
                Log.d("json_received_chaReq", json);
                Log.d("received_apt_idPat", String.valueOf(apt_id));
                Log.d("received_doc_channelId", String.valueOf(doctor_channelId));
                Log.d("received_doc_id", String.valueOf(doctor_userId));

                // show a dialog to enter chatroom

                AlertDialog.Builder builder = new AlertDialog.Builder(PatientMainActivity.this);
                builder.setMessage("Chat Request Accepted! Enter ChatRoom Now?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // navigate to the chatroom
                                Intent intent1 = new Intent(getApplicationContext(), PatientChatRoom.class);
                                intent1.putExtra("apt_id", apt_id);
                                intent1.putExtra("doctor_channelId", doctor_channelId);
                                intent1.putExtra("doctor_userId", doctor_userId);
                                intent1.putExtra("doctor_nickName", doctor_nickName);
                                startActivity(intent1);

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();

            }

            else if (intent.getAction().equals("AppointmentsUpdate")) {


                Snackbar snackbar = Snackbar.make(navigationView, "Appointment Cancelled !", Snackbar.LENGTH_INDEFINITE)
                        .setAction("View", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                getSupportFragmentManager().beginTransaction().replace(R.id.patient_fragment_container, new My_Appointments()).addToBackStack(null).commit();
                            }
                        });
                snackbar.show();
                // Changing message text color
                snackbar.setActionTextColor(Color.RED);

                // Changing action button text color
                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.YELLOW);
                snackbar.show();

                //Toast.makeText(DoctorMainActivity.this, "New Request Received !", Toast.LENGTH_SHORT).show();
            }


        }
    };

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_PHONE_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel: "+ SUPPORT_PHONENUMBER));

                if (ActivityCompat.checkSelfPermission(PatientMainActivity.this,
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(PatientMainActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
                } else startActivity(callIntent);
            }
        }
    }

    @Override
    protected void onPause() {
        unregisterReceiver(mReceiver);

        super.onPause();
    }

    protected void goToHomeFragment() {

        getSupportFragmentManager().beginTransaction().replace(R.id.patient_fragment_container, new PatientHomepage()).commit();
    }

    protected void goToHomeAppointment() {

        getSupportFragmentManager().beginTransaction().replace(R.id.patient_fragment_container, new FindDoctors()).commit();
    }

    protected void goToHomeWallet() {

        getSupportFragmentManager().beginTransaction().replace(R.id.patient_fragment_container, new MyWallet()).commit();
    }

    protected void goToHomeSupport() {

        getSupportFragmentManager().beginTransaction().replace(R.id.patient_fragment_container, new Support()).commit();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.imageButton_home_bottom:
                goToHomeFragment();
                break;

            case R.id.imageButton_appointment_bottom:
                goToHomeAppointment();
                break;
            case R.id.imageButton_wallet_bottom:
                goToHomeWallet();
                break;
            case R.id.imageButton_support_bottom:
                goToHomeSupport();
                break;
        }
    }


    @Override
    public void onStartFailed(SinchError error) {
        Log.d("sinch_error_login", error.toString());
    }

    @Override
    public void onStarted() {

    }

    private void sinchLogin() {
        sinchUsername = getSharedPreferences(MY_PREFS_COMMON_PARAMS_PATIENT, MODE_PRIVATE).getString("pusername", "Patient");
        try {
            if (((getSinchServiceInterface() == null) || (!getSinchServiceInterface().isStarted()))) {
                getSinchServiceInterface().startClient(sinchUsername);
                Log.d("sinch_Patlogin_name", sinchUsername);
                //showSpinner();
            } else {
                Toast.makeText(this, "Patient Online!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("getSinchInterface", e.toString());

        }
    }

    @Override
    public void onServiceConnected() {
        //mLoginButton.setEnabled(true);
        getSinchServiceInterface().setStartListener(this);
    }
}
