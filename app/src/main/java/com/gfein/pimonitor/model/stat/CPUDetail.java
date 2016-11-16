package com.gfein.pimonitor.model.stat;

public class CPUDetail {

    protected double mUser, mSystem, mIdle;
    public double getUser() { return mUser; }
    public double getSystem() { return mSystem; }
    public double getIdle() { return mIdle; }

    protected double mPercent;
    public double getPercent() { return mPercent; }

    protected String mName;
    public String getName() { return mName; }

    public CPUDetail(String name, double user, double system, double idle, double percent) {
        mName = name;
        mUser = user;
        mSystem = system;
        mIdle = idle;
        mPercent = percent;
    }
}
