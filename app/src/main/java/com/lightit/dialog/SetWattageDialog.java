package com.lightit.dialog;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.lightit.R;

public class SetWattageDialog extends DialogFragment implements View.OnClickListener {

    private final String TAG = SetWattageDialog.class.getSimpleName();
    public static String SHARED_WATT_NAME = "SHARED WATT NAME";
    public static String WATTAGE="WATTAGE";

    private EditText mWattage_input;

    private Context context;

    public SetWattageDialog() {
        // Required empty public constructor
    }

    public static SetWattageDialog newInstance() {
        SetWattageDialog setWattageDialog = new SetWattageDialog();
        setWattageDialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        return setWattageDialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            context = getActivity();
        } catch (NullPointerException npe) {
            Log.e(TAG, "Error onCreate");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.dialog_set_wattage, container, false);

        mWattage_input = rootView.findViewById(R.id.editText_watt_input);
        TextView mWattage_set = rootView.findViewById(R.id.set_wattage);
        TextView mWattage_cancel = rootView.findViewById(R.id.cancel_set_wattage);
        mWattage_set.setOnClickListener(this);
        mWattage_cancel.setOnClickListener(this);

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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel_set_wattage:
                getDialog().dismiss();
                break;
            case R.id.set_wattage:
                SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_WATT_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                int wattage = Integer.parseInt(mWattage_input.getText().toString());
                editor.putInt(WATTAGE, wattage);
                editor.apply();

                getDialog().dismiss();
                break;
        }
    }
}
