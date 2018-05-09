package com.lightit.dialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lightit.R;

public class LoginDialog extends DialogFragment {

    private static final String TAG = LoginDialog.class.getSimpleName();

    private TextView mTextViewSSID;
    private EditText mEditTextPassword;
    private TextView mTextViewConnect;
    private TextView mTextViewCancel;

    public LoginDialog() {
        // Required empty public constructor
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_login, container);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        mTextViewSSID = view.findViewById(R.id.text_dialog_SSID);
        mEditTextPassword = view.findViewById(R.id.editText_password);
        mTextViewConnect = view.findViewById(R.id.text_dialog_connect);
        mTextViewCancel = view.findViewById(R.id.text_dialog_cancel);

        if (getArguments() != null) {
            String networkSSID = getArguments().getString("SSID");
            mTextViewSSID.setText(networkSSID);
        }

        // Show soft keyboard automatically and request focus to password field
        mEditTextPassword.requestFocus();
        if (getDialog().getWindow() != null) {
            getDialog().getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        } else {
            Toast.makeText(getContext(), "Cannot start auto keyboard", Toast.LENGTH_SHORT).show();
        }

        mTextViewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        mTextViewConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBackData();
                getDialog().dismiss();
            }
        });
    }

    // Defines the listener interface
    public interface LoginDialogListener {
        void onFinishLoginDialog(String networkSSID, String inputPassword);
    }

    public void sendBackData() {
        LoginDialogListener listener = (LoginDialogListener) getTargetFragment();
        if (listener != null) {
            listener.onFinishLoginDialog(mTextViewSSID.getText().toString(), mEditTextPassword.getText().toString());
        }
        dismiss();
    }
}
