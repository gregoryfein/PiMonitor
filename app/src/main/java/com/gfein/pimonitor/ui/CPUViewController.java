package com.gfein.pimonitor.ui;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.gfein.pimonitor.R;
import com.gfein.pimonitor.model.stat.CPUDetail;

public class CPUViewController {

    private View mRoot;
    private View mUse0, mUse20, mUse40, mUse60, mUse80;
    private TextView mCPUID, mCPUPercent;
    private int color_gray, color_green, color_yellow, color_red;

    public CPUViewController(Context c, View root) {
        mRoot = root;
        color_gray = c.getResources().getColor(R.color.lt_gray);
        color_green = c.getResources().getColor(R.color.green);
        color_yellow = c.getResources().getColor(R.color.yellow);
        color_red = c.getResources().getColor(R.color.red);
        mUse0 = mRoot.findViewById(R.id.bar_0);
        mUse20 = mRoot.findViewById(R.id.bar_20);
        mUse40 = mRoot.findViewById(R.id.bar_40);
        mUse60 = mRoot.findViewById(R.id.bar_60);
        mUse80 = mRoot.findViewById(R.id.bar_80);

        mCPUID = (TextView) mRoot.findViewById(R.id.cpu_id);
        mCPUPercent = (TextView) mRoot.findViewById(R.id.cpu_percent);
        update(0);
    }

    public CPUViewController(Context c, View root, String name) {
        this(c, root);
        mCPUID.setText(name);
    }

    public void setName(String name) { mCPUID.setText(name); }

    private boolean mAscending = true;
    public void setAscending(boolean ascending) {
        mAscending = ascending;
    }

    public void update(CPUDetail detail) {
        mCPUID.setText(detail.getName());
        update(detail.getPercent());
    }

    public void update(double value, String display) {
        mCPUPercent.setText(display);

        int color;

        if(mAscending) {
            color = color_green;
            if (value > 60)
                color = color_yellow;
            if (value > 80)
                color = color_red;
        }
        else {
            color = color_red;
            if (value > 60)
                color = color_yellow;
            if (value > 80)
                color = color_green;
        }
        updateBar(mUse0, value > 0, color);
        updateBar(mUse20, value > 20, color);
        updateBar(mUse40, value > 40, color);
        updateBar(mUse60, value > 60, color);
        updateBar(mUse80, value > 80, color);
    }

    public void update(double value) {
        update(value, String.valueOf(value) + "%");
    }


    private void updateBar(View bar, boolean on, int color) {
        if(on)
            bar.setBackgroundColor(color);
        else
            bar.setBackgroundColor(color_gray);
    }
}
