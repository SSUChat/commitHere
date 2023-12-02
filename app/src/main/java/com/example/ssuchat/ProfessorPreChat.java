package com.example.ssuchat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.ssuchat.databinding.ActivityProfessorPreChatBinding;

public class ProfessorPreChat extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityProfessorPreChatBinding binding = ActivityProfessorPreChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


    }
}