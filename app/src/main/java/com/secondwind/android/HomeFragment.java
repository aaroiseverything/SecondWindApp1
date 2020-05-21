package com.secondwind.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class HomeFragment extends Fragment {
    //21May Aaron
    private onFragmentBtnSelected listener;
    Button exerciseButton;
    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //21May Aaron
        //super.onViewCreated(view, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_home,container,false);
        Button updateProfile = (Button) view.findViewById(R.id.updateProfileButton);
        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onButtonSelected();
            }
        });
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        NavController navController = Navigation.findNavController(view);

        exerciseButton = (Button) view.findViewById(R.id.exerciseButton);
        exerciseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
  //              navController.navigate(R.id.action_homeFragment_to_exerciseOneFragment);
            }
        });

    }
    //21May Aaron
    @Override
    public void onAttach(@NonNull Context context){
        super.onAttach(context);
        if(context instanceof onFragmentBtnSelected){
            listener = (onFragmentBtnSelected) context;
        }else{
            throw new ClassCastException(context.toString()+"must implement listener");
        }
    }
    //21May Aaron
    public interface onFragmentBtnSelected{
        public void onButtonSelected();
    }
}