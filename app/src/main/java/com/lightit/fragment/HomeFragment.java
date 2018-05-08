package com.lightit.fragment;

import android.content.Context;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lightit.MainActivity;
import com.lightit.R;
import com.lightit.database.Day;

import java.util.Calendar;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class HomeFragment extends Fragment {

    private final String TAG = HomeFragment.class.getSimpleName();

    private ImageView image_light;
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

        if (mListener != null) {
            mListener.onFragmentInteraction("Home");
        }

        image_light = rootView.findViewById(R.id.image_light);

        image_light.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!lightIsOn) {
                    ((TransitionDrawable) image_light.getDrawable()).startTransition(3000);

                    startTime = System.currentTimeMillis();
                    Log.i(TAG, "Start time: " + String.valueOf(startTime));

                    lightIsOn = true;
                } else {
                    ((TransitionDrawable) image_light.getDrawable()).resetTransition();

                    long stopTime = System.currentTimeMillis();
                    Log.i(TAG, "Stop time: " + String.valueOf(stopTime));

                    long totalTime = stopTime - startTime;

                    if (MainActivity.dayRoomDatabase.dayDao().getLatestDate() != null) {
                        if (MainActivity.dayRoomDatabase.dayDao().getLatestDate().equals(getCurrentDate())) {
                            MainActivity.dayRoomDatabase.dayDao().updateTime(getCurrentDate(), totalTime / 1000);
                            Log.i(TAG, "Updated time: " + MainActivity.dayRoomDatabase.dayDao().getTotalTimeOfDate(getCurrentDate()));
                        }
                    } else {
                        Day day = new Day();
                        day.setDate(getCurrentDate());
                        day.setWeekDay(getCurrentDay());
                        day.setTotalTime(0);
                        day.setWeekNumber(getCurrentWeekNumber());
                        MainActivity.dayRoomDatabase.dayDao().addDay(day);
                    }

                    lightIsOn = false;
                }

            }
        });

        FragmentManager fragmentManager = getChildFragmentManager();

        LineChartFragment lineChartFragment = new LineChartFragment();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, lineChartFragment).commit();

        ViewPagerFragment viewPagerFragment = new ViewPagerFragment();
        fragmentManager.beginTransaction().replace(R.id.container_viewPager, viewPagerFragment).commit();

        return rootView;
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
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
}
