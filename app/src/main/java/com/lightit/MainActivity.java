package com.lightit;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
    android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

      /*  FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        StartFragment connection = new StartFragment();
        fragmentManager.beginTransaction().replace(R.id.fragment, connection).commit();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        //android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();

        if (id == R.id.nav_start) {
            Toast toast = Toast.makeText(getApplicationContext(),"Hej hej",Toast.LENGTH_SHORT);
            toast.show();

            Fragment startFrag = new StartFragment();
            fragmentManager.beginTransaction().replace(R.id.fragment, startFrag).commit();
//            fragmentManager.beginTransaction();
//            fragmentTransaction.replace(R.id.fragment, startFrag);
//            fragmentTransaction.commit();
            // Handle the connections
            /*StartFragment connection = new StartFragment();
            fragmentManager.beginTransaction().replace(R.id.fragment, connection).commit();*/

        } else if (id == R.id.nav_currentLightbulb) {
            Toast toast = Toast.makeText(getApplicationContext(),"Nummer 2",Toast.LENGTH_SHORT);
            toast.show();

            Fragment lightbulbFragment = new CurrentLightbulb();
            /*fragmentManager.beginTransaction();
            //android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment, lightbulbFragment);
            fragmentTransaction.commit();*/
            fragmentManager.beginTransaction().replace(R.id.fragment, lightbulbFragment).commit();
            //ft.replace(R.id.fragment,fragment);
            //ft.commit();


            /*StartFragment connection = new StartFragment();
            fragmentManager.beginTransaction().replace(R.id.fragment, connection).commit();*/

        } else if (id == R.id.nav_statistic) {
            Fragment statistic = new StatisticFragment();
            fragmentManager.beginTransaction().replace(R.id.fragment, statistic).commit();

        } else if (id == R.id.nav_login){
            Toast.makeText(MainActivity.this,"LOOOOGGG IIIIN",Toast.LENGTH_LONG).show();

        } else if (id == R.id.nav_logout) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Are you sure you want to logout?");
                    alertDialogBuilder.setPositiveButton("yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    Toast.makeText(MainActivity.this,"You clicked yes button",Toast.LENGTH_LONG).show();
                                }
                            });

            alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(MainActivity.this,"You clicked NO",Toast.LENGTH_LONG).show();
                    //finish();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
