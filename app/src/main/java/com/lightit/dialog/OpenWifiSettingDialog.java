package com.lightit.dialog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.lightit.R;

public class OpenWifiSettingDialog extends DialogFragment implements View.OnClickListener {

    private Button cancel;
    private Button connect;

    public OpenWifiSettingDialog() {
        // Required empty public constructor
    }

    public static OpenWifiSettingDialog newInstance() {
        return new OpenWifiSettingDialog();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_open_wifi_setting, container, false);
        getDialog().setTitle("Confirmaiton");

        cancel = rootView.findViewById(R.id.btn_cancel);
        connect = rootView.findViewById(R.id.btn_connect);
        cancel.setOnClickListener(this);
        connect.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View view) {
        if (view == cancel) {
            dismiss();
        }
        if (view == connect) {
            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            dismiss();
        }
    }

}
