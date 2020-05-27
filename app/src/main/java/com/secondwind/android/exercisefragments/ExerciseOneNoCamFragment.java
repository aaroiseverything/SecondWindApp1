package com.secondwind.android.exercisefragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.CountDownTimer;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.secondwind.android.R;

import java.util.Locale;


public class ExerciseOneNoCamFragment extends Fragment {

    private NavController navController;
    private Button mResetBtn;
    private boolean mTimerRunning;
    private static final long START_TIME_IN_MILLIS = 60000;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;
    private LinearLayout mCountdownWrapper;
    //    private NumberPicker mPicker;
    private boolean finished;
    private long startTimeInSeconds;
    private TextView mTextViewCountdown;
    private CountDownTimer mCountDownTimer;
    private ProgressBar mProgressBar;
    private TextView mTextViewInfo;
    private Button mNextExBtn;
    private ExerciseTwoNoCamFragment.ProfilingExerciseAddListener callback;
    private String mReps = "";

    public ExerciseOneNoCamFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startTimeInSeconds = (int) START_TIME_IN_MILLIS / 1000;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_exercise_one_no_cam, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        mTextViewCountdown = view.findViewById(R.id.countdown);
        mProgressBar = view.findViewById(R.id.progressBar);
        mTextViewInfo = view.findViewById(R.id.startBtnInfo);
        mNextExBtn = view.findViewById(R.id.nextExBtn);

        mResetBtn = view.findViewById(R.id.resetBtn);
        mCountdownWrapper = view.findViewById(R.id.countdownWrapper);
        mNextExBtn = view.findViewById(R.id.nextExBtn);

        mNextExBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Reps done");
                View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.dialog_layout, (ViewGroup) getView(), false);
                final EditText input = (EditText) viewInflated.findViewById(R.id.input);
                builder.setView(viewInflated);


                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mReps = input.getText().toString();
                        if (mReps.length() <= 0) {
                            input.setError("Please enter a number");
                        } else {
                            checkAndUpdateResult();
                            dialog.dismiss();
                        }
                    }
                });
            }
        });


        mResetBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                mTimeLeftInMillis = START_TIME_IN_MILLIS;
                finished = false;
                mTextViewInfo.setText(R.string.start_ex);
                updateCountdownText();
                mResetBtn.setVisibility(View.GONE);
            }
        });

        mCountdownWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        callback = (ExerciseTwoNoCamFragment.ProfilingExerciseAddListener) activity;
    }

    private void checkAndUpdateResult() {
        callback.updateFirebaseProfiling(getString(R.string.firebase_key_pushups), String.valueOf(mReps));
        navController.navigate(R.id.action_exerciseOneNoCamFragment_to_exerciseTwoFragment);
    }
}
