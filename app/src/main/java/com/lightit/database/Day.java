package com.lightit.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

@Entity(tableName = "day")
public class Day {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "Date")
    private String date = "";

    @ColumnInfo(name = "TotalTime")
    private float totalTime;

    @ColumnInfo(name = "TotalEnergy")
    private float totalEnergy;

    @ColumnInfo(name = "WeekDay")
    private String weekDay;

    @ColumnInfo(name = "WeekNumber")
    private int weekNumber;

    public Day() {
    }

    @Ignore
    public Day(@NonNull String date) {
        this.date = date;
        this.weekDay = getDayOfWeek(date);
        this.weekNumber = getWeekNumberOfDate(date);
    }

    @Ignore
    public Day(@NonNull String date, float totalTime, float totalEnergy) {
        this.date = date;
        this.weekDay = getDayOfWeek(date);
        this.weekNumber = getWeekNumberOfDate(date);
        this.totalTime = totalTime;
        this.totalEnergy = totalEnergy;
    }

    public int getWeekNumber() {
        return weekNumber;
    }

    public void setWeekNumber(int weekNumber) {
        this.weekNumber = weekNumber;
    }

    public String getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(String weekDay) {
        this.weekDay = weekDay;
    }

    public float getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(float totalTime) {
        this.totalTime = totalTime;
    }

    public float getTotalEnergy() {
        return totalEnergy;
    }

    public void setTotalEnergy(float totalEnergy) {
        this.totalEnergy = totalEnergy;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    private String getDayOfWeek(String date) {
        String dayOfDate = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        Date date1;
        try {
            date1 = dateFormat.parse(date);
            DateFormat dayFormate = new SimpleDateFormat("EEEE", Locale.US);
            dayOfDate = dayFormate.format(date1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dayOfDate;
    }

    private int getWeekNumberOfDate(String date) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        Date date1;
        try {
            date1 = dateFormat.parse(date);
            calendar.setTime(date1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar.get(Calendar.WEEK_OF_YEAR);
    }
}
