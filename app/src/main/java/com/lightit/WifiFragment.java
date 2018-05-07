package com.lightit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import static com.lightit.LoginDialog.*;

public class WifiFragment extends Fragment implements LoginDialogListener {

    private static final String TAG = WifiFragment.class.getSimpleName();

    private WifiManager mWifiManager;
    private BroadcastReceiver mReceiver;
    private ScanResultAdapter mScanResultAdapter;
    private List<ScanResult> mScanResultList;

    private Context context;
    private OnFragmentInteractionListener mListener;

    public WifiFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            context = getActivity();
            if (context != null) {
                mWifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            }
        } catch (NullPointerException npe) {
            Log.e(TAG, "Error setting up Wi-Fi");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_wifi, container, false);
        if (mListener != null) {
            mListener.onFragmentInteraction("Connection");
        }
        setHasOptionsMenu(true);

        mScanResultList = new ArrayList<>();

        mReceiver = new WifiConnectionReceiver();
        mWifiManager.startScan();

        RecyclerView mRecyclerView = rootView.findViewById(R.id.recycler_scan_result);
        mScanResultAdapter = new ScanResultAdapter(mScanResultList);
        mRecyclerView.setAdapter(mScanResultAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mScanResultAdapter.SetOnItemClickListener(new ScanResultAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ScanResult scan_item = mScanResultAdapter.getScanItem(position);
                showLoginDialog(scan_item.SSID);
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        context.registerReceiver(mReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    @Override
    public void onPause() {
        super.onPause();
        context.unregisterReceiver(mReceiver);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_connection, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    /**
     * Password value returned from dialog.
     *
     * @param password - Wi-Fi password entered in dialog
     */
    @Override
    public void onFinishLoginDialog(String SSID, String password) {
        Log.i(TAG, "ssid:     " + SSID);
        Log.i(TAG, "password: " + password);
        connectToWifi(SSID, password);

    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String title);
    }

    /**
     * Show dialog on recyclerView item click
     *
     * @param SSID - Selected Wi-Fi network SSID
     */
    private void showLoginDialog(String SSID) {
        FragmentManager fm = getFragmentManager();

        if (fm != null) {
            LoginDialog connectDialog = LoginDialog.newInstance(SSID);
            connectDialog.setTargetFragment(WifiFragment.this, 300);
            connectDialog.show(fm, "dialog_login");
        }
    }

    /**
     * Connect to the specified Wi-Fi network.
     *
     * @param networkSSID     - The Wi-Fi network SSID
     * @param networkPassword - The Wi-Fi password
     */
    private void connectToWifi(final String networkSSID, final String networkPassword) {

        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }

        WifiConfiguration wifiConf = new WifiConfiguration();
        wifiConf.SSID = "\"" + networkSSID + "\"";

        //For open network
        //wifiConf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

        //For WPA network or hotspot(WPA2-PSK)
        wifiConf.preSharedKey = "\"" + networkPassword + "\"";

        wifiConf.status = WifiConfiguration.Status.ENABLED;
        wifiConf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        wifiConf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        wifiConf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        wifiConf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        wifiConf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        wifiConf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);

        int netID = mWifiManager.addNetwork(wifiConf);
        if (netID != -1) {
            mWifiManager.disconnect();
            mWifiManager.enableNetwork(netID, true);
            mWifiManager.reconnect();

            mWifiManager.saveConfiguration();
        } else {
            Toast.makeText(context, "Connection is not successful", Toast.LENGTH_LONG).show();
        }
    }

    class WifiConnectionReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context c, Intent intent) {
            // Get List of ScanResults
            List<ScanResult> scanResults = mWifiManager.getScanResults();

            // Create Temporary HashMap
            HashMap<String, ScanResult> map = new HashMap<>();
            for (ScanResult scanResult : scanResults) {
                if (scanResult.SSID != null && !scanResult.SSID.isEmpty()) {
                    map.put(scanResult.SSID, scanResult);
                }
            }

            // Add to new List
            List<ScanResult> sortedScanResults = new ArrayList<>(map.values());

            // Create Comparator to sort by level
            Comparator<ScanResult> comparator = new Comparator<ScanResult>() {
                @Override
                public int compare(ScanResult o1, ScanResult o2) {
                    return (Integer.compare(o2.level, o1.level));
                }
            };

            // Apply Comparator and sort
            Collections.sort(sortedScanResults, comparator);
            mScanResultList.clear();
            mScanResultList.addAll(sortedScanResults);
            mScanResultAdapter.notifyDataSetChanged();
        }
    }
}
