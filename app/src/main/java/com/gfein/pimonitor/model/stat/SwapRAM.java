package com.gfein.pimonitor.model.stat;

import android.util.Log;

import org.json.JSONArray;

public class SwapRAM {

    //sswap(total=2097147904, used=296128512, free=1801019392, percent=14.1, sin=304193536, sout=677842944)
    static final String LOGGING_TAG = "SwapRAM";

    protected long mTotal;
    public long getTotal() { return mTotal; }

    protected long mUsed;
    public long getUsed() { return mUsed; }

    protected long mFree;
    public long getFree() { return mFree; }

    protected double mPercent;
    public double getPercent() { return mPercent; }

    public SwapRAM(JSONArray json) {
        try {
            mTotal = json.getLong(0);
            mUsed = json.getLong(1);
            mFree = json.getLong(2);
            mPercent = json.getDouble(3);
        } catch (Exception ex) {
            Log.e(LOGGING_TAG, "Failed parsing swap ram");
        }
    }
}
