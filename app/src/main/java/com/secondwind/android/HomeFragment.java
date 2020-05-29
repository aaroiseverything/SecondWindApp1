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
    private EditProfileBtnAddListener callback;
    private RelativeLayout exerciseButton;
    private RelativeLayout updateProfile;
    private RelativeLayout generateWorkoutButton;
    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //super.onViewCreated(view, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_home, container, false);
        updateProfile = (RelativeLayout) view.findViewById(R.id.updateProfileButton);
        exerciseButton = (RelativeLayout) view.findViewById(R.id.exerciseButton);
        generateWorkoutButton = (RelativeLayout) view.findViewById(R.id.generateWorkoutButton);

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
                callback.onEditProfileBtnSelected();
            }
        });

        generateWorkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), GenerateExerciseActivity.class);
                startActivity(intent);
            }
        });
        return view;

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        callback = (EditProfileBtnAddListener) context;
    }

    public interface EditProfileBtnAddListener {
        public void onEditProfileBtnSelected();
    }
}