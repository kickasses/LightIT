package com.lightit.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.lightit.MainActivity;
import com.lightit.R;
import com.lightit.database.Day;
import com.lightit.dialog.SetWattageDialog;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SelectedValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.LineChartView;

import static com.lightit.dialog.SetWattageDialog.SHARED_WATT_NAME;
import static com.lightit.dialog.SetWattageDialog.WATTAGE;


/**
 * A placeholder fragment containing a simple view.
 */
public class GraphFragment extends Fragment {

    private final String TAG = GraphFragment.class.getSimpleName();

    public String[] weeks = new String[Calendar.getInstance().get(Calendar.WEEK_OF_YEAR) + 1];
    public final static String[] days = new String[]{"Mon", "Tues", "Wed", "Thurs", "Fri", "Sat"
            , "Sun",};
    private int selectedValBottomChart;

    private LineChartView chartTop;
    private ColumnChartView chartBottom;

    private LineChartData lineData;
    private ColumnChartData columnData;

    private Context context;
    private OnFragmentInteractionListener mListener;

    public GraphFragment() {
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_graph, container, false);
        setHasOptionsMenu(true);

        if (mListener != null) {
            mListener.onFragmentInteraction("Graph");
        }

        for (int i = 0; i < weeks.length; i++) {
            weeks[i] = String.valueOf(i + 1);
        }

        // *** TOP LINE CHART ***
        chartTop = rootView.findViewById(R.id.chart_top);
        // Generate and set data for line chart
        generateInitialLineData();

        // *** BOTTOM COLUMN CHART ***
        chartBottom = rootView.findViewById(R.id.chart_bottom);
        // Generate and set data for column chart
        generateColumnData();

        // ********** REWRITE DATABASE **********
        Random random = new Random();
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_WATT_NAME, Context.MODE_PRIVATE);
        int wattage = sharedPreferences.getInt(WATTAGE, 0);



        /*for (int i = 1; i < 6; i++) { // zmonth
            if (i == 1) {
                for (int day = 1; day < 32; day++) {
                    int randomTimeInt = random.nextInt(86400);
                    int totalEnergy = wattage * (randomTimeInt/3600);
                    String date = String.format("%02d-%02d-%d", day, i, 2018);
                    Day newDay = new Day(randomTimeInt,totalEnergy,date);
                    MainActivity.mDayDao.insertDays(newDay);
                }
            }
            if (i == 2) {
                for (int day = 1; day < 29; day++) {
                    int randomTimeInt = random.nextInt(86400);
                    int totalEnergy = wattage * (randomTimeInt/3600);
                    //Log.d(TAG, String.format("%02d-%02d-%d", day, i, 2018));
                    String date = String.format("%02d-%02d-%d", day, i, 2018);
                    Day newDay = new Day(randomTimeInt,totalEnergy,date);
                    MainActivity.mDayDao.insertDays(newDay);
                }
            }
            if (i == 3) {
                for (int day = 1; day < 32; day++) {
                    int randomTimeInt = random.nextInt(86400);
                    int totalEnergy = wattage * (randomTimeInt/3600);
                    //Log.d(TAG, String.format("%02d-%02d-%d", day, i, 2018));
                    String date = String.format("%02d-%02d-%d", day, i, 2018);
                    Day newDay = new Day(randomTimeInt,totalEnergy,date);
                    MainActivity.mDayDao.insertDays(newDay);
                }
            }
            if (i == 4) {
                for (int day = 1; day < 31; day++) {
                    int randomTimeInt = random.nextInt(86400);
                    int totalEnergy = wattage * (randomTimeInt/3600);
                    //Log.d(TAG, String.format("%02d-%02d-%d", day, i, 2018));
                    String date = String.format("%02d-%02d-%d", day, i, 2018);
                    Day newDay = new Day(randomTimeInt,totalEnergy,date);
                    MainActivity.mDayDao.insertDays(newDay);
                }
            }
            if (i == 5) {
                for (int day = 1; day < 23; day++) {
                    int randomTimeInt = random.nextInt(86400);
                    int totalEnergy = wattage * (randomTimeInt/3600);
                    //Log.d(TAG, String.format("%02d-%02d-%d", day, i, 2018));
                    String date = String.format("%02d-%02d-%d", day, i, 2018);
                    Day newDay = new Day(randomTimeInt,totalEnergy,date);
                    MainActivity.mDayDao.insertDays(newDay);
                }
            }
        }*/

        List<Day> days = MainActivity.mDayDao.getAll();
        for (Day day : days) {
            Log.d(TAG, day.getDate() + " " + day.getTotalEnergy() + " " + day.getTotalTime() +
                    " " + day.getWeekDay() + " week" + String.valueOf(day.getWeekNumber()));
        }

        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    // MENU
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_graph, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String title);
    }

    private void generateColumnData() {

        SharedPreferences getWattInfo = context.getSharedPreferences(SHARED_WATT_NAME, Context.MODE_PRIVATE);
        int wattage = getWattInfo.getInt(SetWattageDialog.WATTAGE, 0);
        Log.d(TAG, "get wattage: " + wattage);

        int numSubColumns = 1;
        int numColumns = weeks.length;

        List<AxisValue> axisValues = new ArrayList<>();
        List<Column> columns = new ArrayList<>();
        List<SubcolumnValue> values;
        for (int i = 1; i < numColumns; ++i) {

            values = new ArrayList<>();
            for (int j = 0; j < numSubColumns; ++j) {
                values.add(new SubcolumnValue(MainActivity.mDayRoomDatabase.dayDao().getTotalEnergyPerWeek(i),
                        ChartUtils.pickColor()));
            }

            axisValues.add(new AxisValue(i).setLabel(weeks[i]));

            columns.add(new Column(values).setHasLabelsOnlyForSelected(true));
        }

        columnData = new ColumnChartData(columns);

        columnData.setAxisXBottom(new Axis(axisValues).setHasLines(true));
        columnData.setAxisYLeft(new Axis().setHasLines(true).setMaxLabelChars(2));

        chartBottom.setColumnChartData(columnData);

        // Set value touch listener that will trigger changes for chartTop.
        chartBottom.setOnValueTouchListener(new ValueTouchListener());

        // Set selection mode to keep selected month column highlighted.
        chartBottom.setValueSelectionEnabled(true);

        chartBottom.setZoomType(ZoomType.HORIZONTAL);

        /*chartBottom.setOnClickListener(new View.OnClickListener() {

         @Override
         public void onClick(View v) {
         SelectedValue sv = chartBottom.getSelectedValue();

         //String s = sv.toString();
         //selectedValBottomChart = Integer.parseInt(s,0);
           //  Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
         *//*if (!sv.isSet()) {
         generateInitialLineData();
         }*//*

        }
        });

    }*/
    }

    /**
     * Generates initial data for line chart. At the beginning all Y values are equals 0. That will change when user
     * will select value on column chart.
     */
    private void generateInitialLineData() {
        int numValues = 7;

        List<AxisValue> axisValues = new ArrayList<>();
        List<PointValue> values = new ArrayList<>();
        for (int i = 0; i < numValues; ++i) {
            values.add(new PointValue(i, 0));
            axisValues.add(new AxisValue(i).setLabel(days[i]));
        }

        Line line = new Line(values);
        line.setColor(ChartUtils.COLOR_GREEN).setCubic(true);

        List<Line> lines = new ArrayList<>();
        lines.add(line);

        lineData = new LineChartData(lines);
        lineData.setAxisXBottom(new Axis(axisValues).setHasLines(true));
        lineData.setAxisYLeft(new Axis().setHasLines(true).setMaxLabelChars(3));

        chartTop.setLineChartData(lineData);

        // For build-up animation you have to disable viewport recalculation.
        chartTop.setViewportCalculationEnabled(false);

        // And set initial max viewport and current viewport- remember to set viewports after data.
        Viewport v = new Viewport(0, 50, 6, 0);
        chartTop.setMaximumViewport(v);
        chartTop.setCurrentViewport(v);

        chartTop.setZoomType(ZoomType.HORIZONTAL);
    }

    private void generateLineData(int color, int range) {
        // Cancel last animation if not finished.
        chartTop.cancelDataAnimation();

        // Modify data targets
        Line line = lineData.getLines().get(0);// For this example there is always only one line.
        line.setColor(color);
        for (PointValue value : line.getValues()) {
            // Change target only for Y value.
            //String s = String.valueOf(value.getX());
            //Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
            if (value.getX() == 0.0) {
                value.setTarget(value.getX(), MainActivity.mDayDao.getTotalEnergyInWeekDay(selectedValBottomChart, "Monday"));
                Log.d(TAG,String.valueOf(MainActivity.mDayDao.getTotalEnergyInWeekDay(selectedValBottomChart, "måndag")));
            }
            if (value.getX() == 1.0) {
                value.setTarget(value.getX(), MainActivity.mDayDao.getTotalEnergyInWeekDay(selectedValBottomChart, "Tuesday"));
            }
            if (value.getX() == 2.0) {
                value.setTarget(value.getX(), MainActivity.mDayDao.getTotalEnergyInWeekDay(selectedValBottomChart, "Wednesday"));
            }
            if (value.getX() == 3.0) {
                value.setTarget(value.getX(), MainActivity.mDayDao.getTotalEnergyInWeekDay(selectedValBottomChart, "Thursday"));
            }
            if (value.getX() == 4.0) {
                value.setTarget(value.getX(), MainActivity.mDayDao.getTotalEnergyInWeekDay(selectedValBottomChart, "Friday"));
            }
            if (value.getX() == 5.0) {
                value.setTarget(value.getX(), MainActivity.mDayDao.getTotalEnergyInWeekDay(selectedValBottomChart, "Saturday"));
            }
            if (value.getX() == 6.0) {
                value.setTarget(value.getX(), MainActivity.mDayDao.getTotalEnergyInWeekDay(selectedValBottomChart, "Sunday"));
            }
        }

        // Start new data animation with 300ms duration;
        chartTop.startDataAnimation(300);

    }

    private class ValueTouchListener implements ColumnChartOnValueSelectListener {

        @Override
        public void onValueSelected(int columnIndex, int subcolumnIndex, SubcolumnValue value) {
            generateLineData(value.getColor(), 100);
            selectedValBottomChart = columnIndex + 1; //+1 because its an array that starts on 0
            onValueDeselected();
        }

        @Override
        public void onValueDeselected() {
            generateLineData(ChartUtils.COLOR_GREEN, 0);
        }
    }
}