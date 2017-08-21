package com.example.mydoctor.mydoctor.AdapterHelpers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mydoctor.mydoctor.Navigation.WalletItem;
import com.example.mydoctor.mydoctor.R;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Wenso on 02-Mar-17.
 */

public class WalletRecyclerAdapter extends RecyclerView.Adapter<WalletRecyclerAdapter.MyViewHolder> {

    private List<WalletItem> walletList;
    private Context context;

    public WalletRecyclerAdapter(List<WalletItem> walletList) {
        this.walletList = walletList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.wallet_trans_history, parent, false);
        context = parent.getContext();
        return new WalletRecyclerAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        String dayTime;

        WalletItem walletItem = walletList.get(position);
        dayTime = walletItem.getTime();
        String[] splitStr = dayTime.split("\\s+");
        Log.d("splitString", Arrays.toString(splitStr));
        Log.d("splitMonth", splitStr[1]);
        Log.d("splitDay", splitStr[2]);
        if (splitStr[2].endsWith(",")) {
            splitStr[2] = splitStr[2].substring(0, splitStr[2].length() - 1);
        }
        Log.d("correctedSplitDay", splitStr[2]);

        holder.day.setText(splitStr[2]);
        holder.month.setText(splitStr[1]);
        holder.desc.setText(walletItem.getDesc());
        if (walletItem.getTransType().equals("debit")){
            holder.amount.setTextColor(context.getResources().getColor(R.color.app_red));
        }else holder.amount.setTextColor(context.getResources().getColor(R.color.app_green));
        holder.amount.setText(walletItem.getAmount());
        holder.refId.setText("Ref ID: "+walletItem.getRefId());




    }



    @Override
    public int getItemCount() {
        return walletList.size();
    }

    // write this viewholder

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView desc, day, amount, month, refId;

        public MyViewHolder(View view) {
            super(view);
            //
            view.setOnClickListener(this); // invokes the onclick method
            desc = (TextView) view.findViewById(R.id.walletHisDescTV);
            day = (TextView) view.findViewById(R.id.walletHistory_day);
            amount = (TextView) view.findViewById(R.id.walletHisAmountTV);
            month = (TextView) view.findViewById(R.id.walletHistory_month);
            refId = (TextView) view.findViewById(R.id.walletHisRefidTV);

        }

        @Override
        public void onClick(View view) {

        }
    }

    public WalletItem getItem(int position){

        return walletList.get(position);
    }
}
