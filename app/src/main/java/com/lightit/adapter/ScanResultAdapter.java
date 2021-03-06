package com.lightit.adapter;

import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lightit.R;

import java.util.List;

import static android.net.wifi.WifiManager.calculateSignalLevel;

public class ScanResultAdapter extends RecyclerView.Adapter<ScanResultAdapter.ScanResultHolder> {

    private List<ScanResult> mScanResults;
    private OnItemClickListener mItemClickListener;
    public static String connected_ssid;

    public ScanResultAdapter(List<ScanResult> scanResults) {
        this.mScanResults = scanResults;
    }

  /*  public ScanResultAdapter(List<ScanResult> scanResults, String connected_ssid) {
        this.mScanResults = scanResults;
        this.connected_ssid = connected_ssid;
        Log.d("Connection-adapter",connected_ssid);
    }*/

    @NonNull
    @Override
    public ScanResultHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wifi, parent, false);
        return new ScanResultHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ScanResultHolder holder, int position) {
        ScanResult result = mScanResults.get(position);

        int level = calculateSignalLevel(result.level, 5);
        holder.mImageViewWifiLevel.setImageLevel(level);

        String name = result.SSID;
        holder.mTextViewWifiSSID.setText(name);

        String capacities = result.capabilities.toUpperCase();
        StringBuilder network_type = new StringBuilder();
        if (capacities.contains("WPA") || capacities.contains("WPA2")) {
            network_type.append("Secured");
            if (capacities.contains("WPS")) {
                network_type.append("(WPS available)");
            }
        } else {
            network_type.append("Open");
        }
        holder.mTextViewWifiCapability.setText(network_type);

        if (result.SSID.equals(connected_ssid)) {
            holder.mTextViewWifiSSID.setTextColor(Color.parseColor("#468485"));
            holder.mTextViewWifiCapability.setText(R.string.connected);
        }
    }

    @Override
    public int getItemCount() {
        return mScanResults.size();
    }

    class ScanResultHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mImageViewWifiLevel;
        private TextView mTextViewWifiSSID;
        private TextView mTextViewWifiCapability;

        ScanResultHolder(View view) {
            super(view);
            mImageViewWifiLevel = view.findViewById(R.id.image_wifi_signal);
            mTextViewWifiSSID = view.findViewById(R.id.text_wifi_SSID);
            mTextViewWifiCapability = view.findViewById(R.id.text_wifi_capability);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public ScanResult getClickedItem(int position) {
        return mScanResults.get(position);
    }
}
