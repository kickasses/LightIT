package com.lightit;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class LoginDialog extends DialogFragment {

    private static final String TAG = LoginDialog.class.getSimpleName();

    private TextView mTextViewSSID;
    private EditText mEditTextPassword;
    private CheckBox mCheckBoxShowPassword;
    private TextView mTextViewConnect;
    private TextView mTextViewCancel;

    public LoginDialog() {
        // Empty constructor required for DialogFragment
    }

    public static LoginDialog newInstance(String SSID) {
        LoginDialog loginDialog = new LoginDialog();
        loginDialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        Bundle args = new Bundle();
        args.putString("SSID", SSID);
        loginDialog.setArguments(args);
        return loginDialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_login, container);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        mTextViewSSID = view.findViewById(R.id.text_dialog_SSID);
        mEditTextPassword = view.findViewById(R.id.editText_password);
        mCheckBoxShowPassword = view.findViewById(R.id.checkBox_show_password);
        mTextViewConnect = view.findViewById(R.id.text_dialog_connect);
        mTextViewCancel = view.findViewById(R.id.text_dialog_cancel);

        String SSID = getArguments().getString("SSID");
        mTextViewSSID.setText(SSID);

        // Show soft keyboard automatically and request focus to password field
        mEditTextPassword.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        mTextViewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing dialog");
                getDialog().dismiss();
            }
        });

        mTextViewConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: connect");
                sendBackPassword();
                getDialog().dismiss();
            }
        });
    }

    // Defines the listener interface
    public interface LoginDialogListener {
        void onFinishLoginDialog(String inputText);
    }

    public void sendBackPassword() {
        LoginDialogListener listener = (LoginDialogListener) getTargetFragment();
        listener.onFinishLoginDialog(mEditTextPassword.getText().toString());
        dismiss();
    }
}
