package com.lightit.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.lightit.MainActivity;
import com.lightit.R;
import com.lightit.database.Day;
import com.lightit.dialog.SetWattageDialog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Locale;

import static com.lightit.dialog.SetWattageDialog.SHARED_WATT_NAME;
import static com.lightit.dialog.SetWattageDialog.WATTAGE;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private final String TAG = HomeFragment.class.getSimpleName();

    private ImageView image_light;
    private static boolean enableLightImage = true;
    private boolean lightIsOn = false;
    long startTime = 0;

    private OnFragmentInteractionListener mListener;
    private Context context;

    private FragmentManager mFragmentManager;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            context = getActivity();
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
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_WATT_NAME, Context.MODE_PRIVATE);

        image_light = rootView.findViewById(R.id.image_light);
        image_light.setOnClickListener(this);

        mFragmentManager = getFragmentManager();
        FragmentManager mChildFragmentManager = getChildFragmentManager();

        LineChartFragment lineChartFragment = new LineChartFragment();
        mChildFragmentManager.beginTransaction().replace(R.id.fragment_container, lineChartFragment).commit();

        ViewPagerFragment viewPagerFragment = new ViewPagerFragment();
        mChildFragmentManager.beginTransaction().replace(R.id.container_viewPager, viewPagerFragment).commit();

        boolean isOn = sharedPreferences.getBoolean("lightBoolean", false);

        if (isOn) {
            lightIsOn = true;
            ((TransitionDrawable) image_light.getDrawable()).startTransition(0);
        } else {
            lightIsOn = false;
        }
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
        String onOff = "";
        final String server = "192.168.4.1";
        LineChartFragment lineChartFragment = new LineChartFragment();
        FragmentManager fragmentManager = getChildFragmentManager();
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_WATT_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int wattage = sharedPreferences.getInt(WATTAGE, 0);
        if (wattage <= 0) {
            startWattageDialog();
        } else {
            if (v == image_light) {
                if (enableLightImage) {
                    if (!lightIsOn) {
                        ((TransitionDrawable) image_light.getDrawable()).startTransition(0);

                        startTime = System.currentTimeMillis();
                        editor.putLong("startTime", startTime);
                        editor.apply();
                        Log.i(TAG, "Start time: " + String.valueOf(startTime));
                        editor.putBoolean("lightBoolean", true);
                        editor.apply();
                        onOff = "/2/on";
                    } else {
                        ((TransitionDrawable) image_light.getDrawable()).resetTransition();

                        long stopTime = System.currentTimeMillis();
                        editor.putLong("stopTime", stopTime);
                        editor.apply();
                        Log.i(TAG, "Stop time: " + String.valueOf(stopTime));

                        long totalTime = sharedPreferences.getLong("stopTime", 0) - sharedPreferences.getLong("startTime", 0);
                        editor.putBoolean("lightBoolean", false);
                        editor.apply();
                        if (MainActivity.mDayDao.getDayOfDate(getCurrentDate()) != null) {
                            // Get shared wattage


                            // Update time and energy
                            MainActivity.mDayDao.updateTimeOfDate(getCurrentDate(), totalTime / (float) 1000);
                            MainActivity.mDayDao.updateEnergyOfDate(getCurrentDate(), wattage * (totalTime / 3600));
                            float theTime = MainActivity.mDayDao.getTotalTimeOfDate(getCurrentDate());
                            MainActivity.mDayDao.setEnergyOfDate(getCurrentDate(), wattage * (theTime / (float) 3600));
                            Log.i(TAG, "Updated time  : " + MainActivity.mDayDao.getTotalTimeOfDate(getCurrentDate()));
                            Log.i(TAG, "Updated energy: " + MainActivity.mDayDao.getTotalEnergyOfDate(getCurrentDate()));

                            fragmentManager.beginTransaction().replace(R.id.fragment_container, lineChartFragment).commit();

                        } else {

                            Day day = new Day(getCurrentDate());
                            MainActivity.mDayRoomDatabase.dayDao().addDay(day);

                            MainActivity.mDayDao.updateTimeOfDate(getCurrentDate(), totalTime / (float) 1000);
                            MainActivity.mDayDao.updateEnergyOfDate(getCurrentDate(), wattage * (totalTime / 3600));
                            float theTime = MainActivity.mDayDao.getTotalTimeOfDate(getCurrentDate());
                            MainActivity.mDayDao.setEnergyOfDate(getCurrentDate(), wattage * (theTime / (float) 3600));
                            Log.i(TAG, "Updated time  : " + MainActivity.mDayDao.getTotalTimeOfDate(getCurrentDate()));
                            Log.i(TAG, "Updated energy: " + MainActivity.mDayDao.getTotalEnergyOfDate(getCurrentDate()));

                            fragmentManager.beginTransaction().replace(R.id.fragment_container, lineChartFragment).commit();

                        }
                        onOff = "/2/off";

                    }
                    lightIsOn = !lightIsOn;
                }
                enableLightImage = false;
                TaskEsp taskEsp = new TaskEsp(server + onOff);
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

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String title);
    }

    private String getCurrentDate() {
        return DateFormat.format("dd-MM-yyyy", new java.util.Date()).toString();
    }

    static class TaskEsp extends AsyncTask<Void, Void, String> {

        private String server;

        TaskEsp(String server) {
            this.server = server;
        }

        @Override
        protected String doInBackground(Void... voids) {
            final String p = "http://" + server;
            String serverResponse = "";

            try {
                HttpURLConnection httpURLConnection = (HttpURLConnection) (new URL(p).openConnection());

                if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader =
                            new BufferedReader(new InputStreamReader(inputStream));
                    serverResponse = bufferedReader.readLine();

                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                serverResponse = e.getMessage();
            }

            return serverResponse;
        }

        @Override
        protected void onPostExecute(String s) {
            enableLightImage = true;
        }
    }
}