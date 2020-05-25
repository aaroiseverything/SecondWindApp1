package com.secondwind.android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class ProfilingExerciseActivity extends AppCompatActivity implements ExerciseTwoFragment.ProfilingExerciseAddListener {

    SharedPreferences sharedPreferences;
    DatabaseReference rref;
    String firebaseKey;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profiling_exercise);

        sharedPreferences = getSharedPreferences(getString(R.string.shared_prefs_name), Context.MODE_PRIVATE);
        rref = FirebaseDatabase.getInstance().getReference().child(getString(R.string.firebase_db_members));
        firebaseKey = sharedPreferences.getString(getString(R.string.shared_prefs_key_firebasekey), "");
    }

    public void updateFirebaseProfiling(String field, String input) {
        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put(firebaseKey + getString(R.string.firebase_key_profiling_exercises) + field, input);
        rref.updateChildren(userUpdates);
    }

    public void onProfilingEnd() {
        finish();
    }
}
