package com.example.mydoctor.mydoctor.AdapterHelpers;

/**
 * Created by Wenso on 12-Apr-17.
 */

public class PaymentsHistoryObject {

    String amount, transId, dateTime, description;

    public PaymentsHistoryObject(String amount, String transId, String dateTime, String description) {
        this.amount = amount;
        this.transId = transId;
        this.dateTime = dateTime;
        this.description = description;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
