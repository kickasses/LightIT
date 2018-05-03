package com.lightit;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class FragmentHolderActivity extends AppCompatActivity {

    private static final String TAG = FragmentHolderActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_holder);

        Log.i(TAG, "welcome to " + TAG);

        FragmentManager fragmentManager = getSupportFragmentManager();

        // Get intent String from MainActivity
        String fragmentName = getIntent().getStringExtra(MainActivity.INTENT_NAME);
        switch (fragmentName) {
            case MainActivity.WIFI_FRAGMENT:
                WifiFragment wifiFragment = new WifiFragment();
                fragmentManager.beginTransaction().replace(R.id.container, wifiFragment).commit();
        }
    }
}
