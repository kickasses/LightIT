package com.lightit.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.lightit.R;

public class SetWattageDialog extends DialogFragment implements View.OnClickListener {

    private final String TAG = SetWattageDialog.class.getSimpleName();

    private EditText mWattage_input;
    private RadioGroup mRadioGroup;
    private TextView mWattage_set;
    private TextView mWattage_cancel;

    public SetWattageDialog() {
        // Required empty public constructor
    }

    public static SetWattageDialog newInstance() {
        SetWattageDialog setWattageDialog = new SetWattageDialog();
        setWattageDialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        return setWattageDialog;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.dialog_set_wattage, container, false);

        mWattage_input = rootView.findViewById(R.id.editText_watt_input);
        mRadioGroup = rootView.findViewById(R.id.radioGroup);
        mWattage_set = rootView.findViewById(R.id.set_wattage);
        mWattage_cancel = rootView.findViewById(R.id.cancel_set_wattage);

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = rootView.findViewById(checkedId);
                Log.i(TAG, radioButton.getText().toString());
                if (mWattage_input.getText() != null) {
                    mWattage_input.setText("");
                }
            }
        });
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
                getDialog().dismiss();
                break;
        }
    }
}
