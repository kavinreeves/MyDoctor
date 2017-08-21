package com.example.mydoctor.mydoctor.AdapterHelpers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mydoctor.mydoctor.Doctor.WritePrescription;
import com.example.mydoctor.mydoctor.R;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.example.mydoctor.mydoctor.Login.CommonParams.MY_PREFS_LOGINTYPE;

/**
 * Created by Wenso on 12-Apr-17.
 */

public class PrescriptionAdapter extends RecyclerView.Adapter<PrescriptionAdapter.MyViewHolder> {

    private List<PrescriptionsListObject> prescriptionsList;
    private Context context;

    public PrescriptionAdapter(List<PrescriptionsListObject> prescriptionsList) {
        this.prescriptionsList = prescriptionsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.prescriptions_single, parent, false);

        context = parent.getContext();
        return new PrescriptionAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final String patient_id, prescription, patientName, date;
        final Context context = holder.time.getContext();
        // populate the binder view
        final PrescriptionsListObject object = prescriptionsList.get(position);
        holder.name.setText(object.getName());
        holder.aptId.setText("Apt Id: " + object.getAptId());
        holder.aptType.setText("Consultation: " + object.getApptType());
        holder.time.setText(object.getDateTime());


        patient_id = object.getPatient_id();
        patientName = object.getName();
        prescription = object.getPrescription();
        date = object.getDateTime();
       /* if (prescription.equals("")){
            holder.imageButton.setVisibility(View.INVISIBLE);
        }*/

        // get photo to imageview
        new DownloadImageTask(holder.photo).execute(object.getPhotoUrl());

        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String loginType = context.getSharedPreferences(MY_PREFS_LOGINTYPE, MODE_PRIVATE).getString("type", "null");

                // check if prescription already available and then choose one
                if (prescription.equals("") && loginType.equals("doctor")) {
                    Intent intent = new Intent(context, WritePrescription.class);
                    intent.putExtra("patient_id", patient_id);
                    intent.putExtra("apt_id", object.getAptId());
                    intent.putExtra("apt_type", object.getApptType());
                    intent.putExtra("patient_name", patientName);
                    intent.putExtra("apt_date", date);
                    context.startActivity(intent);

                } else if (!prescription.equals("") && loginType.equals("doctor")) {
                    // show the prescription
                    showPdf(prescription);

                } else if (!prescription.equals("") && loginType.equals("patient")) {
                    // show the prescription
                    showPdf(prescription);

                } else if (prescription.equals("") && loginType.equals("patient")) {
                    Toast.makeText(context, "Prescription not Found!", Toast.LENGTH_SHORT).show();
                }


            }
        });

    }

    private void showPdf(final String pdfUrl) {
       /* Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.fragment_prescription_full_view);
        WebView wb = (WebView) dialog.findViewById(R.id.webview_prescription);
        wb.getSettings().setJavaScriptEnabled(true);
        WebSettings webSettings = wb.getSettings();
        webSettings.setPluginState(WebSettings.PluginState.ON);
        wb.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url){

                view.loadUrl(pdfUrl);
                return false; // then it is not handled by default action
            }
        });


        wb.loadUrl((pdfUrl));
        dialog.setCancelable(true);
        dialog.setTitle("WebView");
        dialog.show();*/

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(pdfUrl));
        context.startActivity(browserIntent);
    }

    @Override
    public int getItemCount() {
        return prescriptionsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView name, time, aptType, aptId;
        public ImageView photo;
        public ImageButton imageButton;

        public MyViewHolder(View view) {
            super(view);
            //
            view.setOnClickListener(this); // invokes the onclick method
            name = (TextView) view.findViewById(R.id.name_prescriptionAll);
            time = (TextView) view.findViewById(R.id.date_time_prescriptionAll);
            aptId = (TextView) view.findViewById(R.id.apt_id_prescriptionAll);
            aptType = (TextView) view.findViewById(R.id.aptType_prescriptionAll);
            imageButton = (ImageButton) view.findViewById(R.id.add_prescription_doc);

            photo = (ImageView) view.findViewById(R.id.profile_image_prescAll);
        }

        @Override
        public void onClick(View view) {

        }
    }

    // method to get the selected recycler item by sending its position in the List
    public PrescriptionsListObject getItem(int position) {
        return prescriptionsList.get(position);
    }

}
