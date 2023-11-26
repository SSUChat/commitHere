package com.example.ssuchat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.ssuchat.databinding.ActivitySsuchatPreChatBinding;

public class SsuchatPreChat extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySsuchatPreChatBinding binding = ActivitySsuchatPreChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backMainPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SsuchatPreChat.this, ssuchat_main_page.class);
                startActivity(intent);
            }
        });

    }
}