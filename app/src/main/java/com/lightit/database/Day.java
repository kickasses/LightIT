package com.lightit.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "day")
public class Day {

    @PrimaryKey(autoGenerate = true)
    private int ID;

    @ColumnInfo(name = "TotalTime")
    private int totalTime;

    @ColumnInfo(name = "TotalEnergy")
    private double totalEnergy;

    @ColumnInfo(name = "Date")
    private String date;

    @ColumnInfo(name = "WeekDay")
    private String weekDay;

    @ColumnInfo(name = "WeekNumber")
    private int weekNumber;

    public Day() {
    }

    public Day(String date, String weekDay, int weekNumber) {
        this.date = date;
        this.weekDay = weekDay;
        this.weekNumber = weekNumber;
    }

    public int getWeekNumber() {
        return weekNumber;
    }

    public void setWeekNumber(int weekNumber) {
        this.weekNumber = weekNumber;
    }

    public int getID() {
        return ID;
    }

    public String getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(String weekDay) {
        this.weekDay = weekDay;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }

    public double getTotalEnergy() {
        return totalEnergy;
    }

    public void setTotalEnergy(double totalEnergy) {
        this.totalEnergy = totalEnergy;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
