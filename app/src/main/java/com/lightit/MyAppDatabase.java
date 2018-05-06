package com.lightit;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

/**
 * Created by Tobias Åkesson on 2018-05-05.
 */

@Database(entities = {User.class}, version = 1) //entities specifies the tables (we only have one table)
public abstract class MyAppDatabase extends RoomDatabase{

    public abstract MyDao myDao();

}
