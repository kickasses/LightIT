package com.lightit.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface DayDao {

    @Insert
    void addDay(Day day);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDays(Day... days);

    @Query("SELECT * FROM day")
    List<Day> getAll();

    @Query("SELECT TotalTime FROM day WHERE Date=:date")
    int getTotalTimeOfDate(String date);

    @Query("UPDATE day SET TotalTime = TotalTime + :onTime WHERE Date = :date")
    void updateTime(String date, long onTime);

    @Query("SELECT SUM(TotalEnergy) FROM day WHERE WeekNumber = :weekNumber")
    float getTotalEnergyPerWeek(int weekNumber);

    @Query("select TotalEnergy FROM day where WeekNumber = :weekNumber")
    List<Float> getTotalEnergyWeekList(int weekNumber);
}
