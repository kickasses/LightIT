package com.lightit.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.lightit.MainActivity;
import com.lightit.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;


/**
 * A simple {@link Fragment} subclass.
 */
public class LineChartFragment extends Fragment {

    public final static String[] days = new String[]{"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

    private LineChartView lineChart;
    private LineChartData lineData;

    public LineChartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_line_chart, container, false);
        lineChart = rootView.findViewById(R.id.lineChart);
        generateInitialLineData();
        return rootView;
    }

    private void generateInitialLineData() {
        int numValues = days.length;

        List<AxisValue> axisValues = new ArrayList<>();
        List<PointValue> values = new ArrayList<>();
        for (int i = 0; i < numValues; ++i) {
            values.add(new PointValue(i, 0));
            axisValues.add(new AxisValue(i).setLabel(days[i]));
        }

        Line line = new Line(values);
        line.setColor(Color.parseColor("#468485")).setCubic(true);

        List<Line> lines = new ArrayList<>();
        lines.add(line);

        lineData = new LineChartData(lines);
        lineData.setAxisXBottom(new Axis(axisValues).setHasLines(true));
        lineData.setAxisYLeft(new Axis().setHasLines(true).setMaxLabelChars(3));

        lineChart.setLineChartData(lineData);

        // For build-up animation you have to disable viewport recalculation.
        lineChart.setViewportCalculationEnabled(false);

        // And set initial max viewport and current viewport- remember to set viewports after data.
        Viewport v = new Viewport(0, 50, 6, 0);
        lineChart.setMaximumViewport(v);
        lineChart.setCurrentViewport(v);

        lineChart.setZoomType(ZoomType.HORIZONTAL_AND_VERTICAL);
        lineChart.setScrollEnabled(true);
        // Cancel last animation if not finished.
        lineChart.cancelDataAnimation();


        List<Float> energyListOfCurrentWeek = MainActivity.mDayDao.getTotalEnergyWeekList(HomeFragment.getCurrentWeekNumber());
        for (int i = 0; i < 7; ++i) {
            // Change target only for Y value.
            PointValue value = line.getValues().get(i);
            //value.setTarget(value.getX(), energyListOfCurrentWeek.get(i));
            if (value.getX() == 0.0) {

                value.setTarget(value.getX(), MainActivity.mDayDao.getLatestWeekdayEnergy(getCurrentWeekNumber(), "Monday"));
            }
            if (value.getX() == 1.0) {

                value.setTarget(value.getX(), MainActivity.mDayDao.getLatestWeekdayEnergy(getCurrentWeekNumber(), "Tuesday"));
            }
            if (value.getX() == 2.0) {
                value.setTarget(value.getX(), MainActivity.mDayDao.getLatestWeekdayEnergy(getCurrentWeekNumber(), "Wednesday"));
            }
            if (value.getX() == 3.0) {
                value.setTarget(value.getX(), MainActivity.mDayDao.getLatestWeekdayEnergy(getCurrentWeekNumber(),"Thursday"));
            }
            if (value.getX() == 4.0) {
                value.setTarget(value.getX(), MainActivity.mDayDao.getLatestWeekdayEnergy(getCurrentWeekNumber(),"Friday"));
            }
            if (value.getX() == 5.0) {
                value.setTarget(value.getX(), MainActivity.mDayDao.getLatestWeekdayEnergy(getCurrentWeekNumber(),"Saturday"));
            }
            if (value.getX() == 6.0) {
                value.setTarget(value.getX(), MainActivity.mDayDao.getLatestWeekdayEnergy(getCurrentWeekNumber(),"Sunday"));
            }
        }

        // Start new data animation with 300ms duration;
        lineChart.startDataAnimation(300);
    }
    public static int getCurrentWeekNumber() {
        return Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);
    }

}
