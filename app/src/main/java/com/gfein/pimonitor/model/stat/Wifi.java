package com.gfein.pimonitor.model.stat;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONObject;

public class Wifi {

    static final String LOGGING_TAG = "Wifi";

    protected String mEncryption;
    public String getEncryption() {
        return mEncryption;
    }

    protected String mSignal;
    public String getSignal() {
        return check(mSignal);
    }

    protected String mESSID;
    public String getESSID() {
        return check(mESSID);
    }

    protected String mAddress;
    public String getAddress() {
        return check(mAddress);
    }
    protected String mFrequency;
    public String getFrequency() {
        return check(mFrequency);
    }

    protected String mQuality;
    public String getQuality() {
        return check(mQuality);
    }

    protected String mChannel;
    public String getChannel() {
        return check(mChannel);
    }

    protected String mMode;
    public String getMode() {
        return check(mMode);
    }

    protected String check(String val) {
        if(TextUtils.isEmpty(val))
            return "---";
        return val;
    }

    public double getQualityPercent() {
        double a = Double.valueOf(mQuality.split("/")[0]);
        double b = Double.valueOf(mQuality.split("/")[1]);
        return (a/b) * 100;
    }

    public Wifi(JSONObject json) {
        try {
            mEncryption = json.optString("encryption", "---");
            mSignal = json.optString("signal", "---");
            mESSID = json.optString("essid", "---");
            mAddress = json.optString("address", "---");
            mFrequency = json.optString("freq", "---");
            mQuality = json.optString("quality", "---");
            mChannel = json.optString("channel", "---");
            mMode = json.optString("mode", "---");
        }
        catch(Exception ex) {
            Log.e(LOGGING_TAG, "Failed to parse wifi: " + ex.getMessage());
        }
    }

    public String description() {
        StringBuffer sb = new StringBuffer();
        sb.append("mEncryption: " + mEncryption + "\n");
        sb.append("mSignal: " + mSignal + "\n");
        sb.append("mESSID: " + mESSID + "\n");
        sb.append("mAddress: " + mAddress + "\n");
        sb.append("mFrequency: " + mFrequency + "\n");
        sb.append("mQuality: " + mQuality + "\n");
        sb.append("mChannel: " + mChannel + "\n");
        sb.append("mMode: " + mMode);
        return sb.toString();
    }
}
