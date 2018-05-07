package com.lightit;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by Tobias Åkesson on 2018-05-05.
 */

@Entity(tableName = "Users") //creates a table with name users
public class User {

    @PrimaryKey(autoGenerate = true) //sets Id to primary key
    private int Id;

    @ColumnInfo(name = "Total_Time_On") //column
    private String totalTime;

    @ColumnInfo(name = "Total_Watt") //column
    private double totalWatt;

    @ColumnInfo(name = "Start_Time") //column
    private String startTime;

    @ColumnInfo(name = "Week_Day") //column
    private String weekDay;

    @ColumnInfo(name = "Week_Number") //column
    private int weekNumber;

    public int getWeekNumber() {
        return weekNumber;
    }

    public void setWeekNumber(int weekNumber) {
        this.weekNumber = weekNumber;
    }

    public int getId() {
        return Id;
    }

    public String getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(String weekDay) {
        this.weekDay = weekDay;
    }

    public void setId(int id) {
        this.Id = id;
    }

    public String getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
    }

    public double getTotalWatt() {
        return totalWatt;
    }

    public void setTotalWatt(double totalWatt) {
        this.totalWatt = totalWatt;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
}
