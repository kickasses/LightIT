package com.lightit.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lightit.R;
import com.lightit.database.Feedback;

public class FeedbackFragment extends Fragment implements View.OnClickListener {

    private EditText mName;
    private EditText mEmail;
    private EditText mFeedback;
    private Button button;

    private OnFragmentInteractionListener mListener;

    public FeedbackFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_feedback, container, false);
        if (mListener != null) {
            mListener.onFragmentInteraction("Feedback");
        }

        mName = rootView.findViewById(R.id.name);
        mEmail = rootView.findViewById(R.id.email);
        mFeedback = rootView.findViewById(R.id.feedback);
        button = rootView.findViewById(R.id.button);
        button.setOnClickListener(this);

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
        if (v == button) {
            // Write a message to the database
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("feedback");

            String name = mName.getText().toString();
            String email = mEmail.getText().toString();
            String feedback = mFeedback.getText().toString();
            if (email.isEmpty() || feedback.isEmpty()) {
                Toast.makeText(getContext(), "Email or/and Feedback is empty", Toast.LENGTH_SHORT).show();
            } else {
                Feedback feedback_obj = new Feedback(name, email, feedback);
                String key = myRef.push().getKey();
                myRef.child(key).setValue(feedback_obj);
                Toast.makeText(getContext(), "Thank you for your Feedback!", Toast.LENGTH_SHORT).show();
                mName.setText("");
                mEmail.setText("");
                mFeedback.setText("");
            }
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String title);
    }
}
