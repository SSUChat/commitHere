package com.example.ssuchat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.ssuchat.databinding.ActivitySsuchatRegisterBinding;
import com.google.firebase.ktx.Firebase;

public class ssuchat_register extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ssuchat_register);

        ActivitySsuchatRegisterBinding binding = ActivitySsuchatRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}