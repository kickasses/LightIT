package com.lightit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
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
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import static com.lightit.LoginDialog.*;

public class WifiFragment extends Fragment implements LoginDialogListener {

    private static final String TAG = WifiFragment.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private WifiManager mWifiManager;
    private BroadcastReceiver mReceiver;
    private ScanResultAdapter mScanResultAdapter;
    private List<ScanResult> mScanResultList;

    public WifiFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Context context = getActivity().getApplicationContext();
            if (context != null) {
                mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            }
        } catch (NullPointerException npe) {
            Log.e(TAG, "Error setting up Wi-Fi");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_wifi, container, false);

        mScanResultList = new ArrayList<>();

        mReceiver = new WifiConnectionReceiver();
        mWifiManager.startScan();

        mRecyclerView = rootView.findViewById(R.id.recycler_scan_result);
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
        getContext().registerReceiver(mReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    @Override
    public void onPause() {
        super.onPause();
        getContext().unregisterReceiver(mReceiver);
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

    /**
     * Show dialog on recyclerView item click
     *
     * @param SSID - Selected Wi-Fi network SSID
     */
    private void showLoginDialog(String SSID) {
        FragmentManager fm = getFragmentManager();
        LoginDialog editNameDialogFragment = LoginDialog.newInstance(SSID);

        // SETS the target fragment for use later when sending results
        editNameDialogFragment.setTargetFragment(WifiFragment.this, 300);
        editNameDialogFragment.show(fm, "dialog_login");
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

        //For WPA network or hotspot(WPA2-PSK)
        wifiConf.preSharedKey = "\"" + networkPassword + "\"";

        WifiManager wifiManager = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        //For open network
        //  wifiConf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

        wifiConf.status = WifiConfiguration.Status.ENABLED;
        wifiConf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        wifiConf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        wifiConf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        wifiConf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        wifiConf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        wifiConf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);

        int netID = mWifiManager.addNetwork(wifiConf);
        if (netID != -1) {
            //mWifiManager.disconnect();
            wifiManager.enableNetwork(netID, true);
            //mWifiManager.reconnect();
            wifiManager.saveConfiguration();
        }
        checkConnectivity();
    }

    /**
     * Check Wi-Fi connectivity
     */
    private void checkConnectivity() {

        Thread thread = new Thread() {
            public void run() {
                ConnectivityManager connManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

                if (mWifi.isConnected()) {
                    Log.i(TAG, "wifi connected");
                } else {
                    Log.i(TAG, "wifi is not connected");
                }

                WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
                Log.i(TAG, "network ID: " + wifiInfo.getNetworkId());
            }
        };

        thread.start();
    }

    class WifiConnectionReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context c, Intent intent) {
            List<ScanResult> scanResults = mWifiManager.getScanResults();
            mScanResultList.clear();
            mScanResultList.addAll(scanResults);
            mScanResultAdapter.notifyDataSetChanged();
        }
    }
}
