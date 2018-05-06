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
}
