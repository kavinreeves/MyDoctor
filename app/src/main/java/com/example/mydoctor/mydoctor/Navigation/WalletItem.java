package com.example.mydoctor.mydoctor.Navigation;

/**
 * Created by Wenso on 02-Mar-17.
 */

public class WalletItem {
    String time, refId, desc, amount, balance, status, transType;

    public WalletItem(String time, String refId, String desc, String amount, String balance, String status, String transType) {
        this.time = time;
        this.refId = refId;
        this.desc = desc;
        this.amount = amount;
        this.balance = balance;
        this.status = status;
        this.transType = transType;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }
}
