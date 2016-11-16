package com.gfein.pimonitor.model.config;

public class Split {

    protected String mGPU;
    public String getGPU() { return mGPU; }

    protected String mCPU;
    public String getCPU() { return mCPU; }

    public int total() {
        return cpuInt() + gpuInt();
    }
    public int cpuInt() {
        return Integer.valueOf(mCPU.replace("M", "").replace("G", ""));
    }
    public int gpuInt() {
        return Integer.valueOf(mGPU.replace("M", "").replace("G", ""));
    }
}
