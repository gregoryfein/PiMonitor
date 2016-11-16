package com.gfein.pimonitor.ui;

import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.gfein.pimonitor.R;
import com.gfein.pimonitor.model.config.PiConfig;
import com.gfein.pimonitor.model.stat.PiStat;
import com.pixplicity.easyprefs.library.Prefs;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    //region Variables
    private static final String LOGGING_TAG = "MainActivity";
    //String mURL = "http://10.0.2.2:8888/stats";
    String mIP = "10.0.2.2";
    String mPort = "8888";
    private ImageView mEditButton;
    private TextView mIPAddress, mLastUpdate;
    private Button mRebootButton;
    private String getStatURL() {
        return "http://" + mIP + ":" + mPort + "/stats";
    }
    private String getConfigURL() {
        return "http://" + mIP + ":" + mPort + "/config";
    }
    private String getRebootURL() {
        return "http://" + mIP + ":" + mPort + "/reboot";
    }

    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private StatFragment mStatFragment;
    private ConfigFragment mConfigFragment;
    private TabLayout mTabLayout;

    private OkHttpClient mHttpClient;
    private int mRunningSeconds;
    private PiStat mStats;
    private PiConfig mConfig;
    private boolean mContinueGettingStats = false;
    //endregion

    //region Lifecycle
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
//        if(getResources().getBoolean(R.bool.portrait_only)){
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);

        mRebootButton = (Button) findViewById(R.id.reboot_button);
        mRebootButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reboot();
            }
        });

        mIPAddress = (TextView) findViewById(R.id.ip_address);
        mLastUpdate = (TextView) findViewById(R.id.last_update);
        mEditButton = (ImageView) findViewById(R.id.edit_button);
        mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeIP();
            }
        });

        mIP = Prefs.getString("IP", "10.0.2.2");
        mPort = Prefs.getString("PORT", "8888");
        updateIP();

        update(null);
    }

    private void updateIP() {
        mIPAddress.setText(mIP + ":" + mPort);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHttpClient = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .build();
        mConfig = null;
        mStats = null;
        update(null);
        startService();
    }

    @Override
    public void onPause() {
        super.onPause();
        mHttpClient = null;
        endService();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }


    //endregion

    //region Change IP
    private void changeIP() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Edit Connection");
        alert.setMessage("Put in the IP of your Pi and the port that the monitoring service is running on (likely 8888).");

        // Set an EditText view to get user input
        View layout = LayoutInflater.from(this).inflate(R.layout.view_ip_input, null);
        final EditText ip_input = (EditText) layout.findViewById(R.id.ip_input);
        ip_input.setText(mIP);
        final EditText port_input = (EditText) layout.findViewById(R.id.port_input);
        port_input.setText(mPort);
        alert.setView(layout);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                mIP = ip_input.getText().toString();
                Prefs.putString("IP", mIP);
                mPort = port_input.getText().toString();
                Prefs.putString("PORT", mPort);
                updateIP();
                update(null);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
    }
    //endregion

    //region Fragment Stuff
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position == 0) {
                if(mStatFragment == null)
                    mStatFragment = StatFragment.newInstance(MainActivity.this);
                return mStatFragment;
            }
            else {
                if(mConfigFragment == null)
                    mConfigFragment = ConfigFragment.newInstance(MainActivity.this);
                return mConfigFragment;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                default:
                case 0:
                    return "Stats";
                case 1:
                    return "Config";
            }
        }
    }
    //endregion

    //region Update
    private Handler mTimerHandle = new Handler();

    private Runnable mTimerRunnable = new Runnable() {
        @Override
        public void run() {
            mRunningSeconds += 1;
            String text;
            if (mRunningSeconds < 10)
                text = "0:0" + mRunningSeconds;
            else if (mRunningSeconds < 60)
                text = "0:" + mRunningSeconds;
            else
                text = (mRunningSeconds / 60) + ":" + mRunningSeconds % 60;
            mLastUpdate.setText(text + " ago");
            mTimerHandle.postDelayed(this, 1000);
        }
    };

    private void resetTimer() {
        mRunningSeconds = 0;
        mTimerHandle.removeCallbacks(mTimerRunnable);
        mTimerHandle.post(mTimerRunnable);
    }

    public void updateNull() {
        mStats = null;
        mConfig = null;
        mLastUpdate.setText("---");
        if(mStatFragment != null)
            mStatFragment.update(null);
        if(mConfigFragment != null)
            mConfigFragment.update(null);
    }

    public void update(Object obj) {

        if(obj instanceof PiStat) {
            PiStat stat = (PiStat) obj;
            if (stat == null) {
                mLastUpdate.setText("---");
                if(mStatFragment != null)
                    mStatFragment.update(null);
                return;
            }

            mStats = stat;
            mStatFragment.update(mStats);
        }
        else if(obj instanceof PiConfig) {
            PiConfig cfg = (PiConfig) obj;
            if (cfg == null) {
                if(mConfigFragment != null)
                    mConfigFragment.update(null);
                return;
            }

            mConfig = cfg;
            mConfigFragment.update(mConfig);
        }
    }
    //endregion

    //regoin Stat/Config Service

    public void releaseConfig() { mConfig = null; }

    private Handler mServiceHandler = new Handler();

    private void startService() {
        endService();
        mContinueGettingStats = true;
        mServiceHandler.post(mServiceRunnable);

    }

    private void endService() {
        mContinueGettingStats = false;
        mServiceHandler.removeCallbacks(mServiceRunnable);
        mTimerHandle.removeCallbacks(mTimerRunnable);
    }

    private Runnable mConfigRunnable = new Runnable() {

        @Override
        public void run() {
            Request request;

            try {
                request = new Request.Builder()
                        .url(getConfigURL())
                        .build();
            } catch (Exception ex) {
                Log.e(LOGGING_TAG, "Couldn't make URL!");
                update(null);
                recall();
                return;
            }

            if(mHttpClient == null) return;
            mHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(LOGGING_TAG, "mConfigRunnable: Error: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        String body = response.body().string();
                        mConfig = new PiConfig(body);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                update(mConfig);
                            }
                        });
                    }
                    catch(Exception ex) {
                        Log.e(LOGGING_TAG, "mConfigRunnable: Error parsing: " + ex.getMessage());
                    }
                }
            });
        }
    };

    private Runnable mServiceRunnable = new Runnable() {


        @Override
        public void run() {
            Request request;

            try {
                request = new Request.Builder()
                        .url(getStatURL())
                        .build();
            } catch (Exception ex) {
                Log.e(LOGGING_TAG, "Couldn't make URL!");
                update(null);
                recall();
                return;
            }

            if(mHttpClient == null) {
                recall();
                return;
            }

            mHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(LOGGING_TAG, "Error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            update(null);
                        }
                    });
                    recall();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        String body = response.body().string();
                        mStats = new PiStat(body);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                update(mStats);
                            }
                        });
                    }
                    catch(Exception ex) {
                        Log.e(LOGGING_TAG, "Error parsing: " + ex.getMessage());
                    }
                    finally {
                        recall();
                        if(mConfig == null || mConfig.isEmpty())
                            mServiceHandler.postDelayed(mConfigRunnable, 1000);
                    }
                }
            });
        }
    };

    private void recall() {
        if (mContinueGettingStats) {
            mServiceHandler.postDelayed(mServiceRunnable, 1000);
            resetTimer();
        }
    }

    //region Reboot
    private void reboot() {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_warning_white_24dp)
                .setTitle("Reboot Your Pi?")
                .setMessage("Are you sure you want to reboot your Raspberry Pi?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        confirmReboot();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    private void confirmReboot() {
        mContinueGettingStats = false;
        mServiceHandler.removeCallbacks(mServiceRunnable);
        mServiceHandler.removeCallbacks(mConfigRunnable);
        mServiceHandler.post(mRebootRunnable);
        updateNull();
        mServiceHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startService();
            }
        }, 15000);
    }

    private Runnable mRebootRunnable = new Runnable() {


        @Override
        public void run() {
            Request request;

            try {
                request = new Request.Builder()
                        .url(getRebootURL())
                        .build();
            } catch (Exception ex) {
                Log.e(LOGGING_TAG, "Couldn't make URL!");
                update(null);
                recall();
                return;
            }

            if(mHttpClient == null) {
                recall();
                return;
            }

            mHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(LOGGING_TAG, "Failure rebooting: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            update(null);
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        String body = response.body().string();
                        Log.d(LOGGING_TAG, "Reboot Response: " + body);
                    }
                    catch(Exception ex) {
                        Log.e(LOGGING_TAG, "Error reboot: " + ex.getMessage());
                    }
                    finally {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                update(null);
                            }
                        });
                    }
                }
            });
        }
    };
    //endregion

}
