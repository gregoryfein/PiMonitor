package com.gfein.pimonitor.model.stat;

import android.util.Log;

import org.json.JSONObject;

public class Voltage {
    static final String LOGGING_TAG = "Voltage";

    protected String mCore;
    public String getmCore() { return mCore; }

    protected String mRAM_P;
    public String getmRAM_P() { return mRAM_P; }

    protected String mRAM_C;
    public String getmRAM_C() { return mRAM_C; }

    protected String mRAM_I;
    public String getmRAM_I() { return mRAM_I; }

    public Voltage(JSONObject json) {
        try {
            mCore = json.getString("core");
            mRAM_C = json.getString("sdram_c");
            mRAM_I = json.getString("sdram_i");
            mRAM_P = json.getString("sdram_p");
        }
        catch(Exception ex) {
            Log.e(LOGGING_TAG, "Couldn't parse Voltage");
        }
    }
}
