package com.gfein.pimonitor.model.config;

import android.util.Log;

import org.json.JSONObject;

public class PiConfig {

    //region Variables
    private static final String LOGGING_TAG = "PiConfig";

    private Voltage mVolt;
    public Voltage getVolt() { return mVolt; }

    private Frequency mFreq;
    public Frequency getFreq() { return mFreq; }

    private Misc mMisc;
    public Misc getMisc() { return mMisc; }

    private Clock mClock;
    public Clock getClock() { return mClock; }

    private Split mSplit;
    public Split getSplit() { return mSplit; }

    private Overscan mOverscan;
    public Overscan getOverscan() { return mOverscan; }
    //endregion

    //region Constructors

    public PiConfig(String body) {
        try {
            setup(new JSONObject(body));
        }
        catch(Exception ex) {
            Log.e(LOGGING_TAG, "Failed to construct piconfig: " + ex.getMessage());
        }
    }

    public PiConfig(JSONObject root) {
        setup(root);
    }

    private void setup(JSONObject root) {
        try {
            JSONObject json = root.getJSONObject("config");

            mOverscan = new Overscan();
            mOverscan.mScale = json.optString("overscan_scale");
            mOverscan.mLeft = json.optString("overscan_left");
            mOverscan.mRight = json.optString("overscan_right");
            mOverscan.mBottom = json.optString("overscan_bottom");
            mOverscan.mTop = json.optString("overscan_top");

            mVolt = new Voltage();
            mVolt.mVoltage = json.optString("over_voltage");
            mVolt.mSDRAM_C = json.optString("over_voltage_sdram_c");
            mVolt.mSDRAM_P = json.optString("over_voltage_sdram_p");
            mVolt.mAVS = json.optString("over_voltage_avs");
            mVolt.mSDRAM_I = json.optString("over_voltage_sdram_i");

            mFreq = new Frequency();
            mFreq.mARM = json.optString("arm_freq");
            mFreq.mSDRAM = json.optString("sdram_freq");
            mFreq.mH264 = json.optString("h264_freq");
            mFreq.mDesiredOSC = json.optString("desired_osc_freq");
            mFreq.mV3D = json.optString("v3d_freq");
            mFreq.mGPU = json.optString("gpu_freq");
            mFreq.mCore = json.optString("core_freq");

            json = root.getJSONObject("split");
            mSplit = new Split();
            mSplit.mGPU = json.optString("gpu");
            mSplit.mCPU = json.optString("cpu");

            json = root.getJSONObject("clock");
            mClock = new Clock();
            mClock.mCore = json.optString("core");
            mClock.mHDMI = json.optString("hdmi");
            mClock.mEMMC = json.optString("emmc");
            mClock.mH264 = json.optString("h264");
            mClock.mISP = json.optString("isp");
            mClock.mPixel = json.optString("pixel");
            mClock.mUART = json.optString("uart");
            mClock.mV3D = json.optString("v3d");
            mClock.mVEC = json.optString("vec");
            mClock.mPWM = json.optString("pwm");
            mClock.mARM = json.optString("arm");
            mClock.mDPI = json.optString("dpi");

            mMisc = new Misc();
            mMisc.mHDMIForceCEC = json.optString("hdmi_force_cec_address");
            mMisc.mLCDFramerate = json.optString("lcd_framerate");
            mMisc.mFramebufferSwap = json.optString("framebuffer_swap");
            mMisc.mHDMIBoost = json.optString("config_hdmi_boost");
            mMisc.mForceEEROM = json.optString("force_eeprom_read");
            mMisc.mDisableCommandLineTags = json.optString("disable_commandline_tags");
            mMisc.mProgramSerialRandom = json.optString("program_serial_random");
            mMisc.mDisableL2 = json.optString("disable_l2cache");
            mMisc.mTempLimit = json.optString("temp_limit");
            mMisc.mFramebufferIgnoreAlpha = json.optString("framebuffer_ignore_alpha");
            mMisc.mSDRAMSchmoo = json.optString("sdram_schmoo");
            mMisc.mPauseBurstFrames = json.optString("pause_burst_frames");
            mMisc.mHDMIDrive = json.optString("hdmi_drive");
            mMisc.mInitUARTClock = json.optString("init_uart_clock");
            mMisc.mAudioPWMMode = json.optString("audio_pwm_mode");
            mMisc.mForcePWMOpen = json.optString("force_pwm_open");


        } catch (Exception ex) {
            Log.e(LOGGING_TAG, "Failed to parse: " + ex.getMessage());
        }
    }
    //endregion

    //region Helpers
    public boolean isEmpty() {
        return mClock == null ||
                mFreq == null ||
                mMisc == null ||
                mOverscan == null ||
                mSplit == null ||
                mVolt == null;
    }
    //endregion



    /*
    "config":{
      "force_pwm_open":"1",
      "arm_freq":"1350",
      "overscan_left":"48",
      "overscan_scale":"1",
      "sdram_freq":"575",
      "hdmi_force_cec_address":"65535",
      "lcd_framerate":"60",
      "framebuffer_swap":"1",
      "config_hdmi_boost":"5",
      "over_voltage_sdram_p":"6",
      "over_voltage_avs":"0x186a0",
      "force_eeprom_read":"1",
      "over_voltage_sdram_i":"4",
      "disable_commandline_tags":"2",
      "program_serial_random":"1",
      "disable_l2cache":"1",
      "h264_freq":"333",
      "over_voltage_sdram_c":"4",
      "desired_osc_freq":"0x36ee80",
      "temp_limit":"85",
      "framebuffer_ignore_alpha":"1",
      "v3d_freq":"500",
      "over_voltage":"6",
      "overscan_right":"48",
      "overscan_bottom":"48",
      "gpu_freq":"575",
      "core_freq":"500",
      "sdram_schmoo":"0x2000020",
      "pause_burst_frames":"1",
      "hdmi_drive":"2",
      "overscan_top":"48",
      "init_uart_clock":"0x2dc6c00",
      "audio_pwm_mode":"1"
   },
   "split":{
      "gpu":"256M",
      "cpu":"752M"
   },
   "clock":{
      "core":"500000000",
      "hdmi":"163683000",
      "emmc":"250000000",
      "h264":"333334000",
      "isp":"500000000",
      "pixel":"148500000",
      "uart":"47999000",
      "v3d":"500000000",
      "vec":"0",
      "pwm":"0",
      "arm":"1350000000",
      "dpi":"0"
   }
}
     */
}
