package com.lightit;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by Tobias Åkesson on 2018-05-05.
 */

@Dao        //Dao = Data access object
public interface MyDao {

    @Insert //call this to insert an object in to the database
    public void addUser(User user);

    @Query("SELECT * FROM Users") //call this to get a list of all users
    public List<User> getUsers();

    @Query("SELECT Total_Energy FROM Users WHERE Week_Number = (SELECT max(Week_Number) FROM Users)")
    public List<Double> getAllTotalWattFromLatestWeek();

    @Query("SELECT Start_Time FROM Users WHERE Id = (SELECT Max(Id) FROM Users)")
    public String getDate();

    @Query("SELECT Total_Time_On FROM Users WHERE Start_Time=:getTimeStamp")
    public int getTime(String getTimeStamp);

    @Query("UPDATE Users SET Total_Time_On = Total_Time_On + :time WHERE Start_Time = :getTimeStamp")
    public void updateTime(String getTimeStamp, long time);


}
