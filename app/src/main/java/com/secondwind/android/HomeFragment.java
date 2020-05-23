package com.secondwind.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {
    //21May Aaron
    private onFragmentBtnSelected listener;
    private RelativeLayout exerciseButton;
    private RelativeLayout updateProfile;
    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //21May Aaron
        //super.onViewCreated(view, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_home, container, false);
        updateProfile = view.findViewById(R.id.updateProfileButton);
        exerciseButton = (RelativeLayout) view.findViewById(R.id.exerciseButton);
        exerciseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProfilingExerciseActivity.class);
                startActivity(intent);
            }
        });

        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onButtonSelected();
            }
        });
        return view;
    }

    //21May Aaron test
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof onFragmentBtnSelected) {
            listener = (onFragmentBtnSelected) context;
        } else {
            throw new ClassCastException(context.toString() + "must implement listener");
        }
    }

    //21May Aaron
    public interface onFragmentBtnSelected {
        public void onButtonSelected();
    }
}