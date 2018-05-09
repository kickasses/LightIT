package com.lightit.dialog;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lightit.R;

public class NotificationDialog extends DialogFragment {


    public NotificationDialog() {
        // Required empty public constructor
    }

    public static NotificationDialog newInstance() {
        NotificationDialog notificationDialog = new NotificationDialog();
        notificationDialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        return notificationDialog;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.dialog_notification, container, false);
    }

}
