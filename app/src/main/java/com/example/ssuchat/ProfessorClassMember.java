package com.example.ssuchat;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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
import java.util.List;
import java.util.Map;

public class ProfessorClassMember extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private DrawerLayout drawer;

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
        mAuth = FirebaseAuth.getInstance();
        drawer = findViewById(R.id.drawerLayout);
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

        binding.menuBtn.setOnClickListener(v -> {
            drawer.openDrawer(GravityCompat.END);
        });

        binding.navigationView.setNavigationItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.nav_home) {
                //기능 추가하기. 일단 예시로 토스트
                Toast.makeText(ProfessorClassMember.this, "NavigationDrawer...home..", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_gallery) {
                Toast.makeText(ProfessorClassMember.this, "NavigationDrawer...gallery..", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_logout) {
                drawer.closeDrawer(GravityCompat.END);

                MenuItem logoutItem = binding.navigationView.getMenu().findItem(R.id.nav_logout);
                logoutItem.getActionView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //로그아웃 기능 넣을 부분
                        FirebaseAuth.getInstance().signOut();

                        // Navigate to the login screen
                        Intent intent = new Intent(ProfessorClassMember.this, ssuchat_login.class);
                        startActivity(intent);
                        finish(); // Optional: close the current activity to prevent going back to it with the back button
                    }
                });
            }
            return false;
        });

        userRef.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                if (task.getResult() != null && task.getResult().exists()) {
                    String userName = task.getResult().getString("name");
                    String userEmail = task.getResult().getString("email");
                    String userStudentId = task.getResult().getString("studentId");

                    // Set user information to TextViews
                    TextView userNameTextView = binding.navigationView.getHeaderView(0).findViewById(R.id.user_name_tv);
                    TextView userEmailTextView = binding.navigationView.getHeaderView(0).findViewById(R.id.user_email_tv);
                    TextView userStudentIdTextView = binding.navigationView.getHeaderView(0).findViewById(R.id.user_studentId_tv);

                    userNameTextView.setText(userName);
                    userEmailTextView.setText(userEmail);
                    userStudentIdTextView.setText(userStudentId);
                }
            } else {
                // Handle the error
                Toast.makeText(ProfessorClassMember.this, "Failed to retrieve user information", Toast.LENGTH_SHORT).show();
            }
        });
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
                                    DocumentReference classRef = db.collection("class").document(className+classClass);
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

                                    classRef.update(updateData)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            Log.d("TAG", "Enrolled students list updated successfully");
                                                        }
                                                    })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w("TAG", "Error updating enrolled students list", e);
                                                    }
                                                });

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

        binding.removeClassMemberProfessor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String removeClassMember = binding.removeClassMember.getText().toString();
                if (removeClassMember.isEmpty()) {
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

                                    DocumentReference updateRef = db.collection(name).document(className + classClass);
                                    Log.d(TAG, "Name + Class : " + className + classClass);

                                    updateRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document = task.getResult();
                                                if (document.exists()) {
                                                    List<String> updatedEnrolledStudentsList = new ArrayList<>();

                                                    updatedEnrolledStudentsList = (List<String>) document.get("enrolledStudents");
                                                    updatedEnrolledStudentsList.remove(removeClassMember);
                                                    Log.d(TAG, "okkkkkk");
                                                    Map<String, Object> updateData = new HashMap<>();
                                                    updateData.put("enrolledStudents", updatedEnrolledStudentsList);

                                                    updateRef.set(updateData, SetOptions.merge()) // 새로운 배열로 업데이트
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

                                                    Log.d(TAG, "getDocument : " + document.getString("enrolledStudents"));
                                                }
                                            }
                                        }
                                    });

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
}