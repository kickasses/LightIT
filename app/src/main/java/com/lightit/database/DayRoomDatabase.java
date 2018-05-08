package com.lightit.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {Day.class}, version = 1)
public abstract class DayRoomDatabase extends RoomDatabase {

    public abstract DayDao dayDao();

}
