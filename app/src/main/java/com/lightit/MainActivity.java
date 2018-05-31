package com.lightit;

import android.arch.persistence.room.Room;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.lightit.database.DayDao;
import com.lightit.database.DayRoomDatabase;
import com.lightit.fragment.GraphFragment;
import com.lightit.fragment.HomeFragment;
import com.lightit.fragment.AnnotationFragment;
import com.lightit.fragment.WifiFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GraphFragment.OnFragmentInteractionListener
        , WifiFragment.OnFragmentInteractionListener, HomeFragment.OnFragmentInteractionListener,
        AnnotationFragment.OnFragmentInteractionListener {

    private FragmentManager mFragmentManager;
    public static DayRoomDatabase mDayRoomDatabase;
    public static DayDao mDayDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFragmentManager = getSupportFragmentManager();
        if (savedInstanceState == null) {
            HomeFragment homeFragment = new HomeFragment();
            mFragmentManager.beginTransaction().replace(R.id.fragment_container, homeFragment).commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);

        mDayRoomDatabase = Room.databaseBuilder(getApplicationContext(), DayRoomDatabase.class, "database")
                .allowMainThreadQueries().build();
        mDayDao = mDayRoomDatabase.dayDao();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                HomeFragment homeFragment = new HomeFragment();
                mFragmentManager.beginTransaction().replace(R.id.fragment_container, homeFragment).addToBackStack(null).commit();
                break;
            case R.id.nav_connection:
                WifiFragment wifiFragment = new WifiFragment();
                mFragmentManager.beginTransaction().replace(R.id.fragment_container, wifiFragment).addToBackStack(null).commit();
                break;
            case R.id.nav_graph:
                GraphFragment graphFragment = new GraphFragment();
                mFragmentManager.beginTransaction().replace(R.id.fragment_container, graphFragment).addToBackStack(null).commit();
                break;
            case R.id.nav_notification:
                AnnotationFragment annotationFragment = new AnnotationFragment();
                mFragmentManager.beginTransaction().replace(R.id.fragment_container, annotationFragment).addToBackStack(null).commit();
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }


}
