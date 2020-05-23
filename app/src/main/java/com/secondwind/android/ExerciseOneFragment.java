package com.secondwind.android;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Locale;

public class ExerciseOneFragment extends Fragment {


    private ExerciseTwoFragment.ProfilingExerciseAddListener callback;

    private static final long START_TIME_IN_MILLIS = 60000;

    NavController navController;

    private Button mNextExBtn;
    private boolean finished;
    private long startTimeInSeconds;
    private TextView mTextViewCountdown;
    private CountDownTimer mCountDownTimer;
    private ProgressBar mProgressBar;
    private YoutubeFragment youtubeFragment;
    private TextView mTextViewInfo;
    private Button mResetBtn;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;
    private LinearLayout mCountdownWrapper;
    private NumberPicker mPicker;

    public ExerciseOneFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startTimeInSeconds = (int) START_TIME_IN_MILLIS / 1000;
        youtubeFragment = YoutubeFragment.newInstance(getString(R.string.youtube_one_id));
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.flYoutube, youtubeFragment).commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_exercise_one, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        mNextExBtn = view.findViewById(R.id.nextExBtn);
        mTextViewCountdown = view.findViewById(R.id.countdown);
        mProgressBar = view.findViewById(R.id.progressBar);
        mTextViewInfo = view.findViewById(R.id.startBtnInfo);
        mResetBtn = view.findViewById(R.id.resetBtn);
        mCountdownWrapper = view.findViewById(R.id.countdownWrapper);

        mPicker = view.findViewById(R.id.numberPicker);
        mPicker.setMaxValue(100);
        mPicker.setMinValue(0);

        mResetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTimeLeftInMillis = START_TIME_IN_MILLIS;
                finished = false;
                mTextViewInfo.setText(R.string.start_ex);
                updateCountdownText();
                mResetBtn.setVisibility(View.GONE);
                mPicker.setValue(0);
            }
        });

        mNextExBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndUpdateResult();
            }
        });

        mCountdownWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                youtubeFragment.pauseVid();
                if (mTimerRunning) {
                    mTextViewInfo.setText(R.string.continue_ex);
                    pauseTimer();
                    mResetBtn.setVisibility(View.VISIBLE);
                } else if (finished == false) {
                    mTextViewInfo.setText(R.string.pause_ex);
                    startTimer();
                    mResetBtn.setVisibility(View.GONE);
                }
            }
        });

        updateCountdownText();
    }

    private void startTimer() {
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountdownText();
            }

            @Override
            public void onFinish() {
                mTextViewInfo.setText(R.string.finished);
                mTimerRunning = false;
                mResetBtn.setVisibility(View.VISIBLE);
                finished = true;
            }
        }.start();
        mTimerRunning = true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateCountdownText() {
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;

        Integer progress = (int) ((double) mTimeLeftInMillis / 1000 / ((double) startTimeInSeconds) * 100);
        mProgressBar.setProgress(progress);

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        mTextViewCountdown.setText(timeLeftFormatted);
    }

    private void pauseTimer() {
        mCountDownTimer.cancel();
        mTimerRunning = false;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        callback = (ExerciseTwoFragment.ProfilingExerciseAddListener) activity;
    }

    private void checkAndUpdateResult() {
        callback.updateFirebaseProfiling(getString(R.string.firebase_key_pushups), String.valueOf(mPicker.getValue()));
        navController.navigate(R.id.action_exerciseOneFragment_to_exerciseTwoFragment);
    }
}
