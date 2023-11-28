package com.example.ssuchat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.ssuchat.databinding.ActivitySsuchatChattingBinding;

public class SsuchatChatting extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySsuchatChattingBinding binding = ActivitySsuchatChattingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



    }
}