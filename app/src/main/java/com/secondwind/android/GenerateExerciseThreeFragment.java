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

    private static final long END_TIME_IN_MILLIS = 120000;
    private GenerateExerciseAddListener callback;

    NavController navController;
    private Button mNextExBtn;
    String firebaseKey;

    private boolean finished;
    private ProgressBar mProgressBar;
    private YoutubeFragment youtubeFragment;
    private TextView mTextViewInfo;
    private Button mResetBtn;
    private boolean mTimerRunning;
    private Chronometer mChronometer;
    private long pauseOffset = 0;
    private LinearLayout mChronometerWrapper;


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
        youtubeFragment = YoutubeFragment.newInstance(getString(R.string.youtube_two_id));
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.flYoutube, youtubeFragment).commit();
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
        mChronometer = view.findViewById(R.id.chronometer);
        mProgressBar = view.findViewById(R.id.progressBar);
        mTextViewInfo = view.findViewById(R.id.startBtnInfo);
        mResetBtn = view.findViewById(R.id.resetBtn);
        mNextExBtn = view.findViewById(R.id.doneBtn);
        mChronometerWrapper = view.findViewById(R.id.chronometerWrapper);

        mResetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetChronometer();
                updateProgressbar();
            }
        });

        mNextExBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndUpdateResult();
            }
        });

        mChronometerWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                youtubeFragment.pauseVid();
                if (mTimerRunning) {
                    mTextViewInfo.setText(R.string.continue_ex);
                    pauseChronometer();
                    mResetBtn.setVisibility(View.VISIBLE);
                    mNextExBtn.setVisibility(View.VISIBLE);
                } else if (finished == false) {
                    mTextViewInfo.setText(R.string.pause_ex);
                    startChronometer();
                    mResetBtn.setVisibility(View.GONE);
                    mNextExBtn.setVisibility(View.GONE);
                }
            }
        });

        mChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                pauseOffset = SystemClock.elapsedRealtime() - mChronometer.getBase();
                updateProgressbar();
                String currentTime = mChronometer.getText().toString();

                int minutes = (int) (END_TIME_IN_MILLIS / 1000) / 60;
                int seconds = (int) (END_TIME_IN_MILLIS / 1000) % 60;
                String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

                if (currentTime.equals(timeFormatted)) //put time according to you
                {
                    mTextViewInfo.setText(R.string.finished);
                    mTimerRunning = false;
                    mNextExBtn.setVisibility(View.VISIBLE);
                    mResetBtn.setVisibility(View.VISIBLE);
                    finished = true;
                    mChronometer.stop();
                }
            }
        });

        updateProgressbar();
    }

    public void startChronometer() {
        if (!mTimerRunning) {
            mChronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            mChronometer.start();
            mTimerRunning = true;
        }
    }

    public void pauseChronometer() {
        if (mTimerRunning) {
            mChronometer.stop();
            pauseOffset = SystemClock.elapsedRealtime() - mChronometer.getBase();
            mTimerRunning = false;
        }
    }

    public void resetChronometer() {
        mChronometer.setBase(SystemClock.elapsedRealtime());
        pauseOffset = 0;
        mTextViewInfo.setText(R.string.start_ex);
        mResetBtn.setVisibility(View.GONE);
        mNextExBtn.setVisibility(View.GONE);
        finished = false;
    }

    private void updateProgressbar() {
        Integer progress = (int) ((double) (pauseOffset) / ((double) END_TIME_IN_MILLIS) * 100);
        mProgressBar.setProgress(progress);
    }
    // Updates firebase
    private void checkAndUpdateResult() {
        String input = String.valueOf((int) pauseOffset / 1000);
        callback.updateFirebaseProfiling(getString(R.string.firebase_key_planks), input);
        callback.onProfilingEnd();
    }
}
