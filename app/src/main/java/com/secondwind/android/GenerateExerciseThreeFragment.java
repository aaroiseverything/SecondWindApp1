package com.secondwind.android;

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
    private Button mNextExBtn;
    String firebaseKey;


    public GenerateExerciseThreeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        callback = (GenerateExerciseAddListener) activity;
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        mNextExBtn = view.findViewById(R.id.doneBtn);

        mNextExBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndUpdateResult();
            }
        });
    }
    // Updates firebase
    private void checkAndUpdateResult() {
//        String input = String.valueOf((int) pauseOffset / 1000);
//        callback.updateFirebaseProfiling(getString(R.string.firebase_key_planks), input);
        callback.onProfilingEnd();
    }
}
