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
    float getTotalTimeOfDate(String date);

    @Query("SELECT TotalEnergy FROM day WHERE Date=:date")
    float getTotalEnergyOfDate(String date);

    @Query("UPDATE day SET TotalTime = TotalTime + :onTime WHERE Date = :date")
    void updateTimeOfDate(String date, float onTime);

    @Query("UPDATE day SET TotalEnergy = TotalEnergy + :onEnergy WHERE Date = :date")
    void updateEnergyOfDate(String date, float onEnergy);

    @Query("UPDATE day SET TotalEnergy = :totalEnergy WHERE Date = :date")
    void setEnergyOfDate(String date, float totalEnergy);

    @Query("SELECT SUM(TotalEnergy) FROM day WHERE WeekNumber = :weekNumber")
    float getTotalEnergyPerWeek(int weekNumber);

    @Query("SELECT TotalEnergy FROM day WHERE WeekNumber = :weekNumber")
    List<Float> getTotalEnergyWeekList(int weekNumber);

    @Query("SELECT * FROM day WHERE Date= :date")
    Day getDayOfDate(String date);

    @Query("UPDATE day SET WeekNumber = :weekNumber WHERE Date = :date")
    void updateWeekNumber(int weekNumber, String date);

    @Query("UPDATE day SET WeekDay = :weekDay WHERE WeekNumber = :weekNumber")
    void updateWeekday(String weekDay, int weekNumber);

    @Query("select TotalEnergy FROM day where WeekNumber = :weekNumber AND WeekDay = :weekDay")
    float getTotalEnergyInWeekDay(int weekNumber, String weekDay);
}
