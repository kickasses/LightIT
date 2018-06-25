package com.lightit.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.TransitionDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lightit.MainActivity;
import com.lightit.R;
import com.lightit.database.Day;
import com.lightit.dialog.OpenWifiSettingDialog;
import com.lightit.dialog.SetWattageDialog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.lightit.dialog.SetWattageDialog.SHARED_WATT_NAME;
import static com.lightit.dialog.SetWattageDialog.WATTAGE;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private final String TAG = HomeFragment.class.getSimpleName();
    private final String serverIP = "192.168.4.1";

    private FragmentManager mFragmentManager;
    private FragmentManager mChildFragmentManager;
    private WifiManager mWifiManager;

    private ImageView image_light;
    private static boolean isOn;
    private long startTime = 0;

    private OnFragmentInteractionListener mListener;
    private Context context;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            context = getActivity();
            if (context != null) {
                mWifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            }
        } catch (NullPointerException npe) {
            Log.e(TAG, "Error onCreate");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        setHasOptionsMenu(true);
        if (mListener != null) {
            mListener.onFragmentInteraction("Home");
        }

        image_light = rootView.findViewById(R.id.image_light);
        image_light.setOnClickListener(this);

        TaskEsp checkLightState = new TaskEsp(serverIP);
        checkLightState.execute();
        if (isOn) {
            ((TransitionDrawable) image_light.getDrawable()).startTransition(0);
        } else {
            ((TransitionDrawable) image_light.getDrawable()).resetTransition();
        }

        mFragmentManager = getFragmentManager();
        mChildFragmentManager = getChildFragmentManager();

        LineChartFragment lineChartFragment = new LineChartFragment();
        mChildFragmentManager.beginTransaction().replace(R.id.fragment_container, lineChartFragment).commit();

        ViewPagerFragment viewPagerFragment = new ViewPagerFragment();
        mChildFragmentManager.beginTransaction().replace(R.id.container_viewPager, viewPagerFragment).commit();

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_home, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_setWattage) {
            startWattageDialog();
        }

        return true;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {

        // if click on the light bulb
        if (v == image_light) {
            // if mobile is not connected to "LightIT", open Wi-Fi Settings
            if (getCurrentSSID(context) == null || !getCurrentSSID(context).equals("LightIT")) {
                startOpenWifiSettingDialog();
            } else {
                //if mobile is connected to "LightIT"
                String onOff = "";

                SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_WATT_NAME, Context.MODE_PRIVATE);
                int wattage = sharedPreferences.getInt(WATTAGE, 0);
                if (wattage <= 0) {
                    startWattageDialog();
                } else {

                    if (!isOn) {    // if light is off, turn it on
                        ((TransitionDrawable) image_light.getDrawable()).startTransition(0);

                        startTime = System.currentTimeMillis();
                        Log.i(TAG, "Start time: " + String.valueOf(startTime));

                        onOff = "/2/on";

                    } else {    // if light is on, turn it off
                        ((TransitionDrawable) image_light.getDrawable()).resetTransition();

                        long stopTime = System.currentTimeMillis();
                        Log.i(TAG, "Stop time: " + String.valueOf(stopTime));

                        if (MainActivity.mDayDao.getDayOfDate(getCurrentDate()) == null) {
                            Day day = new Day(getCurrentDate());
                            MainActivity.mDayRoomDatabase.dayDao().addDay(day);
                        }

                        // Update time and energy
                        int totalTime = (int) (stopTime - startTime) / 1000; // millisecond -> second
                        MainActivity.mDayDao.updateTimeOfDate(getCurrentDate(), totalTime);
                        float totalEnergy = wattage * (totalTime / (float) 3600);
                        MainActivity.mDayDao.updateEnergyOfDate(getCurrentDate(), totalEnergy);
                        Log.i(TAG, "Updated time  : " + MainActivity.mDayDao.getTotalTimeOfDate(getCurrentDate()));
                        Log.i(TAG, "Updated energy: " + MainActivity.mDayDao.getTotalEnergyOfDate(getCurrentDate()));

                        // Update graph
                        LineChartFragment lineChartFragment = new LineChartFragment();
                        mChildFragmentManager.beginTransaction().replace(R.id.fragment_container, lineChartFragment).commit();

                        onOff = "/2/off";
                    }
                    isOn = !isOn;
                }
                TaskEsp taskEsp = new TaskEsp(serverIP + onOff);
                taskEsp.execute();
            }
        }
    }

    private void startWattageDialog() {
        if (mFragmentManager != null) {
            SetWattageDialog setWattageDialog = SetWattageDialog.newInstance();
            setWattageDialog.setTargetFragment(HomeFragment.this, 300);
            setWattageDialog.show(mFragmentManager, "dialog_choose_watt");
        }
    }

    private void startOpenWifiSettingDialog() {
        if (mFragmentManager != null) {
            OpenWifiSettingDialog openWifiSettingDialog = OpenWifiSettingDialog.newInstance();
            openWifiSettingDialog.setTargetFragment(HomeFragment.this, 300);
            openWifiSettingDialog.show(mFragmentManager, "dialog_choose_watt");
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String title);
    }

    private String getCurrentDate() {
        return DateFormat.format("dd-MM-yyyy", new java.util.Date()).toString();
    }

    public String getCurrentSSID(Context context) {
        String ssid = null;
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager != null) {
            NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (networkInfo.isConnected()) {
                final WifiInfo connectionInfo = mWifiManager.getConnectionInfo();
                if (connectionInfo != null && !TextUtils.isEmpty(connectionInfo.getSSID())) {
                    ssid = connectionInfo.getSSID();
                    ssid = ssid.substring(1, ssid.length() - 1);
                }
            }
        }

        return ssid;
    }

    static class TaskEsp extends AsyncTask<Void, Void, String> {
        private String server;

        TaskEsp(String server) {
            this.server = server;
        }

        @Override
        protected String doInBackground(Void... voids) {
            final String p = "http://" + server;
            StringBuilder serverResponse = new StringBuilder();

            try {
                HttpURLConnection httpURLConnection = (HttpURLConnection) (new URL(p).openConnection());

                if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader =
                            new BufferedReader(new InputStreamReader(inputStream));
                    String thisLine;
                    while ((thisLine = bufferedReader.readLine()) != null) {
                        serverResponse.append(thisLine);
                    }
                    Log.d("ServerRes: ", serverResponse.toString());
                    Log.d("state: ", serverResponse.substring(53, 54));

                    String state = serverResponse.substring(53, 54);
                    isOn = state.equals("1");

                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                serverResponse.append(e.getMessage());
            }

            return serverResponse.toString();
        }
    }
}