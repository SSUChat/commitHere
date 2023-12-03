package com.example.ssuchat;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.ssuchat.databinding.ActivityProfessorClassMemberBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProfessorClassMember extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private void initFirebaseAuth() {
        mAuth = FirebaseAuth.getInstance();
    }

    String className;
    String classClass;
    String classNumber;
    String classBuilding;
    String classAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityProfessorClassMemberBinding binding = ActivityProfessorClassMemberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initFirebaseAuth();
        db = FirebaseFirestore.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        DocumentReference userRef = db.collection("users").document(userId);
        ArrayList<String> enrolledStudentsList = new ArrayList<>();

        Intent getIntent = getIntent();
        if(getIntent != null) {
            className = getIntent.getStringExtra("className");
            classClass = getIntent.getStringExtra("classClass");
            classNumber = getIntent.getStringExtra("classNumber");
            classBuilding = getIntent.getStringExtra("classBuilding");
            classAddress = getIntent.getStringExtra("classAddress");
        }

        binding.backPreChatProfessor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfessorClassMember.this, ProfessorPreChat.class);
                startActivity(intent);
            }
        });

        binding.addClassMemberProfessor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String addClassMember = binding.addClassMember.getText().toString();
                if(addClassMember.isEmpty()) {
                    Toast.makeText(ProfessorClassMember.this, "필드를 채워주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    // 사용자 문서가 존재할 경우
                                    String name = document.getString("name");

                                    DocumentReference updateRef = db.collection(name).document(className+classClass);
                                    Log.d(TAG, "Name + Class : " + className + classClass);
                                    enrolledStudentsList.add(addClassMember);

                                    Map<String, Object> updateData = new HashMap<>();
                                    updateData.put("enrolledStudents", FieldValue.arrayUnion(addClassMember));

                                    updateRef.update(updateData)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d("TAG", "Enrolled students list updated successfully");
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w("TAG", "Error updating enrolled students list", e);
                                                }
                                            });

                                    saveClassDataToFirestore(name, className, classClass);

                                    Log.d(TAG, "getDocument : " + document.getString("enrolledStudents"));

                                } else {
                                    // 사용자 문서가 존재하지 않을 경우
                                    Log.d(TAG, "No such document");
                                }
                            } else {
                                // 작업이 실패한 경우
                                Log.d(TAG, "get failed with ", task.getException());
                            }
                        }
                    });
                }
            }
        });



    }

    private void saveClassDataToFirestore(String name, String className, String classClass) {
        db = FirebaseFirestore.getInstance();
        String doc = className + classClass;
        DocumentReference userRef = db.collection(name).document(doc);
        Map<String, Object> userData = new HashMap<>();


    }
}