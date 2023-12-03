package com.example.ssuchat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.ssuchat.databinding.ActivityProfessorPreChatBinding;

public class ProfessorPreChat extends AppCompatActivity {
    String className;
    String classClass;
    String classNumber;
    String classBuilding;
    String classAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityProfessorPreChatBinding binding = ActivityProfessorPreChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent getIntent = getIntent();
        if(getIntent != null) {
            className = getIntent.getStringExtra("className");
            classClass = getIntent.getStringExtra("classClass");
            classNumber = getIntent.getStringExtra("classNumber");
            classBuilding = getIntent.getStringExtra("classBuilding");
            classAddress = getIntent.getStringExtra("classAddress");

            binding.className.setText(className);
        }

        binding.backMainPageProfessor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfessorPreChat.this, ProfessorMainPage.class);
                startActivity(intent);
            }
        });

        binding.buttonEnterChattingProfessor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfessorPreChat.this, SsuchatChatting.class);
                startActivity(intent);
            }
        });

        binding.subjectInformationProfessor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfessorPreChat.this, ProfessorEditClass.class);

                intent.putExtra("className", className);
                intent.putExtra("classClass", classClass);
                intent.putExtra("classNumber", classNumber);
                intent.putExtra("classBuilding", classBuilding);
                intent.putExtra("classAddress", classAddress);

                startActivity(intent);
            }
        });

        binding.attendanceCheckButtonProfessor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfessorPreChat.this, ProfessorClassMember.class);

                intent.putExtra("className", className);
                intent.putExtra("classClass", classClass);
                intent.putExtra("classNumber", classNumber);
                intent.putExtra("classBuilding", classBuilding);
                intent.putExtra("classAddress", classAddress);

                startActivity(intent);
            }
        });

    }
}