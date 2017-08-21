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

import static com.example.mydoctor.mydoctor.Login.CommonParams.MY_PREFS_COMMON_PARAMS_DOCTOR;
import static com.example.mydoctor.mydoctor.Login.CommonParams.MY_PREFS_COMMON_PARAMS_DOCTOR_CHAT;
import static com.example.mydoctor.mydoctor.Login.CommonParams.URL_CHAT;

/**
 * Created by Wenso on 23-Feb-17.
 */

public class MyDoctorService extends Service {


    public Timer timer;
    private android.os.Handler handler;
    private String doctor_username;
    private int doc_channelId, doc_userId;
    private static String TAG = MyDoctorService.class.getSimpleName();

    public boolean isRunning = true;

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
        //Toast.makeText(this, "Doctor Service Started", Toast.LENGTH_LONG).show();
        //get all params
        doc_userId = getSharedPreferences(MY_PREFS_COMMON_PARAMS_DOCTOR, MODE_PRIVATE).getInt("doctor_id", 0);

        doctor_username = getSharedPreferences(MY_PREFS_COMMON_PARAMS_DOCTOR, MODE_PRIVATE).getString("doc_username", null);

        Log.d("SERVICE_doc_usrID", String.valueOf(doc_userId));
        Log.d("SERVICE_doc_usrName", doctor_username);

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
                                      @Override
                                      public void run() {
                                          doc_channelId = getSharedPreferences(MY_PREFS_COMMON_PARAMS_DOCTOR_CHAT, MODE_PRIVATE).getInt("doctor_channel_id", 0);
                                          Log.d("SERVICE_doc_chID", String.valueOf(doc_channelId));

                                          if (doc_channelId !=0) {
                                              callService();
                                          }
                                          if (!isRunning) {
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

        //Toast.makeText(this, "Doctor Service Destroyed", Toast.LENGTH_LONG).show();
    }

    private void callService() {
        SyncHttpClient client = new SyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("type", "receive");
        params.put("channel", "doctor"); // sender channel
        params.put("channel_id", doc_channelId); // for first time send 0 to get channel id
        params.put("userid", doc_userId); // sender userid
        params.put("username", doctor_username); // doctor username
        client.post(URL_CHAT, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                String jsonResponse = response.toString();
                Log.d("json_doctor_service", jsonResponse);
                String jsonStatus = response.optString("success");
                JSONObject notif_data = response.optJSONObject("notif_data");
                Log.d("notif_data", String.valueOf(notif_data));


                // only if the success is "1"
                if (jsonStatus.equals("1")) {
                    //
                    if (response.optJSONObject("notif_data") != null) {
                        String notificationType = notif_data.optString("type");
                        Log.d("notificationType", notificationType);

                        if (notificationType.equals("chatrequest")) {

                            // broadcast the value

                            Intent broadcastIntent = new Intent();
                            broadcastIntent.setAction("ChatRequest");
                            //broadcastIntent.putExtra("Accept Chat?", "Accept Chat?");
                            broadcastIntent.putExtra("json", response.optString("notif_data"));
                            broadcastIntent.putExtra("apt_id", notif_data.optInt("apt_id"));
                            broadcastIntent.putExtra("fromId", notif_data.optInt("fromId"));
                            broadcastIntent.putExtra("fromUserId", notif_data.optInt("fromuserid"));
                            broadcastIntent.putExtra("fromUserNick", notif_data.optString("nick"));

                            sendBroadcast(broadcastIntent);
                        } else if (notificationType.equals("message")) {
                            // always broadcast to the chatroom only
                            Intent chatMessages = new Intent();
                            chatMessages.setAction("ChatMessages");
                            chatMessages.putExtra("message", response.optString("notif_data"));

                            sendBroadcast(chatMessages);

                        } else if (notificationType.equals("typing")) {
                            // always broadcast to the chatroom only
                            Intent chatMessages = new Intent();
                            chatMessages.setAction("ChatMessages");
                            chatMessages.putExtra("message", response.optString("notif_data"));

                            sendBroadcast(chatMessages);
                        } else if (notificationType.equals("typingremove")) {
                            // always broadcast to the chatroom only
                            Intent chatMessages = new Intent();
                            chatMessages.setAction("ChatMessages");
                            chatMessages.putExtra("message", response.optString("notif_data"));

                            sendBroadcast(chatMessages);
                        } else if (notificationType.equals("bookappointment")) {

                            // broadcast the value
                            Intent broadcastIntent = new Intent();
                            broadcastIntent.setAction("AllRequests");
                            broadcastIntent.putExtra("apt_type", "face_face");
                            broadcastIntent.putExtra("text", notif_data.optString("text"));
                            sendBroadcast(broadcastIntent);
                        }
                        else if (notificationType.equals("bookchatappointment")) {

                            // broadcast the value
                            Intent broadcastIntent = new Intent();
                            broadcastIntent.setAction("AllRequests");
                            broadcastIntent.putExtra("apt_type", "face_face");
                            broadcastIntent.putExtra("text", notif_data.optString("text"));
                            sendBroadcast(broadcastIntent);
                        }
                        else if (notificationType.equals("bookvideoappointment")) {

                            // broadcast the value
                            Intent broadcastIntent = new Intent();
                            broadcastIntent.setAction("AllRequests");
                            broadcastIntent.putExtra("apt_type", "face_face");
                            broadcastIntent.putExtra("text", notif_data.optString("text"));
                            sendBroadcast(broadcastIntent);
                        }
                    }


                }


            }


            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

}
