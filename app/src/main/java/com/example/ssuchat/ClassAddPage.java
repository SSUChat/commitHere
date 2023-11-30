package com.example.ssuchat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.ssuchat.databinding.ActivityClassAddPageBinding;

public class ClassAddPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityClassAddPageBinding binding = ActivityClassAddPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.addClassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ClassAddPage.this, ProfessorMainPage.class);
                Toast.makeText(ClassAddPage.this, "강의가 추가되었습니다!!", Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });

        binding.goBackMainPageProfessor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ClassAddPage.this, ProfessorMainPage.class);
                startActivity(intent);
            }
        });

    }
}