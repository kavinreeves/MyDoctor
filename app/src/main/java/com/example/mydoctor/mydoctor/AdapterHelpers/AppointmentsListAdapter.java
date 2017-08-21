package com.example.mydoctor.mydoctor.AdapterHelpers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mydoctor.mydoctor.Login.Splash_Screen;
import com.example.mydoctor.mydoctor.R;
import com.example.mydoctor.mydoctor.Utils.ProgressBarUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

import static android.content.Context.MODE_PRIVATE;
import static com.example.mydoctor.mydoctor.Login.CommonParams.MY_PREFS_COMMON_PARAMS;
import static com.example.mydoctor.mydoctor.Login.CommonParams.MY_PREFS_COMMON_PARAMS_DOCTOR;
import static com.example.mydoctor.mydoctor.Login.CommonParams.MY_PREFS_COMMON_PARAMS_DOCTOR_CHAT;
import static com.example.mydoctor.mydoctor.Login.CommonParams.URL_DOCTOR_OPERATION;
import static com.example.mydoctor.mydoctor.Login.CommonParams.deviceOs;
import static com.example.mydoctor.mydoctor.Login.CommonParams.sha;

/**
 * Created by Wenso on 14-Apr-17.
 */

public class AppointmentsListAdapter extends RecyclerView.Adapter<AppointmentsListAdapter.MyViewHolder> {

    private Context context;
    private String patientUsername, appt_type;
    private String apt_id, patientId, fromChannelId;

    private View progressOverlay;

    private List<DoctorAppointmentsInfo> appointmentsList;

    public AppointmentsListAdapter(List<DoctorAppointmentsInfo> appointmentsList) {
        this.appointmentsList = appointmentsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        context = parent.getContext();

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.doctor_appointment_notifications_single, parent, false);

        return new AppointmentsListAdapter.MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        //create a AppointmentList object
        // params

        DoctorAppointmentsInfo appointmentsInfo = appointmentsList.get(position);
        holder.name.setText(appointmentsInfo.getName());
        holder.time.setText(appointmentsInfo.getTime());
        //holder.aptType.setText(appointmentsInfo.getApt_type());
        holder.city.setText(appointmentsInfo.getCity());
        holder.ageGender.setText(appointmentsInfo.getSex() + " / " + appointmentsInfo.getAge());
        //
        apt_id = appointmentsInfo.getAptId();
        patientId = appointmentsInfo.getPatientId();
        patientUsername = appointmentsInfo.getPatUsername();
        appt_type = appointmentsInfo.getApt_type();

        if (appt_type.equals("direct")) {
            holder.imageView.setBackgroundResource(R.drawable.face_face_approval);

        } else if (appt_type.equals("chat")) {
            holder.imageView.setBackgroundResource(R.drawable.chat_approval);
        } else if (appt_type.equals("video")) {
            holder.imageView.setBackgroundResource(R.drawable.video_approval);
        }


    }


    @Override
    public int getItemCount() {
        return appointmentsList.size();
    }


    // write this viewholder

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name, time, aptType, city, ageGender;
        Button accept, decline;
        public ImageView imageView;

        public MyViewHolder(View view) {
            super(view);
            //
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(view.getContext(), "View Touched", Toast.LENGTH_SHORT).show();
                    showPatientProfile();
                }
            }); // invokes the onclick method
            time = (TextView) view.findViewById(R.id.tv_doc_app_patTime);
            ageGender = (TextView) view.findViewById(R.id.tv_user_age_gender);
            name = (TextView) view.findViewById(R.id.tv_user_app_patName);
            aptType = (TextView) view.findViewById(R.id.tv_user_apt_type);
            aptType.setVisibility(View.GONE); // del later
            city = (TextView) view.findViewById(R.id.tv_doc_app_patCity);
            accept = (Button) view.findViewById(R.id.button_accept_apt);
            decline = (Button) view.findViewById(R.id.button_decline_apt);
            imageView = (ImageView) view.findViewById(R.id.appt_all_notif_imageView);

            progressOverlay = view.findViewById(R.id.progress_overlay);

            accept.setOnClickListener(this);
            decline.setOnClickListener(this);
        }

        private void showPatientProfile() {
            // required params to view patient profile

        }

        @Override
        public void onClick(View view) {

            switch (view.getId()) {

                case R.id.button_accept_apt:
                    acceptAppointment();
                    break;
                case R.id.button_decline_apt:
                    declineAppointment();
                    break;
            }

        }

        private void acceptAppointment() {
            ProgressBarUtils.animateView(progressOverlay, View.VISIBLE, 0.4f, 200);
            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            params.put("type", "confirmappointment");
            params.put("id", context.getSharedPreferences(MY_PREFS_COMMON_PARAMS_DOCTOR, MODE_PRIVATE).getInt("doctor_id", 0));
            params.put("patient_id", patientId);
            params.put("doctor_username", context.getSharedPreferences(MY_PREFS_COMMON_PARAMS_DOCTOR, MODE_PRIVATE).getString("doc_username", "User Name"));
            params.put("patient_username", patientUsername);
            params.put("fromChannel_id", context.getSharedPreferences(MY_PREFS_COMMON_PARAMS_DOCTOR_CHAT, MODE_PRIVATE).getInt("doctor_channel_id", 0));
            params.put("apt_id", apt_id);
            params.put("apt_type", appt_type);
            params.put("device", deviceOs);
            params.put("version", context.getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("AppVersion", null));
            params.put("ipaddress", new Splash_Screen().getLocalIpAddress());
            params.put("udid", context.getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("UDID", null));
            params.put("sha", sha);
            Log.d("allcheckAccept", patientId + " " + patientUsername + " " + apt_id + " " + appt_type);
            client.post(URL_DOCTOR_OPERATION, params, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    Log.d("doc_confirm_notif", response.toString());

                    switch (response.optInt("success")) {
                        case 1:
                            // get the success response
                            removeAt(getAdapterPosition());
                            Toast.makeText(context, "Accepted!", Toast.LENGTH_SHORT).show();
                            ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);

                            break;
                        default:
                            ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
                            break;


                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
                }
            });

        }

        private void declineAppointment() {
            ProgressBarUtils.animateView(progressOverlay, View.VISIBLE, 0.4f, 200);
            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            params.put("type", "cancelappointment");
            params.put("id", context.getSharedPreferences(MY_PREFS_COMMON_PARAMS_DOCTOR, MODE_PRIVATE).getInt("doctor_id", 0));
            params.put("patient_id", patientId);
            params.put("doctor_username", context.getSharedPreferences(MY_PREFS_COMMON_PARAMS_DOCTOR, MODE_PRIVATE).getString("doc_username", "User Name"));
            params.put("patient_username", patientUsername);
            params.put("fromChannel_id", context.getSharedPreferences(MY_PREFS_COMMON_PARAMS_DOCTOR_CHAT, MODE_PRIVATE).getInt("doctor_channel_id", 0));
            params.put("apt_id", apt_id);
            params.put("apt_type", appt_type);
            params.put("device", deviceOs);
            params.put("version", context.getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("AppVersion", null));
            params.put("ipaddress", new Splash_Screen().getLocalIpAddress());
            params.put("udid", context.getSharedPreferences(MY_PREFS_COMMON_PARAMS, MODE_PRIVATE).getString("UDID", null));
            params.put("sha", sha);
            Log.d("allcheckDecline", patientId + " " + patientUsername + " " + apt_id + " " + appt_type);
            client.post(URL_DOCTOR_OPERATION, params, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    Log.d("doc_decline_notif", response.toString());

                    switch (response.optInt("success")) {
                        case 1:
                            // get the success response
                            removeAt(getAdapterPosition());
                            Toast.makeText(context, "Cancelled!", Toast.LENGTH_SHORT).show();
                            ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
                            break;
                        case 4:

                            // get the success response
                            //removeAt(getAdapterPosition());
                            Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show();
                            ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
                            break;

                        default:
                            ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
                            Toast.makeText(context, "Network Error!", Toast.LENGTH_SHORT).show();
                            break;

                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    ProgressBarUtils.animateView(progressOverlay, View.GONE, 0.4f, 200);
                }
            });

        }
    }


    public DoctorAppointmentsInfo getItem(int position) {

        return appointmentsList.get(position);
    }

    public void removeAt(int position) {
        appointmentsList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, appointmentsList.size());
        notifyDataSetChanged();
    }
}
