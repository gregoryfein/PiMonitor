package com.gfein.pimonitor.model.stat;

import android.text.TextUtils;
import android.util.Log;
import android.util.StringBuilderPrinter;

import org.json.JSONArray;
import org.json.JSONObject;

public class PiStat {


    //region Variables
    static final String LOGGING_TAG = "PiStat";
    private double mUptime;
    public double getUptime() { return mUptime; }

    private VirtualRAM mVirtualRam;
    public VirtualRAM getVirtualRam() { return mVirtualRam; }

    private SwapRAM mSwapRam;
    public SwapRAM getSwapRam() { return mSwapRam; }

    private String mTemp;
    public String getTemp() { return mTemp; }

    private CPUDetail[] mCPUDetails;
    public CPUDetail[] getCPUDetails() { return mCPUDetails; }

    private Voltage mVoltage;
    public Voltage getVoltage() { return mVoltage; }

    private Disk mDisk;
    public Disk getDisk() { return mDisk; }

    private Wifi mWiFi;
    public Wifi getWiFi() { return mWiFi; }
    //endregion

    //region Constructor
    public PiStat(String body) {
        try {
            JSONObject json = new JSONObject(body);
            parse(json);
        }
        catch (Exception ex) {
            Log.e(LOGGING_TAG, "Failed to parse pistat");
        }
    }

    public PiStat(JSONObject json) {
        parse(json);
    }

    private void parse(JSONObject json) {
        try {
            mUptime = json.getDouble("uptime");
            mTemp = json.optString("temp", "---");

            mSwapRam = new SwapRAM(json.getJSONObject("ram").getJSONArray("swap"));
            mVirtualRam = new VirtualRAM(json.getJSONObject("ram").getJSONArray("virtual"));

            JSONArray cpu = json.getJSONArray("cpu");
            JSONArray percents = json.getJSONArray("cpu_percent");
            mCPUDetails = new CPUDetail[cpu.length()];
            for(int i = 0; i < cpu.length(); i++) {
                JSONArray arr = cpu.getJSONArray(i);
                CPUDetail detail = new CPUDetail("Core " + (i+1),
                        arr.getDouble(0),
                        arr.getDouble(1),
                        arr.getDouble(2),
                        percents.getDouble(i));
                mCPUDetails[i] = detail;
            }

            mVoltage = new Voltage(json.getJSONObject("volt"));
            mDisk = new Disk(json.getJSONArray("disk"));
            mWiFi = new Wifi(json.getJSONObject("wifi"));

        }
        catch(Exception ex) {
            Log.e(LOGGING_TAG, "Failed to parse: " + ex.getMessage());
        }
    }
    //endregion

    /*
    {
   "uptime":1470268800.0,
   "ram":{
      "swap":[
         2147483648,
         1522008064,
         625475584,
         70.9,
         259364794368,
         3068391424
      ],
      "virtual":[
         17179869184,
         3921952768,
         77.2,
         13773524992,
         1175769088,
         6146854912,
         2746183680,
         4880486400
      ]
   },
   "cpu":[
      547238.47,
      0.0,
      241859.37,
      9973445.78
   ],
   "temp":"",
   "cpu_percent":[
      29.2,
      29.4,
      29.2,
      29.8
   ]
}
     */
}
