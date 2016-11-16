package com.gfein.pimonitor.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gfein.pimonitor.R;
import com.gfein.pimonitor.base.BaseFragment;
import com.gfein.pimonitor.model.config.PiConfig;

public class ConfigFragment extends BaseFragment {


    //region Variables
    private MainActivity mParent;
    private PiConfig mConfig;

    private View mRootView;
    private TextView mFreqCPU, mFreqSDRAM, mFreqGPU, mFreqCore;

    private ProgressBar mSplitBar;
    private TextView mSplitCPU, mSplitGPU;

    private TextView mOverscanScale, mOverscanLeft, mOverscanRight, mOverscanTop, mOverscanBottom;

    private TextView mClockCore, mClockHDMI, mClockEMMC, mClockH264, mClockISP, mClockPixel, mClockUART, mClockV3D, mClockVEC, mClockPWM, mClockARM, mClockDPI;

    private Button mReload;
    //endregion

    //region Constructor

    public ConfigFragment() {
        // Required empty public constructor
    }

    public static ConfigFragment newInstance(MainActivity parent) {
        ConfigFragment fragment = new ConfigFragment();
        fragment.mParent = parent;
        return fragment;
    }
    //endregion

    //region Lifecycle

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_config, container, false);
        findViews();
        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mConfig = null;
        update(null);
    }

    private void findViews() {
        mFreqSDRAM = (TextView) mRootView.findViewById(R.id.freq_sdram);
        mFreqCore = (TextView) mRootView.findViewById(R.id.freq_core);
        mFreqGPU = (TextView) mRootView.findViewById(R.id.freq_gpu);
        mFreqCPU = (TextView) mRootView.findViewById(R.id.freq_arm);

        mSplitBar = (ProgressBar) mRootView.findViewById(R.id.split_bar);
        Drawable customDrawable= getResources().getDrawable(R.drawable.progressbar_custom);
        mSplitBar.setProgressDrawable(customDrawable);

        mSplitCPU = (TextView) mRootView.findViewById(R.id.split_cpu);
        mSplitGPU = (TextView) mRootView.findViewById(R.id.split_gpu);

        mOverscanScale = (TextView) mRootView.findViewById(R.id.overscan_scale);
        mOverscanBottom = (TextView) mRootView.findViewById(R.id.overscan_bottom);
        mOverscanLeft = (TextView) mRootView.findViewById(R.id.overscan_left);
        mOverscanRight = (TextView) mRootView.findViewById(R.id.overscan_right);
        mOverscanTop = (TextView) mRootView.findViewById(R.id.overscan_top);

        mClockARM = (TextView) mRootView.findViewById(R.id.clock_arm);
        mClockCore = (TextView) mRootView.findViewById(R.id.clock_core);
        mClockDPI = (TextView) mRootView.findViewById(R.id.clock_dpi);
        mClockEMMC = (TextView) mRootView.findViewById(R.id.clock_emmc);
        mClockH264 = (TextView) mRootView.findViewById(R.id.clock_h264);
        mClockHDMI = (TextView) mRootView.findViewById(R.id.clock_hdmi);
        mClockISP = (TextView) mRootView.findViewById(R.id.clock_isp);
        mClockPixel = (TextView) mRootView.findViewById(R.id.clock_pixel);
        mClockPWM = (TextView) mRootView.findViewById(R.id.clock_pwm);
        mClockUART = (TextView) mRootView.findViewById(R.id.clock_uart);
        mClockV3D = (TextView) mRootView.findViewById(R.id.clock_v3d);
        mClockVEC = (TextView) mRootView.findViewById(R.id.clock_vec);

        mReload = (Button) mRootView.findViewById(R.id.reload);
        mReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mParent.releaseConfig();
                update(null);
            }
        });
    }
    //endregion

    //region Update

    public void update(PiConfig config) {
        mConfig = config;
        if(mConfig == null) {
            updateNull();
            return;
        }

        mFreqCore.setText(String.valueOf(mConfig.getFreq().getCore()) + MHZ);
        mFreqCPU.setText(String.valueOf(mConfig.getFreq().getARM()) + MHZ);
        mFreqGPU.setText(String.valueOf(mConfig.getFreq().getGPU()) + MHZ);
        mFreqSDRAM.setText(String.valueOf(mConfig.getFreq().getSDRAM()) + MHZ);

        mSplitBar.setMax(mConfig.getSplit().total());
        mSplitBar.setProgress(mConfig.getSplit().cpuInt());
        mSplitCPU.setText(String.valueOf(mConfig.getSplit().getCPU()));
        mSplitGPU.setText(String.valueOf(mConfig.getSplit().getGPU()));

        mOverscanScale.setText("Scale: " + String.valueOf(mConfig.getOverscan().getScale()));
        mOverscanRight.setText(String.valueOf(mConfig.getOverscan().getRight()));
        mOverscanLeft.setText(String.valueOf(mConfig.getOverscan().getLeft()));
        mOverscanTop.setText(String.valueOf(mConfig.getOverscan().getTop()));
        mOverscanBottom.setText(String.valueOf(mConfig.getOverscan().getBottom()));

        mClockARM.setText(mConfig.getClock().getARM());
        mClockCore.setText(mConfig.getClock().getCore());
        mClockDPI.setText(mConfig.getClock().getDPI());
        mClockEMMC.setText(mConfig.getClock().getEMMC());
        mClockH264.setText(mConfig.getClock().getH264());
        mClockHDMI.setText(mConfig.getClock().getHDMI());
        mClockISP.setText(mConfig.getClock().getISP());
        mClockPixel.setText(mConfig.getClock().getPixel());
        mClockPWM.setText(mConfig.getClock().getPWM());
        mClockUART.setText(mConfig.getClock().getUART());
        mClockV3D.setText(mConfig.getClock().getV3D());
        mClockVEC.setText(mConfig.getClock().getVEC());
    }

    @Override
    public void updateNull() {
        mFreqCore.setText(BLANK);
        mFreqCPU.setText(BLANK);
        mFreqGPU.setText(BLANK);
        mFreqSDRAM.setText(BLANK);

        mSplitBar.setMax(100);
        mSplitBar.setProgress(50);
        mSplitCPU.setText(BLANK);
        mSplitGPU.setText(BLANK);

        mOverscanScale.setText(BLANK);
        mOverscanRight.setText(BLANK);
        mOverscanLeft.setText(BLANK);
        mOverscanTop.setText(BLANK);
        mOverscanBottom.setText(BLANK);

        mClockARM.setText(BLANK);
        mClockCore.setText(BLANK);
        mClockDPI.setText(BLANK);
        mClockEMMC.setText(BLANK);
        mClockH264.setText(BLANK);
        mClockHDMI.setText(BLANK);
        mClockISP.setText(BLANK);
        mClockPixel.setText(BLANK);
        mClockPWM.setText(BLANK);
        mClockUART.setText(BLANK);
        mClockV3D.setText(BLANK);
        mClockVEC.setText(BLANK);
    }
    //endregion
}
