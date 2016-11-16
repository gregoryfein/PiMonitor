package com.gfein.pimonitor.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gfein.pimonitor.R;
import com.gfein.pimonitor.base.BaseFragment;
import com.gfein.pimonitor.model.stat.Disk;
import com.gfein.pimonitor.model.stat.PiStat;
import com.gfein.pimonitor.model.stat.Voltage;

public class StatFragment extends BaseFragment {

    //region Variables
    private MainActivity mParent;

    private View mRootView;
    private CPUViewController mCPU1, mCPU2, mCPU3, mCPU4;
    private CPUViewController mRAM1, mRAM2;
    private TextView mRam1Available, mRam1Used, mRam1Total;
    private TextView mRam2Available, mRam2Used, mRam2Total;
    private TextView mTemperature, mUptime;
    private TextView mVoltageCore, mVoltageRAM;
    private View mTempColor;
    private TextView mDiskUsed;

    private CPUViewController mWifiBar;
    private TextView mWifiESSID, mWifiSignal, mWifiEncryption, mWifiAddress, mWifiFrequency, mWifiChannel, mWifiMode;

    private PiStat mStats;
    //endregion

    //region Constructor
    public static StatFragment newInstance(MainActivity activity) {
        StatFragment frag = new StatFragment();
        frag.mParent = activity;
        return frag;
    }
    //endregion

    //region Lifecycle

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_stat, container, false);
        findViews();
        update(null);
        return mRootView;
    }

    private void findViews() {
        mCPU1 = new CPUViewController(mParent, mRootView.findViewById(R.id.cpu_1));
        mCPU2 = new CPUViewController(mParent, mRootView.findViewById(R.id.cpu_2));
        mCPU3 = new CPUViewController(mParent, mRootView.findViewById(R.id.cpu_3));
        mCPU4 = new CPUViewController(mParent, mRootView.findViewById(R.id.cpu_4));

        mRAM1 = new CPUViewController(mParent, mRootView.findViewById(R.id.ram_1_bar), "Physical RAM");
        mRam1Available = (TextView) mRootView.findViewById(R.id.ram_1_available);
        mRam1Total = (TextView) mRootView.findViewById(R.id.ram_1_total);
        mRam1Used = (TextView) mRootView.findViewById(R.id.ram_1_used);

        mRAM2 = new CPUViewController(mParent, mRootView.findViewById(R.id.ram_2_bar), "Swap RAM");
        mRam2Available = (TextView) mRootView.findViewById(R.id.ram_2_available);
        mRam2Total = (TextView) mRootView.findViewById(R.id.ram_2_total);
        mRam2Used = (TextView) mRootView.findViewById(R.id.ram_2_used);

        mDiskUsed = (TextView) mRootView.findViewById(R.id.disk_used);
        mVoltageCore = (TextView) mRootView.findViewById(R.id.voltage_core);
        mVoltageRAM = (TextView) mRootView.findViewById(R.id.voltage_ram);

        mWifiBar = new CPUViewController(mParent, mRootView.findViewById(R.id.wifi_bar));
        mWifiBar.setAscending(false);
        mWifiESSID = (TextView) mRootView.findViewById(R.id.wifi_essid);
        mWifiAddress = (TextView) mRootView.findViewById(R.id.wifi_address);
        mWifiChannel = (TextView) mRootView.findViewById(R.id.wifi_channel);
        mWifiEncryption = (TextView) mRootView.findViewById(R.id.wifi_encryption);
        mWifiFrequency = (TextView) mRootView.findViewById(R.id.wifi_frequency);
        mWifiMode = (TextView) mRootView.findViewById(R.id.wifi_mode);
        mWifiSignal = (TextView) mRootView.findViewById(R.id.wifi_signal);

        mUptime = (TextView) mRootView.findViewById(R.id.uptime);
        mTemperature = (TextView) mRootView.findViewById(R.id.temperature);
        mTempColor = mRootView.findViewById(R.id.temp_circle);
    }

    @Override
    public void onResume() {
        super.onResume();
        mStats = null;
        update(null);
    }
    //endregion

    //region Update
    public void update(PiStat stat) {
        if (stat == null) {
            updateNull();
            return;
        }

        mStats = stat;
        try {
            mCPU1.update(mStats.getCPUDetails()[0]);
            mCPU2.update(mStats.getCPUDetails()[1]);
            mCPU3.update(mStats.getCPUDetails()[2]);
            mCPU4.update(mStats.getCPUDetails()[3]);
        } catch (Exception ex) {
            mCPU1.update(0);
            mCPU2.update(0);
            mCPU3.update(0);
            mCPU4.update(0);
        }

        if (TextUtils.isEmpty(mStats.getTemp()))
            mTemperature.setText("No Value");
        else {
            double fah = (Double.parseDouble(mStats.getTemp()) * (9.0/5.0)) + 32;
            mTemperature.setText(mStats.getTemp() + (char) 0x00B0  + "C / " + DECIMAL_FORMATTER.format(fah) + (char) 0x00B0  + "F");
            double val = Double.parseDouble(mStats.getTemp());
            if(val < 55) {
                mTempColor.setBackgroundResource(R.drawable.circle_green);
            }
            else if(val < 70) {
                mTempColor.setBackgroundResource(R.drawable.circle_yellow);
            }
            else {
                mTempColor.setBackgroundResource(R.drawable.circle_red);
            }
        }

        mRAM1.update(mStats.getVirtualRam().getPercent());
        mRam1Available.setText(bytesToStr(mStats.getVirtualRam().getAvailable()));
        mRam1Used.setText(bytesToStr(mStats.getVirtualRam().getUsed()));
        mRam1Total.setText(bytesToStr(mStats.getVirtualRam().getAvailable() + mStats.getVirtualRam().getUsed()));

        mRAM2.update(mStats.getSwapRam().getPercent());
        mRam2Available.setText(bytesToStr(mStats.getSwapRam().getFree()));
        mRam2Used.setText(bytesToStr(mStats.getSwapRam().getUsed()));
        mRam2Total.setText(bytesToStr(mStats.getSwapRam().getFree() + mStats.getSwapRam().getUsed()));

        Disk d = mStats.getDisk();
        mDiskUsed.setText(d.getUsed() + " / " + d.getTotal() + " (" + d.getPercent() + ")");

        Voltage v = mStats.getVoltage();
        mVoltageCore.setText(v.getmCore());
        mVoltageRAM.setText(v.getmRAM_C() + " / " + v.getmRAM_I() + " / " + v.getmRAM_P());

        mWifiBar.setName("Quality");
        mWifiBar.update(mStats.getWiFi().getQualityPercent(), mStats.getWiFi().getQuality());
        mWifiESSID.setText(mStats.getWiFi().getESSID());
        mWifiAddress.setText(mStats.getWiFi().getAddress());
        mWifiMode.setText(mStats.getWiFi().getMode());
        mWifiSignal.setText(mStats.getWiFi().getSignal());
        mWifiFrequency.setText(mStats.getWiFi().getFrequency());
        mWifiChannel.setText(mStats.getWiFi().getChannel());
        mWifiEncryption.setText(mStats.getWiFi().getEncryption().toUpperCase());

        parseUptime((int) mStats.getUptime());
    }

    @Override
    public void updateNull() {
        mUptime.setText(BLANK);
        mTemperature.setText(BLANK);
        mTempColor.setBackgroundResource(R.drawable.circle_gray);
        mDiskUsed.setText(BLANK);
        mVoltageRAM.setText(BLANK);
        mVoltageCore.setText(BLANK);

        mCPU1.update(0);
        mCPU2.update(0);
        mCPU3.update(0);
        mCPU4.update(0);

        mRAM1.update(0);
        mRam1Available.setText(BLANK);
        mRam1Total.setText(BLANK);
        mRam1Used.setText(BLANK);

        mRAM2.update(0);
        mRam2Available.setText(BLANK);
        mRam2Total.setText(BLANK);
        mRam2Used.setText(BLANK);

        mWifiBar.setName(BLANK);
        mWifiBar.update(0);
        mWifiAddress.setText(BLANK);
        mWifiMode.setText(BLANK);
        mWifiSignal.setText(BLANK);
        mWifiFrequency.setText(BLANK);
        mWifiChannel.setText(BLANK);
        mWifiEncryption.setText(BLANK);
        mWifiESSID.setText(BLANK);
    }

    private void parseUptime(int input) {
        int numberOfDays;
        int numberOfHours;
        int numberOfMinutes;
        int numberOfSeconds;

        numberOfDays = input / 86400;
        numberOfHours = (input % 86400) / 3600;
        numberOfMinutes = ((input % 86400) % 3600) / 60;
        numberOfSeconds = ((input % 86400) % 3600) % 60;

        String text = "";
        if (numberOfDays > 0)
            text += numberOfDays + "d ";
        if (numberOfHours > 0)
            text += numberOfHours + "h ";
        if (numberOfMinutes > 0)
            text += numberOfMinutes + "m ";
        text += numberOfSeconds + "s";
        mUptime.setText(text);
    }
    //endregion
}
