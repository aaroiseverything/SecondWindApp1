package com.secondwind.android;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class GenerateExerciseTwoFragment extends Fragment {

    private GenerateExerciseAddListener callback;

    NavController navController;
    private Button mDoneBtn;
    private SeekBar seekBar2, seekBar3;
    private CheckBox checkBox1,checkBox2,checkBox3,checkBox4,checkBox5,checkBox6;
    private TextView textView5, textView6;
    private ArrayList<String> equipment;
    private Integer timeAvailable, perceivedFatigue;
    public String firebaseKey;
    private DatabaseReference rref;
    private FirebaseAuth mAuth;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    private Button mResetBtn;

    public GenerateExerciseTwoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        callback = (GenerateExerciseAddListener) activity;
    }

    public interface GenerateExerciseAddListener {
        //void onProfilingEnd();
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
        return inflater.inflate(R.layout.fragment_generate_two, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedPreferences = this.getContext().getSharedPreferences(getString(R.string.shared_prefs_name), this.getContext().MODE_PRIVATE);
        //editor = sharedPreferences.edit();

        rref = FirebaseDatabase.getInstance().getReference().child(getString(R.string.firebase_db_members));
        firebaseKey = sharedPreferences.getString(getString(R.string.shared_prefs_key_firebasekey), "");

        mAuth = FirebaseAuth.getInstance();

        // Initalise list of goals
        equipment = new ArrayList<>();

        navController = Navigation.findNavController(view);
        mDoneBtn = view.findViewById(R.id.doneBtn);
        seekBar2 = view.findViewById(R.id.seekBar2);
        seekBar3 = view.findViewById(R.id.seekBar3);
        textView5 = view.findViewById(R.id.textView5);
        textView6 = view.findViewById(R.id.textView6);
        checkBox1 = view.findViewById(R.id.checkBox1);
        checkBox2 = view.findViewById(R.id.checkBox2);
        checkBox3 = view.findViewById(R.id.checkBox3);
        checkBox4 = view.findViewById(R.id.checkBox4);
        checkBox5 = view.findViewById(R.id.checkBox5);
        checkBox6 = view.findViewById(R.id.checkBox6);
        checkBox1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox1.isChecked())
                    equipment.add("No equipment");
                else if (Arrays.asList(equipment).contains("No equipment"))
                    equipment.remove("No equipment");

            }
        });
        checkBox2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox2.isChecked())
                    equipment.add("Home gym");
                else if (Arrays.asList(equipment).contains("Home gym"))
                    equipment.remove("Home gym");

            }
        });
        checkBox3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox3.isChecked())
                    equipment.add("Outdoor/ park gym");
                else if (Arrays.asList(equipment).contains("Outdoor/ park gym"))
                    equipment.remove("Outdoor/ park gym");

            }
        });
        checkBox4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox4.isChecked())
                    equipment.add("Small gym");
                else if (Arrays.asList(equipment).contains("Small gym"))
                    equipment.remove("Small gym");

            }
        });
        checkBox5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox5.isChecked())
                    equipment.add("Medium gym");
                else if (Arrays.asList(equipment).contains("Medium gym"))
                    equipment.remove("Medium gym");

            }
        });
        checkBox6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox6.isChecked())
                    equipment.add("Large gym");
                else if (Arrays.asList(equipment).contains("Large gym"))
                    equipment.remove("Large gym");

            }
        });



        seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textView5.setText(""+progress+ " min");
                timeAvailable = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBar3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textView6.setText(""+Math.round(progress/10) + " /10");
                perceivedFatigue = Math.round(progress/10);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mDoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //push to firebase
                //mDatabase=FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                HashMap<String,Object> usermap = new HashMap<>(); // if you have data in diff data types go for HashMap<String,Object> or you can continue with HashMap<String,String>
                usermap.put(firebaseKey  + "/workoutConfiguration"+"/timeAvailable", timeAvailable);
                usermap.put(firebaseKey  + "/workoutConfiguration"+"/perceivedFatigue", perceivedFatigue);
                usermap.put(firebaseKey  + "/workoutConfiguration"+"/equipment", equipment);
                rref.updateChildren(usermap);
                //back to fragment_generate_one
                navController.navigate(R.id.action_generateExerciseTwoFragment_to_generateExerciseOneFragment);
            }
        });

    }

}
