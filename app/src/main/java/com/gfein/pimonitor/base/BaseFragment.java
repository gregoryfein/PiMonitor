package com.gfein.pimonitor.base;
import android.support.v4.app.Fragment;

import com.gfein.pimonitor.ui.MainActivity;

import java.text.DecimalFormat;

public abstract class BaseFragment extends Fragment {

    protected static final String BLANK = "---";
    protected static final String MHZ =  " MHz";
    protected static final DecimalFormat DECIMAL_FORMATTER = new DecimalFormat("#.0");

    protected MainActivity mParent;

    protected String bytesToStr(long val) {

        // From bytes to kb
        val /= 1024;
        // From kb to mb
        val /= 1024;

        return val + " MB";
    }

    public abstract void updateNull();
}
