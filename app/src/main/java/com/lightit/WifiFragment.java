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
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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

    private String mNetworkPin;

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

        mReceiver = new WifiScanReceiver();
        mWifiManager.startScan();

        mRecyclerView = rootView.findViewById(R.id.recycler_scan_result);
        mScanResultAdapter = new ScanResultAdapter(mScanResultList);
        mRecyclerView.setAdapter(mScanResultAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mScanResultAdapter.SetOnItemClickListener(new ScanResultAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.i(TAG, "item position: " + String.valueOf(position));
                ScanResult scan_item = mScanResultAdapter.getScanItem(position);
                Log.i(TAG, scan_item.SSID);
                showLoginDialog(scan_item.SSID);
                //loginWith(scan_item.SSID, mNetworkPin);
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

    // Call this method to launch the edit dialog
    private void showLoginDialog(String SSID) {
        FragmentManager fm = getFragmentManager();
        LoginDialog editNameDialogFragment = LoginDialog.newInstance(SSID);

        // SETS the target fragment for use later when sending results
        editNameDialogFragment.setTargetFragment(WifiFragment.this, 300);
        editNameDialogFragment.show(fm, "dialog_login");
    }

    // This is called when the dialog is completed and the results have been passed
    @Override
    public void onFinishLoginDialog(String password) {
        Log.i(TAG, "password: " + password);
        mNetworkPin = password;
    }

    private void loginWith(String networkSSID, String networkPin) {
        WifiConfiguration wifiConf = new WifiConfiguration();
        wifiConf.SSID = "\"" + networkSSID + "\"";

        //For WPA network
        wifiConf.preSharedKey = "\"" + networkPin + "\"";

        //For open network
        //wifiConf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

        //Add it to WifiManager
        mWifiManager.addNetwork(wifiConf);

        //Enable, so Android connects to the network
        List<WifiConfiguration> wifiConfigurationList = mWifiManager.getConfiguredNetworks();
        for (WifiConfiguration confItem : wifiConfigurationList) {
            if (confItem.SSID != null && confItem.SSID.equals(wifiConf.SSID)) {

                //Disconnect in case you're already connected
                mWifiManager.disconnect();
                mWifiManager.enableNetwork(confItem.networkId, true);
                mWifiManager.reconnect();

                break;
            }
        }
    }

    class WifiScanReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context c, Intent intent) {
            List<ScanResult> scanResults = mWifiManager.getScanResults();
            mScanResultList.clear();
            mScanResultList.addAll(scanResults);
            mScanResultAdapter.notifyDataSetChanged();
        }
    }
}
