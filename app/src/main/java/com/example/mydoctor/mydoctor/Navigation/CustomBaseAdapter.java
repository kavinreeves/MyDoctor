package com.example.mydoctor.mydoctor.Navigation;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.mydoctor.mydoctor.R;

import java.util.List;

/**
 * Created by Wenso on 02-Mar-17.
 */

public class CustomBaseAdapter extends BaseAdapter {

    Context context;
    List<WalletItem> rowItems;

    public CustomBaseAdapter(Context context, List<WalletItem> rowItems) {
        this.context = context;
        this.rowItems = rowItems;
    }

    /*private view holder class*/
    private class ViewHolder {

        TextView time, refId, desc, amount, balance, status;

    }

    @Override
    public int getCount() {
        return rowItems.size();
    }   // return 0

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        LayoutInflater mInflater = (LayoutInflater)
                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (view == null) {
            view = mInflater.inflate(R.layout.wallet_list_item, null);
            holder = new ViewHolder();
            holder.desc = (TextView) view.findViewById(R.id.user_wallet_desc);
            holder.time = (TextView) view.findViewById(R.id.user_wallet_time);
            holder.amount = (TextView) view.findViewById(R.id.user_wallet_amount);
            holder.balance = (TextView) view.findViewById(R.id.user_wallet_balance);
            holder.refId = (TextView) view.findViewById(R.id.user_wallet_refId);
            holder.status = (TextView) view.findViewById(R.id.user_wallet_status);
            view.setTag(holder);
        }
        else {
            holder = (ViewHolder) view.getTag();
        }

        WalletItem rowItem = (WalletItem) getItem(position);

        holder.desc.setText(rowItem.getDesc());
        holder.time.setText(rowItem.getTime());
        holder.amount.setText(rowItem.getAmount());
        holder.balance.setText(rowItem.getBalance());
        holder.refId.setText(rowItem.getRefId());
        holder.status.setText(rowItem.getStatus());


        return view;

    }
}
