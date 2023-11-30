package com.example.ssuchat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.ssuchat.databinding.ActivityProfessorMainPageBinding;

public class ProfessorMainPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityProfessorMainPageBinding binding = ActivityProfessorMainPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.classAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfessorMainPage.this, ClassAddPage.class);
                startActivity(intent);
            }
        });

    }
}