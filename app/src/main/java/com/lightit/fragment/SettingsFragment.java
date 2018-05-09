package com.lightit.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.lightit.R;
import com.lightit.dialog.NotificationDialog;
import com.lightit.dialog.SetWattageDialog;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class SettingsFragment extends Fragment implements View.OnClickListener {

    private OnFragmentInteractionListener mListener;

    private LinearLayout mLayout_chooseWatt;
    private LinearLayout mLayout_notification;

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        if (mListener != null) {
            mListener.onFragmentInteraction("Settings");
        }

        mLayout_chooseWatt = rootView.findViewById(R.id.linearLayout_choose_watt);
        mLayout_notification = rootView.findViewById(R.id.linearLayout_notification);
        mLayout_chooseWatt.setOnClickListener(this);
        mLayout_notification.setOnClickListener(this);

        return rootView;
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
    public void onClick(View v) {
        FragmentManager fragmentManager = getFragmentManager();

        if (fragmentManager != null) {
            switch (v.getId()) {
                case R.id.linearLayout_choose_watt:
                    SetWattageDialog setWattageDialog = SetWattageDialog.newInstance();
                    setWattageDialog.setTargetFragment(SettingsFragment.this, 300);
                    setWattageDialog.show(fragmentManager, "dialog_choose_watt");
                    break;
                case R.id.linearLayout_notification:
                    NotificationDialog notificationDialog = NotificationDialog.newInstance();
                    notificationDialog.setTargetFragment(SettingsFragment.this, 300);
                    notificationDialog.show(fragmentManager, "dialog_notification");
                    break;
            }
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String title);
    }
}
