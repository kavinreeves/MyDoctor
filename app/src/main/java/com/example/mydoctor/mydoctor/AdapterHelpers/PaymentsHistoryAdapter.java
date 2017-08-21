package com.example.mydoctor.mydoctor.AdapterHelpers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mydoctor.mydoctor.R;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.example.mydoctor.mydoctor.Login.CommonParams.MY_PREFS_COMMON_PARAMS_DOCTOR;
import static com.example.mydoctor.mydoctor.Login.CommonParams.MY_PREFS_COMMON_PARAMS_PATIENT;
import static com.example.mydoctor.mydoctor.Login.CommonParams.MY_PREFS_LOGINTYPE;

/**
 * Created by Wenso on 12-Apr-17.
 */

public class PaymentsHistoryAdapter extends RecyclerView.Adapter<PaymentsHistoryAdapter.MyViewHolder> {

    private List<PaymentsHistoryObject> paymentsHistoryList;
    Context context;

    public PaymentsHistoryAdapter(List<PaymentsHistoryObject> paymentsHistoryList) {
        this.paymentsHistoryList = paymentsHistoryList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.payments_history_single, parent, false);
        context = parent.getContext();
        return new PaymentsHistoryAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String currency;
        if(context.getSharedPreferences(MY_PREFS_LOGINTYPE, MODE_PRIVATE).getString("type", "").equals("patient")){
            currency = context.getSharedPreferences(MY_PREFS_COMMON_PARAMS_PATIENT, MODE_PRIVATE).getString("currency", "");
        }else currency = context.getSharedPreferences(MY_PREFS_COMMON_PARAMS_DOCTOR, MODE_PRIVATE).getString("currency", "");

        PaymentsHistoryObject object = paymentsHistoryList.get(position);
        String amount = object.getAmount();
        amount = amount.replace("-", "");

        holder.amount.setText("Amount: " +currency+" "+ amount);
        holder.Id.setText("Ref Id: " + object.getTransId());
        holder.time.setText(object.getDateTime());
        holder.description.setText(object.getDescription());

    }

    @Override
    public int getItemCount() {
        return paymentsHistoryList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView amount, time, description, Id;

        public MyViewHolder(View view) {
            super(view);
            //
            view.setOnClickListener(this); // invokes the onclick method
            amount = (TextView) view.findViewById(R.id.receipt_amount);
            Id = (TextView) view.findViewById(R.id.receipt_id);
            time = (TextView) view.findViewById(R.id.date_time_receipt);
            description = (TextView) view.findViewById(R.id.description_receipt);
        }

        @Override
        public void onClick(View view) {

        }
    }
}
