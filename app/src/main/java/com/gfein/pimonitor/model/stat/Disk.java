package com.gfein.pimonitor.model.stat;

import android.util.Log;

import org.json.JSONArray;

public class Disk {
    static final String LOGGING_TAG = "Disk";

    protected String mPercent;
    public String getPercent() { return mPercent; }

    protected String mAvailable;
    public String getAvailable() { return mAvailable; }

    protected String mUsed;
    public String getUsed() { return mUsed; }

    protected String mTotal;
    public String getTotal() {
        int a = Integer.valueOf(getUsed().replace("G", ""));
        int b = Integer.valueOf(getAvailable().replace("G", ""));
        return (a+b) + "G";
        //return mTotal;
    }

    public Disk(JSONArray json) {
        try {
            mTotal = json.getString(0);
            mUsed = json.getString(1);
            mAvailable = json.getString(2);
            mPercent = json.getString(3);
        }
        catch (Exception ex) {
            Log.e(LOGGING_TAG, "Failed to parse disk");
        }
    }
}
