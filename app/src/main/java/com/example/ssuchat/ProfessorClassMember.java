package com.example.ssuchat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.ssuchat.databinding.ActivityProfessorClassMemberBinding;

public class ProfessorClassMember extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityProfessorClassMemberBinding binding = ActivityProfessorClassMemberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



    }
}