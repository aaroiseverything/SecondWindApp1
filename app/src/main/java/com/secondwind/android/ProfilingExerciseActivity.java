package com.secondwind.android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class ProfilingExerciseActivity extends AppCompatActivity implements ExerciseTwoFragment.ProfilingExerciseAddListener {

    SharedPreferences sharedPreferences;
    DatabaseReference rref;
    String firebaseKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profiling_exercise);

        sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);
        rref = FirebaseDatabase.getInstance().getReference().child("Members");
        firebaseKey = sharedPreferences.getString("firebaseKey", "");
    }

    public void updateFirebaseProfiling(String field, String input) {
        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put(firebaseKey + "/profilingExercises" + field, input);
        rref.updateChildren(userUpdates);
    }

    public void onProfilingEnd() {
        finish();
    }
}
