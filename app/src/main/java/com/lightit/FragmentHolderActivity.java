package com.lightit;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.support.v7.widget.Toolbar;

public class FragmentHolderActivity extends AppCompatActivity {

    private static final String TAG = FragmentHolderActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_holder);
        Toolbar toolbar = findViewById(R.id.toolbar_holder);
        setSupportActionBar(toolbar);

        Log.i(TAG, "welcome to " + TAG);

        FragmentManager fragmentManager = getSupportFragmentManager();

        // Get intent String from MainActivity
        String fragmentName = getIntent().getStringExtra(MainActivity.INTENT_NAME);
        switch (fragmentName) {
            case MainActivity.EXTRA_WIFIFRAGMENT:
                WifiFragment wifiFragment = new WifiFragment();
                fragmentManager.beginTransaction().replace(R.id.container, wifiFragment).commit();
        }
    }
}
