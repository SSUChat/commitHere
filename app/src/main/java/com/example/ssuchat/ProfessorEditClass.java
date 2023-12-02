package com.example.ssuchat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.ssuchat.databinding.ActivityProfessorEditClassBinding;

public class ProfessorEditClass extends AppCompatActivity {
    String className;
    String classClass;
    String classNumber;
    String classBuilding;
    String classAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityProfessorEditClassBinding binding = ActivityProfessorEditClassBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent getIntent = getIntent();
        if(getIntent != null) {
            className = getIntent.getStringExtra("className");
            classClass = getIntent.getStringExtra("classClass");
            classNumber = getIntent.getStringExtra("classNumber");
            classBuilding = getIntent.getStringExtra("classBuilding");
            classAddress = getIntent.getStringExtra("classAddress");

            binding.textviewClassName.setText(className);
            binding.textviewClassClass.setText(classClass);
            binding.textviewClassNumber.setText(classNumber);
            binding.textviewClassBuilding.setText(classBuilding);
            binding.textviewClassAddress.setText(classAddress);
        }

        binding.backPreChatProfessor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfessorEditClass.this, ProfessorPreChat.class);
                startActivity(intent);
            }
        });

    }
}