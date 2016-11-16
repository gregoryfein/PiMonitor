package com.gfein.pimonitor.model.stat;

import android.util.Log;

import org.json.JSONArray;

public class VirtualRAM {
    //svmem(total=10367352832, available=6472179712, percent=37.6, used=8186245120, free=2181107712, active=4748992512, inactive=2758115328, buffers=790724608, cached=3500347392, shared=787554304)

    static final String LOGGING_TAG = "VirtualRAM";

    protected long mTotal;
    public long getTotal() { return mTotal; }

    protected long mAvailable;
    public long getAvailable() { return mAvailable; }

    protected double mPercent;
    public double getPercent() { return mPercent; }

    protected long mUsed;
    public long getUsed() { return mUsed; }

    protected long mFree;
    public long getFree() { return mFree; }

    public VirtualRAM(JSONArray json) {
        try {

            mTotal = json.getLong(0);
            mAvailable = json.getLong(1);
            mPercent = json.getDouble(2);
            mUsed = json.getLong(3);
            mFree = json.getLong(4);
        }
        catch (Exception ex) {
            Log.e(LOGGING_TAG, "Failed parsing virtual ram");
        }

    }
}
