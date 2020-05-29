package com.secondwind.android.exercisefragments;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.secondwind.android.R;

import java.util.Locale;


public class ExerciseTwoNoCamFragment extends Fragment {

    private static final long END_TIME_IN_MILLIS = 120000;
    private ExerciseTwoNoCamFragment.ProfilingExerciseAddListener callback;
    private Button mNextExBtn;
    String firebaseKey;
    private boolean finished;
    private ProgressBar mProgressBar;
    private TextView mTextViewInfo;
    private Button mResetBtn;
    private boolean mTimerRunning;
    private Chronometer mChronometer;
    private long pauseOffset = 0;
    private LinearLayout mChronometerWrapper;
    private ObjectAnimator progressAnimator;

    public ExerciseTwoNoCamFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        callback = (ExerciseTwoNoCamFragment.ProfilingExerciseAddListener) activity;
    }
    public interface ProfilingExerciseAddListener {
        void onProfilingEnd();
        void updateFirebaseProfiling(String s, String input);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_exercise_two_no_cam, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mChronometer = view.findViewById(R.id.chronometer);
        mProgressBar = view.findViewById(R.id.progressBar);
        mTextViewInfo = view.findViewById(R.id.startBtnInfo);
        mResetBtn = view.findViewById(R.id.resetBtn);
        mNextExBtn = view.findViewById(R.id.nextExBtn);
        mChronometerWrapper = view.findViewById(R.id.chronometerWrapper);

        progressAnimator = ObjectAnimator.ofInt(mProgressBar, "progress", 1000, 0);
        progressAnimator.setDuration(END_TIME_IN_MILLIS);
        progressAnimator.setInterpolator(new LinearInterpolator());

        mResetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressAnimator.end();
                mProgressBar.setProgress(1000);
                resetChronometer();
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
                if (mTimerRunning) {
                    progressAnimator.pause();
                    mTextViewInfo.setText(R.string.continue_ex);
                    pauseChronometer();
                    mResetBtn.setVisibility(View.VISIBLE);
                    mNextExBtn.setVisibility(View.VISIBLE);
                } else if (!finished && pauseOffset > 0) {
                    progressAnimator.resume();
                    mTextViewInfo.setText(R.string.pause_ex);
                    startChronometer();
                    mResetBtn.setVisibility(View.GONE);
                    mNextExBtn.setVisibility(View.GONE);
                } else if (!finished) {
                    progressAnimator.start();
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
    }


//    private void updateProgressbar() {
//        Integer progress = (int) ((double) (pauseOffset) / ((double) END_TIME_IN_MILLIS) * 100);
//        mProgressBar.setProgress(progress);
//    }

    private void checkAndUpdateResult() {
        String input = String.valueOf((int) pauseOffset / 1000);
        callback.updateFirebaseProfiling(getString(R.string.firebase_key_planks), input);
        callback.onProfilingEnd();
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

}
