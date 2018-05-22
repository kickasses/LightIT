package com.lightit.fragment;

import android.content.Context;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

public class HomeFragment extends Fragment implements View.OnClickListener {

    private final String TAG = HomeFragment.class.getSimpleName();

    private ImageView image_light;
    private static boolean enableLightImage;
    private boolean lightIsOn = false;
    long startTime = 0;

    private OnFragmentInteractionListener mListener;

    public HomeFragment() {
        // Required empty public constructor
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

        FragmentManager fragmentManager = getChildFragmentManager();

        LineChartFragment lineChartFragment = new LineChartFragment();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, lineChartFragment).commit();

        ViewPagerFragment viewPagerFragment = new ViewPagerFragment();
        fragmentManager.beginTransaction().replace(R.id.container_viewPager, viewPagerFragment).commit();

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_home, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FragmentManager fragmentManager = getFragmentManager();

        if (fragmentManager != null) {
            if (item.getItemId() == R.id.action_setWattage) {
                SetWattageDialog setWattageDialog = SetWattageDialog.newInstance();
                setWattageDialog.setTargetFragment(HomeFragment.this, 300);
                setWattageDialog.show(fragmentManager, "dialog_choose_watt");
            }
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
        if (v == image_light) {
            if (enableLightImage) {
                if (!lightIsOn) {
                    ((TransitionDrawable) image_light.getDrawable()).startTransition(3000);

                    startTime = System.currentTimeMillis();
                    Log.i(TAG, "Start time: " + String.valueOf(startTime));

                    onOff = "/2/on";
                    lightIsOn = true;
                } else {
                    ((TransitionDrawable) image_light.getDrawable()).resetTransition();

                    long stopTime = System.currentTimeMillis();
                    Log.i(TAG, "Stop time: " + String.valueOf(stopTime));

                    long totalTime = stopTime - startTime;

                    if (MainActivity.mDayRoomDatabase.dayDao().getLatestDate() != null) {
                        if (MainActivity.mDayRoomDatabase.dayDao().getLatestDate().equals(getCurrentDate())) {
                            MainActivity.mDayRoomDatabase.dayDao().updateTime(getCurrentDate(), totalTime / 1000);
                            Log.i(TAG, "Updated time: " + MainActivity.mDayRoomDatabase.dayDao().getTotalTimeOfDate(getCurrentDate()));
                        }
                    } else {
                        Day day = new Day();
                        day.setDate(getCurrentDate());
                        day.setWeekDay(getCurrentDay());
                        day.setTotalTime(0);
                        day.setWeekNumber(getCurrentWeekNumber());
                        MainActivity.mDayRoomDatabase.dayDao().addDay(day);
                    }

                    onOff = "/2/off";
                    lightIsOn = false;
                }
            }
            enableLightImage = false;
            TaskEsp taskEsp = new TaskEsp(server + onOff);
            taskEsp.execute();
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String title);
    }

    private String getCurrentDate() {
        return DateFormat.format("dd-MM-yyyy", new java.util.Date()).toString();
    }

    private String getCurrentDay() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
    }

    private int getCurrentWeekNumber() {
        return Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);
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
            } catch (MalformedURLException e) {
                e.printStackTrace();
                serverResponse = e.getMessage();
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