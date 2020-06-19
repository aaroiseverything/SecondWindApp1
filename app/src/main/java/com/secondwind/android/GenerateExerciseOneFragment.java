package com.secondwind.android;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
// This is the 1st fragment for Generate next workout
public class GenerateExerciseOneFragment extends Fragment {


    private GenerateExerciseTwoFragment.GenerateExerciseAddListener callback;
    NavController navController;
    private Button editBtn, startExBtn;
    private TextView date, sets;
    private TextView workout1, workout2;
    public String firebaseKey;
    private DatabaseReference rrefm, rrefw;
    private FirebaseAuth mAuth;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private ListView myListView;
    private ArrayList<String> myArrayList = new ArrayList<>();
    private ArrayList<String> workoutFocus = new ArrayList<>();



    public GenerateExerciseOneFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_generate_one, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreferences = this.getContext().getSharedPreferences(getString(R.string.shared_prefs_name), this.getContext().MODE_PRIVATE);
        firebaseKey = sharedPreferences.getString(getString(R.string.shared_prefs_key_firebasekey), "");
        mAuth = FirebaseAuth.getInstance();
        rrefm = FirebaseDatabase.getInstance().getReference().child("Members").child(firebaseKey);
        rrefw = FirebaseDatabase.getInstance().getReference().child("Workouts");
        navController = Navigation.findNavController(view);
        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance(DateFormat.LONG).format(calendar.getTime());


        ArrayAdapter<String> myArrayAdapter = new ArrayAdapter<>(GenerateExerciseOneFragment.this.getContext(),android.R.layout.simple_list_item_1,myArrayList);
        myListView = view.findViewById(R.id.listview1);
        myListView.setAdapter(myArrayAdapter);

        editBtn = view.findViewById(R.id.editBtn);
        startExBtn = view.findViewById(R.id.startExBtn);
        date = view.findViewById(R.id.date);
        date.setText(currentDate);
        rrefm.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String lastWorkout = dataSnapshot.child("lastWorkout").getValue().toString();
                Integer last = Integer.parseInt(lastWorkout);
                //Finding out their Workout Focus
                if (dataSnapshot.exists()){
                    workoutFocus.clear();
                    for (DataSnapshot dss:dataSnapshot.child("workoutFocus").getChildren()) {
                        String focus = dss.getValue(String.class);
                        workoutFocus.add(focus);
                    }
                }
                if (workoutFocus.contains("Core") && last%2==0){
                    //Getting the workout using this method: https://www.youtube.com/watch?v=DQTLByBY63E
                    getSet("Core",last);
                } else if (workoutFocus.contains("Upper") && last%2==1){
                    getSet("UpperBody",last);
                }else if (workoutFocus.contains("Lower") && last%2==1){
                    getSet("LowerBody",last);
                }else {getSet("LowerBody",last); }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            };
            public void getSet(String type,Integer last){
                rrefw = rrefw.child(type).child("Set" + Integer.toString(Math.floorDiv(last, 2) + 1));
                rrefw.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        String coreWorkout = dataSnapshot.getValue().toString();
                        myArrayList.add(coreWorkout);
                        myArrayAdapter.notifyDataSetChanged();
                        sets = view.findViewById(R.id.sets);
                        String sz = Integer.toString(myArrayList.size());
                        sets.setText(sz+ " Sets");
                    }@Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        myArrayAdapter.notifyDataSetChanged(); }
                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }
                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });
            }
        });



        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_generateExerciseOneFragment_to_generateExerciseTwoFragment);
            }
        });

        startExBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_generateExerciseOneFragment_to_generateExerciseThreeFragment);
            }
        });


    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        callback = (GenerateExerciseTwoFragment.GenerateExerciseAddListener) activity;
    }


}
