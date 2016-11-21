package com.gfein.pimonitor.ui;

import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gfein.pimonitor.R;
import com.gfein.pimonitor.base.BaseFragment;

public class AboutFragment extends BaseFragment {

    //region Variables
    private static final String LOGGING_TAG = "AboutFragment";
    private View mRootView;
    private TextView mTitle;
    private ImageView mGithub;
    //endregion

    //region Constructor
    public static AboutFragment newInstance(MainActivity parent) {
        AboutFragment frag = new AboutFragment();
        frag.mParent = parent;
        return frag;
    }
    //endregion

    //region Lifecycle
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_about, container, false);
        mTitle = (TextView) mRootView.findViewById(R.id.version_title);
        mGithub = (ImageView) mRootView.findViewById(R.id.github);
        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        String ver = getString(R.string.about_header);
        try {

            PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            String version = pInfo.versionName;
            ver += " v" + version;
        }
        catch(Exception ex) {
            Log.e(LOGGING_TAG, "Failed to get version");
        }
        mTitle.setText(ver);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void updateNull() {

    }
    //endregion

    //region Adapter
    //endregion
}
