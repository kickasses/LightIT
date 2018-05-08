package com.lightit;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;

import static com.lightit.MainActivity.myAppDatabase;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class HomeFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private ImageButton onOffButton;
    private boolean isLightOn; //used to check if the light is on
    long startTime = 0;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        onOffButton = v.findViewById(R.id.onOffButton);

        if (mListener != null) {
            mListener.onFragmentInteraction("Home");
        }

        onOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isLightOn = !isLightOn;

                if (isLightOn == true) {
                    startTime = System.currentTimeMillis();
                    String start = String.valueOf(startTime);
                    Toast.makeText(getContext(),"start: "+ start, Toast.LENGTH_SHORT).show();


                    User user = new User(); //creates a new user that will be inserted into database
                    user.setStartDate(getTimeStamp()); //gets the time that the button was clicked
                    user.setWeekDay(getDay());
                    user.setTotalTime(0);
                    user.setWeekNumber(getWeekNumber());
                    myAppDatabase.myDao().addUser(user);

                }
                else {

                    long stoptime = System.currentTimeMillis();
                    Toast.makeText(getContext(),"stop: "+ stoptime, Toast.LENGTH_SHORT).show();

                    long totalTime = stoptime - startTime;
                    Toast.makeText(getContext(),"gettimestamp: "+ getTimeStamp(), Toast.LENGTH_SHORT).show();
                    Toast.makeText(getContext(),"getdate: "+ myAppDatabase.myDao().getDate(), Toast.LENGTH_LONG).show();


                    if (myAppDatabase.myDao().getDate().equals(getTimeStamp())){
                        Toast.makeText(getContext(), "fan va bra", Toast.LENGTH_SHORT).show();
                        myAppDatabase.myDao().updateTime(getTimeStamp(),totalTime/1000); //convert it to seconds
                        Toast.makeText(getContext(), "updated time: " + myAppDatabase.myDao().getTime(getTimeStamp()), Toast.LENGTH_SHORT).show();

                    }else {
                        Toast.makeText(getContext(), "fan va kass", Toast.LENGTH_SHORT).show();
                        User user = new User();
                        user.setStartDate(getTimeStamp()); //gets the time that the button was clicked
                        user.setWeekDay(getDay());
                        user.setTotalTime(0);
                        user.setWeekNumber(getWeekNumber());
                        myAppDatabase.myDao().addUser(user);
                    }

                }

            }
        });

        FragmentManager fragmentManager = getChildFragmentManager();

        LineChartFragment lineChartFragment = new LineChartFragment();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, lineChartFragment).commit();

        ViewPagerFragment viewPagerFragment = new ViewPagerFragment();
        fragmentManager.beginTransaction().replace(R.id.container_viewPager, viewPagerFragment).commit();

        return v;
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

    private String getTimeStamp() {
        String date = DateFormat.format("dd-MM-yyyy", new java.util.Date()).toString();
        return date;
    }

    private String getDay() {
        Calendar calendar = Calendar.getInstance();
        String currentDay = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
        return currentDay;
    }

    private int getWeekNumber() {
        return Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);
    }
}
