package com.secondwind.android.exercisefragments;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.secondwind.android.R;
import com.secondwind.android.youtube.YoutubeFragment;

public class ExerciseOneFragment extends Fragment {

    NavController navController;
    private Button mNextExBtn;
    private YoutubeFragment youtubeFragment;

    public ExerciseOneFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        mNextExBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_exerciseOneFragment_to_exerciseOneNoCamFragment);
            }
        });
    }
}
