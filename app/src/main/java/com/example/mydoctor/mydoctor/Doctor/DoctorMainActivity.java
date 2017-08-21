package com.example.mydoctor.mydoctor.Doctor;

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
import android.support.annotation.IdRes;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mydoctor.mydoctor.AdapterHelpers.DownloadImageTask;
import com.example.mydoctor.mydoctor.ChooseLoginActivity;
import com.example.mydoctor.mydoctor.DoctorNavigation.DoctorAllBookings;
import com.example.mydoctor.mydoctor.DoctorNavigation.DoctorChatsFragment;
import com.example.mydoctor.mydoctor.DoctorNavigation.DoctorHomepage;
import com.example.mydoctor.mydoctor.DoctorNavigation.DoctorProfileFragment;
import com.example.mydoctor.mydoctor.DoctorNavigation.DoctorWalletFragment;
import com.example.mydoctor.mydoctor.DoctorNavigation.PendingRequestsFragment;
import com.example.mydoctor.mydoctor.Login.Splash_Screen;
import com.example.mydoctor.mydoctor.Login.Support;
import com.example.mydoctor.mydoctor.R;
import com.example.mydoctor.mydoctor.Services.MyDoctorService;
import com.example.mydoctor.mydoctor.Services.SinchService;
import com.example.mydoctor.mydoctor.Utils.BarclaysPaymentPage;
import com.example.mydoctor.mydoctor.VideoCalling.BaseActivity;
import com.facebook.login.LoginManager;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sinch.android.rtc.SinchError;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.support.design.widget.Snackbar.make;
import static com.example.mydoctor.mydoctor.Login.CommonParams.MY_PREFS_COMMON_PARAMS;
import static com.example.mydoctor.mydoctor.Login.CommonParams.MY_PREFS_COMMON_PARAMS_DOCTOR;
import static com.example.mydoctor.mydoctor.Login.CommonParams.MY_PREFS_COMMON_PARAMS_DOCTOR_CHAT;
import static com.example.mydoctor.mydoctor.Login.CommonParams.SUPPORT_EMAIL;
import static com.example.mydoctor.mydoctor.Login.CommonParams.SUPPORT_PHONENUMBER;
import static com.example.mydoctor.mydoctor.Login.CommonParams.URL_CHAT;
import static com.example.mydoctor.mydoctor.Login.CommonParams.URL_DOCTOR_OPERATION;
import static com.example.mydoctor.mydoctor.Login.CommonParams.URL_DOCTOR_WALLET;
import static com.example.mydoctor.mydoctor.Login.CommonParams.deviceOs;
import static com.example.mydoctor.mydoctor.Login.CommonParams.sha;

public class DoctorMainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, SinchService.StartFailedListener {

    // Switch for enabling video consultation
    Switch enableVideoChat;
    TextView walletCount, appointmentsCount, headerBalance, headerDocName;
    private long lastBackPressTime = 0;
    CircleImageView doctorProfilePic = null;
    TextView doctorName = null;
    private Toast toastBackPressed;
    NavigationView navigationView;
    Toolbar toolbar;
    private IntentFilter mIntentFilter;
    String balance, sinchUsername, currency;
    private ProgressDialog mSpinner;
    ImageButton home, wallet, support, bookings;
    private boolean exit = false;

    private static final int REQUEST_AUDIO = 11;

    private static final int REQUEST_PHONE_CALL = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/

        setContentView(R.layout.activity_doctor_main_nav_drawer);
        // get data from Intent and store into SP
        getValuesFromIntentToSP();
        // get doctor channelID
        getDoctorChannelID();
        // set the main fragment initially
        DoctorHomepage mainFragment = new DoctorHomepage();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.doctor_fragment_container, mainFragment);
        fragmentTransaction.commit();

        headerDocName = (TextView) findViewById(R.id.tvMainHeaderDocName);
        headerBalance = (TextView) findViewById(R.id.tvMainHeaderDocBalance);

        headerDocName.setText("Dr " + getSharedPreferences(MY_PREFS_COMMON_PARAMS_DOCTOR, MODE_PRIVATE).getString("doctorFullname", "User Name"));
        headerBalance.setText(getSharedPreferences(MY_PREFS_COMMON_PARAMS_DOCTOR, MODE_PRIVATE).getString("currency", "")+" "+getSharedPreferences(MY_PREFS_COMMON_PARAMS_DOCTOR, MODE_PRIVATE).getString("balance", "0.00"));


        toolbar = (Toolbar) findViewById(R.id.toolbar_doctor);
        //toolbar.setTitle("Welcome Dr "+getSharedPreferences(MY_PREFS_COMMON_PARAMS_DOCTOR, MODE_PRIVATE).getString("doc_username", "User Name"));
        setSupportActionBar(toolbar);


        home = (ImageButton) findViewById(R.id.doc_imageButton_home_bottom);
        bookings = (ImageButton) findViewById(R.id.doc_imageButton_appointment_bottom);
        wallet = (ImageButton) findViewById(R.id.doc_imageButton_wallet_bottom);
        support = (ImageButton) findViewById(R.id.doc_imageButton_support_bottom);

        home.setOnClickListener(this);
        bookings.setOnClickListener(this);
        wallet.setOnClickListener(this);
        support.setOnClickListener(this);

        // start service ----- also add service in the manifest
        Intent intent = new Intent(this, MyDoctorService.class);
        startService(intent);
        // receive broadcasts
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("ChatRequest");
        mIntentFilter.addAction("AllRequests");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        doctorProfilePic = (CircleImageView) headerView.findViewById(R.id.doctor_profile_image_navhead);
        //new DownloadImageTask(doctorProfilePic).execute("http://static.dnaindia.com/sites/default/files/styles/half/public/2016/05/31/466224-suriya-759.jpg?itok=Ivmz4ALB");
        // top header items
        doctorName = (TextView) headerView.findViewById(R.id.tv_doctorName_navhead);
        doctorName.setText("Dr " + getSharedPreferences(MY_PREFS_COMMON_PARAMS_DOCTOR, MODE_PRIVATE).getString("doctorFullname", "User Name"));
        navigationView.setNavigationItemSelectedListener(this);
        // set profile image
        new DownloadImageTask(doctorProfilePic).execute(getSharedPreferences(MY_PREFS_COMMON_PARAMS_DOCTOR, MODE_PRIVATE).getString("doctor_photo", ""));

// for actionview
       /* walletCount=(TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                findItem(R.id.doc_nav_wallet));*/
        appointmentsCount = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                findItem(R.id.doc_nav_apt_requests));
        enableVideoChat = (Switch) MenuItemCompat.getActionView(navigationView.getMenu().findItem(R.id.doc_nav_video_chats));
        switchFunction();
        //This method will initialize the count value
        //initializeCountDrawer();
        //setMenuCounter(R.id.doc_nav_wallet, 13450);

        // check permission for audio
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                    == PackageManager.PERMISSION_DENIED) {

                Log.d("permission", "permission denied to SEND_SMS - requesting it");
                String[] permissions = {Manifest.permission.RECORD_AUDIO};

                requestPermissions(permissions, REQUEST_AUDIO);

            }
        }

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 300ms
                loginClicked();

            }
        }, 500);
    }

    private void setMenuCounter(@IdRes int itemId, int count) {
        TextView view = (TextView) navigationView.getMenu().findItem(itemId).getActionView().findViewById(R.id.counterViewTV);
        view.setText(count > 0 ? String.valueOf(count) : null);
        view.setBackgroundResource(R.color.app_blue);
    }

    private void switchFunction() {
        enableVideoChat.setChecked(true);
        enableVideoChat.setHighlightColor(getResources().getColor(R.color.app_orange));
        //enableVideoChat.setBackgroundColor(getResources().getColor(R.color.app_orange));
        enableVideoChat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {

                    // check permission for audio
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

                        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                                == PackageManager.PERMISSION_DENIED) {

                            Log.d("permission", "permission denied to SEND_SMS - requesting it");
                            String[] permissions = {Manifest.permission.RECORD_AUDIO};

                            requestPermissions(permissions, REQUEST_AUDIO);

                        }
                    }

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Do something after 100ms
                            loginClicked();

                        }
                    }, 100);

                    make(compoundButton, "Video Calling Enabled !", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                } else {

                    if (getSinchServiceInterface() != null) {
                        getSinchServiceInterface().stopClient();
                    }
                    make(compoundButton, "Video Calling Disabled !", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }


    private void initializeCountDrawer() {
        walletCount.setGravity(Gravity.CENTER_VERTICAL);
        walletCount.setTypeface(null, Typeface.BOLD);
        walletCount.setTextColor(getResources().getColor(R.color.app_dark_blue));
        walletCount.setText("£13400");
        walletCount.setBackgroundResource(R.drawable.accept_green_button);

        appointmentsCount.setGravity(Gravity.CENTER_VERTICAL);
        appointmentsCount.setTypeface(null, Typeface.BOLD);
        appointmentsCount.setTextColor(getResources().getColor(R.color.app_orange));
        appointmentsCount.setText("7 new");
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
                            DoctorMainActivity.this.finish();
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
        getMenuInflater().inflate(R.menu.doctor_main_nav_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement  doc_action_logout
         if (id == R.id.doc_action_logout) {
            logoutApp();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.doc_nav_home) {
            // set the main fragment initially   --------   Main fragment ------  this is intialy set in this MainActivity
            getSupportActionBar().setTitle("Home");

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.doctor_fragment_container, new DoctorHomepage());
            fragmentTransaction.commit();
        } else if (id == R.id.doc_nav_profile) {
            getSupportActionBar().setTitle("Profile");
            DoctorProfileFragment profileFragment = new DoctorProfileFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.doctor_fragment_container, profileFragment);
            fragmentTransaction.commit();

        } else if (id == R.id.doc_nav_wallet) {
            getSupportActionBar().setTitle("Wallet");
            DoctorWalletFragment walletFragment = new DoctorWalletFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.doctor_fragment_container, walletFragment);
            fragmentTransaction.commit();

        } else if (id == R.id.doc_nav_chats) {
            getSupportActionBar().setTitle("Chats");
            DoctorChatsFragment chatsFragment = new DoctorChatsFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.doctor_fragment_container, chatsFragment).commit();


        }else if (id == R.id.doc_nav_video_chats) {    // // TODO: 15-Jun-17   remove it

            BarclaysPaymentPage chatsFragment = new BarclaysPaymentPage();
            getSupportFragmentManager().beginTransaction().replace(R.id.doctor_fragment_container, chatsFragment).commit();


        } else if (id == R.id.doc_nav_apt_requests) {
            PendingRequestsFragment notificationFragment = new PendingRequestsFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.doctor_fragment_container, notificationFragment).commit();

        } else if (id == R.id.doc_nav_appointments) {

            DoctorAllBookings appointmentsFragment = new DoctorAllBookings();
            getSupportFragmentManager().beginTransaction().replace(R.id.doctor_fragment_container, appointmentsFragment).commit();

        } else if (id == R.id.doc_nav_logout) {

            logoutApp();

        } else if (id == R.id.doc_nav_contact) {

            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel: "+ SUPPORT_PHONENUMBER));

            if (ActivityCompat.checkSelfPermission(DoctorMainActivity.this,
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(DoctorMainActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
            } else startActivity(callIntent);

        } else if (id == R.id.doc_nav_send) {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", SUPPORT_EMAIL, null));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Support");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
            startActivity(Intent.createChooser(emailIntent, "Send email..."));
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_PHONE_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel: "+ SUPPORT_PHONENUMBER));

                if (ActivityCompat.checkSelfPermission(DoctorMainActivity.this,
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(DoctorMainActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
                } else startActivity(callIntent);
            }
        }
    }

    private void logoutApp() {
        LoginManager.getInstance().logOut();
        stopService(new Intent(this, MyDoctorService.class));
        // remove SP chat values
        getSharedPreferences(MY_PREFS_COMMON_PARAMS_DOCTOR_CHAT, MODE_PRIVATE).edit().clear().apply();
        Intent login = new Intent(DoctorMainActivity.this, ChooseLoginActivity.class);
        startActivity(login);
        finish();
    }

    private void getDoctorChannelID() {

        int channel_id = 0; // first time channel id is zero

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("type", "receive");
        params.put("channel", "doctor"); // channel whom it should return value
        params.put("channel_id", channel_id); // for first time send 0 to get channel id
        params.put("userid", getSharedPreferences(MY_PREFS_COMMON_PARAMS_DOCTOR, MODE_PRIVATE).getInt("doctor_id", 0)); // doctor userid
        params.put("username", getSharedPreferences(MY_PREFS_COMMON_PARAMS_DOCTOR, MODE_PRIVATE).getString("doc_username", null)); // doctor username

        //client.addHeader("Content-Type", "application/x-www-form-urlencoded");
        client.post(URL_CHAT, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String jsonResponse = String.valueOf(response);
                Log.d("json_docChanID", jsonResponse);
                String jsonStatus = response.optString("success");
                switch (jsonStatus) {

                    case "1":
                        // save the channel-id from the response
                        int doc_channel_id = response.optInt("channel_id");
                        Log.d("new_doctor_channel_id", String.valueOf(doc_channel_id));
                        String doc_nick_name = response.optString("my_nick");
                        Log.d("new_doc_nick_name", doc_nick_name);
                        // save channel_id
                        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_COMMON_PARAMS_DOCTOR_CHAT, MODE_PRIVATE).edit();
                        editor.putInt("doctor_channel_id", doc_channel_id);
                        editor.putString("doc_nick_name", doc_nick_name);
                        editor.apply();

                        Log.d("doctor_channelid_SP", String.valueOf(getSharedPreferences(MY_PREFS_COMMON_PARAMS_DOCTOR_CHAT, MODE_PRIVATE).getInt("doctor_channel_id", 0)));
                        Log.d("doctor_nickname_SP", String.valueOf(getSharedPreferences(MY_PREFS_COMMON_PARAMS_DOCTOR_CHAT, MODE_PRIVATE).getString("doc_nick_name", null)));
                        break;

                    default:
                        Toast.makeText(DoctorMainActivity.this, "Network Error!", Toast.LENGTH_SHORT).show();
                        break;
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                String JsonResponse = String.valueOf(errorResponse);
                Log.d("json response error", JsonResponse);
            }
        });

    }

    public String getBalance() {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("type", "balance");
        params.put("id", getSharedPreferences(MY_PREFS_COMMON_PARAMS_DOCTOR, MODE_PRIVATE).getInt("doctor_id", 0)); // doctor id
        params.put("device", deviceOs); // sender username
        params.put("version", getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("AppVersion", null));
        params.put("ipaddress", new Splash_Screen().getLocalIpAddress());
        params.put("udid", getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("UDID", null));
        params.put("sha", sha);
        client.post(URL_DOCTOR_WALLET, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d("getbalance", response.toString());
                if (response.optInt("success") == 1) {

                    balance = response.optString("balance");
                    Log.d("getbalanceDoc", balance);
                    currency = response.optString("currency");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });

        return balance;
    }

    public void getValuesFromIntentToSP() {

        Intent intent = getIntent();
        String type = intent.getStringExtra("type");
        String doctorname = intent.getStringExtra("doctorusername");
        String doctorFullname = intent.getStringExtra("doctorFullname");
        String doctorpass = intent.getStringExtra("doctorpass");
        int doctor_id = intent.getIntExtra("doctor_id", 0);
        String photo = intent.getStringExtra("photo");
        String balance = intent.getStringExtra("balance");
        String currency = intent.getStringExtra("currency");
        Log.d("doctor_photo", photo);
        Log.d("doctor_id", String.valueOf(doctor_id));
        Log.d("username_pass", doctorname + " " + doctorpass);
        Log.d("balance", balance);
        if (currency.equals("GBP")){
            currency = "£ ";
        }else if (currency.equals("INR")){
            currency = "Rs ";
        }else if (currency.equals("AED")){
            currency = "Dhs ";
        }
        // store in SP
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_COMMON_PARAMS_DOCTOR, MODE_PRIVATE).edit();
        editor.putString("type", type);
        editor.putString("doc_username", doctorname);
        editor.putString("doc_userpass", doctorpass);
        editor.putString("balance", balance);
        editor.putString("doctorFullname", doctorFullname);
        editor.putString("doctor_photo", photo);
        editor.putInt("doctor_id", doctor_id);
        editor.putString("currency", "£"); // TODO: 01-Jun-17 change it to currency
        editor.apply();

    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals("ChatRequest")) {

                String json = intent.getStringExtra("json");
                final int apt_id = intent.getIntExtra("apt_id", 0);
                final int patient_channelId = intent.getIntExtra("fromId", 0);
                final int patient_userId = intent.getIntExtra("fromUserId", 0);
                final String patient_nickName = intent.getStringExtra(("fromUserNick"));

                Log.d("json_receivedDocChatReq", json);
                Log.d("received_apt_idDoc", String.valueOf(apt_id));

                // dont immediately call service instead navigate to chatrequets page and then

                AlertDialog.Builder builder = new AlertDialog.Builder(DoctorMainActivity.this);
                builder.setMessage("Accept Incoming Chat Request?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //Doctor.this.finish();
                                // call service
                                doctorAcceptChat(apt_id, patient_channelId, patient_userId, patient_nickName);

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();

            } else if (intent.getAction().equals("AllRequests")) {


                Snackbar snackbar = Snackbar.make(navigationView, "New Request Received !", Snackbar.LENGTH_INDEFINITE)
                        .setAction("View", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                getSupportFragmentManager().beginTransaction().replace(R.id.doctor_fragment_container, new PendingRequestsFragment()).addToBackStack(null).commit();
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
    protected void onPause() {
        unregisterReceiver(mReceiver);

        if (mSpinner != null) {
            mSpinner.dismiss();
        }
        super.onPause();
    }

    private void doctorAcceptChat(final int apt_id, final int patient_channelId, final int patientUserId, final String patientNickName) {

        // doctor operation service
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("type", "confirm_chat");
        params.put("id", getSharedPreferences(MY_PREFS_COMMON_PARAMS_DOCTOR, MODE_PRIVATE).getInt("doctor_id", 0)); // doctor id
        params.put("request_id", apt_id); // apt_id
        params.put("device", deviceOs); // sender username
        params.put("version", getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("AppVersion", null));
        params.put("ipaddress", new Splash_Screen().getLocalIpAddress());
        params.put("udid", getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("UDID", null));
        params.put("sha", sha);

        client.addHeader("Content-Type", "application/x-www-form-urlencoded");
        client.post(URL_DOCTOR_OPERATION, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                String jsonResponse = response.toString();
                Log.d("jsonchatrequest1", jsonResponse);
                String jsonStatus = response.optString("success");

                if (jsonStatus.equals("1")) {

                    AsyncHttpClient client = new AsyncHttpClient();
                    RequestParams params = new RequestParams();
                    params.put("type", "acceptchatrequest");
                    params.put("channel", "patient"); // receiver channel
                    params.put("channel_id", getSharedPreferences(MY_PREFS_COMMON_PARAMS_DOCTOR_CHAT, MODE_PRIVATE).getInt("doctor_channel_id", 0)); // doc channel id
                    params.put("userid", getSharedPreferences(MY_PREFS_COMMON_PARAMS_DOCTOR, MODE_PRIVATE).getInt("doctor_id", 0)); // doc userid
                    params.put("toChannel_id", patient_channelId); // patient channel id
                    params.put("nick", getSharedPreferences(MY_PREFS_COMMON_PARAMS_DOCTOR_CHAT, MODE_PRIVATE).getString("doc_nick_name", null)); // doctor patName
                    params.put("apt_id", apt_id); // request id
                    params.put("fromuserid", getSharedPreferences(MY_PREFS_COMMON_PARAMS_DOCTOR, MODE_PRIVATE).getInt("doctor_id", 0)); //doc userid
                    params.put("touserid", patientUserId); // patient user id
                    client.addHeader("Content-Type", "application/x-www-form-urlencoded");
                    client.post(URL_CHAT, params, new JsonHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);
                            String jsonResponse = response.toString();
                            Log.d("jsonchatrequest2", jsonResponse);
                            String jsonStatus = response.optString("success");
                            if (jsonStatus.equals("1")) {
                                //Toast.makeText(DoctorMainActivity.this, "Doctor Accepted Chat Request", Toast.LENGTH_SHORT).show();
                                // go to my chats
                                // store patientchannelid in
                                Intent intent = new Intent(getApplicationContext(), DoctorChatRoom.class);
                                intent.putExtra("patient_userId", patientUserId);
                                intent.putExtra("patient_channelId", patient_channelId);
                                intent.putExtra("apt_id", apt_id);
                                intent.putExtra("patientNickName", patientNickName);
                                startActivity(intent);


                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                        }
                    });

                }

            }
        });


    }

    protected void goToHomeFragment() {

        getSupportFragmentManager().beginTransaction().replace(R.id.doctor_fragment_container, new DoctorHomepage()).commit();
    }

    protected void goToHomeAppointment() {

        getSupportFragmentManager().beginTransaction().replace(R.id.doctor_fragment_container, new DoctorAllBookings()).commit();
    }

    protected void goToHomeWallet() {

        getSupportFragmentManager().beginTransaction().replace(R.id.doctor_fragment_container, new DoctorWalletFragment()).commit();
    }

    protected void goToHomeSupport() {

        getSupportFragmentManager().beginTransaction().replace(R.id.doctor_fragment_container, new Support()).commit();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.doc_imageButton_home_bottom:
                goToHomeFragment();
                break;

            case R.id.doc_imageButton_appointment_bottom:
                goToHomeAppointment();
                break;
            case R.id.doc_imageButton_wallet_bottom:
                goToHomeWallet();
                break;
            case R.id.doc_imageButton_support_bottom:
                goToHomeSupport();
                break;
        }

    }


    @Override
    public void onStartFailed(SinchError error) {
        //Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show();
        Log.d("sinch_error_Doclogin", error.toString());
        if (mSpinner != null) {
            mSpinner.dismiss();
        }
    }

    @Override
    public void onStarted() {

    }

    private void loginClicked() {
        sinchUsername = getSharedPreferences(MY_PREFS_COMMON_PARAMS_DOCTOR, MODE_PRIVATE).getString("doc_username", "User Name");
        try {
            if (((getSinchServiceInterface() == null) || (!getSinchServiceInterface().isStarted()))) {
                getSinchServiceInterface().startClient(sinchUsername);
                Log.d("sinch_Doclogin_name", sinchUsername);
                //showSpinner();
                //Toast.makeText(this, "Doctor Online!", Toast.LENGTH_SHORT).show();
            } else {
                //Toast.makeText(this, "Doctor Online!", Toast.LENGTH_SHORT).show();
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

    private void showSpinner() {
        mSpinner = new ProgressDialog(this);
        mSpinner.setTitle("Logging in");
        mSpinner.setMessage("Please wait...");
        mSpinner.show();
    }

}
