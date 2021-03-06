package com.secondwind.android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import java.util.Locale;

public class GenerateExerciseThreeFragment extends Fragment {

    private GenerateExerciseAddListener callback;

    NavController navController;
    private Button startExBtn, editWorkoutBtn;
    private TextView textExercise;
    String firebaseKey;


    public GenerateExerciseThreeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //callback = (GenerateExerciseAddListener) activity;
    }

    public interface GenerateExerciseAddListener {
        void onProfilingEnd();
        void updateFirebaseProfiling(String s, String input);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_generate_three, container, false);
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        startExBtn = view.findViewById(R.id.startExBtn);
        editWorkoutBtn = view.findViewById(R.id.editWorkoutBtn);
        textExercise = view.findViewById(R.id.textView22);

        textExercise.setText("Exercise 1: \n"+ getArguments().getString("Exercise 1"));  //This gets the exercises from the bundle from ExOneFrag

        startExBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //checkAndUpdateResult();
                navController.navigate(R.id.action_generateExerciseThreeFragment_to_generateExerciseFourFragment);
            }
        });
        editWorkoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_generateExerciseThreeFragment_to_generateExerciseTwoFragment);
            }
        });
    }
    // Updates firebase
    private void checkAndUpdateResult() {
//        String input = String.valueOf((int) pauseOffset / 1000); //
//        callback.updateFirebaseProfiling(getString(R.string.firebase_key_planks), input);
        callback.onProfilingEnd();
    }
}
