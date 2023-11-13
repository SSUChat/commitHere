package com.example.ssuchat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.ssuchat.databinding.ActivitySsuchatLoginBinding;

public class ssuchat_login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ssuchat_login);

        ActivitySsuchatLoginBinding binding = ActivitySsuchatLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



    }
}