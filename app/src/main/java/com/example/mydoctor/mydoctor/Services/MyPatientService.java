package com.example.mydoctor.mydoctor.Services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.Header;

import static com.example.mydoctor.mydoctor.Login.CommonParams.MY_PREFS_COMMON_PARAMS_PATIENT;
import static com.example.mydoctor.mydoctor.Login.CommonParams.MY_PREFS_COMMON_PARAMS_PATIENT_CHAT;
import static com.example.mydoctor.mydoctor.Login.CommonParams.URL_CHAT;

/**
 * Created by Wenso on 28-Feb-17.
 */

public class MyPatientService extends Service {


    private Timer timer;
    private android.os.Handler handler;

    private static String TAG = MyDoctorService.class.getSimpleName();

    public boolean isRunning = true;

    private int patient_channel_id, patient_user_id;
    private String patient_username;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        //Toast.makeText(this, "Patient Service Started", Toast.LENGTH_LONG).show();
        // get all params values
        //patient_channel_id = getSharedPreferences(MY_PREFS_COMMON_PARAMS_PATIENT_CHAT, MODE_PRIVATE).getInt("patient_channel_id", 0);
        patient_user_id = getSharedPreferences(MY_PREFS_COMMON_PARAMS_PATIENT, MODE_PRIVATE).getInt("patient_id", 0);
        patient_username = getSharedPreferences(MY_PREFS_COMMON_PARAMS_PATIENT, MODE_PRIVATE).getString("pusername", "null");

        Log.d("SERVICE_pat_usrID", String.valueOf(patient_user_id));
        Log.d("SERVICE_pat_usrName", patient_username);


        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
                                      @Override
                                      public void run() {

                                          patient_channel_id = getSharedPreferences(MY_PREFS_COMMON_PARAMS_PATIENT_CHAT, MODE_PRIVATE).getInt("patient_channel_id", 0);
                                          Log.d("SERVICE_pat_chID", String.valueOf(patient_channel_id));

                                          if (patient_channel_id !=0) {
                                              callService();
                                          }


                                          if(!isRunning){
                                              timer.cancel();
                                          }
                                      }
                                  }, 0, 1000


        );
        return START_STICKY;
    }



    @Override
    public void onDestroy() {

        isRunning = false;
        super.onDestroy();
        //Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }

    private void callService() {
        SyncHttpClient client = new SyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("type", "receive");
        params.put("channel", "patient"); // sender channel
        params.put("channel_id", patient_channel_id); // patient channelid
        params.put("userid", patient_user_id); // sender userid
        params.put("username", patient_username); // sender username
        client.addHeader("Content-Type", "application/x-www-form-urlencoded");
        client.post(URL_CHAT, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                String jsonResponse = response.toString();
                Log.d("json_patient_service", jsonResponse);
                String jsonStatus = response.optString("success");
                JSONObject notif_data = response.optJSONObject("notif_data");

                Log.d("notif_data", String.valueOf(notif_data));
                // only if the success is "1"
                if(jsonStatus.equals("1")){
                    //
                    if(response.optJSONObject("notif_data") != null){

                        String notificationType = notif_data.optString("type");
                        Log.d("notificationType", notificationType);

                        if(notificationType.equals("acceptchatrequest")){

                            // broadcast the value

                            Intent broadcastIntent = new Intent();
                            broadcastIntent.setAction("ChatRequestAccepted");

                            broadcastIntent.putExtra("json", response.optString("notif_data"));
                            broadcastIntent.putExtra("apt_id", notif_data.optInt("apt_id"));
                            broadcastIntent.putExtra("fromId", notif_data.optInt("fromId"));
                            broadcastIntent.putExtra("fromuserid", notif_data.optInt("fromuserid"));
                            broadcastIntent.putExtra("doctor_nickName", notif_data.optString("nick"));
                            sendBroadcast(broadcastIntent);

                        } else if (notificationType.equals("message")){
                            // broadcast to the chatroom
                            Intent broadcastMessage = new Intent();
                            broadcastMessage.setAction("PatientChatMessages");
                            broadcastMessage.putExtra("jsonChatMessages", response.optString("notif_data"));

                            sendBroadcast(broadcastMessage);

                        }else if (notificationType.equals("deliveryreport")){


                        }else if (notificationType.equals("readreceipt")){


                        }else if (notificationType.equals("typing")){

                            Log.d("typetypeing", "true");

                            Intent broadcastMessage = new Intent();
                            broadcastMessage.setAction("PatientChatMessages");
                            broadcastMessage.putExtra("jsonChatMessages", response.optString("notif_data"));
                            //broadcastMessage.putExtra("typingStatus", notif_data.optString("type"));
                            sendBroadcast(broadcastMessage);

                        }else if (notificationType.equals("typingremove")){
                            Intent broadcastMessage = new Intent();
                            broadcastMessage.setAction("PatientChatMessages");
                            broadcastMessage.putExtra("jsonChatMessages", response.optString("notif_data"));
                            sendBroadcast(broadcastMessage);

                        }else if (notificationType.equals("cancelappointment")) {
                            Intent broadcastMessage = new Intent();
                            broadcastMessage.setAction("AppointmentsUpdate");
                            broadcastMessage.putExtra("jsonAptMessages", response.optString("notif_data"));
                            sendBroadcast(broadcastMessage);
                        }
                    }


                }




            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                //Log.e("httpfailurejson", errorResponse.toString());
            }
        });
    }

}
