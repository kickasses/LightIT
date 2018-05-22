package com.lightit.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface DayDao {

    @Insert
    void addDay(Day day);

    @Query("SELECT * FROM day")
    List<Day> getDays();

    @Query("SELECT TotalEnergy FROM day WHERE WeekNumber = (SELECT max(WeekNumber) FROM day)")
    List<Double> getTotalEnergyListOfThisWeek();

    @Query("SELECT Date FROM day WHERE Id = (SELECT Max(Id) FROM day)")
    String getLatestDate();

    @Query("SELECT TotalTime FROM day WHERE Date=:date")
    int getTotalTimeOfDate(String date);

    @Query("UPDATE day SET TotalTime = TotalTime + :onTime WHERE Date = :date")
    void updateTime(String date, long onTime);

    @Update
    void updateDay(Day day);

    @Query("SELECT  SUM(TotalEnergy) FROM day WHERE Date LIKE '___' + :currentMonth + '%'")
    float getTotalEnergyFromSpecificMonth(String currentMonth);
}
