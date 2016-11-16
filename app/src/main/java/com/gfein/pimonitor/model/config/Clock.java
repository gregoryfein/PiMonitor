package com.gfein.pimonitor.model.config;

public class Clock {
    protected String mDPI;
    public String getDPI() { return clean(mDPI); }

    protected String mARM;
    public String getARM() { return clean(mARM); }

    protected String mPWM;
    public String getPWM() { return clean(mPWM); }

    protected String mVEC;
    public String getVEC() { return clean(mVEC); }

    protected String mV3D;
    public String getV3D() { return clean(mV3D); }

    protected String mUART;
    public String getUART() { return clean(mUART); }

    protected String mPixel;
    public String getPixel() { return clean(mPixel); }

    protected String mISP;
    public String getISP() { return clean(mISP); }

    protected String mH264;
    public String getH264() { return clean(mH264); }

    protected String mEMMC;
    public String getEMMC() { return clean(mEMMC); }

    protected String mHDMI;
    public String getHDMI() { return clean(mHDMI); }

    protected String mCore;
    public String getCore() { return clean(mCore); }

    public String clean(String val) {
        int a = Integer.valueOf(val);

        if(a  > 0) {
            a /= 1000000;
            return a + " MHz";
        }
        return "---";
    }
}
